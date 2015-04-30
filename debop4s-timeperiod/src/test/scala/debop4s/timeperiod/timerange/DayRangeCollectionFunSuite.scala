package debop4s.timeperiod.timerange

import debop4s.core.conversions.jodatime._
import debop4s.timeperiod.TimeSpec._
import debop4s.timeperiod.tests.AbstractTimeFunSuite
import debop4s.timeperiod.utils.Times
import debop4s.timeperiod.utils.Times._

class DayRangeCollectionFunSuite extends AbstractTimeFunSuite {

  test("single daysView") {
    val start = asDate(2004, 2, 22)
    val days = new DayRangeCollection(start, 1)

    days.dayCount should equal(1)

    days.startYear should equal(start.getYear)
    days.startMonthOfYear should equal(start.getMonthOfYear)
    days.startDayOfMonth should equal(start.getDayOfMonth)
  }

  test("calendar daysView") {
    val dayCount = 3

    val start = asDate(2004, 2, 22)
    val end = start + (dayCount - 1).day
    val days = DayRangeCollection(start, dayCount)

    days.dayCount should equal(dayCount)
    days.startYear should equal(start.getYear)
    days.startMonthOfYear should equal(start.getMonthOfYear)
    days.startDayOfMonth should equal(start.getDayOfMonth)

    assert(days.endYear == end.getYear)
    assert(days.endMonthOfYear == end.getMonthOfYear)
    assert(days.endDayOfMonth == end.getDayOfMonth)

    val dayList = days.daysView
    assert(dayList.size == dayCount)

    (0 until dayCount).foreach {
      i =>
        assert(dayList(i).isSamePeriod(DayRange(start + i.day)))
    }
  }

  test("calendar hoursView") {
    val dayCounts = Array(1, 6, 48, 180, 480)

    dayCounts.par.foreach { dayCount =>
      val today = Times.today
      val days = DayRangeCollection(today, dayCount)

      val start = today + days.calendar.startOffset
      val end = start + dayCount.day + days.calendar.endOffset

      assert(days.start == start)
      assert(days.end == end)
      assert(days.dayCount == dayCount)

      val items = days.hoursView
      assert(items.size == dayCount * HoursPerDay)
      var i = 0
      while (i < items.size) {
        assert(items(i).start == start + i.hour)
        assert(items(i).end == days.calendar.mapEnd(start + (i + 1).hour))
        assert(items(i).isSamePeriod(HourRange(days.start + i.hour)))
        i += 1
      }
    }
  }

}
