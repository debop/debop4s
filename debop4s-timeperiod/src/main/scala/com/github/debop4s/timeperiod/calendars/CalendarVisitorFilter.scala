package com.github.debop4s.timeperiod.calendars

import com.github.debop4s.timeperiod.DayOfWeek.DayOfWeek
import com.github.debop4s.timeperiod._
import com.github.debop4s.timeperiod.{TimePeriodCollection, ITimePeriodCollection}
import scala.annotation.varargs
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
 * com.github.debop4s.timeperiod.calendars.CalendarVisitorFilter
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 3. 오전 10:17
 */
@SerialVersionUID(3032428178497692848L)
class CalendarVisitorFilter extends ICalendarVisitorFilter {

    lazy val _excludePeriods = TimePeriodCollection()
    lazy val _years = ListBuffer[Int]()
    lazy val _monthOfYears = ListBuffer[Int]()
    lazy val _dayOfMonths = ListBuffer[Int]()
    lazy val _hourOfDays = ListBuffer[Int]()
    lazy val _minuteOfHours = ListBuffer[Int]()
    lazy val _weekDays = mutable.HashSet[DayOfWeek]()

    def excludePeriods: ITimePeriodCollection = _excludePeriods

    def years: ListBuffer[Int] = _years

    def monthOfYears: ListBuffer[Int] = _monthOfYears

    def dayOfMonths: ListBuffer[Int] = _dayOfMonths

    def weekDays: mutable.HashSet[DayOfWeek] = _weekDays

    def hourOfDays: ListBuffer[Int] = _hourOfDays

    def minuteOfHours: ListBuffer[Int] = _minuteOfHours

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
