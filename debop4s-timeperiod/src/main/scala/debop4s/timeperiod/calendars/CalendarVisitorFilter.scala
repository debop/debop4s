package debop4s.timeperiod.calendars

import debop4s.timeperiod.TimeSpec._
import debop4s.timeperiod._

import scala.annotation.varargs
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
 * debop4s.timeperiod.calendars.CalendarVisitorFilter
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 3. 오전 10:17
 */
@SerialVersionUID(3032428178497692848L)
class CalendarVisitorFilter extends ICalendarVisitorFilter {

  lazy val _excludePeriods = TimePeriodCollection()
  lazy val _years = ArrayBuffer[Int]()
  lazy val _monthOfYears = ArrayBuffer[Int]()
  lazy val _dayOfMonths = ArrayBuffer[Int]()
  lazy val _hourOfDays = ArrayBuffer[Int]()
  lazy val _minuteOfHours = ArrayBuffer[Int]()
  lazy val _weekDays = mutable.HashSet[DayOfWeek]()

  def excludePeriods = _excludePeriods
  def getExcludePeriods = _excludePeriods

  def years = _years
  def getYears = _years

  @varargs
  def addYears(yrs: Int*) = _years ++= yrs

  def monthOfYears = _monthOfYears
  def getMonthOfYears = _monthOfYears

  @varargs
  def addMonthOfYears(months: Int*) = _monthOfYears ++= months

  def dayOfMonths = _dayOfMonths
  def getDayOfMonths = dayOfMonths

  @varargs
  def addDayOfMonths(days: Int*) = _dayOfMonths ++= days

  def weekDays = _weekDays
  def getWeekDays = _weekDays

  @varargs
  def addWeekDays(days: DayOfWeek*) = _weekDays ++= days

  def hourOfDays = _hourOfDays
  def getHourOfDays = _hourOfDays

  @varargs
  def addHourOfDays(hrs: Int*) = _hourOfDays ++= hrs

  def minuteOfHours = _minuteOfHours
  def getMinuteOfHours = _minuteOfHours

  @varargs
  def addMinuteOfHours(minutes: Int*) = _minuteOfHours ++= minutes

  def addWorkingWeekdays() {
    addWeekdays(Weekdays: _*)
  }

  def addWorkingWeekends() {
    addWeekdays(Weekends: _*)
  }

  @varargs
  def addWeekdays(days: DayOfWeek*) {
    _weekDays ++= days
  }

  def clear() {
    _years.clear()
    _monthOfYears.clear()
    _dayOfMonths.clear()
    _hourOfDays.clear()
    _minuteOfHours.clear()
    _weekDays.clear()
  }

  override def toString: String = Calendars.asString(this)
}
