package debop4s.timeperiod.timerange

import debop4s.timeperiod._
import debop4s.timeperiod.tests.AbstractTimeFunSuite
import debop4s.timeperiod.utils.Times

class DayRangeFunSuite extends AbstractTimeFunSuite {

  test("initValues") {
    val currentTime = Times.now
    val firstDay = Times.startTimeOfDay(currentTime)

    val dr = DayRange(currentTime, TimeSpec.EmptyOffsetTimeCalendar)

    dr.start should equal(firstDay)
    dr.end should equal(firstDay.plusDays(1))
  }

  test("defaultCalendar") {
    val yearStart = Times.startTimeOfYear(Times.now)

    (1 to TimeSpec.MonthsPerYear).par.foreach {
      m =>
        val monthStart = Times.asDate(yearStart.getYear, m, 1)
        val monthEnd = Times.endTimeOfMonth(monthStart)

        (1 until monthEnd.getDayOfMonth).foreach {
          day =>
            val dayRange = DayRange(monthStart.plusDays(day - 1))
            dayRange.year should equal(yearStart.getYear)
            dayRange.monthOfYear should equal(monthStart.getMonthOfYear)
        }
    }
  }

  test("construct test") {
    val dayRange = DayRange(Times.now)
    dayRange.start should equal(Times.today)

    val dayRange2 = DayRange(Times.now.getYear, Times.now.getMonthOfYear, Times.now.getDayOfMonth)
    dayRange2.start shouldEqual Times.today
  }

  test("dayOfWeek") {
    val dayRange = DayRange(Times.now)
    dayRange.dayOfWeek shouldEqual TimeSpec.DefaultTimeCalendar.dayOfWeek(Times.now)
  }

  test("addDays") {
    val time = Times.now
    val day = Times.today
    val dayRange = DayRange(time)

    dayRange.previousDay.start should equal(day.plusDays(-1))
    dayRange.nextDay.start should equal(day.plusDays(1))

    dayRange.addDays(0) should equal(dayRange)

    Range(-60, 120).par.foreach { i =>
      dayRange.addDays(i).start shouldEqual day.plusDays(i)
    }
  }

  test("get hours") {
    val dayRange = DayRange()
    val hours = dayRange.hours

    var index = 0
    hours.foreach { h =>
      h.start should equal(dayRange.start.plusHours(index))
      h.end should equal(h.calendar.mapEnd(h.start.plusHours(1)))
      index += 1
    }
    index shouldEqual TimeSpec.HoursPerDay
  }

}
