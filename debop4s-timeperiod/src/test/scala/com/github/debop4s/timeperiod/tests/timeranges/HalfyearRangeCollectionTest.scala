package com.github.debop4s.timeperiod.tests.timeranges

import com.github.debop4s.timeperiod._
import com.github.debop4s.timeperiod.tests.AbstractTimePeriodTest
import com.github.debop4s.timeperiod.timerange.{HalfyearRange, HalfyearRangeCollection}
import com.github.debop4s.timeperiod.utils.Times
import com.github.debop4s.timeperiod.utils.Times._

/**
 * HalfyearRangeCollectionTest
 * Created by debop on 2014. 2. 15.
 */
class HalfyearRangeCollectionTest extends AbstractTimePeriodTest {

    test("yearBaseMonth") {
        val moment = Times.asDate(2009, 2, 15)
        val year = yearOf(moment.getYear, moment.getMonthOfYear)
        val halfyears = HalfyearRangeCollection(moment, 3)

        halfyears.start should equal(asDate(year, 1, 1))
    }

    test("single Halfyear") {
        val startYear = 2004
        val startHalfyear = Halfyear.Second

        val halfyears = HalfyearRangeCollection(startYear, startHalfyear, 1)

        halfyears.halfyearCount should equal(1)
        halfyears.startHalfyear should equal(startHalfyear)

        halfyears.endYear should equal(startYear)
        halfyears.endHalfyear should equal(startHalfyear)

        val halfyearList = halfyears.halfyears
        halfyearList.size should equal(1)
        halfyearList.head.isSamePeriod(new HalfyearRange(startYear, startHalfyear)) should equal(true)

        val halfyearView = halfyears.halfyearsView
        halfyearView.size should equal(1)
        halfyearView.head.isSamePeriod(new HalfyearRange(startYear, startHalfyear)) should equal(true)
    }

    test("first calendar halfyears") {
        val startYear = 2004
        val startHalfyear = Halfyear.First
        val halfyearCount = 3

        val halfyears = HalfyearRangeCollection(startYear, startHalfyear, halfyearCount)

        halfyears.halfyearCount should equal(halfyearCount)
        halfyears.startHalfyear should equal(startHalfyear)
        halfyears.endYear should equal(startYear + 1)
        halfyears.endHalfyear should equal(Halfyear.First)

        val halfyearList = halfyears.halfyears

        halfyearList.size should equal(halfyearCount)
        halfyearList(0).isSamePeriod(HalfyearRange(startYear, Halfyear.First)) should equal(true)
        halfyearList(1).isSamePeriod(HalfyearRange(startYear, Halfyear.Second)) should equal(true)
        halfyearList(2).isSamePeriod(HalfyearRange(startYear + 1, Halfyear.First)) should equal(true)

        val halfyearView = halfyears.halfyearsView

        halfyearView.size should equal(halfyearCount)
        halfyearView(0).isSamePeriod(HalfyearRange(startYear, Halfyear.First)) should equal(true)
        halfyearView(1).isSamePeriod(HalfyearRange(startYear, Halfyear.Second)) should equal(true)
        halfyearView(2).isSamePeriod(HalfyearRange(startYear + 1, Halfyear.First)) should equal(true)
    }

    test("secondCalendarHalfyears") {
        val startYear = 2004
        val startHalfyear = Halfyear.Second
        val halfyearCount = 3

        val halfyears = HalfyearRangeCollection(startYear, startHalfyear, halfyearCount)

        halfyears.halfyearCount should equal(halfyearCount)
        halfyears.startHalfyear should equal(startHalfyear)
        halfyears.endYear should equal(startYear + 1)
        halfyears.endHalfyear should equal(Halfyear.Second)

        val halfyearList = halfyears.halfyears

        halfyearList.size should equal(halfyearCount)
        halfyearList(0).isSamePeriod(HalfyearRange(startYear, Halfyear.Second)) should equal(true)
        halfyearList(1).isSamePeriod(HalfyearRange(startYear + 1, Halfyear.First)) should equal(true)
        halfyearList(2).isSamePeriod(HalfyearRange(startYear + 1, Halfyear.Second)) should equal(true)
    }
}
