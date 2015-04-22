package debop4s.core.concurrent

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.{CancellationException, ExecutorService, Executors, Future => JFuture, RejectedExecutionException}

import debop4s.core.utils.Local

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.Try

/**
 * A FuturePool executes tasks asynchronously, typically using a pool of worker threads.
 */
trait FuturePool {

  def apply[T](f: => T): Future[T]

}

object FuturePool {

  /**
   * Creates a FuturePool backed by an ExecutorService.
   *
   * Note: for consumers from Java, there is not a java friendly api
   * for using FuturePool.apply.  However, you can directly construct
   * an ExecutorServiceFuturePool without problems.
   */
  def apply(executor: ExecutorService): ExecutorServiceFuturePool =
    new ExecutorServiceFuturePool(executor)

  /**
   * Creates a FuturePool backed by an ExecutorService which propagates cancellation.
   */
  def interruptible(executor: ExecutorService) =
    new InterruptibleExecutorServiceFuturePool(executor)

  val immediatePool = new FuturePool {
    def apply[T](f: => T): Future[T] = {
      val p = Promise[T]()
      p complete Try { f }
      p.future
    }
  }

  private lazy val defaultExecutor: ExecutorService =
    Executors.newCachedThreadPool(NamedPoolThreadFactory("UnboundedFuturePool", makeDaemons = true))

  /**
   * The default future pool, using a cached threadpool, provided by
   * [[java.util.concurrent.Executors.newCachedThreadPool]]. Note
   * that this is intended for IO concurrency; computational
   * parallelism typically requires special treatment. If an interrupt
   * is raised on a returned Future and the work has started, the worker
   * thread will not be interrupted.
   */
  lazy val unboundedPool: ExecutorServiceFuturePool =
    new ExecutorServiceFuturePool(defaultExecutor)

  /**
   * The default future pool, using a cached threadpool, provided by
   * [[java.util.concurrent.Executors.newCachedThreadPool]]. Note
   * that this is intended for IO concurrency; computational
   * parallelism typically requires special treatment.  If an interrupt
   * is raised on a returned Future and the work has started, an attempt
   * will will be made to interrupt the worker thread.
   */
  lazy val interruptibleUnboundedPool: InterruptibleExecutorServiceFuturePool =
    new InterruptibleExecutorServiceFuturePool(defaultExecutor)
}


class InterruptibleExecutorServiceFuturePool(executor: ExecutorService)
  extends ExecutorServiceFuturePool(executor, true)

/**
 * A FuturePool implementation backed by an ExecutorService.
 *
 * If a piece of work has started, it cannot be cancelled and will not propagate
 * cancellation unless interruptible is true.
 *
 * If you want to propagate cancellation, use
 */
class ExecutorServiceFuturePool protected[this](val executor: ExecutorService,
                                                val interruptible: Boolean) extends FuturePool {

  def this(executor: ExecutorService) = this(executor, false)

  override def apply[T](f: => T): Future[T] = {
    val runOk = new AtomicBoolean(true)
    val p = Promise[T]()

    val task = new Runnable {
      val saved = Local.save()
      def run() {
        // Make an effort to skip work in the case the promise
        // has been cancelled or already defined.
        if (!runOk.compareAndSet(true, false))
          return

        val current = Local.save()
        Local.restore(saved)

        try {
          val result = f
          p success result
        } catch {
          case t: Throwable => p failure t
        } finally {
          Local.restore(current)
        }
      }
    }

    // This is safe: the only thing that can call task.run() is executor,
    // the only thing that can raise an interrupt is the receiver of this value,
    // which will then be fully initialized.
    val javaFuture = try executor.submit(task) catch {
      case e: RejectedExecutionException =>
        runOk.set(false)
        p failure e
        null
    }

    p.future onFailure {
      case cause: InterruptedException =>
        if (interruptible || runOk.compareAndSet(true, false)) {
          val exc = new CancellationException()
          exc.initCause(cause)
          javaFuture.cancel(true)
        }
    }

    p.future
  }
}

