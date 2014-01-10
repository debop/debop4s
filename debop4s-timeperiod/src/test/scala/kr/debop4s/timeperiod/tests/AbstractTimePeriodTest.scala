package kr.debop4s.timeperiod.tests

import kr.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime
import org.scalatest.junit.AssertionsForJUnit
import org.slf4j.LoggerFactory

/**
 * kr.debop4s.timeperiod.tests.AbstractTimePeriodTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 2. 오전 11:12
 */
abstract class AbstractTimePeriodTest extends AssertionsForJUnit {

    implicit lazy val log = LoggerFactory.getLogger(getClass)

    val testDate = new DateTime(2000, 10, 2, 13, 45, 53, 673)
    val testDiffDate = new DateTime(2002, 9, 3, 7, 14, 22, 234)
    val testNow = Times.now

}
