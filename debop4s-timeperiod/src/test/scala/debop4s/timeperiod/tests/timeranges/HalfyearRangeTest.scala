package debop4s.timeperiod.tests.timeranges

import debop4s.core.parallels.Parallels
import debop4s.timeperiod._
import debop4s.timeperiod.tests.AbstractTimePeriodTest
import debop4s.timeperiod.timerange.HalfyearRange
import debop4s.timeperiod.utils.Times
import debop4s.timeperiod.utils.Times._

/**
 * HalfyearRangeTest
 * Created by debop on 2014. 2. 16.
 */
class HalfyearRangeTest extends AbstractTimePeriodTest {

  val now = Times.now
  val currentYear = now.getYear
  val calendar = EmptyOffsetTimeCalendar

  test("init values") {

    val firstHalfyear = startTimeOfHalfyear(now.getYear, Halfyear.First)
    val secondHalfyear = startTimeOfHalfyear(now.getYear, Halfyear.Second)

    val hyr = HalfyearRange(now.getYear, Halfyear.First, EmptyOffsetTimeCalendar)

    hyr.start.year shouldEqual firstHalfyear.year
    hyr.start.getMonthOfYear shouldEqual firstHalfyear.getMonthOfYear
    hyr.start.getDayOfMonth shouldEqual firstHalfyear.getDayOfMonth
    hyr.start.getHourOfDay shouldEqual 0
    hyr.start.getMinuteOfHour shouldEqual 0
    hyr.start.getSecondOfMinute shouldEqual 0
    hyr.start.getMillisOfSecond shouldEqual 0

    hyr.end.year shouldEqual secondHalfyear.year
    hyr.end.getMonthOfYear shouldEqual secondHalfyear.getMonthOfYear
    hyr.end.getDayOfMonth shouldEqual secondHalfyear.getDayOfMonth
    hyr.end.getHourOfDay shouldEqual 0
    hyr.end.getMinuteOfHour shouldEqual 0
    hyr.end.getSecondOfMinute shouldEqual 0
    hyr.end.getMillisOfSecond shouldEqual 0
  }

  test("default calendar") {
    val yearStart = startTimeOfYear(currentYear)
    for (halfyear <- Halfyear.values) {
      val offset = halfyear.id - 1
      val hyr = HalfyearRange(yearStart.plusMonths(MonthsPerHalfyear * offset))

      hyr.start shouldEqual hyr.calendar.mapStart(yearStart.plusMonths(MonthsPerHalfyear * offset))
      hyr.end shouldEqual hyr.calendar.mapEnd(yearStart.plusMonths(MonthsPerHalfyear * ( offset + 1 )))
    }
  }

  test("moment") {
    HalfyearRange().halfyear shouldEqual ( if (now.getMonthOfYear < 7) Halfyear.First else Halfyear.Second )

    Parallels.runAction1(MonthsPerYear)(m => {
      val month = m + 1
      HalfyearRange(currentYear, Halfyear.First).startMonthOfYear shouldEqual 1
      HalfyearRange(currentYear, Halfyear.Second).startMonthOfYear shouldEqual 7
    })
  }

  test("multiple calendar years") {
    val currentYear = now.getYear
    HalfyearRange(currentYear, Halfyear.First).isMultipleCalendarYears shouldEqual false
  }

  test("calendar halfyear") {

    val h1 = HalfyearRange(currentYear, Halfyear.First, calendar)

    h1.isReadonly shouldEqual true
    h1.halfyear shouldEqual Halfyear.First
    h1.start shouldEqual asDate(currentYear, 1, 1)
    h1.end shouldEqual asDate(currentYear, 7, 1)

    val h2 = HalfyearRange(currentYear, Halfyear.Second, calendar)

    h2.isReadonly shouldEqual true
    h2.halfyear shouldEqual Halfyear.Second
    h2.start shouldEqual asDate(currentYear, 7, 1)
    h2.end shouldEqual asDate(currentYear + 1, 1, 1)
  }

