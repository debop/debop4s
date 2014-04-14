package debop4s.core.concurrent

import debop4s.core.AbstractCoreTest
import debop4s.core.parallels.Asyncs
import java.util.concurrent.{RejectedExecutionException, ConcurrentLinkedQueue}
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Promise, Future}
import scala.util.Try


/**
 * AsyncSemaphoreTest
 * @author Sunghyouk Bae
 */
class AsyncSemaphoreTest extends AbstractCoreTest {

    class AsyncSemaphoreHelper(val sem: AsyncSemaphore,
                               var count: Int,
                               val permits: ConcurrentLinkedQueue[Permit]) {
        def copy(sem: AsyncSemaphore = this.sem,
                 count: Int = this.count,
                 permits: ConcurrentLinkedQueue[Permit] = this.permits) =
            new AsyncSemaphoreHelper(sem, count, permits)
    }

    var sem: AsyncSemaphore = _
    var helper: AsyncSemaphoreHelper = _

    before {
        sem = new AsyncSemaphore(2)
        helper = new AsyncSemaphoreHelper(sem, 0, new ConcurrentLinkedQueue[Permit])
    }

    @inline
    private def acquire(s: AsyncSemaphoreHelper): Future[Permit] = {
        val fPermit = s.sem.acquire()
        fPermit onSuccess { case permit =>
            s.count += 1
            s.permits add permit
        }
        fPermit onFailure {
            case e: Exception => println(s"aquire error. $e")
        }
        fPermit
    }

    test("should execute immediately while permits are available") {
        assert(helper.sem.numPermitsAvailable === 2)
        acquire(helper)
        assert(helper.sem.numPermitsAvailable === 1)
        // assert(helper.count === 1)

        acquire(helper)
        assert(helper.sem.numPermitsAvailable === 0)
        // assert(helper.count === 2)

        acquire(helper)
        assert(helper.sem.numPermitsAvailable === 0)
        assert(helper.count === 2)
    }

    test("should execute deferred computations when permits are released") {
        acquire(helper)
        acquire(helper)
        acquire(helper)
        acquire(helper)

        Thread.sleep(1)

        assert(helper.count === 2)
        assert(helper.sem.numPermitsAvailable === 0)

        helper.permits.poll().release()
        assert(helper.sem.numPermitsAvailable === 0)
        Thread.sleep(1)
        assert(helper.count === 3)

        helper.permits.poll().release()
        assert(helper.sem.numPermitsAvailable === 0)
        Thread.sleep(1)
        assert(helper.count === 4)

        helper.permits.poll().release()
        assert(helper.sem.numPermitsAvailable === 1)
        Thread.sleep(1)
        assert(helper.count === 4)
    }

    test("should bound the number of waiters") {
        val helper2 = helper.copy(sem = new AsyncSemaphore(2, 3))

        // The first two acquires obtain a permit.
        acquire(helper2)
        acquire(helper2)

        Thread.sleep(1)
        assert(helper2.count === 2)

        // The next three acquires wait.
        acquire(helper2)
        acquire(helper2)
        acquire(helper2)

        assert(helper2.sem.numWaiters === 3)
        assert(helper2.count === 2)

        // The next acquire should be rejected
        val permit = acquire(helper2)
        Thread.sleep(1)
        assert(helper2.sem.numWaiters === 3)
        intercept[RejectedExecutionException] {
            Asyncs.result(permit)
        }

        assert(helper2.permits.size() === 2)

        // Waiting tasks should still execute once permits are available.
        helper2.permits.poll().release()
        Thread.sleep(1)
        helper2.permits.poll().release()
        Thread.sleep(1)
        helper2.permits.poll().release()
        Thread.sleep(1)
        assert(helper2.count === 5)
    }

    test("should satisfy futures with exceptions if they are unterrupted") {
        val p1 = acquire(helper)
        val p2 = acquire(helper)
        val p3 = acquire(helper)

        Asyncs.result(p2).release()
        Asyncs.result(p1).release()
    }

