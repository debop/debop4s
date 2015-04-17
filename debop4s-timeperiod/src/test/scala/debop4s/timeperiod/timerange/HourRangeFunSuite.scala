package debop4s.timeperiod.timerange

import debop4s.core.conversions.jodatime._
import debop4s.timeperiod.TimeSpec._
import debop4s.timeperiod.tests.AbstractTimeFunSuite
import debop4s.timeperiod.utils.Times

/**
 * HourRangeTest
 * Created by debop on 2014. 2. 16.
 */
class HourRangeFunSuite extends AbstractTimeFunSuite {

  test("init values") {
    val now = Times.now
    val firstHour = Times.trimToMinute(now)
    val secondHour = firstHour + 1.hour

    val hourRange = HourRange(now, EmptyOffsetTimeCalendar)

    hourRange.start.getYear shouldEqual firstHour.getYear
    hourRange.start.getMonthOfYear shouldEqual firstHour.getMonthOfYear
    hourRange.start.getDayOfMonth shouldEqual firstHour.getDayOfMonth
    hourRange.start.getHourOfDay shouldEqual firstHour.getHourOfDay
    hourRange.start.getMinuteOfHour shouldEqual 0
    hourRange.start.getSecondOfMinute shouldEqual 0
    hourRange.start.getMillisOfSecond shouldEqual 0

    hourRange.end.getYear shouldEqual secondHour.getYear
    hourRange.end.getMonthOfYear shouldEqual secondHour.getMonthOfYear
    hourRange.end.getDayOfMonth shouldEqual secondHour.getDayOfMonth
    hourRange.end.getHourOfDay shouldEqual secondHour.getHourOfDay
    hourRange.end.getMinuteOfHour shouldEqual 0
    hourRange.end.getSecondOfMinute shouldEqual 0
    hourRange.end.getMillisOfSecond shouldEqual 0
  }

  test("default calendar") {
    val today = Times.today

    (0 until HoursPerDay).par.foreach { h =>
      val hr = HourRange(today + h.hour)

      hr.year shouldEqual today.getYear
      hr.monthOfYear shouldEqual today.getMonthOfYear
      hr.dayOfMonth shouldEqual today.getDayOfMonth
      hr.hourOfDay shouldEqual h
      hr.start shouldEqual hr.calendar.mapStart(today + h.hour)
      hr.end shouldEqual hr.calendar.mapEnd(today + (h + 1).hour)
    }
  }

  test("HourRange constructor test") {
    val now = Times.now

    val hr = HourRange(now)
    hr.year shouldEqual now.getYear
    hr.monthOfYear shouldEqual now.getMonthOfYear
    hr.dayOfMonth shouldEqual now.getDayOfMonth
    hr.hourOfDay shouldEqual now.getHourOfDay

    val hr2 = HourRange(now.getYear, now.getMonthOfYear, now.getDayOfMonth, now.getHourOfDay)

    hr2.year shouldEqual now.getYear
    hr2.monthOfYear shouldEqual now.getMonthOfYear
    hr2.dayOfMonth shouldEqual now.getDayOfMonth
    hr2.hourOfDay shouldEqual now.getHourOfDay
  }

  test("add hour") {
    val hr = HourRange()
    hr.previousHour.hourOfDay shouldEqual hr.start.plusHours(-1).getHourOfDay
    hr.nextHour.hourOfDay shouldEqual hr.start.plusHours(1).getHourOfDay

    val hr2 = HourRange(EmptyOffsetTimeCalendar)
    hr2.addHours(0) shouldEqual hr2

    val prevRange = hr2.previousHour
    prevRange.year shouldEqual hr2.addHours(-1).year
    prevRange.monthOfYear shouldEqual hr2.addHours(-1).monthOfYear
    prevRange.dayOfMonth shouldEqual hr2.addHours(-1).dayOfMonth
    prevRange.hourOfDay shouldEqual hr2.addHours(-1).hourOfDay

    val nextRange = hr2.nextHour
    nextRange.year shouldEqual hr2.addHours(1).year
    nextRange.monthOfYear shouldEqual hr2.addHours(1).monthOfYear
    nextRange.dayOfMonth shouldEqual hr2.addHours(1).dayOfMonth
    nextRange.hourOfDay shouldEqual hr2.addHours(1).hourOfDay

    for (h <- -100 to 100 by 5) {
      val r = hr2.addHours(h)
      val r2 = hr2.addHours(h)

      r.year shouldEqual r2.year
      r.monthOfYear shouldEqual r2.monthOfYear
      r.dayOfMonth shouldEqual r2.dayOfMonth
      r.hourOfDay shouldEqual r2.hourOfDay
    }
  }

  test("getMinutes") {
    val hr = HourRange()
    val minutes = hr.minutes

    minutes.size shouldEqual MinutesPerHour

    // for (i <- 0 until MinutesPerHour) {
    hr.minutes.take(MinutesPerHour) foreach { mr =>
      debug(s"mr=$mr")
      mr.start shouldEqual hr.start + mr.minuteOfHour.minute
      mr.unmappedStart shouldEqual (hr.start + mr.minuteOfHour.minute)

      mr.end shouldEqual mr.calendar.mapEnd(hr.start + (mr.minuteOfHour + 1).minute)
      mr.unmappedEnd shouldEqual hr.start + (mr.minuteOfHour + 1).minute
    }
  }


}
