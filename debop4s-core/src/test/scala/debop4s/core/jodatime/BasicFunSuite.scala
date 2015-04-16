package debop4s.core.jodatime

import debop4s.core.AbstractCoreFunSuite
import debop4s.core.conversions.jodatime._
import org.joda.time.{DateTime, Interval}

class BasicFunSuite extends AbstractCoreFunSuite {

  test("DateTime manipulation") {
    val now = JodaDateTime.now
    now shouldEqual now
    (now + 1.hour) isAfter now
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

    expected shouldEqual actual
  }

  test("basic test") {
    JodaDateTime.nextMonth should be < JodaDateTime.now + 2.months

    val x: Interval = JodaDateTime.now to JodaDateTime.tomorrow
    print(s"x=[$x]")
    (JodaDateTime.now to JodaDateTime.nextSecond).millis shouldEqual 1000L +- 2L
  }

}
