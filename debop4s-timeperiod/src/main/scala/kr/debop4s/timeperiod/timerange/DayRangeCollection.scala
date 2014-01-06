package kr.debop4s.timeperiod.timerange

import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime
import scala.collection.mutable.ArrayBuffer

/**
 * kr.debop4s.timeperiod.timerange.DayRangeCollection
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 28. 오후 10:45
 */
class DayRangeCollection(private[this] val _moment: DateTime,
                         private[this] val _dayCount: Int,
                         private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
    extends DayTimeRange(Times.asDate(_moment), _dayCount, _calendar) {

    def this(year: Int, monthOfYear: Int, dayOfMonth: Int, dayCount: Int, calendar: ITimeCalendar) {
        this(Times.asDate(year, monthOfYear, dayOfMonth), dayCount, calendar)
    }

    def this(year: Int, monthOfYear: Int, dayOfMonth: Int, dayCount: Int) {
        this(year, monthOfYear, dayOfMonth, dayCount, DefaultTimeCalendar)
    }

    def getDays: Seq[DayRange] = {
        val days = ArrayBuffer[DayRange]()
        val startDay = Times.asDate(getStart)
        (0 until dayCount).foreach(d => days += new DayRange(startDay.plusDays(d), calendar))
        days
    }
}
