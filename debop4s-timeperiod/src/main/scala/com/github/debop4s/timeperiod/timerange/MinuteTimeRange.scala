package com.github.debop4s.timeperiod.timerange

import com.github.debop4s.timeperiod._
import com.github.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

/**
 * com.github.debop4s.timeperiod.timerange.MinuteTimeRange
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 27. 오후 7:03
 */
@SerialVersionUID(-5669915582907325590L)
class MinuteTimeRange(private[this] val _start: DateTime,
                      val minuteCount: Int = 1,
                      private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
    extends CalendarTimeRange(Times.relativeMinutePeriod(_start, minuteCount), _calendar) {

    assert(minuteCount > 0)

    val endMinute = start.plusMinutes(minuteCount).getMinuteOfHour
}

object MinuteTimeRange {

    def apply(moment: DateTime, minuteCount: Int): MinuteTimeRange =
        apply(moment, minuteCount, DefaultTimeCalendar)

    def apply(moment: DateTime, minuteCount: Int, calendar: ITimeCalendar): MinuteTimeRange =
        new MinuteTimeRange(moment, minuteCount, calendar)

    def apply(year: Int,
              monthOfYear: Int,
              dayOfMonth: Int,
              hourOfDay: Int,
              minuteOfHour: Int,
              minuteCount: Int): MinuteTimeRange =
        apply(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, minuteCount, DefaultTimeCalendar)

    def apply(year: Int,
              monthOfYear: Int,
              dayOfMonth: Int,
              hourOfDay: Int,
              minuteOfHour: Int,
              minuteCount: Int,
              calendar: ITimeCalendar): MinuteTimeRange =
        new MinuteTimeRange(new DateTime(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour), minuteCount, calendar)
}

