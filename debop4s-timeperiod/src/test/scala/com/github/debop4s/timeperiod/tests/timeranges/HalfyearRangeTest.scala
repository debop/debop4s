package com.github.debop4s.timeperiod.tests.timeranges

import com.github.debop4s.core.parallels.Parallels
import com.github.debop4s.timeperiod._
import com.github.debop4s.timeperiod.tests.AbstractTimePeriodTest
import com.github.debop4s.timeperiod.timerange.HalfyearRange
import com.github.debop4s.timeperiod.utils.Times
import com.github.debop4s.timeperiod.utils.Times._

/**
 * HalfyearRangeTest
 * Created by debop on 2014. 2. 16.
 */
class HalfyearRangeTest extends AbstractTimePeriodTest {

    val now = Times.now
    val currentYear = now.getYear
    val calendar = EmptyOffsetTimeCalendar

    test("init values") {

        val firstHalfyear = startTimeOfHalfyear(now.getYear, Halfyear.First)
        val secondHalfyear = startTimeOfHalfyear(now.getYear, Halfyear.Second)

        val hyr = HalfyearRange(now.getYear, Halfyear.First, EmptyOffsetTimeCalendar)

        hyr.start.year should equal(firstHalfyear.year)
        hyr.start.getMonthOfYear should equal(firstHalfyear.getMonthOfYear)
        hyr.start.getDayOfMonth should equal(firstHalfyear.getDayOfMonth)
        hyr.start.getHourOfDay should equal(0)
        hyr.start.getMinuteOfHour should equal(0)
        hyr.start.getSecondOfMinute should equal(0)
        hyr.start.getMillisOfSecond should equal(0)

        hyr.end.year should equal(secondHalfyear.year)
        hyr.end.getMonthOfYear should equal(secondHalfyear.getMonthOfYear)
        hyr.end.getDayOfMonth should equal(secondHalfyear.getDayOfMonth)
        hyr.end.getHourOfDay should equal(0)
        hyr.end.getMinuteOfHour should equal(0)
        hyr.end.getSecondOfMinute should equal(0)
        hyr.end.getMillisOfSecond should equal(0)
    }

    test("default calendar") {
        val yearStart = startTimeOfYear(currentYear)
        for (halfyear <- Halfyear.values) {
            val offset = halfyear.id - 1
            val hyr = HalfyearRange(yearStart.plusMonths(MonthsPerHalfyear * offset))

            hyr.start should equal(hyr.calendar.mapStart(yearStart.plusMonths(MonthsPerHalfyear * offset)))
            hyr.end should equal(hyr.calendar.mapEnd(yearStart.plusMonths(MonthsPerHalfyear * (offset + 1))))
        }
    }

    test("moment") {
        HalfyearRange().halfyear should equal(if (now.getMonthOfYear < 7) Halfyear.First else Halfyear.Second)

        Parallels.runAction1(MonthsPerYear)(m => {
            val month = m + 1
            HalfyearRange(currentYear, Halfyear.First).startMonthOfYear should equal(1)
            HalfyearRange(currentYear, Halfyear.Second).startMonthOfYear should equal(7)
        })
    }

    test("multiple calendar years") {
        val currentYear = now.getYear
        HalfyearRange(currentYear, Halfyear.First).isMultipleCalendarYears should equal(false)
    }

    test("calendar halfyear") {

        val h1 = HalfyearRange(currentYear, Halfyear.First, calendar)

        h1.isReadonly should equal(true)
        h1.halfyear should equal(Halfyear.First)
        h1.start should equal(asDate(currentYear, 1, 1))
        h1.end should equal(asDate(currentYear, 7, 1))

        val h2 = HalfyearRange(currentYear, Halfyear.Second, calendar)

        h2.isReadonly should equal(true)
        h2.halfyear should equal(Halfyear.Second)
        h2.start should equal(asDate(currentYear, 7, 1))
        h2.end should equal(asDate(currentYear + 1, 1, 1))
    }

    test("halfyear quaters") {

        val h1 = HalfyearRange(currentYear, Halfyear.First, calendar)
        val h1quarters = h1.getQuarters

        var h1index = 0
        h1quarters.foreach { qr =>
            log.trace(s"qr=$qr")
            qr.quarter should equal(if (h1index == 0) Quarter.First else Quarter.Second)
            qr.start should equal(h1.start.plusMonths(h1index * MonthsPerQuarter))
            qr.end should equal(h1.calendar.mapEnd(qr.start.plusMonths(MonthsPerQuarter)))
            h1index += 1
        }

        val h2 = HalfyearRange(currentYear, Halfyear.Second, calendar)
        val h2quarters = h2.getQuarters

        var h2index = 0
        h2quarters.foreach { qr =>
            log.trace(s"qr=$qr")
            qr.quarter should equal(if (h2index == 0) Quarter.Third else Quarter.Fourth)
            qr.start should equal(h2.start.plusMonths(h2index * MonthsPerQuarter))
            qr.end should equal(qr.calendar.mapEnd(qr.start.plusMonths(MonthsPerQuarter)))
            h2index += 1
        }
    }

    test("halfyear getMonths") {

        val h1 = HalfyearRange(currentYear, Halfyear.First, calendar)
        val months = h1.getMonths
        months.size should equal(MonthsPerHalfyear)

        var index = 0
        months.foreach { m =>
            m.start should equal(h1.start.plusMonths(index))
            m.end should equal(calendar.mapEnd(m.start.plusMonths(1)))
            index += 1
        }
    }

    test("add halfyears") {

        val h1 = HalfyearRange(currentYear, Halfyear.First, calendar)

        var prevH1 = h1.addHalfyears(-1)
        prevH1.halfyear should equal(Halfyear.Second)
        prevH1.year should equal(currentYear - 1)
        prevH1.start should equal(h1.start.plusMonths(-MonthsPerHalfyear))
        prevH1.end should equal(h1.start)

        prevH1 = h1.addHalfyears(-2)
        prevH1.halfyear should equal(Halfyear.First)
        prevH1.year should equal(currentYear - 1)
        prevH1.start should equal(h1.start.plusMonths(-2 * MonthsPerHalfyear))
        prevH1.end should equal(h1.start.plusMonths(-1 * MonthsPerHalfyear))

        prevH1 = h1.addHalfyears(-3)
        prevH1.halfyear should equal(Halfyear.Second)
        prevH1.year should equal(currentYear - 2)
        prevH1.start should equal(h1.start.plusMonths(-3 * MonthsPerHalfyear))
        prevH1.end should equal(h1.start.plusMonths(-2 * MonthsPerHalfyear))

        var nextH1 = h1.addHalfyears(1)
        nextH1.halfyear should equal(Halfyear.Second)
        nextH1.year should equal(currentYear)
        nextH1.start should equal(h1.start.plusMonths(MonthsPerHalfyear))
        nextH1.end should equal(h1.start.plusMonths(2 * MonthsPerHalfyear))

        nextH1 = h1.addHalfyears(2)
        nextH1.halfyear should equal(Halfyear.First)
        nextH1.year should equal(currentYear + 1)
        nextH1.start should equal(h1.start.plusMonths(2 * MonthsPerHalfyear))
        nextH1.end should equal(h1.start.plusMonths(3 * MonthsPerHalfyear))

        nextH1 = h1.addHalfyears(3)
        nextH1.halfyear should equal(Halfyear.Second)
        nextH1.year should equal(currentYear + 1)
        nextH1.start should equal(h1.start.plusMonths(3 * MonthsPerHalfyear))
        nextH1.end should equal(h1.start.plusMonths(4 * MonthsPerHalfyear))
    }

}
