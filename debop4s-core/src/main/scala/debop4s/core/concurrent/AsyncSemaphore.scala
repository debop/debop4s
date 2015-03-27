package debop4s.core.concurrent

import java.util
import java.util.concurrent.RejectedExecutionException

import debop4s.core.concurrent.AsyncSemaphore._
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.util.control.NonFatal

/**
 * AsyncSemaphore
 * Created by debop on 2014. 4. 5.
 */
class AsyncSemaphore protected(initialPermits: Int, maxWaiters: Option[Int]) {

  private lazy val log = LoggerFactory.getLogger(getClass)


  def this(initialPermits: Int = 0) = this(initialPermits, None)
  def this(initialPermits: Int, maxWaiters: Int) = this(initialPermits, Some(maxWaiters))

  require(maxWaiters.getOrElse(0) >= 0)

  private[this] val waitq = new util.ArrayDeque[Promise[Permit]]
  private[this] var availablePermits = initialPermits

  private[this] class SemaphorePermit extends Permit {
    override def release() {
      val run: Promise[Permit] = AsyncSemaphore.this.synchronized {
        val next = waitq.pollFirst()
        if (next == null) availablePermits += 1
        next
      }

      if (run != null) run success new SemaphorePermit
    }
  }

  def numWaiters: Int = synchronized(waitq.size)
  def numPermitsAvailable: Int = synchronized(availablePermits)

  /**
   * Acquire a Permit, asynchronously. Be sure to permit.release() in a 'finally'
   * block of your onSuccess() callback.
   *
   * Interrupting this future is only advisory, and will not release the permit
   * if the future has already been satisfied.
   *
   * @return a Future[Permit] when the Future is satisfied, computation can proceed,
   *         or a Future.Exception[RejectedExecutionException] if the configured maximum number of waitq
   *         would be exceeded.
   */
  def acquire(): Future[Permit] = synchronized {
    // LOG.debug(s"acquire... availablePermits=$availablePermits")
    if (availablePermits > 0) {
      availablePermits -= 1
      Future.successful(new SemaphorePermit)
    } else {
      val promise = Promise[Permit]()
      maxWaiters match {
        case Some(max) if waitq.size >= max =>
          promise.failure(MaxWaitersExceededException)
        case _ =>
          promise.future onFailure {
            case t: Throwable =>
              AsyncSemaphore.this.synchronized {
                waitq.remove(promise)
              }
          }
          waitq.addLast(promise)
      }
      promise.future
    }
  }

  /**
   * Execute the function asynchronously when a permit becomes available.
   *
   * If the function throws a non-fatal exception, the exception is returned as part of the Future.
   * For all exceptions, the permit would be released before returning.
   *
   * @return a Future[T] equivalent to the return value of the input function. If the configured
   *         maximum value of waitq is reached, Future.Exception[RejectedExecutionException] is
   *         returned.
   */
  def acquireAndRun[T](func: => Future[T]): Future[T] = {
    acquire() flatMap { permit =>
      val f =
        try {
          func
        } catch {
          case NonFatal(e) =>
            Future.failed(e)
          case e: Throwable =>
            permit.release()
            throw e
        }
      f onComplete { case _ => permit.release()}

      f
    }
  }

  /**
   * Execute the function when a permit becomes available.
   *
   * If the function throws an exception, the exception is returned as part of the Future.
   * For all exceptions, the permit would be released before returning.
   *
   * @return a Future[T] equivalent to the return value of the input function. If the configured
   *         maximum value of waitq is reached, Future.Exception[RejectedExecutionException] is
   *         returned.
   */
  def acquireAndRunSync[T](func: => T): Future[T] = {
    acquire() flatMap { permit =>
      val result = Future {func}
      result onComplete { _ => permit.release()}
      result
    }
  }
}

object AsyncSemaphore {
  private val MaxWaitersExceededException =
    new RejectedExecutionException("Max waiters exceeded")
}
