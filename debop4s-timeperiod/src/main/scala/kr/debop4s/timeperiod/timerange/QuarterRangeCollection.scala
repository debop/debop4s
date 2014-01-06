package kr.debop4s.timeperiod.timerange

import kr.debop4s.timeperiod.Quarter.Quarter
import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime
import scala.collection.mutable.ArrayBuffer

/**
 * kr.debop4s.timeperiod.timerange.QuarterRangeCollection
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 29. 오후 5:41
 */
@SerialVersionUID(-1191375103809489196L)
class QuarterRangeCollection(private val _year: Int,
                             private val _quarter: Quarter,
                             private val _quarterCount: Int,
                             private val _calendar: ITimeCalendar = DefaultTimeCalendar)
    extends QuarterTimeRange(_year, _quarter, _quarterCount, _calendar) {

    def this(moment: DateTime, quarterCount: Int, calendar: ITimeCalendar) {
        this(moment.getYear, Times.getQuarterOfMonth(moment.getMonthOfYear), quarterCount, calendar)
    }

    def this(moment: DateTime, quarterCount: Int) {
        this(moment.getYear, Times.getQuarterOfMonth(moment.getMonthOfYear), quarterCount, DefaultTimeCalendar)
    }

    def getQuarters: Seq[QuarterRange] = {
        val quarters = ArrayBuffer[QuarterRange]()
        for (q <- 0 until quarterCount) {
            quarters += new QuarterRange(start.plusMonths(q * MonthsPerQuarter), calendar)
        }
        quarters
    }
}
