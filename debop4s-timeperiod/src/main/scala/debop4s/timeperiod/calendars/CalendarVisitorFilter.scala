package debop4s.timeperiod.calendars

import debop4s.timeperiod.DayOfWeek.DayOfWeek
import debop4s.timeperiod._
import debop4s.timeperiod.{ TimePeriodCollection, ITimePeriodCollection }
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

  def excludePeriods: ITimePeriodCollection = _excludePeriods

  def years: ArrayBuffer[Int] = _years

  def monthOfYears: ArrayBuffer[Int] = _monthOfYears

  def dayOfMonths: ArrayBuffer[Int] = _dayOfMonths

  def weekDays: mutable.HashSet[DayOfWeek] = _weekDays

  def hourOfDays: ArrayBuffer[Int] = _hourOfDays

  def minuteOfHours: ArrayBuffer[Int] = _minuteOfHours

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
