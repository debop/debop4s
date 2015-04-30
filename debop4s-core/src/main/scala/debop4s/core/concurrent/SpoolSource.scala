package debop4s.core.concurrent

import java.util.concurrent.atomic.AtomicReference

import scala.annotation.tailrec
import scala.concurrent.{Future, Promise}


/**
 * A SpoolSource is a simple object for creating and populating a Spool-chain.  apply()
 * returns a Future[Spool] that is populated by calls to offer().  This class is thread-safe.
 */
class SpoolSource[@miniboxed A] {

  // a reference to the current outstanding promise for the next Future[Spool[A]] result
  private val promiseRef = new AtomicReference[Promise[Spool[A]]]

  // when the SpoolSource is closed, promiseRef will be permanently set to emptyPromise,
  // which always returns an empty spool.
  private val emptyPromise: Promise[Spool[A]] = {
    val empty = Promise[Spool[A]]()
    empty success Spool.empty[A]
    empty
  }

  // set the first promise to be fulfilled by the first call to offer()
  promiseRef.set(Promise[Spool[A]]())

  /**
   * Gets the current outstanding Future for the next Spool value.  The returned Spool
   * will see all future values passed to offer(), up until close() is called.
   * Previous values passed to offer() will not be seen in the Spool.
   */
  def apply(): Future[Spool[A]] = promiseRef.get.future

  /**
   * Puts a value into the spool.  Unless this SpoolSource has been closed, the current
   * Future[Spool[A]] value will be fulfilled with a Spool that contains the
   * provided value.  If the SpoolSource has been closed, then this value is ignored.
   * If multiple threads call offer simultaneously, the operation is thread-safe but
   * the resulting order of values in the spool is non-deterministic.
   */
  final def offer(value: A): Unit = {
    val nextPromise = Promise[Spool[A]]()
    updatingTailCall(nextPromise) { currentPromise =>
      currentPromise success Spool.cons(value, nextPromise.future)
    }
  }

  /**
   * Closes this SpoolSource, which also terminates the generated Spool.
   * This method is idempotent.
   */
  final def close(): Unit = {
    updatingTailCall(emptyPromise) { currentPromise =>
      currentPromise success Spool.empty[A]
    }
  }

  /**
   * Raises exception on this SpoolSource, which also terminates the generated Spool.
   * This method is idempotent.
   */
  final def raise(e: Throwable): Unit = {
    updatingTailCall(emptyPromise) { currentPromise =>
      currentPromise failure e
    }
  }

  @tailrec
  private[this] def updatingTailCall(newPromise: Promise[Spool[A]])(f: Promise[Spool[A]] => Unit): Unit = {
    val currentPromise = promiseRef.get()

    if (currentPromise ne emptyPromise) {
      if (promiseRef.compareAndSet(currentPromise, newPromise)) {
        f(currentPromise)
      } else {
        // try again
        updatingTailCall(newPromise)(f)
      }
    }
  }
}
