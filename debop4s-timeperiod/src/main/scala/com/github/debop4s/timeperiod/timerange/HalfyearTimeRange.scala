package com.github.debop4s.timeperiod.timerange

import com.github.debop4s.timeperiod.Halfyear.Halfyear
import com.github.debop4s.timeperiod._
import com.github.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime
import scala.collection.mutable.ArrayBuffer

/**
 * com.github.debop4s.timeperiod.timerange.HalfyearTimeRange
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 29. 오후 5:49
 */
object HalfyearTimeRange {
    def apply(moment: DateTime, halfyearCount: Int, calendar: ITimeCalendar): HalfyearTimeRange = {
        new HalfyearTimeRange(moment.getYear,
            Times.halfyearOfMonth(moment.getMonthOfYear),
            halfyearCount,
            calendar)
    }

    def apply(moment: DateTime, halfyearCount: Int): HalfyearTimeRange = {
        new HalfyearTimeRange(moment.getYear,
            Times.halfyearOfMonth(moment.getMonthOfYear),
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
                        private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
    extends CalendarTimeRange(HalfyearTimeRange.getPeriodOf(year, halfyear, halfyearCount),
        _calendar) {

    val startHalfyear: Halfyear = halfyear
    val endHalfyear: Halfyear = Times.halfyearOfMonth(endMonthOfYear)

    def isMultipleCalendarYears: Boolean = startYear != endYear

    @inline
    def getQuarters: Seq[QuarterRange] = {
        val quarterCount = halfyearCount * QuartersPerHalfyear
        val startQuarter = Times.quarterOf(startMonthOfYear)

        val quarters = new ArrayBuffer[QuarterRange](quarterCount)
        for (q <- 0 until quarterCount) {
            val yq = Times.addQuarter(startYear, startQuarter, q)
            quarters += new QuarterRange(yq.year, yq.quarter, calendar)
        }
        quarters
    }

    @inline
    def getMonths: Seq[MonthRange] = {
        val monthCount = halfyearCount * MonthsPerHalfyear
        val months = new ArrayBuffer[MonthRange](monthCount)

        for (m <- 0 until monthCount) {
            val ym = Times.addMonth(startYear, startMonthOfYear, m)
            months += new MonthRange(ym.year, ym.monthOfYear, calendar)
        }
        months
    }
}
