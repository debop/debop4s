package kr.debop4s.timeperiod.tests.timeranges

import kr.debop4s.timeperiod.tests.AbstractTimePeriodTest
import kr.debop4s.timeperiod.timerange.CalendarTimeRange
import kr.debop4s.timeperiod.utils.Times
import kr.debop4s.timeperiod.{TimeRange, TimeCalendar}


class CalendarTimeRangeTest extends AbstractTimePeriodTest {

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