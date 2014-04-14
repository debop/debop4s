package debop4s.timeperiod.tests.timeranges

import debop4s.core.jodatime._
import debop4s.core.parallels.Parallels
import debop4s.timeperiod._
import debop4s.timeperiod.tests.AbstractTimePeriodTest
import debop4s.timeperiod.timerange.DayRange
import debop4s.timeperiod.utils.Times._

/**
 * DayRangeTest
 * Created by debop on 2014. 2. 15.
 */
class DayRangeTest extends AbstractTimePeriodTest {

    test("initValues") {
        val currentTime = now
        val firstDay = startTimeOfDay(currentTime)

        val dr = DayRange(currentTime, EmptyOffsetTimeCalendar)

        dr.start should equal(firstDay)
        dr.end should equal(firstDay + 1.day)
    }

    test("defaultCalendar") {
        val yearStart = startTimeOfYear(now)

        (1 to MonthsPerYear).par.foreach {
            m =>
                val monthStart = asDate(yearStart.getYear, m, 1)
                val monthEnd = endTimeOfMonth(monthStart)

                (1 until monthEnd.getDayOfMonth).foreach {
                    day =>
                        val dayRange = DayRange(monthStart.plusDays(day - 1))
                        dayRange.year should equal(yearStart.getYear)
                        dayRange.monthOfYear should equal(monthStart.getMonthOfYear)
                }
        }
    }

    test("construct test") {
        val dayRange = DayRange(now)
        dayRange.start should equal(today)

        val dayRange2 = DayRange(now.getYear, now.getMonthOfYear, now.getDayOfMonth)
        dayRange2.start should equal(today)
    }

    test("dayOfWeek") {
        val dayRange = DayRange(now)
        dayRange.dayOfWeek should equal(DefaultTimeCalendar.dayOfWeek(now))
    }

    test("addDays") {
        val time = now
        val day = today
        val dayRange = DayRange(time)

        dayRange.previousDay.start should equal(day.plusDays(-1))
        dayRange.nextDay.start should equal(day.plusDays(1))

        dayRange.addDays(0) should equal(dayRange)

        Parallels.runAction1(Range(-60, 120))(i => dayRange.addDays(i).start should equal(day.plusDays(i)))
    }

    test("get hours") {
        val dayRange = DayRange()
        val hours = dayRange.hours

        var index = 0
        hours.foreach { h =>
            h.start should equal(dayRange.start.plusHours(index))
            h.end should equal(h.calendar.mapEnd(h.start.plusHours(1)))
            index += 1
        }
        index should equal(HoursPerDay)
    }

}
