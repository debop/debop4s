package debop4s.core.utils

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.{CountDownLatch => JavaCountDownLatch, TimeUnit}

import debop4s.core.AbstractCoreFunSuite
import debop4s.core.concurrent.Asyncs
import org.mockito.Mockito._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.util.{Failure, Success}

/**
 * MemorizeFunSuite
 * @author Sunghyouk Bae
 */
class MemorizeFunSuite extends AbstractCoreFunSuite {

  test("Memorize.apply: only runs the function once for the same input") {
    // mokito can't spy anonymous class,
    // and this was the simplest approach i could come up with.
    class Adder extends (Int => Int) {
      override def apply(i: Int) = i + 1
    }

    val adder = spy(new Adder)
    val memorizer = Memorize { adder(_: Int) }

    memorizer(1) shouldEqual 2
    memorizer(1) shouldEqual 2
    memorizer(2) shouldEqual 3

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
      item shouldEqual results(0)
    }

    callCount.get() shouldEqual 1
  }

  test("handles exceptions during computations") {

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
        case Success(i) => debug(i.toString)
        case Failure(e) =>
          e.isInstanceOf[RuntimeException] shouldEqual true
      }
    }

    startUpLatch.countDown()
    Thread.sleep(1000)
    callCount.get() shouldEqual 2
  }

}
