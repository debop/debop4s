package debop4s.timeperiod.tests.timeranges

import debop4s.core.jodatime._
import debop4s.timeperiod._
import debop4s.timeperiod.tests.AbstractTimePeriodTest
import debop4s.timeperiod.timerange.HourRange
import debop4s.timeperiod.utils.Times


/**
 * HourRangeTest
 * Created by debop on 2014. 2. 16.
 */
class HourRangeTest extends AbstractTimePeriodTest {

    test("init values") {
        val now = Times.now
        val firstHour = Times.trimToMinute(now)
        val secondHour = firstHour + 1.hour

        val hourRange = HourRange(now, EmptyOffsetTimeCalendar)

        hourRange.start.getYear should equal(firstHour.getYear)
        hourRange.start.getMonthOfYear should equal(firstHour.getMonthOfYear)
        hourRange.start.getDayOfMonth should equal(firstHour.getDayOfMonth)
        hourRange.start.getHourOfDay should equal(firstHour.getHourOfDay)
        hourRange.start.getMinuteOfHour should equal(0)
        hourRange.start.getSecondOfMinute should equal(0)
        hourRange.start.getMillisOfSecond should equal(0)

        hourRange.end.getYear should equal(secondHour.getYear)
        hourRange.end.getMonthOfYear should equal(secondHour.getMonthOfYear)
        hourRange.end.getDayOfMonth should equal(secondHour.getDayOfMonth)
        hourRange.end.getHourOfDay should equal(secondHour.getHourOfDay)
        hourRange.end.getMinuteOfHour should equal(0)
        hourRange.end.getSecondOfMinute should equal(0)
        hourRange.end.getMillisOfSecond should equal(0)
    }

    test("default calendar") {
        val now = Times.now
        val today = Times.today

        for (h <- 0 until HoursPerDay) {
            val hr = HourRange(today + h.hour)

            hr.year should equal(today.getYear)
            hr.monthOfYear should equal(today.getMonthOfYear)
            hr.dayOfMonth should equal(today.getDayOfMonth)
            hr.hourOfDay should equal(h)
            hr.start should equal(hr.calendar.mapStart(today + h.hour))
            hr.end should equal(hr.calendar.mapEnd(today + (h + 1).hour))
        }
    }

    test("HourRange constructor test") {
        val now = Times.now

        val hr = HourRange(now)
        hr.year should equal(now.getYear)
        hr.monthOfYear should equal(now.getMonthOfYear)
        hr.dayOfMonth should equal(now.getDayOfMonth)
        hr.hourOfDay should equal(now.getHourOfDay)

        val hr2 = HourRange(now.getYear, now.getMonthOfYear, now.getDayOfMonth, now.getHourOfDay)

        hr2.year should equal(now.getYear)
        hr2.monthOfYear should equal(now.getMonthOfYear)
        hr2.dayOfMonth should equal(now.getDayOfMonth)
        hr2.hourOfDay should equal(now.getHourOfDay)
    }

    test("add hour") {
        val hr = HourRange()
        hr.previousHour.hourOfDay should equal(hr.start.plusHours(-1).getHourOfDay)
        hr.nextHour.hourOfDay should equal(hr.start.plusHours(1).getHourOfDay)

        val hr2 = HourRange(EmptyOffsetTimeCalendar)
        hr2.addHours(0) should equal(hr2)

        val prevRange = hr2.previousHour
        prevRange.year should equal(hr2.addHours(-1).year)
        prevRange.monthOfYear should equal(hr2.addHours(-1).monthOfYear)
        prevRange.dayOfMonth should equal(hr2.addHours(-1).dayOfMonth)
        prevRange.hourOfDay should equal(hr2.addHours(-1).hourOfDay)

        val nextRange = hr2.nextHour
        nextRange.year should equal(hr2.addHours(1).year)
        nextRange.monthOfYear should equal(hr2.addHours(1).monthOfYear)
        nextRange.dayOfMonth should equal(hr2.addHours(1).dayOfMonth)
        nextRange.hourOfDay should equal(hr2.addHours(1).hourOfDay)

        for (h <- -100 to 100 by 5) {
            val r = hr2.addHours(h)
            val r2 = hr2.addHours(h)

            r.year should equal(r2.year)
            r.monthOfYear should equal(r2.monthOfYear)
            r.dayOfMonth should equal(r2.dayOfMonth)
            r.hourOfDay should equal(r2.hourOfDay)
        }
    }

    test("getMinutes") {
        val hr = HourRange()
        val minutes = hr.minutes

        minutes.size should equal(MinutesPerHour)

        for (i <- 0 until MinutesPerHour) {
            val minute = minutes(i)
            minute.start should equal(hr.start + i.minute)
            minute.unmappedStart should equal(hr.start + i.minute)

            minute.end should equal(minute.calendar.mapEnd(hr.start + (i + 1).minute))
            minute.unmappedEnd should equal(hr.start + (i + 1).minute)
        }
    }


}
