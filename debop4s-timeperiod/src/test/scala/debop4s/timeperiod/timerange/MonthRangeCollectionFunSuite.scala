package debop4s.timeperiod.timerange

import debop4s.core.conversions.jodatime._
import debop4s.timeperiod.tests.AbstractTimeFunSuite
import debop4s.timeperiod.utils.Times

class MonthRangeCollectionFunSuite extends AbstractTimeFunSuite {

  test("single month") {
    val startYear = 2004
    val startMonth = 6

    val mrs = MonthRangeCollection(startYear, startMonth, 1)
    mrs.monthCount should equal(1)

    val months = mrs.months
    months.size should equal(1)
    months(0).isSamePeriod(new MonthRange(startYear, startMonth)) should equal(true)

    mrs.startYear should equal(startYear)
    mrs.endYear should equal(startYear)
    mrs.startMonthOfYear should equal(startMonth)
    mrs.endMonthOfYear should equal(startMonth)
  }

  test("calendar months") {
    val startYear = 2004
    val startMonth = 11
    val monthCount = 5

    val mrs = MonthRangeCollection(startYear, startMonth, monthCount)

    mrs.monthCount should equal(monthCount)
  }

  test("month counts") {
    val monthCounts = Array(1, 6, 48, 180, 360)

    val now = Times.now
    val today = Times.today


    monthCounts.foreach { m =>
      val mrs = MonthRangeCollection(now, m)
      val startTime = mrs.calendar.mapStart(Times.trimToDay(today))
      val endTime = mrs.calendar.mapEnd(startTime + m.month)

      mrs.start shouldEqual startTime
      mrs.end shouldEqual endTime

      val items = mrs.months

      (0 until m).par.foreach { i =>
        val item = items(i)

        item.start shouldEqual startTime + i.month
        item.end shouldEqual mrs.calendar.mapEnd(startTime + (i + 1).month)

        item.unmappedStart shouldEqual startTime + i.month
        item.unmappedEnd shouldEqual startTime + (i + 1).month

        item.isSamePeriod(MonthRange(mrs.start + i.month)) shouldEqual true

        val ym = Times.addMonth(now.getYear, now.getMonthOfYear, i)
        item.isSamePeriod(MonthRange(ym.year, ym.monthOfYear)) shouldEqual true
      }
    }
  }
}
