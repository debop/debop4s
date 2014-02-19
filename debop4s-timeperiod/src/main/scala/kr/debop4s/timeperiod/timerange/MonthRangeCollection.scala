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

    def getMonths: Seq[MonthRange] = {
        val months = ArrayBuffer[MonthRange]()
        for (m <- 0 until monthCount) {
            months += MonthRange(start.plusMonths(m), calendar)
        }
        months
    }
}

object MonthRangeCollection {

    def apply(year: Int, monthOfYear: Int, monthCount: Int): MonthRangeCollection =
        apply(year, monthOfYear, monthCount, DefaultTimeCalendar)

    def apply(year: Int, monthOfYear: Int, monthCount: Int, calendar: ITimeCalendar): MonthRangeCollection =
        new MonthRangeCollection(year, monthOfYear, monthCount, calendar)

    def apply(moment: DateTime, monthCount: Int): MonthRangeCollection = {
        apply(moment, monthCount, DefaultTimeCalendar)
    }

    def apply(moment: DateTime, monthCount: Int, calendar: ITimeCalendar): MonthRangeCollection = {
        new MonthRangeCollection(moment.getYear, moment.getMonthOfYear, monthCount, calendar)
    }

}
