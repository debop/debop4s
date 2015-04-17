package debop4s.timeperiod.tests

import debop4s.core.Logging
import debop4s.timeperiod.utils.Times
import org.joda.time.DateTime
import org.scalatest.{BeforeAndAfter, FunSuite, Matchers}

/**
 * debop4s.timeperiod.tests.AbstractTimeFunSuite
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 2. 오전 11:12
 */
abstract class AbstractTimeFunSuite extends FunSuite with Matchers with BeforeAndAfter with Logging {

  val testDate = new DateTime(2000, 10, 2, 13, 45, 53, 673)
  val testDiffDate = new DateTime(2002, 9, 3, 7, 14, 22, 234)
  val testNow = Times.now

}
