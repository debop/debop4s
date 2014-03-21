package com.github.debop4s.timeperiod.tests.timeranges

import com.github.debop4s.timeperiod._
import com.github.debop4s.timeperiod.tests.AbstractTimePeriodTest
import com.github.debop4s.timeperiod.timerange.{HourRange, MinuteRange}
import com.github.debop4s.timeperiod.utils.Times

/**
 * MinuteRangeTest
 * Created by debop on 2014. 2. 16.
 */
class MinuteRangeTest extends AbstractTimePeriodTest {

    test("init values") {
        val now = Times.now
        val firstMin = Times.trimToSecond(now)
        val secondMin = firstMin + 1.minute

        val minRange = MinuteRange(now, EmptyOffsetTimeCalendar)

        minRange.start.getYear should equal(firstMin.getYear)
        minRange.start.getMonthOfYear should equal(firstMin.getMonthOfYear)
        minRange.start.getDayOfMonth should equal(firstMin.getDayOfMonth)
        minRange.start.getHourOfDay should equal(firstMin.getHourOfDay)
        minRange.start.getMinuteOfHour should equal(firstMin.getMinuteOfHour)
        minRange.start.getSecondOfMinute should equal(0)
        minRange.start.getMillisOfSecond should equal(0)
    }

    test("default calendar") {
        val now = Times.now
        val today = Times.today

        (0 until MinutesPerHour).par.foreach { m =>
            val mr = MinuteRange(today + m.minute)

            mr.year should equal(today.getYear)
            mr.monthOfYear should equal(today.getMonthOfYear)
            mr.dayOfMonth should equal(today.getDayOfMonth)
            mr.hourOfDay should equal(today.getHourOfDay)
            mr.minuteOfHour should equal(m)
            mr.start should equal(mr.calendar.mapStart(today + m.minute))
            mr.end should equal(mr.calendar.mapEnd(today + (m + 1).minute))
        }
    }

    test("constructor") {
        val now = Times.now

        val mr = MinuteRange(now)
        mr.year should equal(now.getYear)
        mr.monthOfYear should equal(now.getMonthOfYear)
        mr.dayOfMonth should equal(now.getDayOfMonth)
        mr.hourOfDay should equal(now.getHourOfDay)
        mr.minuteOfHour should equal(now.getMinuteOfHour)

        val mr2 = MinuteRange(now.getYear, now.getMonthOfYear, now.getDayOfMonth, now.getHourOfDay, now.getMinuteOfHour)
        mr2.year should equal(now.getYear)
        mr2.monthOfYear should equal(now.getMonthOfYear)
        mr2.dayOfMonth should equal(now.getDayOfMonth)
        mr2.hourOfDay should equal(now.getHourOfDay)
        mr2.minuteOfHour should equal(now.getMinuteOfHour)
    }

    test("add minutes") {
        val mr = MinuteRange()

        mr.previousMinute.minuteOfHour should equal(mr.start.plusMinutes(-1).getMinuteOfHour)
        mr.nextMinute.minuteOfHour should equal(mr.start.plusMinutes(1).getMinuteOfHour)

        val mr2 = MinuteRange(EmptyOffsetTimeCalendar)
        mr2.addMinutes(0) should equal(mr2)

        val prevRange = mr2.previousMinute
        val expectedPrevRange = mr2.addMinutes(-1)
        prevRange.year should equal(expectedPrevRange.year)
        prevRange.monthOfYear should equal(expectedPrevRange.monthOfYear)
        prevRange.dayOfMonth should equal(expectedPrevRange.dayOfMonth)
        prevRange.hourOfDay should equal(expectedPrevRange.hourOfDay)
        prevRange.minuteOfHour should equal(expectedPrevRange.minuteOfHour)

        val nextRange = mr2.nextMinute
        val expectedNextRange = mr2.addMinutes(1)
        nextRange.year should equal(expectedNextRange.year)
        nextRange.monthOfYear should equal(expectedNextRange.monthOfYear)
        nextRange.dayOfMonth should equal(expectedNextRange.dayOfMonth)
        nextRange.hourOfDay should equal(expectedNextRange.hourOfDay)
        nextRange.minuteOfHour should equal(expectedNextRange.minuteOfHour)

        (-100 to 100).par.foreach { m =>
            val r1 = mr2.addMinutes(m)
            val r2 = mr2.addMinutes(m)
            r1.year should equal(r2.year)
            r1.monthOfYear should equal(r2.monthOfYear)
            r1.dayOfMonth should equal(r2.dayOfMonth)
            r1.hourOfDay should equal(r2.hourOfDay)
            r1.minuteOfHour should equal(r2.minuteOfHour)
        }
    }

    test("getMinutes") {
        val hr = HourRange()
        val minutes = hr.minutes

        minutes.size should equal(MinutesPerHour)

        (0 until MinutesPerHour).par.foreach { i =>
            val m = minutes(i)

            m.start should equal(hr.start + i.minute)
            m.unmappedStart should equal(hr.calendar.unmapStart(hr.start) + i.minute)
            m.end should equal(m.calendar.mapEnd(hr.start + (i + 1).minute))
            m.unmappedEnd should equal(hr.start + (i + 1).minute)
        }
    }

}
