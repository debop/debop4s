package kr.debop4s.time.tests

import kr.debop4s.time.StaticDateTime
import org.joda.time.DateTime
import org.junit.Test
import org.scalatest.junit.AssertionsForJUnit

/**
 * kr.debop4s.time.tests.BasicTest 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 7. 오후 2:50
 */
class BasicTest extends AssertionsForJUnit {

    @Test
    def dateTimeManupulation() {
        val now = StaticDateTime.now
        assert(now == now)

        assert((now plusHours 1) isAfter now)
    }

    @Test
    def dateTimeSetter() {
        val actual =
            DateTime.parse("2014-01-01T01:01:01.123+0900")
                .withYear(2013)
                .withMonthOfYear(3)
                .withDayOfMonth(2)
                .withHourOfDay(7)
                .withMinuteOfHour(8)
                .withSecondOfMinute(9)

        val expected = DateTime.parse("2013-03-02T07:08:09.123+0900")
        assert(actual === expected)
    }

}
