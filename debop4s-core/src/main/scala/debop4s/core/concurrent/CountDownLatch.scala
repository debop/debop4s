package debop4s.core.concurrent

import java.util.concurrent
import java.util.concurrent.TimeUnit

import scala.concurrent.TimeoutException
import scala.concurrent.duration._

/**
 * Java CountDownLatch를 Wrapping 합니다.
 * Created by debop on 2014. 4. 6.
 */
class CountDownLatch(val initialCount: Int) {

  val underlying: concurrent.CountDownLatch = new java.util.concurrent.CountDownLatch(initialCount)

  def count: Long = underlying.getCount

  def isZero: Boolean = count == 0

  def countDown(): Unit = underlying.countDown()

  def await(): Unit = underlying.await()

  def await(timeout: Duration): Boolean = underlying.await(timeout.toMillis, TimeUnit.MILLISECONDS)

  def within(timeout: Duration): Boolean =
    await(timeout) || {
      throw new TimeoutException(timeout.toString)
    }

}
