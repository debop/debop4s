package kr.debop4s.timeperiod.calendars

import kr.debop4s.timeperiod.DayOfWeek._
import kr.debop4s.timeperiod.ITimePeriodCollection
import scala.collection.mutable

/**
 * kr.debop4s.timeperiod.calendars.ICalendarVisitorFilter
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 3. 오전 10:40
 */
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
