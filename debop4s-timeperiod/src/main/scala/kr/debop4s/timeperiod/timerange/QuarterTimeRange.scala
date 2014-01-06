package kr.debop4s.timeperiod.timerange

import kr.debop4s.core.utils.Hashs
import kr.debop4s.timeperiod.Quarter.Quarter
import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime
import scala.collection.mutable.ArrayBuffer

/**
 * kr.debop4s.timeperiod.timerange.QuarterTimeRange
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 29. 오후 4:54
 */
@SerialVersionUID(-1642725884160403253L)
class QuarterTimeRange(private val _year: Int,
                       private val _quarter: Quarter,
                       val quarterCount: Int,
                       private val _calendar: ITimeCalendar = DefaultTimeCalendar)
    extends CalendarTimeRange(TimeRange(Times.startTimeOfQuarter(_year, _quarter),
                                        Times.addQuarter(_year, _quarter, quarterCount).end),
                              _calendar) {

    val startQuarter: Quarter = _quarter
    val endQuarter: Quarter = Times.getQuarterOfMonth(end.getMonthOfYear)

    def this(moment: DateTime, quarterCount: Int, calendar: ITimeCalendar) {
        this(moment.getYear, Times.getQuarterOfMonth(moment.getMonthOfYear), quarterCount, calendar)
    }

    def this(moment: DateTime, quarterCount: Int) {
        this(moment.getYear, Times.getQuarterOfMonth(moment.getMonthOfYear), quarterCount, DefaultTimeCalendar)
    }

    override def startMonthOfYear: Int = Times.startMonthOfQuarter(startQuarter)

    override def endMonthOfYear: Int = Times.endMonthOfQuarter(endQuarter)

    def isMultipleCalendarYears: Boolean =
        startYear != endYear

    def getMonths: Seq[MonthRange] = {
        val months = ArrayBuffer[MonthRange]()
        val monthCount = quarterCount * MonthsPerQuarter
        for (m <- 0 until monthCount) {
            months += new MonthRange(getStart.plusMonths(m), calendar)
        }
        months
    }


    override def hashCode() = Hashs.compute(startYear, startQuarter, endYear, endQuarter)

    override protected def buildStringHelper =
        super.buildStringHelper
            .add("startYear", startYear)
            .add("startQuarter", startQuarter)
            .add("endYear", endYear)
            .add("endQuarter", endQuarter)
}
