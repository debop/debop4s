package debop4s.timeperiod.utils

import java.util.Locale

import debop4s.timeperiod._
import org.joda.time.{DateTime, Duration}

/**
 * Joda Time의 [[Duration]] 을 위한 Helper Class 입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 14. 오후 10:49
 */
object Durations {

  val Zero = Duration.ZERO

  def currentLocale: Locale = Locale.getDefault

  def negate(duration: Duration): Duration = Duration.millis(-duration.getMillis)

  def create(start: DateTime, end: DateTime) = new Duration(start, end)

  def year(year: Int): Duration = {
    val start = new DateTime(year, 1, 1, 0, 0)
    val end = start.plusYears(1)
    create(start, end)
  }

  def halfyear(year: Int, halfyear: Halfyear): Duration = {
    val start = Times.startTimeOfHalfyear(year, halfyear)
    val end = start.plusMonths(TimeSpec.MonthsPerHalfyear)
    new Duration(start, end)
  }

  def quarter(year: Int, quarter: Quarter): Duration = {
    val start = Times.startTimeOfQuarter(year, quarter)
    val end = start.plusMonths(TimeSpec.MonthsPerQuarter)
    new Duration(start, end)
  }

  def month(year: Int, monthOfYear: Int): Duration = {
    val start = Times.startTimeOfMonth(year, monthOfYear)
    val end = start.plusMonths(1)
    new Duration(start, end)
  }

  lazy val Week = weeks(1)

  def weeks(weeks: Int): Duration =
    if (weeks == 0) Zero else days(weeks * TimeSpec.DaysPerWeek)

  lazy val Day: Duration = Duration.standardDays(1)

  def days(days: Int): Duration =
    if (days == 0) Zero else Duration.standardDays(days)

  def days(d: Int, h: Int): Duration = days(d, h, 0, 0, 0)
  def days(d: Int, h: Int, m: Int): Duration = days(d, h, m, 0, 0)
  def days(d: Int, h: Int, m: Int, s: Int): Duration = days(d, h, m, s, 0)
  def days(d: Int, h: Int, m: Int, s: Int, mi: Int): Duration =
    Duration.millis(d * TimeSpec.MillisPerDay +
                    h * TimeSpec.MillisPerHour +
                    m * TimeSpec.MillisPerMinute +
                    s * TimeSpec.MillisPerSecond +
                    mi)

  lazy val Hour: Duration = Duration.standardHours(1)

  def hours(hrs: Int): Duration = hours(hrs, 0, 0, 0)
  def hours(hrs: Int, mins: Int): Duration = hours(hrs, mins, 0, 0)
  def hours(hrs: Int, mins: Int, secs: Int): Duration = hours(hrs, mins, secs, 0)
  def hours(hrs: Int, mins: Int, secs: Int, millis: Int): Duration =
    Duration.millis(hrs * TimeSpec.MillisPerHour +
                    mins * TimeSpec.MillisPerMinute +
                    secs * TimeSpec.MillisPerSecond +
                    millis)

  lazy val Minute: Duration = Duration.standardMinutes(1)

  def minutes(mins: Int): Duration = minutes(mins, 0, 0)
  def minutes(mins: Int, seconds: Int): Duration = minutes(mins, seconds, 0)
  def minutes(mins: Int, seconds: Int = 0, millis: Int = 0): Duration =
    Duration.millis(mins * TimeSpec.MillisPerMinute +
                    seconds * TimeSpec.MillisPerSecond +
                    millis)


  lazy val Second: Duration = Duration.standardSeconds(1)

  def seconds(secs: Int): Duration = seconds(secs, 0)
  def seconds(secs: Int, millis: Int): Duration =
    Duration.millis(secs * TimeSpec.MillisPerSecond + millis)

  lazy val Millisecond: Duration = millis(1)

  def millis(millisecond: Int): Duration = Duration.millis(millisecond)
  def millis(millisecond: Long): Duration = Duration.millis(millisecond)
}
