package debop4s.timeperiod.tests.calendars

import debop4s.core._
import debop4s.timeperiod._
import debop4s.timeperiod.calendars.DateDiff
import debop4s.timeperiod.tests.AbstractTimePeriodTest
import debop4s.timeperiod.utils.{Durations, Times}
import org.joda.time.{DateTimeConstants, Duration, DateTime}


/**
 * debop4s.timeperiod.tests.calendars.DateDiffTest
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
    val years = Array(1, 3, 15, 30, 60, 120)

    years.foreach {
      year =>
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

        val date12DayMillis = Durations.create(date1, date2).getMillis

        dateDiff12.days should equal(date12DayMillis / DateTimeConstants.MILLIS_PER_DAY)
        dateDiff12.hours should equal(date12DayMillis / DateTimeConstants.MILLIS_PER_HOUR)
        dateDiff12.minutes should equal(date12DayMillis / DateTimeConstants.MILLIS_PER_MINUTE)
        dateDiff12.seconds should equal(date12DayMillis / DateTimeConstants.MILLIS_PER_SECOND)

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

        val date13DayMillis = Durations.create(date1, date3).getMillis

        dateDiff13.days should equal(date13DayMillis / DateTimeConstants.MILLIS_PER_DAY)
        dateDiff13.hours should equal(date13DayMillis / DateTimeConstants.MILLIS_PER_HOUR)
        dateDiff13.minutes should equal(date13DayMillis / DateTimeConstants.MILLIS_PER_MINUTE)
        dateDiff13.seconds should equal(date13DayMillis / DateTimeConstants.MILLIS_PER_SECOND)

    }
  }

  test("quarters difference") {
    val date1 = new DateTime(2011, 5, 15, 15, 32, 44, 245)
    val date2 = date1.plusMonths(MonthsPerQuarter)
    val date3 = date1.plusMonths(-MonthsPerQuarter)

    val dateDiff12 = DateDiff(date1, date2)
    val days12 = Durations.create(date1, date2).getStandardDays

    dateDiff12.elapsedYears should equal(0)
    dateDiff12.elapsedMonths should equal(MonthsPerQuarter)
    dateDiff12.elapsedDays should equal(0)
    dateDiff12.elapsedHours should equal(0)
    dateDiff12.elapsedMinutes should equal(0)
    dateDiff12.elapsedSeconds should equal(0)

    dateDiff12.years should equal(0)
    dateDiff12.quarters should equal(1)
    dateDiff12.months should equal(MonthsPerQuarter)
    dateDiff12.weeks should equal(14)
    dateDiff12.days should equal(days12)
    dateDiff12.hours should equal(days12 * HoursPerDay)
    dateDiff12.minutes should equal(days12 * HoursPerDay * MinutesPerHour)
    dateDiff12.seconds should equal(days12 * HoursPerDay * MinutesPerHour * SecondsPerMinute)

    val dateDiff13 = DateDiff(date1, date3)
    val days13 = Durations.create(date1, date3).getStandardDays

    dateDiff13.elapsedYears should equal(0)
    dateDiff13.elapsedMonths should equal(-MonthsPerQuarter)
    dateDiff13.elapsedDays should equal(0)
    dateDiff13.elapsedHours should equal(0)
    dateDiff13.elapsedMinutes should equal(0)
    dateDiff13.elapsedSeconds should equal(0)

    dateDiff13.years should equal(0)
    dateDiff13.quarters should equal(-1)
    dateDiff13.months should equal(-MonthsPerQuarter)
    dateDiff13.weeks should equal(-12)
    dateDiff13.days should equal(days13)
    dateDiff13.hours should equal(days13 * HoursPerDay)
    dateDiff13.minutes should equal(days13 * HoursPerDay * MinutesPerHour)
    dateDiff13.seconds should equal(days13 * HoursPerDay * MinutesPerHour * SecondsPerMinute)
  }

  test("months difference") {
    val date1 = new DateTime(2011, 5, 15, 15, 32, 44, 245)
    val date2 = date1 + 1.month
    val date3 = date1 - 1.month

    val dateDiff12 = DateDiff(date1, date2)
    val days12 = Durations.create(date1, date2).getStandardDays

    dateDiff12.elapsedYears should equal(0)
    dateDiff12.elapsedQuarters should equal(0)
    dateDiff12.elapsedMonths should equal(1)
    dateDiff12.elapsedDays should equal(0)
    dateDiff12.elapsedHours should equal(0)
    dateDiff12.elapsedMinutes should equal(0)
    dateDiff12.elapsedSeconds should equal(0)

    dateDiff12.years should equal(0)
    dateDiff12.quarters should equal(0)
    dateDiff12.months should equal(1)
    dateDiff12.weeks should equal(5)
    dateDiff12.days should equal(days12)
    dateDiff12.hours should equal(days12 * HoursPerDay)
    dateDiff12.minutes should equal(days12 * HoursPerDay * MinutesPerHour)
    dateDiff12.seconds should equal(days12 * HoursPerDay * MinutesPerHour * SecondsPerMinute)

    val dateDiff13 = DateDiff(date1, date3)
    val days13 = Durations.create(date1, date3).getStandardDays

    dateDiff13.elapsedYears should equal(0)
    dateDiff12.elapsedQuarters should equal(0)
    dateDiff13.elapsedMonths should equal(-1)
    dateDiff13.elapsedDays should equal(0)
    dateDiff13.elapsedHours should equal(0)
    dateDiff13.elapsedMinutes should equal(0)
    dateDiff13.elapsedSeconds should equal(0)

    dateDiff13.years should equal(0)
    dateDiff13.quarters should equal(0)
    dateDiff13.months should equal(-1)
    dateDiff13.weeks should equal(-4)
    dateDiff13.days should equal(days13)
    dateDiff13.hours should equal(days13 * HoursPerDay)
    dateDiff13.minutes should equal(days13 * HoursPerDay * MinutesPerHour)
    dateDiff13.seconds should equal(days13 * HoursPerDay * MinutesPerHour * SecondsPerMinute)
  }

  test("week difference") {
    val date1 = new DateTime(2011, 5, 15, 15, 32, 44, 245)
    val date2 = date1 + 1.week
    val date3 = date1 - 1.week

    val dateDiff12 = DateDiff(date1, date2)
    val days12 = Durations.create(date1, date2).getStandardDays

    dateDiff12.years should equal(0)
    dateDiff12.quarters should equal(0)
    dateDiff12.months should equal(0)
    dateDiff12.weeks should equal(1)
    dateDiff12.days should equal(days12)
    dateDiff12.hours should equal(days12 * HoursPerDay)
    dateDiff12.minutes should equal(days12 * HoursPerDay * MinutesPerHour)
    dateDiff12.seconds should equal(days12 * HoursPerDay * MinutesPerHour * SecondsPerMinute)

    val dateDiff13 = DateDiff(date1, date3)
    val days13 = Durations.create(date1, date3).getStandardDays

    dateDiff13.years should equal(0)
    dateDiff13.quarters should equal(0)
    dateDiff13.months should equal(0)
    dateDiff13.weeks should equal(-1)
    dateDiff13.days should equal(days13)
    dateDiff13.hours should equal(days13 * HoursPerDay)
    dateDiff13.minutes should equal(days13 * HoursPerDay * MinutesPerHour)
    dateDiff13.seconds should equal(days13 * HoursPerDay * MinutesPerHour * SecondsPerMinute)
  }

  test("days difference") {

    val days = Array(1, 3)

    days.par.foreach {
      day =>
        val date1 = new DateTime(2011, 5, 19, 15, 32, 44, 245)
        val date2 = date1 + day.day
        val date3 = date1 - day.day

        val dateDiff12 = DateDiff(date1, date2)
        val days12 = Durations.create(date1, date2).getStandardDays

        dateDiff12.elapsedYears should equal(0)
        dateDiff12.elapsedQuarters should equal(0)
        dateDiff12.elapsedMonths should equal(0)
        dateDiff12.elapsedDays should equal(day)
        dateDiff12.elapsedHours should equal(0)
        dateDiff12.elapsedMinutes should equal(0)
        dateDiff12.elapsedSeconds should equal(0)

        dateDiff12.years should equal(0)
        dateDiff12.quarters should equal(0)
        dateDiff12.months should equal(0)
        dateDiff12.weeks should equal(0)
        dateDiff12.days should equal(days12)
        dateDiff12.hours should equal(days12 * HoursPerDay)
        dateDiff12.minutes should equal(days12 * HoursPerDay * MinutesPerHour)
        dateDiff12.seconds should equal(days12 * HoursPerDay * MinutesPerHour * SecondsPerMinute)

        val dateDiff13 = DateDiff(date1, date3)
        val days13 = Durations.create(date1, date3).getStandardDays

        dateDiff13.elapsedYears should equal(0)
        dateDiff12.elapsedQuarters should equal(0)
        dateDiff13.elapsedMonths should equal(0)
        dateDiff13.elapsedDays should equal(-day)
        dateDiff13.elapsedHours should equal(0)
        dateDiff13.elapsedMinutes should equal(0)
        dateDiff13.elapsedSeconds should equal(0)

        dateDiff13.years should equal(0)
        dateDiff13.quarters should equal(0)
        dateDiff13.months should equal(0)
        dateDiff13.weeks should equal(0)
        dateDiff13.days should equal(days13)
        dateDiff13.hours should equal(days13 * HoursPerDay)
        dateDiff13.minutes should equal(days13 * HoursPerDay * MinutesPerHour)
        dateDiff13.seconds should equal(days13 * HoursPerDay * MinutesPerHour * SecondsPerMinute)
    }
  }

  test("hours difference") {

    val hours = Array(1, 3, 5)

    hours.par.foreach {
      hour =>
        val date1 = new DateTime(2011, 5, 19, 15, 32, 44, 245)
        val date2 = date1 + hour.hour
        val date3 = date1 - hour.hour

        val dateDiff12 = DateDiff(date1, date2)
        val hours12 = Durations.create(date1, date2).getStandardHours

        dateDiff12.elapsedYears should equal(0)
        dateDiff12.elapsedQuarters should equal(0)
        dateDiff12.elapsedMonths should equal(0)
        dateDiff12.elapsedDays should equal(0)
        dateDiff12.elapsedHours should equal(hour)
        dateDiff12.elapsedMinutes should equal(0)
        dateDiff12.elapsedSeconds should equal(0)

        dateDiff12.years should equal(0)
        dateDiff12.quarters should equal(0)
        dateDiff12.months should equal(0)
        dateDiff12.weeks should equal(0)
        dateDiff12.days should equal(0)
        dateDiff12.hours should equal(hours12)
        dateDiff12.minutes should equal(hours12 * MinutesPerHour)
        dateDiff12.seconds should equal(hours12 * MinutesPerHour * SecondsPerMinute)

        val dateDiff13 = DateDiff(date1, date3)
        val hours13 = Durations.create(date1, date3).getStandardHours

        dateDiff13.elapsedYears should equal(0)
        dateDiff12.elapsedQuarters should equal(0)
        dateDiff13.elapsedMonths should equal(0)
        dateDiff13.elapsedDays should equal(0)
        dateDiff13.elapsedHours should equal(-hour)
        dateDiff13.elapsedMinutes should equal(0)
        dateDiff13.elapsedSeconds should equal(0)

        dateDiff13.years should equal(0)
        dateDiff13.quarters should equal(0)
        dateDiff13.months should equal(0)
        dateDiff13.weeks should equal(0)
        dateDiff13.days should equal(0)
        dateDiff13.hours should equal(hours13)
        dateDiff13.minutes should equal(hours13 * MinutesPerHour)
        dateDiff13.seconds should equal(hours13 * MinutesPerHour * SecondsPerMinute)
    }
  }

  test("minutes difference") {

    val minutes = Array(1, 3, 5)

    minutes.par.foreach {
      minute =>
        val date1 = new DateTime(2011, 5, 19, 15, 32, 44, 245)
        val date2 = date1 + minute.minute
        val date3 = date1 - minute.minute

        val dateDiff12 = DateDiff(date1, date2)
        val minutes12 = Durations.create(date1, date2).getStandardMinutes

        dateDiff12.elapsedYears should equal(0)
        dateDiff12.elapsedQuarters should equal(0)
        dateDiff12.elapsedMonths should equal(0)
        dateDiff12.elapsedDays should equal(0)
        dateDiff12.elapsedHours should equal(0)
        dateDiff12.elapsedMinutes should equal(minute)
        dateDiff12.elapsedSeconds should equal(0)

        dateDiff12.years should equal(0)
        dateDiff12.quarters should equal(0)
        dateDiff12.months should equal(0)
        dateDiff12.weeks should equal(0)
        dateDiff12.days should equal(0)
        dateDiff12.hours should equal(0)
        dateDiff12.minutes should equal(minutes12)
        dateDiff12.seconds should equal(minutes12 * SecondsPerMinute)

        val dateDiff13 = DateDiff(date1, date3)
        val minutes13 = Durations.create(date1, date3).getStandardMinutes

        dateDiff13.elapsedYears should equal(0)
        dateDiff12.elapsedQuarters should equal(0)
        dateDiff13.elapsedMonths should equal(0)
        dateDiff13.elapsedDays should equal(0)
        dateDiff13.elapsedHours should equal(0)
        dateDiff13.elapsedMinutes should equal(-minute)
        dateDiff13.elapsedSeconds should equal(0)

        dateDiff13.years should equal(0)
        dateDiff13.quarters should equal(0)
        dateDiff13.months should equal(0)
        dateDiff13.weeks should equal(0)
        dateDiff13.days should equal(0)
        dateDiff13.hours should equal(0)
        dateDiff13.minutes should equal(minutes13)
        dateDiff13.seconds should equal(minutes13 * SecondsPerMinute)
    }
  }

  test("seconds difference") {

    val seconds = Array(1, 3, 5)

    seconds.par.foreach {
      second =>
        val date1 = new DateTime(2011, 5, 19, 15, 32, 44, 245)
        val date2 = date1 + second.second
        val date3 = date1 - second.second

        val dateDiff12 = DateDiff(date1, date2)
        val seconds12 = Durations.create(date1, date2).getStandardSeconds

        dateDiff12.elapsedYears should equal(0)
        dateDiff12.elapsedQuarters should equal(0)
        dateDiff12.elapsedMonths should equal(0)
        dateDiff12.elapsedDays should equal(0)
        dateDiff12.elapsedHours should equal(0)
        dateDiff12.elapsedMinutes should equal(0)
        dateDiff12.elapsedSeconds should equal(second)

        dateDiff12.years should equal(0)
        dateDiff12.quarters should equal(0)
        dateDiff12.months should equal(0)
        dateDiff12.weeks should equal(0)
        dateDiff12.days should equal(0)
        dateDiff12.hours should equal(0)
        dateDiff12.minutes should equal(0)
        dateDiff12.seconds should equal(seconds12)

        val dateDiff13 = DateDiff(date1, date3)
        val seconds13 = Durations.create(date1, date3).getStandardSeconds

        dateDiff13.elapsedYears should equal(0)
        dateDiff12.elapsedQuarters should equal(0)
        dateDiff13.elapsedMonths should equal(0)
        dateDiff13.elapsedDays should equal(0)
        dateDiff13.elapsedHours should equal(0)
        dateDiff13.elapsedMinutes should equal(0)
        dateDiff13.elapsedSeconds should equal(-second)

        dateDiff13.years should equal(0)
        dateDiff13.quarters should equal(0)
        dateDiff13.months should equal(0)
        dateDiff13.weeks should equal(0)
        dateDiff13.days should equal(0)
        dateDiff13.hours should equal(0)
        dateDiff13.minutes should equal(0)
        dateDiff13.seconds should equal(seconds13)
    }
  }

  test("positive duration") {
    val diffs = Array(1, 3, 5)

    diffs.par.foreach {
      diff =>
        val date1 = Times.now
        val date2 = date1 + diff.year + diff.month + diff.day + diff.hour + diff.minute + diff.second
        val date3 = date1 - diff.year - diff.month - diff.day - diff.hour - diff.minute - diff.second

        val dateDiff12 = DateDiff(date1, date2)

        dateDiff12.elapsedYears should equal(diff)
        dateDiff12.elapsedMonths should equal(diff)
        dateDiff12.elapsedDays should equal(diff)
        dateDiff12.elapsedHours should equal(diff)
        dateDiff12.elapsedMinutes should equal(diff)
        dateDiff12.elapsedSeconds should equal(diff)

        val dateDiff13 = DateDiff(date1, date3)

        dateDiff13.elapsedYears should equal(-diff)
        dateDiff13.elapsedMonths should equal(-diff)
        dateDiff13.elapsedDays should equal(-diff)
        dateDiff13.elapsedHours should equal(-diff)
        dateDiff13.elapsedMinutes should equal(-diff)
        dateDiff13.elapsedSeconds should equal(-diff)
    }
  }
}
