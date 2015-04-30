package debop4s.timeperiod.timerange

import debop4s.core.conversions.jodatime._
import debop4s.timeperiod.TimeSpec._
import debop4s.timeperiod._
import debop4s.timeperiod.tests.AbstractTimeFunSuite
import debop4s.timeperiod.utils.Times
import debop4s.timeperiod.utils.Times._

class QuarterRangeFunSuite extends AbstractTimeFunSuite {

  test("init values") {
    val now = Times.now
    val firstQuarter = startTimeOfQuarter(now.getYear, Quarter.First)
    val secondQuarter = startTimeOfQuarter(now.getYear, Quarter.Second)

    val qr = QuarterRange(now.getYear, Quarter.First, EmptyOffsetTimeCalendar)

    qr.start.getYear shouldEqual firstQuarter.getYear
    qr.start.getMonthOfYear shouldEqual firstQuarter.getMonthOfYear
    qr.start.getDayOfMonth shouldEqual firstQuarter.getDayOfMonth
    qr.start.getHourOfDay shouldEqual 0
    qr.start.getMinuteOfHour shouldEqual 0
    qr.start.getSecondOfMinute shouldEqual 0
    qr.start.getMillisOfSecond shouldEqual 0

    qr.end.getYear shouldEqual secondQuarter.getYear
    qr.end.getMonthOfYear shouldEqual secondQuarter.getMonthOfYear
    qr.end.getDayOfMonth shouldEqual secondQuarter.getDayOfMonth
    qr.end.getHourOfDay shouldEqual 0
    qr.end.getMinuteOfHour shouldEqual 0
    qr.end.getSecondOfMinute shouldEqual 0
    qr.end.getMillisOfSecond shouldEqual 0
  }

  test("default calendar") {
    val yearStart = Times.startTimeOfYear(Times.currentYear)

    Quarter.values.par.foreach { quarter =>
      val offset = quarter.getValue - 1
      val qr = QuarterRange(yearStart + (MonthsPerQuarter * offset).month)

      qr.start should equal(qr.calendar.mapStart(yearStart + (MonthsPerQuarter * offset).month))
      qr.end should equal(qr.calendar.mapEnd(yearStart + (MonthsPerQuarter * (offset + 1)).month))
    }
  }

  test("moment") {
    val now = Times.now
    val currentYear = now.getYear

    QuarterRange(asDate(currentYear, 1, 1)).quarter shouldEqual Quarter.First
    QuarterRange(asDate(currentYear, 3, 31)).quarter shouldEqual Quarter.First

    QuarterRange(asDate(currentYear, 4, 1)).quarter shouldEqual Quarter.Second
    QuarterRange(asDate(currentYear, 6, 30)).quarter shouldEqual Quarter.Second

    QuarterRange(asDate(currentYear, 7, 1)).quarter shouldEqual Quarter.Third
    QuarterRange(asDate(currentYear, 9, 30)).quarter shouldEqual Quarter.Third

    QuarterRange(asDate(currentYear, 10, 1)).quarter shouldEqual Quarter.Fourth
    QuarterRange(asDate(currentYear, 12, 31)).quarter shouldEqual Quarter.Fourth
  }

  test("start month") {
    val now = Times.now
    val currentYear = now.getYear

    QuarterRange(currentYear, Quarter.First).startMonthOfYear shouldEqual 1
    QuarterRange(currentYear, Quarter.Second).startMonthOfYear shouldEqual 4
    QuarterRange(currentYear, Quarter.Third).startMonthOfYear shouldEqual 7
    QuarterRange(currentYear, Quarter.Fourth).startMonthOfYear shouldEqual 10
  }

  test("isMultipleCalendarYears") {
    val now = Times.now
    val currentYear = now.getYear
    QuarterRange(currentYear, Quarter.First).isMultipleCalendarYears shouldEqual false
  }

