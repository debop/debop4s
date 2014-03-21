package com.github.debop4s.timeperiod.timerange

import com.github.debop4s.timeperiod.Quarter.Quarter
import com.github.debop4s.timeperiod._
import com.github.debop4s.timeperiod.utils.Times

/**
 * com.github.debop4s.timeperiod.timerange.QuarterTimeRange
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 29. 오후 4:54
 */
@SerialVersionUID(-1642725884160403253L)
abstract class QuarterTimeRange(private val _year: Int,
                                private val _quarter: Quarter,
                                val quarterCount: Int,
                                private val _calendar: ITimeCalendar = DefaultTimeCalendar)
    extends CalendarTimeRange(QuarterTimeRange.getPeriodOf(_year, _quarter, quarterCount, _calendar),
        _calendar) {

    val startQuarter: Quarter = _quarter
    val endQuarter: Quarter = Times.quarterOfMonth(end.getMonthOfYear)

    override def startMonthOfYear: Int = Times.startMonthOfQuarter(startQuarter)

    override def endMonthOfYear: Int = Times.endMonthOfQuarter(endQuarter)

    def isMultipleCalendarYears: Boolean =
        startYear != endYear

    @inline
    def months = {
        val monthCount = quarterCount * MonthsPerQuarter

        (0 until monthCount).view.map { m =>
            MonthRange(start.plusMonths(m), calendar)
        }
    }
}

object QuarterTimeRange {

    private[timerange] def getPeriodOf(year: Int,
                                       quarter: Quarter,
                                       quarterCount: Int,
                                       calendar: ITimeCalendar): ITimePeriod = {
        require(quarterCount > 0)

        val yearStart = Times.asDate(year, 1, 1)
        val start = yearStart.plusMonths((quarter.id - 1) * MonthsPerQuarter)
        val end = start.plusMonths(quarterCount * MonthsPerQuarter)

        TimeRange(start, end)
    }
}