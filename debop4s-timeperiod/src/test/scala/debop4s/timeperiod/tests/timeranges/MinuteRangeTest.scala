package debop4s.timeperiod.tests.timeranges

import debop4s.core.jodatime._
import debop4s.timeperiod._
import debop4s.timeperiod.tests.AbstractTimePeriodTest
import debop4s.timeperiod.timerange.{HourRange, MinuteRange}
import debop4s.timeperiod.utils.Times

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

        minRange.start.getYear shouldEqual firstMin.getYear
        minRange.start.getMonthOfYear shouldEqual firstMin.getMonthOfYear
        minRange.start.getDayOfMonth shouldEqual firstMin.getDayOfMonth
        minRange.start.getHourOfDay shouldEqual firstMin.getHourOfDay
        minRange.start.getMinuteOfHour shouldEqual firstMin.getMinuteOfHour
        minRange.start.getSecondOfMinute shouldEqual 0
        minRange.start.getMillisOfSecond shouldEqual 0
    }

    test("default calendar") {
        val now = Times.now
        val today = Times.today

        (0 until MinutesPerHour).par.foreach { m =>
            val mr = MinuteRange(today + m.minute)

            mr.year shouldEqual today.getYear
            mr.monthOfYear shouldEqual today.getMonthOfYear
            mr.dayOfMonth shouldEqual today.getDayOfMonth
            mr.hourOfDay shouldEqual today.getHourOfDay
            mr.minuteOfHour shouldEqual m
            mr.start shouldEqual mr.calendar.mapStart(today + m.minute)
            mr.end shouldEqual mr.calendar.mapEnd(today + (m + 1).minute)
        }
    }

    test("constructor") {
        val now = Times.now

        val mr = MinuteRange(now)
        mr.year shouldEqual now.getYear
        mr.monthOfYear shouldEqual now.getMonthOfYear
        mr.dayOfMonth shouldEqual now.getDayOfMonth
        mr.hourOfDay shouldEqual now.getHourOfDay
        mr.minuteOfHour shouldEqual now.getMinuteOfHour

        val mr2 = MinuteRange(now.getYear, now.getMonthOfYear, now.getDayOfMonth, now.getHourOfDay, now.getMinuteOfHour)
        mr2.year shouldEqual now.getYear
        mr2.monthOfYear shouldEqual now.getMonthOfYear
        mr2.dayOfMonth shouldEqual now.getDayOfMonth
        mr2.hourOfDay shouldEqual now.getHourOfDay
        mr2.minuteOfHour shouldEqual now.getMinuteOfHour
    }

    test("add minutes") {
        val mr = MinuteRange()

        mr.previousMinute.minuteOfHour shouldEqual mr.start.plusMinutes(-1).getMinuteOfHour
        mr.nextMinute.minuteOfHour shouldEqual mr.start.plusMinutes(1).getMinuteOfHour

        val mr2 = MinuteRange(EmptyOffsetTimeCalendar)
        mr2.addMinutes(0) shouldEqual mr2

        val prevRange = mr2.previousMinute
        val expectedPrevRange = mr2.addMinutes(-1)
        prevRange.year shouldEqual expectedPrevRange.year
        prevRange.monthOfYear shouldEqual expectedPrevRange.monthOfYear
        prevRange.dayOfMonth shouldEqual expectedPrevRange.dayOfMonth
        prevRange.hourOfDay shouldEqual expectedPrevRange.hourOfDay
        prevRange.minuteOfHour shouldEqual expectedPrevRange.minuteOfHour

        val nextRange = mr2.nextMinute
        val expectedNextRange = mr2.addMinutes(1)
        nextRange.year shouldEqual expectedNextRange.year
        nextRange.monthOfYear shouldEqual expectedNextRange.monthOfYear
        nextRange.dayOfMonth shouldEqual expectedNextRange.dayOfMonth
        nextRange.hourOfDay shouldEqual expectedNextRange.hourOfDay
        nextRange.minuteOfHour shouldEqual expectedNextRange.minuteOfHour

        (-100 to 100).par.foreach { m =>
            val r1 = mr2.addMinutes(m)
            val r2 = mr2.addMinutes(m)
            r1.year shouldEqual r2.year
            r1.monthOfYear shouldEqual r2.monthOfYear
            r1.dayOfMonth shouldEqual r2.dayOfMonth
            r1.hourOfDay shouldEqual r2.hourOfDay
            r1.minuteOfHour shouldEqual r2.minuteOfHour
        }
    }

    test("getMinutes") {
        val hr = HourRange()
        val minutes = hr.minutes

        minutes.size shouldEqual MinutesPerHour

        (0 until MinutesPerHour).par.foreach { i =>
            val m = minutes(i)

            m.start shouldEqual (hr.start + i.minute)
            m.unmappedStart shouldEqual (hr.calendar.unmapStart(hr.start) + i.minute)
            m.end shouldEqual m.calendar.mapEnd(hr.start + (i + 1).minute)
            m.unmappedEnd shouldEqual (hr.start + (i + 1).minute)
        }
    }

}
