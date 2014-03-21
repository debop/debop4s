package com.github.debop4s.timeperiod.timerange

import com.github.debop4s.timeperiod.Quarter.Quarter
import com.github.debop4s.timeperiod._
import com.github.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime
import scala.collection.SeqView

/**
 * com.github.debop4s.timeperiod.timerange.QuarterRangeCollection
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 29. 오후 5:41
 */
@SerialVersionUID(-1191375103809489196L)
class QuarterRangeCollection(private val _year: Int,
                             private val _quarter: Quarter,
                             private val _quarterCount: Int,
                             private val _calendar: ITimeCalendar = DefaultTimeCalendar)
    extends QuarterTimeRange(_year, _quarter, _quarterCount, _calendar) {

    @inline
    def quarters: SeqView[QuarterRange, Seq[_]] = {
        (0 until quarterCount).view.map { q =>
            QuarterRange(start.plusMonths(q * MonthsPerQuarter), calendar)
        }
    }
}

object QuarterRangeCollection {

    def apply(year: Int, quarter: Quarter, quarterCount: Int): QuarterRangeCollection =
        apply(year, quarter, quarterCount, DefaultTimeCalendar)

    def apply(year: Int, quarter: Quarter, quarterCount: Int, calendar: ITimeCalendar): QuarterRangeCollection =
        new QuarterRangeCollection(year, quarter, quarterCount, calendar)

    def apply(moment: DateTime, quarterCount: Int): QuarterRangeCollection =
        apply(moment, quarterCount, DefaultTimeCalendar)

    def apply(moment: DateTime, quarterCount: Int, calendar: ITimeCalendar): QuarterRangeCollection =
        new QuarterRangeCollection(moment.getYear, Times.quarterOfMonth(moment.getMonthOfYear), quarterCount, calendar)


}
