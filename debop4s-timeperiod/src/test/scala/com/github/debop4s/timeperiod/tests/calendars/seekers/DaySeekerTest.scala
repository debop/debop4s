package com.github.debop4s.timeperiod.tests.calendars.seekers

import com.github.debop4s.timeperiod._
import com.github.debop4s.timeperiod.calendars.CalendarVisitorFilter
import com.github.debop4s.timeperiod.calendars.seeker.DaySeeker
import com.github.debop4s.timeperiod.tests.AbstractTimePeriodTest
import com.github.debop4s.timeperiod.timerange.{DayRangeCollection, DayRange}
import com.github.debop4s.timeperiod.utils.Times

/**
 * DaySeekerTest
 * Created by debop on 2014. 2. 18.
 */
class DaySeekerTest extends AbstractTimePeriodTest {

  test("simple forward") {
    val start = DayRange()
    val daySeeker = DaySeeker()

    val day1 = daySeeker.findDay(start, 0)
    assert(day1 != null)
    day1.isSamePeriod(start) should equal(true)

    val day2 = daySeeker.findDay(start, 1)
    assert(day2 != null)
    day2.isSamePeriod(start.nextDay) should equal(true)

    (-10 until 20).par.foreach { i =>
      val offset = i * 5
      val day = daySeeker.findDay(start, offset)
      day.isSamePeriod(start.addDays(offset)) should equal(true)
    }
  }

  test("simple backward") {
    val start = DayRange()
    val daySeeker = DaySeeker(SeekDirection.Backward)

    val day1 = daySeeker.findDay(start, 0)
    day1.isSamePeriod(start) should equal(true)

    (-10 until 20).par.foreach { i =>
      val offset = i * 5
      val day = daySeeker.findDay(start, offset)
      day.isSamePeriod(start.addDays(-offset)) should equal(true)
    }
  }

  test("seek direction") {
    val start = DayRange()
    val daySeeker = DaySeeker()

    (-10 until 20).par.foreach { i =>
      val offset = i * 5
      val day = daySeeker.findDay(start, offset)
      day.isSamePeriod(start.addDays(offset)) should equal(true)
    }

    val backwardSeeker = DaySeeker(SeekDirection.Backward)

    (-10 until 20).foreach { i =>
      val offset = i * 5
      val day = backwardSeeker.findDay(start, offset)
      day.isSamePeriod(start.addDays(-offset)) should equal(true)
    }
  }

  test("min date") {
    val daySeeker = DaySeeker()
    val day = daySeeker.findDay(DayRange(MinPeriodTime), -10)
    day should equal(null)
  }

  test("max date") {
    val daySeeker = DaySeeker()
    val day = daySeeker.findDay(DayRange(MaxPeriodTime), 10)
    day should equal(null)
  }

  test("seek weekend holiday") {
    val start = DayRange(Times.asDate(2011, 2, 15))

    val filter = new CalendarVisitorFilter()
    filter.addWorkingWeekdays()
    filter.excludePeriods.add(DayRangeCollection(2011, 2, 27, 14)) // 14 days => week 9 and week 10

    val daySeeker = new DaySeeker(filter)

    val day1 = daySeeker.findDay(start, 3)
    day1 should equal(DayRange(2011, 2, 18))

    // 주말 (19, 20) 제외
    daySeeker.findDay(start, 4) should equal(DayRange(2011, 2, 21))

    // 주말 (19, 20) 제외, 2.27 일부터 14일간 휴가
    daySeeker.findDay(start, 10) should equal(DayRange(2011, 3, 15))
  }
}
