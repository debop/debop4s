package debop4s.core.utils

import debop4s.core.AbstractCoreTest
import debop4s.core.concurrent.Asyncs
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.{CountDownLatch => JavaCountDownLatch, TimeUnit}
import org.mockito.Mockito._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.util.{Success, Failure}

/**
 * MemorizeTest
 * @author Sunghyouk Bae
 */
class MemorizeTest extends AbstractCoreTest {

    test("Memorize.apply: only runs the function once for the same input") {
        // mokito can't spy anonymous class,
        // and this was the simplest approach i could come up with.
        class Adder extends (Int => Int) {
            override def apply(i: Int) = i + 1
        }

        val adder = spy(new Adder)
        val memorizer = Memorize { adder(_: Int) }

        assert(memorizer(1) == 2)
        assert(memorizer(1) == 2)
        assert(memorizer(2) == 3)

        verify(adder, times(1))(1)
        verify(adder, times(1))(2)
    }

    test("Memorize.apply: only executes the memorized computation once per input") {
        val callCount = new AtomicInteger(0)

        val startUpLatch = new JavaCountDownLatch(1)

        val memorizer = Memorize { i: Int =>

        // Wait for all of the threads to be started before
        // continuing. This gives races a chance to happen.
            startUpLatch.await()

            // Perform the effect of incrementing the counter, so that we
            // can detect whether this code is executed more than once.
            callCount.incrementAndGet()

            // Return a new object so that object equality will not pass
            // if two different result values are used.
            "." * i
        }

        val concurencyLevel = 5
        val computations = (0 until concurencyLevel).map { _ => Future { memorizer(concurencyLevel) } }.seq

        startUpLatch.countDown()
        val results = Asyncs.result(Future.sequence(computations))

        results foreach { item =>
            assert(item === results(0))
            assert(item eq results(0))
        }

        assert(callCount.get() === 1)
    }

    test("handles exceptios during computations") {

        val startUpLatch = new JavaCountDownLatch(1)
        val callCount = new AtomicInteger(0)

        val memo = Memorize { i: Int =>
        // wait for all caller has been started
            startUpLatch.await(200, TimeUnit.MILLISECONDS)

            val n = callCount.incrementAndGet()

            if (n == 1) throw new RuntimeException()
            else i + 1
        }

        val concurencyLevel = 5
        val computations = (0 until concurencyLevel).map { _ => Future { memo(concurencyLevel) } }.seq

        computations foreach { f =>
            f onComplete {
                case Success(i) => println(i)
                case Failure(e) =>
                    assert(e.isInstanceOf[RuntimeException])
            }
        }

        startUpLatch.countDown()

        Thread.sleep(200)

        assert(callCount.get() == 2)
    }

}
