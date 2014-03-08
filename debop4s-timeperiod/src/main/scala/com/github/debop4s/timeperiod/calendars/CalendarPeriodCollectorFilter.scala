package com.github.debop4s.timeperiod.calendars

import com.github.debop4s.timeperiod.{DayHourRange, HourRangeInDay, DayRangeInMonth, MonthRangeInYear}
import scala.collection.mutable.ArrayBuffer

/**
 * com.github.debop4s.timeperiod.calendars.CalendarPeriodCollectorFilter
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 4. 오후 11:09
 */
@SerialVersionUID(-8493624843659994378L)
class CalendarPeriodCollectorFilter extends CalendarVisitorFilter with ICalendarPeriodCollectorFilter {

  val collectingMonths = ArrayBuffer[MonthRangeInYear]()
  val collectingDays = ArrayBuffer[DayRangeInMonth]()
  val collectingHours = ArrayBuffer[HourRangeInDay]()
  val collectingDayHours = ArrayBuffer[DayHourRange]()

  override def clear() {
    super.clear()
    collectingMonths.clear()
    collectingDays.clear()
    collectingHours.clear()
    collectingDayHours.clear()
  }
}
