package kr.debop4s.timeperiod.tests.timeranges

import kr.debop4s.timeperiod.tests.AbstractTimePeriodTest
import kr.debop4s.timeperiod.timerange.CalendarTimeRange
import kr.debop4s.timeperiod.utils.Times
import kr.debop4s.timeperiod.{TimeRange, TimeCalendar}
import org.slf4j.LoggerFactory


class CalendarTimeRangeTest extends AbstractTimePeriodTest {

    lazy val log = LoggerFactory.getLogger(getClass)

    test("calendar test") {
        val calendar = TimeCalendar()
        val range = new CalendarTimeRange(TimeRange.Anytime, calendar)

        range.calendar shouldBe calendar
        range.isAnytime shouldBe true
    }

    test("moment test") {
        intercept[AssertionError] {
            new CalendarTimeRange(Times.today, Times.today)
        }
    }
}
