package debop4s.core.concurrent

/**
 * AsyncMutex
 * @author Sunghyouk Bae
 */
class AsyncMutex private(maxWaiters: Option[Int]) extends AsyncSemaphore(1, maxWaiters) {

    def this() = this(None)
    def this(maxWaiters: Int) = this(Some(maxWaiters))

}
