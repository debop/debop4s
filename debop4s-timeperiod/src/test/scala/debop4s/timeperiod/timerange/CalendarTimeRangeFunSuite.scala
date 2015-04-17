package debop4s.timeperiod.timerange

import debop4s.timeperiod._
import debop4s.timeperiod.tests.AbstractTimeFunSuite
import debop4s.timeperiod.utils.Times

class CalendarTimeRangeFunSuite extends AbstractTimeFunSuite {

  test("calendar test") {
    val calendar = TimeCalendar()
    val range = new CalendarTimeRange(TimeRange.Anytime, calendar)

    range.calendar shouldBe calendar
    range.isAnytime shouldBe true
  }

  test("moment test") {
    intercept[AssertionError] {
      CalendarTimeRange(Times.today, Times.today)
    }
  }
}