  test("calendar quarter") {
    val now = Times.now
    val currentYear = now.getYear
    val calendar = EmptyOffsetTimeCalendar

    val q1 = QuarterRange(currentYear, Quarter.First, calendar)
    q1.isReadonly should equal(true)
    q1.year should equal(currentYear)
    q1.quarter should equal(Quarter.First)
    q1.start should equal(asDate(currentYear, 1, 1))
    q1.end should equal(asDate(currentYear, 4, 1))

    val q2 = QuarterRange(currentYear, Quarter.Second, calendar)
    q2.isReadonly should equal(true)
    q2.year should equal(currentYear)
    q2.quarter should equal(Quarter.Second)
    q2.start should equal(asDate(currentYear, 4, 1))
    q2.end should equal(asDate(currentYear, 7, 1))

    val q3 = QuarterRange(currentYear, Quarter.Third, calendar)
    q3.isReadonly should equal(true)
    q3.year should equal(currentYear)
    q3.quarter should equal(Quarter.Third)
    q3.start should equal(asDate(currentYear, 7, 1))
    q3.end should equal(asDate(currentYear, 10, 1))

    val q4 = QuarterRange(currentYear, Quarter.Fourth, calendar)
    q4.isReadonly should equal(true)
    q4.year should equal(currentYear)
    q4.quarter should equal(Quarter.Fourth)
    q4.start should equal(asDate(currentYear, 10, 1))
    q4.end should equal(asDate(currentYear + 1, 1, 1))
  }

  test("add quartersView") {
    val now = Times.now
    val currentYear = now.getYear
    val calendar = EmptyOffsetTimeCalendar

    val q1 = QuarterRange(currentYear, Quarter.First, calendar)

    var prevQ1 = q1.addQuarters(-1)
    prevQ1.quarter should equal(Quarter.Fourth)
    prevQ1.start should equal(q1.start - MonthsPerQuarter.month)
    prevQ1.end should equal(q1.start)

    prevQ1 = q1.addQuarters(-2)
    prevQ1.quarter should equal(Quarter.Third)
    prevQ1.start should equal(q1.start - (2 * MonthsPerQuarter).month)
    prevQ1.end should equal(q1.start - MonthsPerQuarter.month)

    prevQ1 = q1.addQuarters(-3)
    prevQ1.quarter should equal(Quarter.Second)
    prevQ1.start should equal(q1.start - (3 * MonthsPerQuarter).month)
    prevQ1.end should equal(q1.start - (2 * MonthsPerQuarter).month)

    prevQ1 = q1.addQuarters(-4)
    prevQ1.quarter should equal(Quarter.First)
    prevQ1.start should equal(q1.start - (4 * MonthsPerQuarter).month)
    prevQ1.end should equal(q1.start - (3 * MonthsPerQuarter).month)


    var nextQ1 = q1.addQuarters(1)
    nextQ1.quarter should equal(Quarter.Second)
    nextQ1.start should equal(q1.start + MonthsPerQuarter.month)
    nextQ1.end should equal(q1.start + (2 * MonthsPerQuarter).month)

    nextQ1 = q1.addQuarters(2)
    nextQ1.quarter should equal(Quarter.Third)
    nextQ1.start should equal(q1.start + (2 * MonthsPerQuarter).month)
    nextQ1.end should equal(q1.start + (3 * MonthsPerQuarter).month)

    nextQ1 = q1.addQuarters(3)
    nextQ1.quarter should equal(Quarter.Fourth)
    nextQ1.start should equal(q1.start + (3 * MonthsPerQuarter).month)
    nextQ1.end should equal(q1.start + (4 * MonthsPerQuarter).month)

    nextQ1 = q1.addQuarters(4)
    nextQ1.quarter should equal(Quarter.First)
    nextQ1.start should equal(q1.start + (4 * MonthsPerQuarter).month)
    nextQ1.end should equal(q1.start + (5 * MonthsPerQuarter).month)
  }


}
