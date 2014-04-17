package debop4s.core

import java.util.concurrent.atomic.{AtomicInteger, AtomicReference}
import scala.collection.generic.CanBuild
import scala.collection.immutable.Queue
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success, Try}

/**
 * twitter/util Event 를 porting 했습니다.
 *
 * Events are instantaneous values, defined only at particular
 * instants in time (cf. [[com.twitter.util.Var Vars]], which are
 * defined at all times). It is possible to view Events as the
 * discrete counterpart to [[com.twitter.util.Var Var]]'s continuous
 * nature.
 *
 * Events are observed by registering [[debop4s.core.Witness Witness]] to which the Event's values are notified.
 *
 * Created by debop on 2014. 4. 13.
 */
trait Event[+T] {self =>

    /**
    * Register the given [[debop4s.core.Witness]] to this Event.
    * Witness are notified of new values until it is deregistered by the returned [[debop4s.core.Closable]]
    */
    def register(s: Witness[T]): Closable

    final def respond(s: T => Unit): Closable = register(Witness(s))

    def collect[U](f: PartialFunction[T, U]): Event[U] = new Event[U] {
        def register(s: Witness[U]) =
            self respond { t =>
                if (f.isDefinedAt(t)) s.notify(f(t))
            }
    }

    /**
     * Build a new Event by keeping only those Event values that match
     * the predicate `p`.
     */
    def filter(predicate: T => Boolean): Event[T] =
        collect { case t if predicate(t) => t }

    /**
     * Build a new Event by transforming each new event value with `f`.
     */
    def map[U](func: T => U): Event[U] =
        collect { case t => func(t) }

    /**
     * Build a new Event by incrementally accumulating over events,
     * starting with value `z`. Each intermediate aggregate is notified
     * to the derived event.
     */
    def foldLeft[U](z: U)(func: (U, T) => U): Event[U] = new Event[U] {
        def register(s: Witness[U]) = {
            var a = z
            val mu = new {}
            self respond mu.synchronized { t =>
                a = func(a, t)
                s.notify(a)
            }
        }
    }

    /**
     * Build a new Event representing a sliding window of at-most `n`.
     * Each event notified by the parent are added to a queue of size
     * at-most `n`. This queue is in turn notified to register of
     * the returned event.
     */
    def sliding(n: Int): Event[Seq[T]] = new Event[Seq[T]] {
        require(n > 0)
        override def register(s: Witness[Seq[T]]): Closable = {
            val mu = new {}
            var q = Queue.empty[T]
            self respond { t =>
                s.notify(mu.synchronized {
                    q = q enqueue t
                    while (q.length > n) {
                        val (_, q1) = q.dequeue
                        q = q1
                    }
                    q
                })
            }
        }
    }

    def mergeMap[U](func: T => Event[U]): Event[U] = new Event[U] {
        override def register(s: Witness[U]): Closable = {
            @volatile var inners = Nil: List[Closable]
            val outer = self respond { el =>
                inners.synchronized { inners ::= func(el).register(s) }
            }
            Closable.make { deadline =>
                Closable.all(inners: _*).close(deadline)
                outer.close(deadline)
            }
        }
    }

    /**
     * Merge two Events of different types.
     */
    def select[U](other: Event[U]): Event[Either[T, U]] = new Event[Either[T, U]] {
        override def register(s: Witness[Either[T, U]]): Closable = {
            Closable.all {
                self.register(s comap { t => Left(t) })
                other.register(s comap { u => Right(u) })
            }
        }
    }

    /**
     * Merge two event streams in lock-step, combining corresponding
     * event values.
     *
     * @note This can be dangerous! Since the implementation needs to
     * queue outstanding Event-values from the slower producer, if one
     * Event outpaces another, this queue can grow in an unbounded
     * fashion.
     */
    def zip[U](other: Event[U]): Event[(T, U)] = new Event[(T, U)] {
        override def register(s: Witness[(T, U)]): Closable = {
            val mu = new {}
            var state: Option[Either[Queue[T], Queue[U]]] = None

            val left = self respond mu.synchronized { t =>
                state match {
                    case None => state = Some(Left(Queue(t)))
                    case Some(Left(q)) => state = Some(Left(q enqueue t))
                    case Some(Right(Queue(u, rest@_*))) =>
                        if (rest.isEmpty) state = None
                        else state = Some(Right(Queue(rest: _*)))
                        s.notify((t, u))
                }
            }

            val right = other respond mu.synchronized { u =>
                state match {
                    case None => state = Some(Right(Queue(u)))
                    case Some(Right(q)) => state = Some(Right(q enqueue u))
                    case Some(Left(Queue(t, rest@_*))) =>
                        if (rest.isEmpty) state = None
                        else state = Some(Left(Queue(rest: _*)))
                        s.notify((t, u))
                }
            }

            Closable.all(left, right)
        }
    }

