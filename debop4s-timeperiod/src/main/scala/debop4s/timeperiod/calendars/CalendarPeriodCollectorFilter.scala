package debop4s.timeperiod.calendars

import debop4s.timeperiod.{DayHourRange, HourRangeInDay, DayRangeInMonth, MonthRangeInYear}
import scala.collection.mutable.ArrayBuffer

/**
 * debop4s.timeperiod.calendars.CalendarPeriodCollectorFilter
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 4. 오후 11:09
 */
@SerialVersionUID(-8493624843659994378L)
class CalendarPeriodCollectorFilter extends CalendarVisitorFilter with ICalendarPeriodCollectorFilter {

    lazy val collectingMonths = ArrayBuffer[MonthRangeInYear]()
    lazy val collectingDays = ArrayBuffer[DayRangeInMonth]()
    lazy val collectingHours = ArrayBuffer[HourRangeInDay]()
    lazy val collectingDayHours = ArrayBuffer[DayHourRange]()

    override def clear() {
        super.clear()
        collectingMonths.clear()
        collectingDays.clear()
        collectingHours.clear()
        collectingDayHours.clear()
    }
}
