package com.github.debop4s.timeperiod.tests.timeranges

import com.github.debop4s.timeperiod._
import com.github.debop4s.timeperiod.tests.AbstractTimePeriodTest
import com.github.debop4s.timeperiod.timerange.{MonthRange, MonthRangeCollection}
import com.github.debop4s.timeperiod.utils.Times

/**
 * MonthRangeCollectionTest
 * Created by debop on 2014. 2. 16.
 */
class MonthRangeCollectionTest extends AbstractTimePeriodTest {

    test("single month") {
        val startYear = 2004
        val startMonth = 6

        val mrs = MonthRangeCollection(startYear, startMonth, 1)
        mrs.monthCount should equal(1)

        val months = mrs.getMonths
        months.size should equal(1)
        months(0).isSamePeriod(new MonthRange(startYear, startMonth)) should equal(true)

        mrs.startYear should equal(startYear)
        mrs.endYear should equal(startYear)
        mrs.startMonthOfYear should equal(startMonth)
        mrs.endMonthOfYear should equal(startMonth)
    }

    test("calendar months") {
        val startYear = 2004
        val startMonth = 11
        val monthCount = 5

        val mrs = MonthRangeCollection(startYear, startMonth, monthCount)

        mrs.monthCount should equal(monthCount)
    }

    test("month counts") {
        val monthCounts = Array(1, 6, 48, 180, 360)

        val now = Times.now
        val today = Times.today

        monthCounts.par.foreach(m => {
            val mrs = MonthRangeCollection(now, m)
            val startTime = mrs.calendar.mapStart(Times.trimToDay(today))
            val endTime = mrs.calendar.mapEnd(startTime + m.month)

            mrs.start should equal(startTime)
            mrs.end should equal(endTime)

            val items = mrs.getMonths

            for (i <- 0 until m) {
                val item = items(i)

                item.start should equal(startTime + i.month)
                item.end should equal(mrs.calendar.mapEnd(startTime + (i + 1).month))

                item.unmappedStart should equal(startTime + i.month)
                item.unmappedEnd should equal(startTime + (i + 1).month)

                item.isSamePeriod(MonthRange(mrs.start + i.month)) should equal(true)

                val ym = Times.addMonth(now.getYear, now.getMonthOfYear, i)
                item.isSamePeriod(MonthRange(ym.year, ym.monthOfYear)) should equal(true)
            }
        })
    }
}
