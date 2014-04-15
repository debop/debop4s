package debop4s.core.utils

import debop4s.core.concurrent.{Asyncs, CountDownLatch}
import debop4s.core.conversions.time._
import debop4s.core.{Time, AbstractCoreTest}
import java.util.concurrent.ExecutorService
import org.mockito.ArgumentCaptor
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.concurrent.Eventually._
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.mock.MockitoSugar
import org.scalatest.time.{Seconds, Span}
import scala.actors.threadpool.AtomicInteger
import scala.concurrent.{Await, Future}

/**
 * TimerTest
 * Created by debop on 2014. 4. 12.
 */
class TimerTest extends AbstractCoreTest with MockitoSugar {

    test("ThreadStoppingTimer 는 다른 스레드에 있는 timer를 중지시켜야 합니다.") {

        val executor = mock[ExecutorService]
        val underlying = mock[Timer]
        val timer = ThreadStoppingTimer(underlying)(executor)

        verify(executor, never()).submit(any[Runnable])
        timer.stop()
        verify(underlying, never()).stop()
        val runnableCaptor = ArgumentCaptor.forClass(classOf[Runnable])
        verify(executor).submit(runnableCaptor.capture())
        runnableCaptor.getValue.run()
        verify(underlying).stop()
    }

    test("ReferenceCoutingTimer의 factory는 처음 acquire 가 호출될 때 때 생성됩니다.") {
        val underlying = mock[Timer]
        val factory = mock[() => Timer]
        when(factory()).thenReturn(underlying)

        val refcounted = ReferenceCountingTimer(factory)

        verify(factory, never()).apply()
        refcounted.acquire()
        verify(factory).apply()
    }

    test("ReferenceCoutingTimer는 acquire count 가 0이 되면 underlying timer를 중지시킵니다.") {
        val underlying = mock[Timer]
        val factory = mock[() => Timer]
        when(factory()).thenReturn(underlying)

        val refcounted = ReferenceCountingTimer(factory)

        refcounted.acquire()
        refcounted.acquire()
        refcounted.acquire()
        verify(factory).apply()

        refcounted.stop()
        verify(underlying, never()).stop()
        refcounted.stop()
        verify(underlying, never()).stop()
        refcounted.stop()
        verify(underlying).stop()
    }

    test("ScheduledThreadPoolTimer should initialize and stop") {
        val timer = ScheduledThreadPoolTimer(1)
        assert(timer != null)
        timer.stop()
    }

    test("ScheduledThreadPoolTimer should increment a counter") {
        val timer = ScheduledThreadPoolTimer()
        val counter = new AtomicInteger(0)

        timer.schedule(100.milliseconds, 200.milliseconds) {
            counter.incrementAndGet()
        }

        eventually(Timeout(Span(4, Seconds))) { assert(counter.get() > 2) }
        timer.stop()
    }

    test("ScheduledThreadPoolTimer schedule(when)") {
        val timer = ScheduledThreadPoolTimer()
        val counter = new AtomicInteger(0)

        timer.schedule(Time.now + 200.milliseconds) {
            counter.incrementAndGet()
        }

        eventually(Timeout(Span(4, Seconds))) { assert(counter.get() === 1) }
        timer.stop()
    }

    test("ScheduledThreadPoolTimer cancel schedule(when)") {
        val timer = ScheduledThreadPoolTimer()
        val counter = new AtomicInteger(0)

        val task = timer.schedule(Time.now + 200.milliseconds) {
            counter.incrementAndGet()
        }

        task.cancel()
        Thread.sleep(1.seconds.toMillis)
        assert(counter.get != 1)
        timer.stop()
    }

    test("JavaTimer - 예외발생 시 작업을 중단하지 않습니다.") {
        var errors = 0
        var latch = new CountDownLatch(1)

        val timer = new JavaTimer {
            override def logError(t: Throwable) {
                errors += 1
                latch.countDown()
            }
        }

        timer.schedule(Time.now) {
            throw new scala.MatchError()
        }

        latch.await(30.seconds)
        assert(errors == 1)

        var result = 0
        latch = new CountDownLatch(1)
        timer.schedule(Time.now) {
            result = 1 + 1
            latch.countDown()
        }

        latch.await(30.seconds)

        assert(result == 2)
        assert(errors == 1)

    }

    test("JavaTimer should schedule(when)") {
        val timer = JavaTimer()
        val counter = new AtomicInteger(0)
        timer.schedule(Time.now + 20.milliseconds) {
            counter.incrementAndGet()
        }

        Thread.sleep(40.milliseconds.toMillis)
        eventually(Timeout(Span(4, Seconds))) { assert(counter.get() == 1) }
        timer.stop()
    }

    test("Timer should doLater") {
        val result = "boom"
        val timer = MockTimer()
        val f: Future[String] = timer.doLater(1.milliseconds)(result)
        assert(!f.isCompleted)
        Thread.sleep(2)
        timer.tick()
        assert(f.isCompleted)
        assert(Await.result(f, 10.milliseconds) == result)
    }

    test("Timer should doLater throws exception") {
        val timer = MockTimer()
        val ex = new Exception()
        def task: String = throw ex
        val f = timer.doLater(1.milliseconds)(task)
        assert(!f.isCompleted)
        Thread.sleep(2)
        timer.tick()
        assert(f isCompleted)
        intercept[Throwable] {
            Asyncs.result(f)
        }
    }

    test("Timer should doAt") {
        val result = "boom"
        val timer = MockTimer()
        val f: Future[String] = timer.doAt(Time.now + 1.milliseconds)(result)
        assert(!f.isCompleted)
        Thread.sleep(2)
        timer.tick()
        assert(f.isCompleted)
        assert(Await.result(f, 10.milliseconds) == result)
    }

    test("Timer should schedule(when)") {
        val timer = MockTimer()
        val counter = new AtomicInteger(0)
        timer.schedule(Time.now + 1.milliseconds) { counter.incrementAndGet() }
        Thread.sleep(2)
        timer.tick()
        assert(counter.get() == 1)
    }

    test("Timer should cancel schedule(when)") {
        val result = "boom"
        val timer = MockTimer()
        val counter = new AtomicInteger(0)
        val task = timer.schedule(Time.now + 1.milliseconds) { counter.incrementAndGet() }
        task.cancel()
        Thread.sleep(2)
        timer.tick()
        assert(counter.get() == 0)
    }

}