    test("should execute queued up async functions as permits become available") {
        var counter = 0
        val queue = new mutable.Queue[Promise[Unit]]()
        val func = new (() => Future[Unit]) {
            def apply(): Future[Unit] = {
                counter = counter + 1
                val promise = Promise[Unit]()
                queue.enqueue(promise)
                promise.future
            }
        }

        assert(helper.sem.numPermitsAvailable === 2)

        helper.sem.acquireAndRun(func())
        Thread.sleep(1)
        assert(counter == 1)
        assert(helper.sem.numPermitsAvailable == 1)

        helper.sem.acquireAndRun(func())
        Thread.sleep(1)
        assert(counter == 2)
        assert(helper.sem.numPermitsAvailable == 0)

        helper.sem.acquireAndRun(func())
        Thread.sleep(1)
        assert(counter == 2)
        assert(helper.sem.numPermitsAvailable == 0)

        queue.dequeue() success Unit
        Thread.sleep(1)
        assert(counter == 3)
        assert(helper.sem.numPermitsAvailable == 0)

        queue.dequeue() success Unit
        Thread.sleep(1)
        assert(helper.sem.numPermitsAvailable == 1)

        queue.dequeue() failure new RuntimeException("test")
        Thread.sleep(1)
        assert(helper.sem.numPermitsAvailable == 2)
    }

    test("should release permit even if queued up function throws an exception") {
        val badFunc = new (() => Future[Unit]) {
            def apply(): Future[Unit] = throw new RuntimeException("bad func calling")
        }
        helper.sem.acquireAndRun(badFunc())
        Thread.sleep(1)
        assert(helper.sem.numPermitsAvailable == 2)
    }


    test("should execute queued up sync functions as permits become available") {
        var counter = 0
        val queue = new mutable.Queue[Promise[Unit]]()
        val funcFuture = new (() => Future[Unit]) {
            def apply(): Future[Unit] = {
                counter = counter + 1
                val promise = Promise[Unit]()
                queue.enqueue(promise)
                promise.future
            }
        }
        val func = () => {
            counter = counter + 1
            counter
        }

        assert(helper.sem.numPermitsAvailable == 2)

        helper.sem.acquireAndRun(funcFuture())
        Thread.sleep(1)
        assert(counter == 1)
        assert(helper.sem.numPermitsAvailable == 1)

        helper.sem.acquireAndRun(funcFuture())
        Thread.sleep(1)
        assert(counter == 2)
        assert(helper.sem.numPermitsAvailable == 0)

        val future = helper.sem.acquireAndRunSync(func())
        assert(counter == 2)
        assert(helper.sem.numPermitsAvailable == 0)
        // sync func is blocked at this point.
        // But it should be executed as soon as one of the queued up future functions finish

        queue.dequeue() success Unit
        Thread.sleep(10)
        assert(counter == 3)
        val result = Asyncs.result(future)
        assert(result == 3)
        assert(helper.sem.numPermitsAvailable == 1)
    }

    test("should handle queued up sync functions which throw exception") {
        var counter = 0
        val queue = new mutable.Queue[Promise[Unit]]()
        val funcFuture = new (() => Future[Unit]) {
            def apply(): Future[Unit] = {
                counter = counter + 1
                val promise = Promise[Unit]()
                queue.enqueue(promise)
                promise.future
            }
        }
        val badFunc = new (() => Int) {
            def apply(): Int = {
                throw new Exception("error!")
            }
        }
        assert(helper.sem.numPermitsAvailable == 2)

        helper.sem.acquireAndRun(funcFuture())
        Thread.sleep(1)
        assert(counter == 1)
        assert(helper.sem.numPermitsAvailable == 1)

        helper.sem.acquireAndRun(funcFuture())
        Thread.sleep(1)
        assert(counter == 2)
        assert(helper.sem.numPermitsAvailable == 0)

        val future = helper.sem.acquireAndRunSync(badFunc())
        assert(counter == 2)
        assert(helper.sem.numPermitsAvailable == 0)
        // sync func is blocked at this point.
        // But it should be executed as soon as one of the queued up future functions finish

        queue.dequeue() success Unit
        Thread.sleep(10)
        assert(counter == 2)
        assert(Try(Asyncs.result(future)).isFailure)
        assert(helper.sem.numPermitsAvailable == 1)
    }

}
