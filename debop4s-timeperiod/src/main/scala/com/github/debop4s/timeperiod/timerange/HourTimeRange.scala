package com.github.debop4s.timeperiod.timerange

import com.github.debop4s.timeperiod._
import com.github.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime
import scala.collection.mutable.ArrayBuffer

/**
 * com.github.debop4s.timeperiod.timerange.HourTimeRange
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 28. 오후 9:32
 */
class HourTimeRange(private[this] val _moment: DateTime,
                    val hourCount: Int = 1,
                    private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
    extends CalendarTimeRange(Times.relativeHourPeriod(_moment, hourCount), _calendar) {

    val endHour: Int = start.plusHours(hourCount).getHourOfDay

    @inline
    def getMinutes: Seq[MinuteRange] = {
        val minutes = new ArrayBuffer[MinuteRange](hourCount * MinutesPerHour)

        for (h <- 0 until hourCount) {
            for (m <- 0 until MinutesPerHour) {
                minutes += MinuteRange(start.plusHours(h).plusMinutes(m), calendar)
            }
        }

        minutes
    }
}

object HourTimeRange {

    def apply(moment: DateTime, hourCount: Int): HourTimeRange =
        apply(moment, hourCount, DefaultTimeCalendar)

    def apply(moment: DateTime, hourCount: Int, calendar: ITimeCalendar): HourTimeRange =
        new HourTimeRange(moment, hourCount, calendar)
}
