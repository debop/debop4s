package kr.debop4s.timeperiod.timerange

import kr.debop4s.timeperiod._
import org.joda.time.DateTime
import scala.collection.mutable.ArrayBuffer

/**
 * kr.debop4s.timeperiod.timerange.MonthRangeCollection
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 29. 오후 4:51
 */
@SerialVersionUID(-3955343194292107018L)
class MonthRangeCollection(private[this] val _year: Int,
                           private[this] val _monthOfYear: Int,
                           private[this] val _monthCount: Int,
                           private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
    extends MonthTimeRange(_year, _monthOfYear, _monthCount, _calendar) {

    def this(moment: DateTime, monthCount: Int, calendar: ITimeCalendar) {
        this(moment.getYear, moment.getMonthOfYear, monthCount, calendar)
    }

    def this(moment: DateTime, monthCount: Int) {
        this(moment.getYear, moment.getMonthOfYear, monthCount, DefaultTimeCalendar)
    }

    def getMonths: Seq[MonthRange] = {
        val months = ArrayBuffer[MonthRange]()
        for (m <- 0 until monthCount) {
            months += new MonthRange(getStart.plusMonths(m), calendar)
        }
        months
    }
}
