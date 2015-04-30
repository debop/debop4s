package debop4s.timeperiod.calendars

import debop4s.core.Logging
import debop4s.timeperiod._

import scala.collection.mutable

trait ICalendarVisitorFilter extends Serializable {

  def excludePeriods: ITimePeriodCollection

  def years: Seq[Int]

  def monthOfYears: Seq[Int]

  def dayOfMonths: Seq[Int]

  def weekDays: mutable.Set[DayOfWeek]

  def hourOfDays: Seq[Int]

  def minuteOfHours: Seq[Int]

  def addWorkingWeekdays()

  def addWorkingWeekends()

  def addWeekdays(days: DayOfWeek*)

  def clear()
}
