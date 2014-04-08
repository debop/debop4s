package debop4s.timeperiod.tests

import debop4s.timeperiod._
import org.joda.time.{Interval, DateTime}
import org.scalatest.{BeforeAndAfter, Matchers, FunSuite}
import org.slf4j.LoggerFactory

/**
 * com.github.time.tests.debop4s.timeperiod.tests.BasicTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 7. 오후 2:50
 */
class BasicTest extends FunSuite with Matchers with BeforeAndAfter {

  lazy val log = LoggerFactory.getLogger(getClass)

  test("DateTime manipulation") {
    val now = StaticDateTime.now
    assert(now == now)

    assert((now plusHours 1) isAfter now)
  }

  test("DateTime setter") {
    val actual =
      DateTime.parse("2014-01-01T01:01:01.123+0900")
        .withYear(2013)
        .withMonthOfYear(3)
        .withDayOfMonth(2)
        .withHourOfDay(7)
        .withMinuteOfHour(8)
        .withSecondOfMinute(9)

    val expected = DateTime.parse("2013-03-02T07:08:09.123+0900")
    assert(actual == expected)
  }

  test("basic test") {
    assert(TDateTime.nextMonth < TDateTime.now + 2.months)

    val x: Interval = TDateTime.now to TDateTime.tomorrow

    print(s"x=[$x]")

    assert((TDateTime.now to TDateTime.nextSecond).millis == 1000)
  }

}
