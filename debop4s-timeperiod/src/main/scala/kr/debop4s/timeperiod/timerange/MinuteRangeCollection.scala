package kr.debop4s.timeperiod.timerange

import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime
import scala.collection.mutable.ArrayBuffer

/**
 * kr.debop4s.timeperiod.timerange.MinuteRangeCollection
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 28. 오후 9:00
 */
@SerialVersionUID(-5566298718095890768L)
class MinuteRangeCollection(private val _moment: DateTime,
                            private val _minuteCount: Int,
                            private val _calendar: ITimeCalendar = DefaultTimeCalendar)
    extends MinuteTimeRange(_moment, _minuteCount, _calendar) {

    def this(year: Int,
             monthOfYear: Int,
             dayOfMonth: Int,
             hourOfDay: Int,
             minuteOfHour: Int,
             minuteCount: Int,
             calendar: ITimeCalendar) {
        this(new DateTime(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour), minuteCount, calendar)
    }

    def this(year: Int,
             monthOfYear: Int,
             dayOfMonth: Int,
             hourOfDay: Int,
             minuteOfHour: Int,
             minuteCount: Int) {
        this(new DateTime(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour), minuteCount, DefaultTimeCalendar)
    }

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