    def joinLast[U](other: Event[U]): Event[(T, U)] = new Event[(T, U)] {
        override def register(s: Witness[(T, U)]): Closable = {
            import Event.JoinState
            import JoinState._

            var state: JoinState[T, U] = Empty
            val mu = new {}

            val left = self respond mu.synchronized { t =>
                state match {
                    case Empty | LeftHalf(_) =>
                        state = LeftHalf(t)
                    case RightHalf(u) =>
                        state = Full(t, u)
                        s.notify((t, u))
                    case Full(_, u) =>
                        state = Full(t, u)
                        s.notify((t, u))
                }
            }
            val right = other respond mu.synchronized { u =>
                state match {
                    case Empty | RightHalf(_) =>
                        state = RightHalf(u)
                    case LeftHalf(t) =>
                        state = Full(t, u)
                        s.notify((t, u))
                    case Full(t, _) =>
                        state = Full(t, u)
                        s.notify((t, u))
                }
            }

            Closable.all(left, right)
        }
    }

    /**
     * An event which consists of the first `howmany` values
     * in the parent Event.
     */
    def take(howmany: Int): Event[T] = new Event[T] {
        override def register(s: Witness[T]): Closable = {
            val n = new AtomicInteger(0)
            val c = new AtomicReference(Closable.nop)
            c.set(self respond { t =>
                if (n.incrementAndGet() <= howmany) s.notify(t)
                else c.getAndSet(Closable.nop).close()
            })

            if (n.get() == howmany)
                c.getAndSet(Closable.nop).close()

            Closable.ref(c)
        }
    }

    /**
    * Merge two events; the resulting event interleaves events from this and `other`
    */
    def merge[U >: T](other: Event[U]): Event[U] = new Event[U] {
        override def register(s: Witness[U]): Closable = {
            val c1 = self.register(s)
            val c2 = other.register(s)
            Closable.all(c1, c2)
        }
    }

    /**
     * Progressively build a collection of events using the passed-in builder.
     * A value containing the current version of the collection
     * is notified for each incoming event.
     */
    def build[U >: T, That](implicit cbf: CanBuild[U, That]) = new Event[That] {
        override def register(s: Witness[That]): Closable = {
            val b = cbf()
            self respond { t =>
                b += t
                s.notify(b.result())
            }
        }
    }

    def toFuture: Future[T] = {
        val p = Promise[T]()
        val c = register(Witness(p))

        p.future onComplete {
            case Success(_) => c.close()
            case Failure(exc) => p failure exc
        }

        p.future
    }

}


object Event {
    private sealed trait JoinState[+T, +U]
    private object JoinState {
        object Empty extends JoinState[Nothing, Nothing]
        case class LeftHalf[T](t: T) extends JoinState[T, Nothing]
        case class RightHalf[U](u: U) extends JoinState[Nothing, U]
        case class Full[T, U](t: T, u: U) extends JoinState[T, U]
    }

    def apply[T](): Event[T] with Witness[T] = new Event[T] with Witness[T] {
        @volatile var registers: List[Witness[T]] = Nil

        override def register(s: Witness[T]): Closable = {
            registers ::= s
            Closable.make { _ =>
                registers = registers filter (_ ne s)
                Future {}
            }
        }

        override def notify(t: T) = synchronized {
            for (s <- registers) s.notify(t)
        }
    }
}

/** A witness is the recipient of [[debop4s.core.Event]] */
trait Witness[-N] {self =>

    /** Notify this witness with the given note. */
    def notify(note: N)

    def comap[M](f: M => N): Witness[M] = new Witness[M] {
        def notify(m: M) = self.notify(f(m))
    }
}

object Witness {

    def apply[T](ref: AtomicReference[T]): Witness[T] = new Witness[T] {
        def notify(t: T) { ref.set(t) }
    }

    def apply[T](p: Promise[T]): Witness[T] = new Witness[T] {
        def notify(t: T) {
            p complete Try { t }
        }
    }

    def apply[T](f: T => Unit): Witness[T] = new Witness[T] {
        def notify(t: T) { f(t) }
    }

    //    def apply[T](u: Updatable[T]): Witness[T] = new Witness[T] {
    //        def notify(t: T) = { u() = t }
    //    }

    val printer: Witness[Any] = Witness(println(_))
}