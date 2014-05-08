package debop4s.core.concurrent

import debop4s.core.AbstractCoreTest
import debop4s.core.conversions.time._
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.{Future => JFuture, _}
import org.scalatest.concurrent.Eventually
import org.scalatest.time.{Millis, Seconds, Span}
import scala.concurrent.{Future, Promise}

/**
 * FuturePoolTest
 * @author Sunghyouk Bae
 */
class FuturePoolTest extends AbstractCoreTest with Eventually {

    implicit override val patienceConfig =
        PatienceConfig(timeout = scaled(Span(2, Seconds)), interval = scaled(Span(5, Millis)))

    test("FuturePool should dispatch to another thread") {
        val executor = Executors.newFixedThreadPool(1).asInstanceOf[ThreadPoolExecutor]
        val pool = FuturePool(executor)

        val source = Promise[Int]()
        val result = pool { Asyncs.result(source.future) } // simulate blocking call

        source success 1
        Asyncs.result(result) shouldEqual 1
    }

    test("Executor failing contains failures") {
        val badExecutor = new ScheduledThreadPoolExecutor(1) {
            override def submit(runnable: Runnable): JFuture[_] = {
                throw new RejectedExecutionException()
            }
        }
        val pool = FuturePool(badExecutor)
        val runCount = new AtomicInteger(0)
        val result1 = pool {
            runCount.incrementAndGet()
        }
        runCount.get shouldEqual 0
    }

    test("does not execute interrupted tasks") {
        val executor = Executors.newFixedThreadPool(1).asInstanceOf[ThreadPoolExecutor]
        val pool = FuturePool(executor)

        val runCount = new AtomicInteger(0)

        val source1 = Promise[Int]()
        val source2 = Promise[Int]()

        val result1 = pool { runCount.incrementAndGet(); Asyncs.result(source1.future) }
        val result2 = pool { runCount.incrementAndGet(); Asyncs.result(source2.future) }

        source2 failure new Exception
        source1 success 1

        eventually { executor.getCompletedTaskCount shouldEqual 2 }

        runCount.get shouldEqual 2
        Asyncs.result(result1) shouldEqual 1
        intercept[Exception] { Asyncs.result(result2) }
    }

    test("continue to run a task if it's interrupted while running") {
        val executor = Executors.newFixedThreadPool(1).asInstanceOf[ThreadPoolExecutor]
        val pool = FuturePool(executor)
        val runCount = new AtomicInteger(0)
        val source = Promise[Int]()

        val startedLatch = new CountDownLatch(1)
        val cancelledLatch = new CountDownLatch(1)

        val result: Future[Int] = pool {
            try {
                startedLatch.countDown()
                runCount.incrementAndGet()
                cancelledLatch.await()
                throw new RuntimeException()
            } finally {
                runCount.incrementAndGet()
            }
            runCount.get()
        }

        startedLatch.await(50.milliseconds)
        // result new Exception()
        cancelledLatch.countDown()

        eventually { executor.getCompletedTaskCount shouldEqual 1 }

        runCount.get shouldEqual 2

        intercept[Exception] {
            Asyncs.result(result)
        }
    }

    test("returns exceptions that result from submitting a task to the pool") {
        val executor = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue(1))
        val pool = FuturePool(executor)

        val source = Promise[Int]()
        val blocker1 = pool { Asyncs.result(source.future) } // occupy the thread
        val blocker2 = pool { Asyncs.result(source.future) } // fill the queue

        val rv = pool { "yay!" }

        rv.value.isDefined shouldEqual true
        intercept[RejectedExecutionException] { Asyncs.result(rv) }

        source success 1
    }

    test("interrupt threads when interruptible") {
        val executor = Executors.newFixedThreadPool(1).asInstanceOf[ThreadPoolExecutor]
        val started = Promise[Unit]()
        val interrupted = Promise[Unit]()
        val ipool = FuturePool.interruptible(executor)

        val f = ipool {
            try {
                started success()
                interrupted success()
                new InterruptedException("aaa")
            } catch {
                case exc: InterruptedException => interrupted success()
                case exc: Throwable => interrupted success()
            }
        }
        Asyncs.result(started.future)
        // intercept[RuntimeException] { Asyncs.result(f) }
        Asyncs.result(f)
        Asyncs.result(interrupted.future) shouldEqual()
    }

    test("not interrupt threads when not interruptible") {
        val executor = Executors.newFixedThreadPool(1).asInstanceOf[ThreadPoolExecutor]
        val a = Promise[Unit]()
        val b = Promise[Unit]()
        val nipool = FuturePool(executor)

        val f = nipool {
            a success()
            Asyncs.result(b.future)
            throw new RuntimeException("foo")
        }

        Asyncs.result(a.future)
        b success()
        intercept[RuntimeException] { Asyncs.result(f) }
    }
}
