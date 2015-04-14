package debop4s.core.pool

import debop4s.core.AbstractCoreTest
import debop4s.core.concurrent.Asyncs
import java.util.concurrent.atomic.AtomicInteger
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._

/**
 * PoolTest
 * @author Sunghyouk Bae
 */
class PoolTest extends AbstractCoreTest {

  test("SimplePool - it reserves FIFO orders") {
    val queue = new mutable.Queue[Int] ++ List(1, 2, 3)
    val pool = new SimplePool[Int](queue)

    Asyncs.result(pool.reserve()) shouldEqual 1
    Asyncs.result(pool.reserve()) shouldEqual 2
    pool.release(2)
    Asyncs.result(pool.reserve()) shouldEqual 3
    Asyncs.result(pool.reserve()) shouldEqual 2
    pool.release(1)
    pool.release(2)
    pool.release(3)
  }

  test("reserve and release") {
    val pool = createPool(4)

    Asyncs.result(pool.reserve()) shouldEqual 2
    Asyncs.result(pool.reserve()) shouldEqual 4
    Asyncs.result(pool.reserve()) shouldEqual 6
    Asyncs.result(pool.reserve()) shouldEqual 8

    val item = pool.reserve()
    intercept[TimeoutException] {
      Asyncs.result(item, 1 millis)
    }

    pool.release(8)
    pool.release(6)
    Asyncs.result(item) shouldEqual 8
    Asyncs.result(pool.reserve()) shouldEqual 6
    intercept[TimeoutException] {
      Asyncs.result(pool.reserve(), 1 millis)
    }
  }

  test("reserve and dispose") {
    val pool = createPool(4)

    Asyncs.result(pool.reserve()) shouldEqual 2
    Asyncs.result(pool.reserve()) shouldEqual 4
    Asyncs.result(pool.reserve()) shouldEqual 6
    Asyncs.result(pool.reserve()) shouldEqual 8
    intercept[TimeoutException] {
      Asyncs.result(pool.reserve(), 1 millis)
    }
    pool.dispose(2)
    Asyncs.result(pool.reserve(), 20.millis) shouldEqual 10
  }

  test("reserve serial") {
    val pool = createPool(10)

    (0 until 10) foreach { _ =>
      log.debug(Asyncs.result(pool.reserve()).toString)
    }
  }

  private def createPool(size: Int) = {
    val count = new AtomicInteger(0)

    new FactoryPool[Int](size) {
      def makeItem(): Future[Int] = {
        Thread.sleep(10)
        Future(count.incrementAndGet())
      }
      def isHealthy(i: Int) = i % 2 == 0
    }
  }
}
