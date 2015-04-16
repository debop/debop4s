package debop4s.core.pool

import java.util.concurrent.atomic.AtomicInteger

import debop4s.core.AbstractCoreFunSuite
import debop4s.core._

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._

/**
 * PoolFunSuite
 * @author Sunghyouk Bae
 */
class PoolFunSuite extends AbstractCoreFunSuite {

  test("SimplePool - it reserves FIFO orders") {
    val queue = new mutable.Queue[Int] ++ List(1, 2, 3)
    val pool = new SimplePool[Int](queue)

    pool.reserve().await shouldEqual 1
    pool.reserve().await shouldEqual 2
    pool.release(2)
    pool.reserve().await shouldEqual 3
    pool.reserve().await shouldEqual 2
    pool.release(1)
    pool.release(2)
    pool.release(3)
  }

  test("reserve and release") {
    val pool = createPool(4)

    pool.reserve().await shouldEqual 2
    pool.reserve().await shouldEqual 4
    pool.reserve().await shouldEqual 6
    pool.reserve().await shouldEqual 8

    val item = pool.reserve()
    intercept[TimeoutException] {
      item.await(1 millis)
    }

    pool.release(8)
    pool.release(6)
    item.await shouldEqual 8
    pool.reserve().await shouldEqual 6
    intercept[TimeoutException] {
      pool.reserve().await(1 millis)
    }
  }

  test("reserve and dispose") {
    val pool = createPool(4)

    pool.reserve().await shouldEqual 2
    pool.reserve().await shouldEqual 4
    pool.reserve().await shouldEqual 6
    pool.reserve().await shouldEqual 8

    intercept[TimeoutException] {
      pool.reserve().await(1 millis)
    }
    pool.dispose(2)
    pool.reserve().await(20.millis) shouldEqual 10
  }

  test("reserve serial") {
    val pool = createPool(10)

    (0 until 10) foreach { _ =>
      log.debug(pool.reserve().await.toString)
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
