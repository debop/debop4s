package com.github.debop4s.timeperiod.tests.timeranges

import com.github.debop4s.core.parallels.Parallels
import com.github.debop4s.timeperiod._
import com.github.debop4s.timeperiod.tests.AbstractTimePeriodTest
import com.github.debop4s.timeperiod.timerange.DayRange
import com.github.debop4s.timeperiod.utils.Times
import com.github.debop4s.timeperiod.utils.Times._

/**
 * DayRangeTest
 * Created by debop on 2014. 2. 15.
 */
class DayRangeTest extends AbstractTimePeriodTest {

    test("initValues") {
        val now = Times.now
        val firstDay = startTimeOfDay(now)

        val dr = DayRange(now, EmptyOffsetTimeCalendar)

        dr.start should equal(firstDay)
        dr.end should equal(firstDay + 1.day)
    }

    test("defaultCalendar") {
        val yearStart = startTimeOfYear(now)

        (1 to MonthsPerYear).par.foreach(m => {
            val monthStart = asDate(yearStart.getYear, m, 1)
            val monthEnd = endTimeOfMonth(monthStart)

            (1 until monthEnd.getDayOfMonth).foreach(day => {
                val dayRange = DayRange(monthStart.plusDays(day - 1))
                dayRange.year should equal(yearStart.getYear)
                dayRange.monthOfYear should equal(monthStart.getMonthOfYear)
            })
        })
    }

    test("construct test") {
        val dayRange = DayRange(now)
        dayRange.start should equal(today)

        val dayRange2 = new DayRange(now.getYear, now.getMonthOfYear, now.getDayOfMonth)
        dayRange2.start should equal(today)
    }

    test("dayOfWeek") {
        val dayRange = DayRange(now)
        dayRange.dayOfWeek should equal(DefaultTimeCalendar.getDayOfWeek(now))
    }

    test("addDays") {
        val now = Times.now
        val today = Times.today
        val dayRange = DayRange(now)

        dayRange.previousDay.start should equal(today.plusDays(-1))
        dayRange.nextDay.start should equal(today.plusDays(1))

        dayRange.addDays(0) should equal(dayRange)

        Parallels.runAction1(Range(-60, 120))(i => dayRange.addDays(i).start should equal(today.plusDays(i)))
    }

    test("get hours") {
        val dayRange = DayRange()
        val hours = dayRange.getHours

        var index = 0
        hours.foreach(h => {
            h.start should equal(dayRange.start.plusHours(index))
            h.end should equal(h.calendar.mapEnd(h.start.plusHours(1)))
            index += 1
        })
        index should equal(HoursPerDay)
    }

}
