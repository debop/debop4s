package com.github.debop4s.timeperiod.tests.timeranges


import com.github.debop4s.timeperiod.Quarter
import com.github.debop4s.timeperiod.tests.AbstractTimePeriodTest
import com.github.debop4s.timeperiod.timerange.{QuarterRange, QuarterRangeCollection}
import com.github.debop4s.timeperiod.utils.Times._

/**
 * QuarterRangeCollectionTest
 * Created by debop on 2014. 2. 16.
 */
class QuarterRangeCollectionTest extends AbstractTimePeriodTest {

    test("year test") {
        val moment = asDate(2009, 2, 15)
        val year = yearOf(moment.getYear, moment.getMonthOfYear)
        val qr = QuarterRangeCollection(moment, 3)

        qr.startYear should equal(year)
        qr.start should equal(asDate(year, 1, 1))
    }

    test("single quarter") {
        val startYear = 2004
        val startQuarter = Quarter.Second

        val qr = QuarterRangeCollection(startYear, startQuarter, 1)

        qr.quarterCount should equal(1)
        qr.startYear should equal(startYear)
        qr.startQuarter should equal(startQuarter)
        qr.endYear should equal(startYear)
        qr.endQuarter should equal(startQuarter)

        val quarters = qr.getQuarters
        quarters.size should equal(1)
        quarters(0).isSamePeriod(QuarterRange(startYear, startQuarter)) should equal(true)
    }

    test("first calendar halfyears") {
        val startYear = 2004
        val startQuarter = Quarter.First
        val quarterCount = 5

        val qrs = QuarterRangeCollection(startYear, startQuarter, quarterCount)

        qrs.quarterCount should equal(quarterCount)
        qrs.startYear should equal(startYear)
        qrs.startQuarter should equal(startQuarter)
        qrs.endYear should equal(startYear + 1)
        qrs.endQuarter should equal(Quarter.First)

        val quarters = qrs.getQuarters

        quarters.size should equal(quarterCount)
        quarters(0).isSamePeriod(QuarterRange(startYear, Quarter.First)) should equal(true)
        quarters(1).isSamePeriod(QuarterRange(startYear, Quarter.Second)) should equal(true)
        quarters(2).isSamePeriod(QuarterRange(startYear, Quarter.Third)) should equal(true)
        quarters(3).isSamePeriod(QuarterRange(startYear, Quarter.Fourth)) should equal(true)
        quarters(4).isSamePeriod(QuarterRange(startYear + 1, Quarter.First)) should equal(true)
    }

    test("second calendar halfyears") {
        val startYear = 2004
        val startQuarter = Quarter.Second
        val quarterCount = 5

        val qrs = QuarterRangeCollection(startYear, startQuarter, quarterCount)

        qrs.quarterCount should equal(quarterCount)
        qrs.startYear should equal(startYear)
        qrs.startQuarter should equal(startQuarter)
        qrs.endYear should equal(startYear + 1)
        qrs.endQuarter should equal(Quarter.Second)

        val quarters = qrs.getQuarters

        quarters.size should equal(quarterCount)
        quarters(0).isSamePeriod(QuarterRange(startYear, Quarter.Second)) should equal(true)
        quarters(1).isSamePeriod(QuarterRange(startYear, Quarter.Third)) should equal(true)
        quarters(2).isSamePeriod(QuarterRange(startYear, Quarter.Fourth)) should equal(true)
        quarters(3).isSamePeriod(QuarterRange(startYear + 1, Quarter.First)) should equal(true)
        quarters(4).isSamePeriod(QuarterRange(startYear + 1, Quarter.Second)) should equal(true)
    }

}
