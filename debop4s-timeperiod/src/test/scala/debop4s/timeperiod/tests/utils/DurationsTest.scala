package debop4s.timeperiod.tests.utils

import debop4s.core.conversions.jodatime._
import debop4s.timeperiod._
import debop4s.timeperiod.tests.AbstractTimePeriodTest
import debop4s.timeperiod.utils.{ Durations, Times }
import java.util.Locale
import org.joda.time.Duration

/**
 * debop4s.timeperiod.tests.base.DurationsTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 17. 오전 11:15
 */
class DurationsTest extends AbstractTimePeriodTest {

  val currentYear = Times.currentYear.getYear
  val currentLocale = Locale.getDefault()
  val range = ( -100 to 100 )

  test("year duartion") {
    Durations.year(currentYear) should equal(new Duration(Times.startTimeOfYear(currentYear), Times.startTimeOfYear(currentYear + 1)))
    Durations.year(currentYear + 1) should equal(new Duration(Times.startTimeOfYear(currentYear + 1), Times.startTimeOfYear(currentYear + 2)))
    Durations.year(currentYear - 1) should equal(new Duration(Times.startTimeOfYear(currentYear - 1), Times.startTimeOfYear(currentYear)))

    Durations.year(currentYear) should equal(Durations.days(Times.daysOfYear(currentYear)))
    Durations.year(currentYear + 1) should equal(Durations.days(Times.daysOfYear(currentYear + 1)))
    Durations.year(currentYear - 1) should equal(Durations.days(Times.daysOfYear(currentYear - 1)))
  }

  test("halfyear duration") {
    Halfyear.values.par.foreach {
      hy =>
        val months = Times.monthsOfHalfyear(hy)
        var duration = Durations.Zero

        months.foreach {
          m =>
            val daysInMonth = Times.daysInMonth(currentYear, m)
            duration = duration + ( daysInMonth * MillisPerDay )
        }
        Durations.halfyear(currentYear, hy) should equal(duration)
    }
  }

  test("quarter duration") {
    Quarter.values.par.foreach {
      q =>
        val months = Times.monthsOfQuarter(q)
        var duration = Durations.Zero

        months.foreach {
          m =>
            val daysInMonth = Times.daysInMonth(currentYear, m)
            duration = duration + ( daysInMonth * MillisPerDay )
        }
        Durations.quarter(currentYear, q) should equal(duration)
    }
  }

  test("month duration") {
    ( 1 to MonthsPerYear ).par.foreach {
      m =>
        Durations.month(currentYear, m) should equal(Duration.millis(Times.daysInMonth(currentYear, m) * MillisPerDay))
    }
  }

  test("week duration") {
    Durations.Week should equal(Duration.millis(DaysPerWeek * MillisPerDay))

    range.par.foreach {
      w =>
        Durations.weeks(w) should equal(Duration.standardDays(w * DaysPerWeek))
    }
  }

  test("day duration") {
    Durations.Day.getMillis should equal(MillisPerDay)

    range.par.foreach {
      d =>
        Durations.days(d) should equal(Duration.standardDays(d))
    }
  }

  test("hour duration") {
    Durations.Hour should equal(Duration.standardHours(1))

    range.par.foreach {
      h =>
        Durations.hours(h) should equal(Duration.standardHours(h))
    }
  }

  test("minute duration") {
    Durations.Minute should equal(Duration.standardMinutes(1))

    range.par.foreach {
      m =>
        Durations.minutes(m) should equal(Duration.standardMinutes(m))
    }
  }

  test("second duration") {
    Durations.Second should equal(Duration.standardSeconds(1))

    range.par.foreach {
      s =>
        Durations.seconds(s) should equal(Duration.standardSeconds(s))
    }
  }

  test("millis duration") {
    Durations.Millisecond.getMillis should equal(1)

    range.par.foreach {
      ms =>
        Durations.millis(ms).getMillis should equal(ms)
    }
  }
}
