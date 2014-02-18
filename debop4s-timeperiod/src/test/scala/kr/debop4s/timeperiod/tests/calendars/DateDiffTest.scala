package kr.debop4s.timeperiod.tests.calendars

import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.calendars.DateDiff
import kr.debop4s.timeperiod.tests.AbstractTimePeriodTest
import kr.debop4s.timeperiod.utils.{Durations, Times}
import org.joda.time.{Duration, DateTime}

/**
 * kr.debop4s.timeperiod.tests.calendars.DateDiffTest 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 18. 오후 6:34
 */
class DateDiffTest extends AbstractTimePeriodTest {

    test("empty date diff") {
        val now = Times.now
        val dateDiff = DateDiff(now, now)

        dateDiff.isEmpty should equal(true)
        dateDiff.difference should equal(Durations.Zero)

        dateDiff.years should equal(0)
        dateDiff.quarters should equal(0)
        dateDiff.months should equal(0)
        dateDiff.weeks should equal(0)
        dateDiff.days should equal(0)
        dateDiff.hours should equal(0)
        dateDiff.minutes should equal(0)
        dateDiff.seconds should equal(0)

        dateDiff.elapsedYears should equal(0)
        dateDiff.elapsedMonths should equal(0)
        dateDiff.elapsedDays should equal(0)
        dateDiff.elapsedHours should equal(0)
        dateDiff.elapsedMinutes should equal(0)
        dateDiff.elapsedSeconds should equal(0)
    }

    test("difference") {
        val date1 = new DateTime(2008, 10, 12, 15, 32, 44, 243)
        val date2 = new DateTime(2010, 1, 3, 23, 22, 9, 345)

        val dateDiff = DateDiff(date1, date2)

        dateDiff.difference should equal(new Duration(date1, date2))
    }

    test("years difference") {
        val years = Array(1, 3, 15, 60, 100)

        years.par.foreach(year => {
            val date1 = Times.now
            val date2 = date1 + year.year
            val date3 = date1 - year.year

            log.trace(s"date1=$date1, date2=$date2, date3=$date3")

            val dateDiff12 = DateDiff(date1, date2)

            dateDiff12.elapsedYears should equal(year)
            dateDiff12.elapsedMonths should equal(0)
            dateDiff12.elapsedDays should equal(0)
            dateDiff12.elapsedHours should equal(0)
            dateDiff12.elapsedMinutes should equal(0)
            dateDiff12.elapsedSeconds should equal(0)

            dateDiff12.years should equal(year)
            dateDiff12.quarters should equal(year * QuartersPerYear)
            dateDiff12.months should equal(year * MonthsPerYear)

            val date12Days = Durations.create(date1, date2).getStandardDays

            dateDiff12.days should equal(date12Days)
            dateDiff12.hours should equal(date12Days * HoursPerDay)
            dateDiff12.minutes should equal(date12Days * HoursPerDay * MinutesPerHour)
            dateDiff12.seconds should equal(date12Days * HoursPerDay * MinutesPerHour * SecondsPerMinute)

            val dateDiff13 = DateDiff(date1, date3)

            dateDiff13.elapsedYears should equal(-year)
            dateDiff13.elapsedMonths should equal(0)
            dateDiff13.elapsedDays should equal(0)
            dateDiff13.elapsedHours should equal(0)
            dateDiff13.elapsedMinutes should equal(0)
            dateDiff13.elapsedSeconds should equal(0)

            dateDiff13.years should equal(-year)
            dateDiff13.quarters should equal(-year * QuartersPerYear)
            dateDiff13.months should equal(-year * MonthsPerYear)

            val date13Days = Durations.create(date1, date3).getStandardDays

            dateDiff13.days should equal(date13Days)
            dateDiff13.hours should equal(date13Days * HoursPerDay)
            dateDiff13.minutes should equal(date13Days * HoursPerDay * MinutesPerHour)
            dateDiff13.seconds should equal(date13Days * HoursPerDay * MinutesPerHour * SecondsPerMinute)

        })
    }

}
