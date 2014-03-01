package com.github.debop4s.timeperiod.timerange

import com.github.debop4s.timeperiod._
import com.github.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime
import scala.collection.mutable.ArrayBuffer

/**
 * com.github.debop4s.timeperiod.timerange.MinuteRangeCollection
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 28. 오후 9:00
 */
@SerialVersionUID(-5566298718095890768L)
class MinuteRangeCollection(private[this] val _moment: DateTime,
                            private[this] val _minuteCount: Int,
                            private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
    extends MinuteTimeRange(_moment, _minuteCount, _calendar) {


    lazy val minutes: Seq[MinuteRange] = getMinutes

    def getMinutes: Seq[MinuteRange] = {
        val minutes = ArrayBuffer[MinuteRange]()
        val startMin = Times.trimToSecond(start)

        for (m <- 0 until minuteCount) {
            minutes += new MinuteRange(startMin.plusMinutes(m), calendar)
        }
        minutes
    }
}

object MinuteRangeCollection {

    def apply(moment: DateTime, minuteCount: Int): MinuteRangeCollection = {
        apply(moment, minuteCount, DefaultTimeCalendar)
    }

    def apply(moment: DateTime, minuteCount: Int, calendar: ITimeCalendar): MinuteRangeCollection = {
        new MinuteRangeCollection(moment, minuteCount, calendar)
    }

    def apply(year: Int,
              monthOfYear: Int,
              dayOfMonth: Int,
              hourOfDay: Int,
              minuteOfHour: Int,
              minuteCount: Int): MinuteRangeCollection = {
        apply(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, minuteCount, DefaultTimeCalendar)
    }

    def apply(year: Int,
              monthOfYear: Int,
              dayOfMonth: Int,
              hourOfDay: Int,
              minuteOfHour: Int,
              minuteCount: Int,
              calendar: ITimeCalendar): MinuteRangeCollection = {
        new MinuteRangeCollection(new DateTime(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour),
                                     minuteCount,
                                     calendar)
    }


}
