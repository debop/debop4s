package debop4s.core

import java.util.concurrent.atomic.AtomicBoolean
import scala.concurrent.duration.Duration
import scala.concurrent.{CanAwait, Future, Promise, Awaitable}

/**
 * A mixin to make an [[Awaitable]] out of a [[Closable]].
 *
 * Use `closeAwaitably` in the definition of `close`:
 *
 * {{{
 * class MyClosable extends Closable with CloseAwaitably {
 *   def close(deadline: Time) = closeAwaitably {
 *     // close the resource
 *   }
 * }
 * }}}
 */
trait CloseAwaitably extends Awaitable[Unit] {

  private[this] val onClose = Promise[Unit]()
  private[this] val closed = new AtomicBoolean(false)

  protected def closeAwaitably(f: => Future[Unit]): Future[Unit] = {
    if (closed.compareAndSet(false, true)) {
      onClose.completeWith(f)
    }
    onClose.future
  }

  def ready(timeout: Duration)(implicit permit: CanAwait): this.type = {
    onClose.future.ready(timeout)
    this
  }

  def result(timeout: Duration)(implicit permit: CanAwait): Unit = {
    onClose.future.result(timeout)
  }

  def isReady(implicit permit: CanAwait): Boolean = {
    onClose.future.isCompleted
  }
}
