package debop4s.core

import java.lang.ref.{PhantomReference, Reference, ReferenceQueue}
import java.util
import java.util.concurrent.atomic.AtomicReference

import debop4s.core.concurrent.Asyncs
import debop4s.core.utils.Time
import org.slf4j.LoggerFactory

import scala.annotation.varargs
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
import scala.util.control.NonFatal

/**
 * Closable is a mixin trait to describe a closable `resource`.
 * Created by debop on 2014. 4. 6.
 */
trait Closable {
  //self =>

  final def close(): Future[Unit] = close(Time.MinusInf)

  def close(deadline: Time): Future[Unit]

  def close(after: Duration): Future[Unit] = close(new Time(after.toNanos + Time.now.inNanoseconds))

}

object Closable {

  private[this] lazy val log = LoggerFactory.getLogger(getClass)

  /**
   * Concurrent composition: creates a new closable which, when
   * closed, closes all of the underlying resources simultaneously.
   */
  @varargs
  def all(closables: Closable*) = new Closable {
    def close(deadline: Time): Future[Unit] = {
      Asyncs.readyAll(closables.map(_.close(deadline)))
      Future.successful(Unit)
    }
  }

  /**
   * Sequential composition: create a new Closable which, when
   * closed, closes all of the underlying ones in sequence: that is,
   * resource ''n+1'' is not closed until resource ''n'' is.
   */
  @varargs
  def sequence(closables: Closable*) = new Closable {
    private final def closeSeq(deadline: Time, closable: Seq[Closable]): Future[Unit] = {
      closables match {
        case Seq() => Future.successful(Unit)
        case Seq(head, tail @ _*) => head.close(deadline) flatMap {
          _ => closeSeq(deadline, tail)
        }
      }
    }

    def close(deadline: Time): Future[Unit] = closeSeq(deadline, closables)
  }

  /** A Closable that does nothing immediately. */
  val nop: Closable = new Closable {
    def close(deadline: Time) = Future.successful(Unit)
  }

  /** Make a new Closable whose close method invokes f. */
  def make(f: Time => Future[Unit]): Closable = new Closable {
    def close(deadline: Time) = f(deadline)
  }

  def ref(r: AtomicReference[Closable]): Closable = new Closable {
    def close(deadline: Time) = r.getAndSet(nop).close(deadline)
  }

  private[this] val refs = new util.HashMap[Reference[Object], Closable]
  private[this] val refq = new ReferenceQueue[Object]

  private val collectorThread = new Thread("CollectClosables") {
    override def run(): Unit = {
      while (true) {
        try {
          val ref = refq.remove()
          val closable = refs.synchronized(refs.remove(ref))
          if (closable != null) {
            log.debug(s"close $closable")
            closable.close()
          }
          ref.clear()
        } catch {
          case NonFatal(e) =>
            log.error("Closable collector threw exception", e)
          case fatal: Throwable =>
            log.error("Closable collector threw exception", fatal)
            throw fatal
        }
      }
    }

    setDaemon(true)
    start()
  }

  /**
   * Close the given closable when `obj` is collected.
   */
  def closeOnCollect(closable: Closable, obj: Object): Closable = refs.synchronized {
    refs.put(new PhantomReference(obj, refq), closable)
  }
}
