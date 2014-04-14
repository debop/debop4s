package debop4s.core.concurrent

import java.util
import java.util.concurrent.RejectedExecutionException
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.util.Try

/**
 * AsyncSemaphore
 * Created by debop on 2014. 4. 5.
 */
class AsyncSemaphore protected(initialPermits: Int, maxWaiters: Option[Int]) {

    import AsyncSemaphore._


    def this(initialPermits: Int = 0) = this(initialPermits, None)

    def this(initialPermits: Int, maxWaiters: Int) = this(initialPermits, Some(maxWaiters))

    require(maxWaiters.getOrElse(0) >= 0)

    private[this] val waitq = new util.ArrayDeque[Promise[Permit]]
    private[this] var availablePermits = initialPermits

    private[this] class SemaphorePermit extends Permit {
        override def release(): Unit = {
            val run: Promise[Permit] = AsyncSemaphore.this.synchronized {
                val next = waitq.pollFirst()
                if (next == null) availablePermits += 1
                next
            }

            if (run != null) run.success(new SemaphorePermit)
        }
    }

    def numWaiters: Int = synchronized(waitq.size)

    def numPermitsAvailable: Int = synchronized(availablePermits)

    def aquire(): Future[Permit] = synchronized {
        if (availablePermits > 0) {
            availablePermits -= 1
            Future(new SemaphorePermit)
        } else {
            val promise = Promise[Permit]()
            maxWaiters match {
                case Some(max) if waitq.size >= max =>
                    promise.failure(MaxWaitersExceededException)
                case _ =>
                    promise.complete(Try[Permit] {
                        new SemaphorePermit
                    })
                    waitq.addLast(promise)
            }
            promise.future
        }
    }

    def aquireAndRun[T](func: => Future[T]): Future[T] = {
        aquire() flatMap {
            permit =>
                val f = func
                f onFailure {
                    case e =>
                        permit.release()
                        throw e
                }
                f
        }
    }

    def aquireAndRunSync[T](func: => T): Future[T] = {
        aquire() flatMap { permit =>
            val result = Future { func }
            result onComplete { x => permit.release() }
            result
        }
    }
}

object AsyncSemaphore {
    private val MaxWaitersExceededException =
        new RejectedExecutionException("Max waiters exceeded")
}
