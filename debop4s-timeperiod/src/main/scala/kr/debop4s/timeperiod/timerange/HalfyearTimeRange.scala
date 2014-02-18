package kr.debop4s.timeperiod.timerange

import kr.debop4s.core.utils.Hashs
import kr.debop4s.timeperiod.Halfyear.Halfyear
import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime
import scala.collection.mutable.ArrayBuffer

/**
 * kr.debop4s.timeperiod.timerange.HalfyearTimeRange
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 29. 오후 5:49
 */
object HalfyearTimeRange {
    def apply(moment: DateTime, halfyearCount: Int, calendar: ITimeCalendar): HalfyearTimeRange = {
        new HalfyearTimeRange(moment.getYear,
            Times.getHalfyearOfMonth(moment.getMonthOfYear),
            halfyearCount,
            calendar)
    }

    def apply(moment: DateTime, halfyearCount: Int): HalfyearTimeRange = {
        new HalfyearTimeRange(moment.getYear,
            Times.getHalfyearOfMonth(moment.getMonthOfYear),
            halfyearCount,
            DefaultTimeCalendar)
    }

    def getPeriodOf(year: Int, halfyear: Halfyear, halfyearCount: Int): TimeRange = {
        val yearStart = Times.startTimeOfYear(year)
        val start = yearStart.plusMonths((halfyear.id - 1) * MonthsPerHalfyear)
        val end = start.plusMonths(halfyearCount * MonthsPerHalfyear)
        TimeRange(start, end)
    }
}

class HalfyearTimeRange(val year: Int,
                        val halfyear: Halfyear,
                        val halfyearCount: Int,
                        private val _calendar: ITimeCalendar = DefaultTimeCalendar)
    extends CalendarTimeRange(HalfyearTimeRange.getPeriodOf(year, halfyear, halfyearCount),
        _calendar) {

    val startHalfyear: Halfyear = halfyear
    val endHalfyear: Halfyear = Times.getHalfyearOfMonth(endMonthOfYear)

    def isMultipleCalendarYears: Boolean = startYear != endYear

    def getQuarters: Seq[QuarterRange] = {
        val quarterCount = halfyearCount * QuartersPerHalfyear
        val startQuarter = Times.quarterOf(startMonthOfYear)

        val quarters = ArrayBuffer[QuarterRange]()
        for (q <- 0 until quarterCount) {
            val yq = Times.addQuarter(startYear, startQuarter, q)
            quarters += new QuarterRange(yq.year, yq.quarter, calendar)
        }
        quarters
    }

    def getMonths: Seq[MonthRange] = {
        val monthCount = halfyearCount * MonthsPerHalfyear
        val months = ArrayBuffer[MonthRange]()

        for (m <- 0 until monthCount) {
            val ym = Times.addMonth(startYear, startMonthOfYear, m)
            months += new MonthRange(ym.year, ym.monthOfYear, calendar)
        }
        months
    }

    override def hashCode() = Hashs.compute(startYear, startHalfyear, endYear, endHalfyear)

    override protected def buildStringHelper =
        super.buildStringHelper
            .add("startYear", startYear)
            .add("startHalfyear", startHalfyear)
            .add("endYear", endYear)
            .add("endHalfyear", endHalfyear)
            .add("halfyearCount", halfyearCount)
}
