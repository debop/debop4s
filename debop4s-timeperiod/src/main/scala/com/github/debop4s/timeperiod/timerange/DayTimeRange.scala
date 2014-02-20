package com.github.debop4s.timeperiod.timerange

import com.github.debop4s.core.utils.ToStringHelper
import com.github.debop4s.timeperiod.DayOfWeek.DayOfWeek
import com.github.debop4s.timeperiod._
import com.github.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime
import scala.collection.mutable.ArrayBuffer

/**
 * com.github.debop4s.timeperiod.timerange.DayTimeRange
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 28. 오후 10:27
 */
class DayTimeRange(private[this] val _start: DateTime,
                   val dayCount: Int,
                   private[this] var _calendar: ITimeCalendar = DefaultTimeCalendar)
    extends CalendarTimeRange(Times.relativeDayPeriod(_start, dayCount), _calendar) {

    def startDayOfWeek: DayOfWeek = calendar.getDayOfWeek(start)

    def endDayOfWeek: DayOfWeek = calendar.getDayOfWeek(end)

    def getHours: Seq[HourRange] = {
        val day = startDayStart
        val hours = ArrayBuffer[HourRange]()

        for (d <- 0 until dayCount) {
            for (h <- 0 until HoursPerDay) {
                hours += new HourRange(day.plusHours(d * HoursPerDay + h), calendar)
            }
        }
        hours
    }
}

object DayTimeRange {

    def apply(moment: DateTime, dayCount: Int): DayTimeRange =
        apply(moment, dayCount, DefaultTimeCalendar)

    def apply(moment: DateTime, dayCount: Int, calendar: ITimeCalendar): DayTimeRange =
        new DayTimeRange(moment, dayCount, calendar)

    def apply(year: Int, monthOfYear: Int, dayOfMonth: Int, dayCount: Int): DayTimeRange =
        apply(year, monthOfYear, dayOfMonth, dayCount, DefaultTimeCalendar)

    def apply(year: Int, monthOfYear: Int, dayOfMonth: Int, dayCount: Int, calendar: ITimeCalendar): DayTimeRange =
        new DayTimeRange(Times.asDate(year, monthOfYear, dayOfMonth), dayCount, calendar)

}