  test("halfyear quaters") {

    val h1 = HalfyearRange(currentYear, Halfyear.First, calendar)
    val h1quarters = h1.getQuarters

    var h1index = 0
    h1quarters.foreach { qr =>
      log.trace(s"qr=$qr")
      qr.quarter shouldEqual ( if (h1index == 0) Quarter.First else Quarter.Second )
      qr.start shouldEqual h1.start.plusMonths(h1index * MonthsPerQuarter)
      qr.end shouldEqual h1.calendar.mapEnd(qr.start.plusMonths(MonthsPerQuarter))
      h1index += 1
    }

    val h2 = HalfyearRange(currentYear, Halfyear.Second, calendar)
    val h2quarters = h2.getQuarters

    var h2index = 0
    h2quarters.foreach { qr =>
      log.trace(s"qr=$qr")
      qr.quarter shouldEqual ( if (h2index == 0) Quarter.Third else Quarter.Fourth )
      qr.start shouldEqual h2.start.plusMonths(h2index * MonthsPerQuarter)
      qr.end shouldEqual qr.calendar.mapEnd(qr.start.plusMonths(MonthsPerQuarter))
      h2index += 1
    }
  }

  test("halfyear getMonths") {

    val h1 = HalfyearRange(currentYear, Halfyear.First, calendar)
    val months = h1.getMonths
    months.size shouldEqual MonthsPerHalfyear

    var index = 0
    months.foreach { m =>
      m.start shouldEqual h1.start.plusMonths(index)
      m.end shouldEqual calendar.mapEnd(m.start.plusMonths(1))
      index += 1
    }
  }

  test("add halfyears") {

    val h1 = HalfyearRange(currentYear, Halfyear.First, calendar)

    var prevH1 = h1.addHalfyears(-1)
    prevH1.halfyear shouldEqual Halfyear.Second
    prevH1.year shouldEqual ( currentYear - 1 )
    prevH1.start shouldEqual h1.start.plusMonths(-MonthsPerHalfyear)
    prevH1.end shouldEqual h1.start

    prevH1 = h1.addHalfyears(-2)
    prevH1.halfyear shouldEqual Halfyear.First
    prevH1.year shouldEqual ( currentYear - 1 )
    prevH1.start shouldEqual h1.start.plusMonths(-2 * MonthsPerHalfyear)
    prevH1.end shouldEqual h1.start.plusMonths(-1 * MonthsPerHalfyear)

    prevH1 = h1.addHalfyears(-3)
    prevH1.halfyear shouldEqual Halfyear.Second
    prevH1.year shouldEqual ( currentYear - 2 )
    prevH1.start shouldEqual h1.start.plusMonths(-3 * MonthsPerHalfyear)
    prevH1.end shouldEqual h1.start.plusMonths(-2 * MonthsPerHalfyear)

    var nextH1 = h1.addHalfyears(1)
    nextH1.halfyear shouldEqual Halfyear.Second
    nextH1.year shouldEqual currentYear
    nextH1.start shouldEqual h1.start.plusMonths(MonthsPerHalfyear)
    nextH1.end shouldEqual h1.start.plusMonths(2 * MonthsPerHalfyear)

    nextH1 = h1.addHalfyears(2)
    nextH1.halfyear shouldEqual Halfyear.First
    nextH1.year shouldEqual ( currentYear + 1 )
    nextH1.start shouldEqual h1.start.plusMonths(2 * MonthsPerHalfyear)
    nextH1.end shouldEqual h1.start.plusMonths(3 * MonthsPerHalfyear)

    nextH1 = h1.addHalfyears(3)
    nextH1.halfyear shouldEqual Halfyear.Second
    nextH1.year shouldEqual ( currentYear + 1 )
    nextH1.start shouldEqual h1.start.plusMonths(3 * MonthsPerHalfyear)
    nextH1.end shouldEqual h1.start.plusMonths(4 * MonthsPerHalfyear)
  }

}
