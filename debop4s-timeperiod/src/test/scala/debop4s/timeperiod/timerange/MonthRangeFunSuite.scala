package debop4s.timeperiod.timerange

import debop4s.core.conversions.jodatime._
import debop4s.timeperiod.TimeSpec._
import debop4s.timeperiod.tests.AbstractTimeFunSuite
import debop4s.timeperiod.utils.Times

class MonthRangeFunSuite extends AbstractTimeFunSuite {

  test("init values") {
    val now = Times.now
    val firstMonth = Times.startTimeOfMonth(now)
    val secondMonth = firstMonth + 1.month

    val mr = MonthRange(now, EmptyOffsetTimeCalendar)
    mr.start shouldEqual firstMonth
    mr.end shouldEqual secondMonth
  }

  test("default calendar") {
    val yearStart = Times.startTimeOfYear(Times.now)

    (0 until MonthsPerYear).par.foreach { m =>
      val mr = MonthRange(yearStart + m.month)
      mr.year shouldEqual yearStart.getYear
      mr.monthOfYear shouldEqual (m + 1)

      mr.unmappedStart shouldEqual (yearStart + m.month)
      mr.unmappedEnd shouldEqual (yearStart + (m + 1).month)
    }
  }

  test("get daysView") {
    val mr = MonthRange()
    val days = mr.daysView

    var index = 0

    days.foreach { day =>
      day.start shouldEqual (mr.start + index.day)
      day.end shouldEqual day.calendar.mapEnd(day.start + 1.day)
      index += 1
    }
    index shouldEqual Times.daysInMonth(mr.year, mr.monthOfYear)
  }

  test("add monthsView") {
    val now = Times.now
    val startMonth = Times.startTimeOfMonth(now)
    val mr = MonthRange(now)

    mr.previousMonth.start shouldEqual (startMonth - 1.month)
    mr.nextMonth.start shouldEqual (startMonth + 1.month)

    mr shouldEqual mr.addMonths(0)

    (-60 until 120).par.foreach { m =>
      mr.addMonths(m).start shouldEqual (startMonth + m.month)
    }

  }

}
