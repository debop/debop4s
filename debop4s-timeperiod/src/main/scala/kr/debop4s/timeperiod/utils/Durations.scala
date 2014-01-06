package kr.debop4s.timeperiod.utils

import java.util.Locale
import kr.debop4s.core.logging.Logger
import kr.debop4s.timeperiod.Halfyear.Halfyear
import kr.debop4s.timeperiod.Quarter.Quarter
import kr.debop4s.timeperiod._
import org.joda.time.{DateTime, Duration}

/**
 * kr.debop4s.timeperiod.tools.Durations
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 14. 오후 10:49
 */
object Durations {

    lazy val log = Logger(getClass)

    val Zero = Duration.ZERO

    def currentLocale = Locale.getDefault

    def negate(duration: Duration) = Duration.millis(-duration.getMillis)

    def create(start: DateTime, end: DateTime) = new Duration(start, end)

    def year(year: Int): Duration = {
        val start = new DateTime(year, 1, 1, 0, 0)
        val end = start.plusYears(1)
        create(start, end)
    }

    def halfyear(year: Int, halfyear: Halfyear): Duration = {
        val start = Times.startTimeOfHalfyear(year, halfyear)
        val end = start.plusMonths(MonthsPerHalfyear)
        new Duration(start, end)
    }

    def quarter(year: Int, quarter: Quarter): Duration = {
        val start = Times.startTimeOfQuarter(year, quarter)
        val end = start.plusMonths(MonthsPerQuarter)
        new Duration(start, end)
    }

    def month(year: Int, monthOfYear: Int): Duration = {
        val start = Times.startTimeOfMonth(year, monthOfYear)
        val end = start.plusMonths(1)
        new Duration(start, end)
    }

    def weeks(weeks: Int): Duration =
        if (weeks == 0) Zero else days(weeks * DaysPerWeek)

    val Day: Duration = Duration.standardDays(1)

    def days(days: Int): Duration =
        if (days == 0) Zero else Duration.standardDays(days)

    def days(days: Int, hours: Int, minutes: Int = 0, seconds: Int = 0, millis: Int = 0): Duration =
        Duration.millis(days * MillisPerDay +
                        hours * MillisPerHour +
                        minutes * MillisPerMinute +
                        seconds * MillisPerSecond +
                        millis)

    val Hour: Duration = Duration.standardHours(1)

    def hours(hours: Int, minutes: Int = 0, seconds: Int = 0, millis: Int = 0): Duration =
        Duration.millis(hours * MillisPerHour +
                        minutes * MillisPerMinute +
                        seconds * MillisPerSecond +
                        millis)

    val Minute: Duration = Duration.standardMinutes(1)

    def minutes(minutes: Int, seconds: Int = 0, millis: Int = 0): Duration =
        Duration.millis(minutes * MillisPerMinute +
                        seconds * MillisPerSecond +
                        millis)


    val Second: Duration = Duration.standardSeconds(1)

    def seconds(seconds: Int, millis: Int = 0): Duration =
        Duration.millis(seconds * MillisPerSecond + millis)

    val Millisecond: Duration = millis(1)

    def millis(millisecond: Int): Duration = Duration.millis(millisecond)

}
