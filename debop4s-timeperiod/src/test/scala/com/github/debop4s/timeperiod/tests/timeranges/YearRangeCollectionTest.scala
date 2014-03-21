package com.github.debop4s.timeperiod.tests.timeranges

import com.github.debop4s.timeperiod._
import com.github.debop4s.timeperiod.tests.AbstractTimePeriodTest
import com.github.debop4s.timeperiod.timerange.{YearRange, YearRangeCollection}
import com.github.debop4s.timeperiod.utils.Times

/**
 * com.github.debop4s.timeperiod.tests.timeranges.YearRangeCollectionTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 17. 오전 10:41
 */
class YearRangeCollectionTest extends AbstractTimePeriodTest {

    test("single years") {
        val startYear = 2014

        val yrs = YearRangeCollection(startYear, 1)
        yrs.yearCount should equal(1)
        yrs.startYear should equal(startYear)
        yrs.endYear should equal(startYear)

        val years = yrs.years
        years.size should equal(1)
        years(0).isSamePeriod(YearRange(startYear)) should equal(true)
    }

    test("calendar months") {
        val startYear = 2014
        val yearCount = 5

        val yrs = YearRangeCollection(startYear, yearCount, EmptyOffsetTimeCalendar)
        yrs.yearCount should equal(yearCount)
        yrs.startYear should equal(startYear)
        yrs.endYear should equal(startYear + yearCount)
    }

    test("year counts") {
        val yearCounts = Array(1, 6, 12, 48, 180, 360)
        val now = Times.now
        val today = Times.today

        yearCounts.foreach { yearCount =>
            val yearRanges = YearRangeCollection(now, yearCount)
            val startTime = yearRanges.calendar.mapStart(Times.trimToYear(today))
            val endTime = yearRanges.calendar.mapEnd(startTime + yearCount.year)

            yearRanges.start should equal(startTime)
            yearRanges.end should equal(endTime)

            val items = yearRanges.years

            (0 until yearCount).par.foreach { y =>
                val item = items(y)

                item.start should equal(startTime + y.year)
                item.end should equal(yearRanges.calendar.mapEnd(startTime + (y + 1).year))

                item.unmappedStart should equal(startTime + y.year)
                item.unmappedEnd should equal(startTime + (y + 1).year)

                item.isSamePeriod(YearRange(yearRanges.start + y.year)) should equal(true)
            }
        }
    }

}
