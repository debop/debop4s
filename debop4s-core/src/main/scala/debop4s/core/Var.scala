//package debop4s.core
//
//import debop4s.core.concurrent.Asyncs
//import java.util.concurrent.atomic.{AtomicLong, AtomicReferenceArray, AtomicReference}
//import scala.annotation.tailrec
//import scala.collection.generic.CanBuildFrom
//import scala.collection.immutable
//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.concurrent.{Promise, Future}
//import scala.reflect.ClassTag
//
//// TODO: Var 와 Event 에 대한 예제를 만들어 보자.
//// NOTE: twitter/util 에 있는 Var, Event 를 Porting 했습니다.
//
///**
// * Trait Var represents a variable. It is a reference cell which is
// * composable: dependent Vars (derived through flatMap) are
// * recomputed automatically when independent variables change -- they
// * implement a form of self-adjusting computation.
// *
// * Vars are observed, notifying users whenever the variable changes.
// *
// * @note Vars do not always perform the minimum amount of re-computation.
// *
// * @note There are no well-defined error semantics for Var. Vars are
// * computed lazily, and the updating thread will receive any
// * exceptions thrown while computing derived Vars.
// */
//trait Var[+T] {self =>
//
//    import Var.Observer
//
//    /**
//     * Observe this Var. `f` is invoked each time the variable changes,
//     * and synchronously with the first call to this method.
//     */
//    final def observe(f: T => Unit): Closable = observe(0, Observer(f))
//
//    /**
//     * Concrete implementations of Var implement observe. This is
//     * called for each toplevel observe. Depths indicate the relative
//     * structural depth of the observation, from the frame of reference
//     * of the root call to observe. (Each Var derived via flatMap
//     * increases the depth.) Depths are used to order the invocation of
//     * update callbacks. This is used to ensure that updates proceed in
//     * topological order so that every input variable is fully resolved
//     * before recomputing a derived variable.
//     */
//    protected def observe(depth: Int, obs: Observer[T]): Closable
//
//    /** Synonymous with observe */
//    def foreach(f: T => Unit) = observe(f)
//
//    /** Create a derived variable by applying `f` to the contained value. */
//    def map[U](f: T => U): Var[U] = flatMap(t => Var.value(f(t)))
//
//    /**
//     * Create a dependent Var which behaves as `f` applied to the
//     * current value of this Var. FlatMap manages a dynamic dependency
//     * graph: the dependent Var is detached and recomputed  whenever
//     * the outer Var changes, but only if there are any observers.  An
//     * unobserved Var returned by flatMap will not invoke `f`
//     */
//    def flatMap[U](f: T => Var[U]): Var[U] = new Var[U] {
//        def observe(depth: Int, obs: Observer[U]) = {
//            val inner = new AtomicReference(Closable.nop)
//            val outer = self.observe(depth, Observer { t =>
//            // TODO: Right now we rely on synchronous propagation; and
//            // thus also synchronous closes. We should instead perform
//            // asynchronous propagation so that it is is safe &
//            // predicatable to have asynchronously closing Vars, for
//            // example. Currently the only source of potentially
//            // asynchronous closing is Var.async; here we have modified
//            // the external process to close asynchronously with the Var
//            // itself so that it is safe to Await here.
//                Asyncs.ready(inner.getAndSet(f(t).observe(depth + 1, obs)).close())
//            })
//
//            Closable.sequence(outer, Closable.ref(inner))
//        }
//    }
//
//    def join[U](other: Var[U]): Var[(T, U)] =
//        for {t <- self; u <- other} yield (t, u)
//
//    /**
//     * Observe this Var into the given AtomicReference.
//     * Observation stops when the returned closable is closed.
//     */
//    @deprecated("Use changes (Event)", "6.8.2")
//    def observeTo[U >: T](ref: AtomicReference[U]): Closable =
//        this observe { newv => ref.set(newv) }
//
//    lazy val changes: Event[T] = new Event[T] {
//        def register(s: Witness[T]) = observe { newv => s.notify(newv) }
//    }
//
//    /**
//     * A one-shot predicate observation. The returned future
//     * is satisfied with the first observed value of Var that obtains
//     * the predicate `pred`. Observation stops when the future is
//     * satisfied.
//     *
//     * Interrupting the future will also satisfy the future (with the
//     * interrupt exception) and close the observation.
//     */
//    @deprecated("Use changes (Event)", "6.8.2")
//    def observeUntil(pred: T => Boolean): Future[T] = {
//        val p = Promise[T]()
//
//        val o = observe {
//            case el if pred(el) => p success el
//            case _ =>
//        }
//
//        p.future onComplete { x => o.close() }
//        p.future
//    }
//}
//
//object Var {
//
//    /**
//     * A Var observer. Observers are owned by exactly one producer, enforced by a leasing mechanism.
//     */
//    private[core] class Observer[-T](observe: T => Unit) {
//        private[this] var thisOwner: AnyRef = null
//        private[this] var thisVersion = Long.MinValue
//
//        /**
//         * Claim this observer with owner `newOwner`.
//         * Claiming  an observer gives the owner exclusive rights to publish
//         * to it while it has not been claimed by another owner.
//         */
//        def claim(newOwner: AnyRef): Unit = synchronized {
//            if (thisOwner ne newOwner) {
//                thisOwner = newOwner
//                thisVersion = Long.MinValue
//            }
//        }
//
//        /**
//         * Publish the given versioned value with the given owner.
//         * If the owner is not current (because another has claimed
//         * the observer), or if the version has already published (by
//         * assumption of a monotonically increasing version number)
//         * the publish operation is a no-op.
//         */
//        def publish(owner: AnyRef, value: T, version: Long): Unit = synchronized {
//            if ((owner eq thisOwner) && thisVersion < version) {
//                thisVersion = version
//                observe(value)
//            }
//        }
//    }
//
//    private[core] object Observer {
//        def apply[T](k: T => Unit) = new Observer(k)
//    }
//
//    /**
//     * Sample the current value of this Var. Note that this may lead to
//     * surprising results for lazily defined Vars: the act of observing
//     * a Var may be kick off a process to populate it; the value
//     * returned from sample may then reflect an intermediate value.
//     */
//    def sample[T](v: Var[T]): T = {
//        var opt: Option[T] = None
//        v.observe(v => opt = Some(v)).close()
//        opt.get
//    }
//
//    object Sampled {
//        def apply[T](v: T): Var[T] = value(v)
//        def unapply[T](v: Var[T]): Option[T] = Some(sample(v))
//    }
//
//    /**
//   * Create a new, updatable Var with an initial value. We call
//   * such Vars independent -- derived Vars being dependent
//   * on these.
//   */
//    def apply[T](init: T): Var[T] with Updatable[T] with Extractable[T] =
//        new UpdatableVar(init)
//
//    /**
//     * Constructs a Var from an initial value plus an event stream of
//     * changes. Note that this eagerly subscribes to the event stream;
//     * it is unsubscribed whenever the returned Var is collected.
//     */
//    def apply[T](init: T, e: Event[T]): Var[T] = {
//        val v = Var(init)
//        Closable.closeOnCollect(e.register(Witness(v)), v)
//        v
//    }
//
//    /**
//     * Create a new, constant, v-valued Var.
//     */
//    def value[T](v: T): Var[T] = new Var[T] {
//        protected def observe(depth: Int, obs: Observer[T]): Closable = {
//            obs.claim(this)
//            obs.publish(this, v, 0)
//            Closable.nop
//        }
//    }
//
//    def collect[T, CC[X] <: Traversable[X]](vars: CC[Var[T]])
//                                           (implicit newBuilder: CanBuildFrom[CC[T], T, CC[T]], cm: ClassTag[T])
//    : Var[CC[T]] = async(newBuilder().result()) { v =>
//        val N = vars.size
//        val cur = new AtomicReferenceArray[T](N)
//        @volatile var filling = true
//
//        def build() = {
//            val b = newBuilder()
//            (0 until N) foreach { i => b += cur.get(i) }
//            b.result()
//        }
//
//        def publish(i: Int, newi: T) = {
//            cur.set(i, newi)
//            if (!filling) v() = build()
//        }
//
//        val closes = new Array[Closable](N)
//        var i = 0
//        for (u <- vars) {
//            closes(i) = u observe { newj => publish(i, newj) }
//            i += 1
//        }
//
//        filling = false
//        v() = build()
//
//        Closable.all(closes: _*)
//    }
//
//    private object create {
//        sealed trait State[+T]
//        object Idle extends State[Nothing]
//        case class Observing[T](n: Int, v: Var[T], c: Closable) extends State[T]
//    }
//
//    /**
//    * Create a new Var whose values are provided asynchronously by
//    * `update`. The returned Var is dormant until it is observed:
//    * `update` is called by-need. Such observations are also reference
//    * counted so that simultaneous observervations do not result in
//    * multiple invocations of `update`. When the last observer stops
//    * observing, the [[com.twitter.util.Closable]] returned
//    * from `update` is closed. Subsequent observations result in a new
//    * call to `update`.
//    *
//    * `empty` is used to fill the returned Var until `update` has
//    * provided a value. The first observation of the returned Var is
//    * synchronous with the call to `update`--it is guaranteed the the
//    * opportunity to fill the Var before the observer sees any value
//    * at all.
//    *
//    * Updates from `update` are ignored after the returned
//    * [[com.twitter.util.Closable]] is closed.
//    */
//    def async[T](empty: T)(update: Updatable[T] => Closable): Var[T] = new Var[T] {
//        import create._
//        private var state: State[T] = Idle
//
//        private val closable = Closable.make { deadline =>
//            synchronized {
//                state match {
//                    case Idle => Future.successful(Unit)
//                    case Observing(1, _, c) =>
//                        state = Idle
//                        // We close the external process asynchronously from the
//                        // async Var so that it is safe to Await Var.close() in
//                        // flatMap. (See the TODO there.)
//                        c.close(deadline)
//                        Future.successful(Unit)
//                    case Observing(n, v, c) =>
//                        state = Observing(n - 1, v, c)
//                        Future.successful(Unit)
//                }
//            }
//        }
//
//        protected def observe(depth: Int, obs: Observer[T]): Closable = {
//            val v = synchronized {
//                state match {
//                    case Idle =>
//                        val v = Var(empty)
//                        val c = update(v)
//                        state = Observing(1, v, c)
//                        v
//                    case Observing(n, v, c) =>
//                        state = Observing(n + 1, v, c)
//                        v
//                }
//            }
//
//            val c = v.observe(depth, obs)
//            Closable.sequence(c, closable)
//        }
//    }
//}
//
///**
//* Updatable container
//*/
//trait Updatable[T] {
//    /** Update the container with value `t` */
//    def update(t: T)
//}
//
//trait Extractable[T] {
//    def apply(): T
//}
//
//private object UpdatableVar {
//
//    import Var.Observer
//
//    case class Party[T](obs: Observer[T], depth: Int, n: Long) {
//        @volatile var active = true
//    }
//
//    case class State[T](value: T, version: Long, parties: immutable.SortedSet[Party[T]]) {
//        def -(p: Party[T]) = copy(parties = parties - p)
//        def +(p: Party[T]) = copy(parties = parties + p)
//        def :=(newv: T) = copy(value = newv, version = version + 1)
//    }
//
//    implicit def order[T] = new Ordering[Party[T]] {
//        def compare(a: Party[T], b: Party[T]): Int = {
//            val c1 = a.depth compare b.depth
//
//            if (c1 != 0) c1
//            else a.n compare b.n
//        }
//    }
//}
//
//private class UpdatableVar[T](init: T) extends Var[T] with Updatable[T] with Extractable[T] {
//
//    import UpdatableVar._
//    import Var.Observer
//
//    private[this] val n = new AtomicLong(0)
//    private[this] val state = new AtomicReference(State[T](init, 0, immutable.SortedSet.empty))
//
//    @tailrec
//    private[this] def cas(next: State[T] => State[T]): State[T] = {
//        val from = state.get()
//        val to = next(from)
//        if (state.compareAndSet(from, to)) to else cas(next)
//    }
//
//    def apply(): T = state.get().value
//
//    def update(newv: T): Unit = synchronized {
//        val State(value, version, parties) = cas(_ := newv)
//        for (p@Party(obs, _, _) <- parties) {
//            // An antecedent update may have closed the current
//            // party (e.g. flatMap does this); we need to check that
//            // the party is active here in order to prevent stale updates.
//            if (p.active)
//                obs.publish(this, value, version)
//        }
//    }
//
//    protected def observe(depth: Int, obs: Observer[T]): Closable = {
//        obs.claim(this)
//        val party = Party(obs, depth, n.getAndIncrement)
//        val State(value, version, _) = cas(_ + party)
//        obs.publish(this, value, version)
//
//        new Closable {
//            def close(deadline: Time) = {
//                party.active = false
//                cas(_ - party)
//                Future.successful(Unit)
//            }
//        }
//    }
//
//    override def toString = s"Var(${ state.get.value })@$hashCode"
//}