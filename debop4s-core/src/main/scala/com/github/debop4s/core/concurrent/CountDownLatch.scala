package com.github.debop4s.core.concurrent

import java.util.concurrent.TimeUnit
import scala.concurrent.TimeoutException
import scala.concurrent.duration._

/**
 * Java CountDownLatch를 Wrapping 합니다.
 * Created by debop on 2014. 4. 6.
 */
class CountDownLatch(val initialCount: Int) {

  val underlying = new java.util.concurrent.CountDownLatch(initialCount)

  def count = underlying.getCount
  def isZero = count == 0
  def countDown() = underlying.countDown()
  def await() = underlying.await()
  def await(timeout: Duration) = underlying.await(timeout.toMillis, TimeUnit.MILLISECONDS)
  def within(timeout: Duration) = await(timeout) || {
    throw new TimeoutException(timeout.toString)
  }

}
