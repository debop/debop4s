package debop4s.timeperiod.tests.timeranges

import debop4s.core._
import debop4s.timeperiod._
import debop4s.timeperiod.tests.AbstractTimePeriodTest
import debop4s.timeperiod.timerange.MonthRange
import debop4s.timeperiod.utils.Times

/**
 * MonthRangeTest
 * Created by debop on 2014. 2. 16.
 */
class MonthRangeTest extends AbstractTimePeriodTest {

    test("init values") {
        val now = Times.now
        val firstMonth = Times.startTimeOfMonth(now)
        val secondMonth = firstMonth + 1.month

        val mr = MonthRange(now, EmptyOffsetTimeCalendar)
        mr.start should equal(firstMonth)
        mr.end should equal(secondMonth)
    }

    test("default calendar") {
        val yearStart = Times.startTimeOfYear(Times.now)

        (0 until MonthsPerYear).par.foreach { m =>
            val mr = MonthRange(yearStart + m.month)
            mr.year should equal(yearStart.getYear)
            mr.monthOfYear should equal(m + 1)

            mr.unmappedStart should equal(yearStart + m.month)
            mr.unmappedEnd should equal(yearStart + (m + 1).month)
        }
    }

    test("get days") {
        val now = Times.now
        val mr = MonthRange()
        val days = mr.days

        var index = 0

        days.foreach { day =>
            day.start should equal(mr.start + index.day)
            day.end should equal(day.calendar.mapEnd(day.start + 1.day))
            index += 1
        }
        index should equal(Times.daysInMonth(mr.year, mr.monthOfYear))
    }

    test("add months") {
        val now = Times.now
        val startMonth = Times.startTimeOfMonth(now)
        val mr = MonthRange(now)

        mr.previousMonth.start should equal(startMonth - 1.month)
        mr.nextMonth.start should equal(startMonth + 1.month)

        mr should equal(mr.addMonths(0))

        (-60 until 120).par.foreach {
            m =>
                mr.addMonths(m).start should equal(startMonth + m.month)
        }
    }

}
