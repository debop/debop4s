package kr.debop4s.timeperiod.timerange

import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime
import scala.collection.mutable.ArrayBuffer

/**
 * kr.debop4s.timeperiod.timerange.HourCollectionRange
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 28. 오후 10:06
 */
@SerialVersionUID(8973240176036662074L)
class HourRangeCollection(private val _moment: DateTime,
                          private val _hourCount: Int,
                          private val _calendar: ITimeCalendar = DefaultTimeCalendar)
    extends HourTimeRange(Times.trimToSecond(_moment), _hourCount, _calendar) {

    def this(year: Int, monthOfYear: Int, dayOfMonth: Int, hourOfDay: Int, hourCount: Int, calendar: ITimeCalendar) {
        this(new DateTime(year, monthOfYear, dayOfMonth, hourOfDay), hourCount, calendar)
    }

    def this(year: Int, monthOfYear: Int, dayOfMonth: Int, hourOfDay: Int, hourCount: Int) {
        this(new DateTime(year, monthOfYear, dayOfMonth, hourOfDay), hourCount, DefaultTimeCalendar)
    }

    def getHours: Seq[HourRange] = {
        val startHour = Times.trimToMinute(getStart)

        val hours = ArrayBuffer[HourRange]()
        for (h <- 0 until hourCount) {
            hours += new HourRange(startHour.plusHours(h), calendar)
        }
        hours
    }
}
