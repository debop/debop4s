package debop4s.timeperiod.calendars

import debop4s.core.conversions.jodatime._
import debop4s.core.utils.Hashs
import debop4s.core.{Logging, ToStringHelper}
import debop4s.timeperiod.TimeSpec._
import debop4s.timeperiod._
import debop4s.timeperiod.utils.Times
import org.joda.time.{DateTime, Duration}

import scala.beans.BeanProperty

/**
 * kr.hconnect.timeperiod.calendars.DateDiff
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 5. 오전 3:40
 */
class DateDiff(val start: DateTime,
               val end: DateTime,
               val calendar: ITimeCalendar) extends Logging {

  def this(start: DateTime) = this(start, Times.now, DefaultTimeCalendar)
  def this(start: DateTime, end: DateTime) = this(start, end, DefaultTimeCalendar)

  @BeanProperty val difference = new Duration(start, end)

  @BeanProperty lazy val years = calcYears()
  @BeanProperty lazy val quarters = calcQuarters()
  @BeanProperty lazy val months = calcMonths()
  @BeanProperty lazy val weeks = calcWeeks()
  @BeanProperty lazy val days = difference.getStandardDays
  @BeanProperty lazy val hours = difference.getStandardHours
  @BeanProperty lazy val minutes = difference.getStandardMinutes
  @BeanProperty lazy val seconds = difference.getStandardSeconds

  @BeanProperty lazy val elapsedYears = years
  @BeanProperty lazy val elapsedQuarters = quarters
  @BeanProperty lazy val elapsedMonths = months - elapsedYears * MonthsPerYear

  @BeanProperty lazy val elapsedStartDays = start.plusYears(elapsedYears.toInt).plusMonths(elapsedMonths.toInt)
  @BeanProperty lazy val elapsedDays = new Duration(elapsedStartDays, end).getStandardDays.toLong

  @BeanProperty lazy val elapsedStartHours =
    start.plusYears(elapsedYears.toInt)
    .plusMonths(elapsedMonths.toInt)
    .plusDays(elapsedDays.toInt)

  @BeanProperty lazy val elapsedHours = new Duration(elapsedStartHours, end).getStandardHours.toLong

  @BeanProperty lazy val elapsedStartMinutes =
    start.plusYears(elapsedYears.toInt)
    .plusMonths(elapsedMonths.toInt)
    .plusDays(elapsedDays.toInt)
    .plusHours(elapsedHours.toInt)

  @BeanProperty lazy val elapsedMinutes = new Duration(elapsedStartMinutes, end).getStandardMinutes.toLong

  @BeanProperty lazy val elapsedStartSeconds =
    start.plusYears(elapsedYears.toInt)
    .plusMonths(elapsedMonths.toInt)
    .plusDays(elapsedDays.toInt)
    .plusHours(elapsedHours.toInt)
    .plusMinutes(elapsedMinutes.toInt)

  @BeanProperty lazy val elapsedSeconds = new Duration(elapsedStartSeconds, end).getStandardSeconds.toLong

  def isEmpty = difference.isEqual(Duration.ZERO)

  def startYear = calendar.year(start)
  def endYear = calendar.year(end)
  def startMonthOfYear = calendar.monthOfYear(start)
  def endMonthOfYear = calendar.monthOfYear(end)

  def getStartYear = startYear
  def getEndYear = endYear
  def getStartMonthOfYear = startMonthOfYear
  def getEndMonthOfYear = endMonthOfYear

  @inline
  private def calcYears(): Long = {
    // if (Objects.equals(start, end)) return 0
    if (start == end)
      return 0

    val compareDay = math.min(end.getDayOfMonth, calendar.daysInMonth(startYear, endMonthOfYear))
    var compareDate = Times.asDate(startYear, endMonthOfYear, compareDay).plusMillis(end.getMillisOfDay)

    if (end > start) {
      if (compareDate < start) {
        compareDate = compareDate + 1.year
      }
    } else if (compareDate > start) {
      compareDate = compareDate - 1.year
    }
    endYear - calendar.year(compareDate)
  }

  @inline
  private def calcQuarters(): Long = {
    // if (Objects.equals(start, end))
    if (start == end)
      return 0

    val y1 = Times.yearOf(startYear, startMonthOfYear)
    val q1 = Times.quarterOfMonth(startMonthOfYear)

    val y2 = Times.yearOf(endYear, endMonthOfYear)
    val q2 = Times.quarterOfMonth(endMonthOfYear)

    (y2 * QuartersPerYear + q2.getValue) - (y1 * QuartersPerYear + q1.getValue)
  }

  @inline
  private def calcMonths(): Long = {
    // if (Objects.equals(start, end))
    if (start == end)
      return 0

    val compareDay = math.min(end.getDayOfMonth, calendar.daysInMonth(startYear, startMonthOfYear))
    var compareDate = Times.asDate(startYear, startMonthOfYear, compareDay).plusMillis(end.getMillisOfDay)

    if (end > start) {
      if (compareDate < start)
        compareDate = compareDate + 1.month
    } else if (compareDate > start) {
      compareDate = compareDate - 1.month
    }

    (endYear * MonthsPerYear + endMonthOfYear) -
    (calendar.year(compareDate) * MonthsPerYear + calendar.monthOfYear(compareDate))
  }

  @inline
  private def calcWeeks(): Long = {
    // if (Objects.equals(start, end)) 0
    if (start == end)
      return 0

    val w1 = Times.startOfWeek(start)
    val w2 = Times.startOfWeek(end)

    if (w1 == w2) 0
    else (new Duration(w1, w2).getStandardDays / DaysPerWeek).toLong
  }

  @inline
  private def roundEx(n: Double): Double = {
    math.round(n)
    //        if (n >= 0.0) math.round(n)
    //        else -math.round(-n)
  }

  override def hashCode(): Int =
    Hashs.compute(start, end, calendar)

  override def toString: String =
    ToStringHelper(this)
    .add("start", start)
    .add("end", end)
    .add("diffrence", difference)
    .add("calendar", calendar)
    .toString
}


object DateDiff {

  def apply(start: DateTime, end: DateTime): DateDiff =
    apply(start, end, DefaultTimeCalendar)

  def apply(start: DateTime, end: DateTime, calendar: ITimeCalendar): DateDiff =
    new DateDiff(start, end, calendar)

  def apply(moment: DateTime): DateDiff =
    apply(moment, DefaultTimeCalendar)

  def apply(moment: DateTime, calendar: ITimeCalendar): DateDiff =
    apply(moment, Times.now, calendar)
}
