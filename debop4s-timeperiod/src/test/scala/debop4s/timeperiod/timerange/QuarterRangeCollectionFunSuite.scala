package debop4s.timeperiod.timerange

import debop4s.timeperiod._
import debop4s.timeperiod.tests.AbstractTimeFunSuite
import debop4s.timeperiod.utils.Times._

class QuarterRangeCollectionFunSuite extends AbstractTimeFunSuite {

  test("year test") {
    val moment = asDate(2009, 2, 15)
    val year = yearOf(moment.getYear, moment.getMonthOfYear)
    val qr = QuarterRangeCollection(moment, 3)

    qr.startYear shouldEqual year
    qr.start shouldEqual asDate(year, 1, 1)
  }

  test("single quarter") {
    val startYear = 2004
    val startQuarter = Quarter.Second

    val qr = QuarterRangeCollection(startYear, startQuarter, 1)

    qr.quarterCount should equal(1)
    qr.startYear should equal(startYear)
    qr.startQuarter should equal(startQuarter)
    qr.endYear should equal(startYear)
    qr.endQuarter should equal(startQuarter)

    val quarters = qr.quartersView
    quarters.size should equal(1)
    quarters(0).isSamePeriod(QuarterRange(startYear, startQuarter)) should equal(true)
  }

  test("first calendar halfyearsView") {
    val startYear = 2004
    val startQuarter = Quarter.First
    val quarterCount = 5

    val qrs = QuarterRangeCollection(startYear, startQuarter, quarterCount)

    qrs.quarterCount should equal(quarterCount)
    qrs.startYear should equal(startYear)
    qrs.startQuarter should equal(startQuarter)
    qrs.endYear should equal(startYear + 1)
    qrs.endQuarter should equal(Quarter.First)

    val quarters = qrs.quartersView

    quarters.size should equal(quarterCount)
    quarters(0).isSamePeriod(QuarterRange(startYear, Quarter.First)) should equal(true)
    quarters(1).isSamePeriod(QuarterRange(startYear, Quarter.Second)) should equal(true)
    quarters(2).isSamePeriod(QuarterRange(startYear, Quarter.Third)) should equal(true)
    quarters(3).isSamePeriod(QuarterRange(startYear, Quarter.Fourth)) should equal(true)
    quarters(4).isSamePeriod(QuarterRange(startYear + 1, Quarter.First)) should equal(true)
  }

  test("second calendar halfyearsView") {
    val startYear = 2004
    val startQuarter = Quarter.Second
    val quarterCount = 5

    val qrs = QuarterRangeCollection(startYear, startQuarter, quarterCount)

    qrs.quarterCount should equal(quarterCount)
    qrs.startYear should equal(startYear)
    qrs.startQuarter should equal(startQuarter)
    qrs.endYear should equal(startYear + 1)
    qrs.endQuarter should equal(Quarter.Second)

    val quarters = qrs.quartersView

    quarters.size should equal(quarterCount)
    quarters(0).isSamePeriod(QuarterRange(startYear, Quarter.Second)) should equal(true)
    quarters(1).isSamePeriod(QuarterRange(startYear, Quarter.Third)) should equal(true)
    quarters(2).isSamePeriod(QuarterRange(startYear, Quarter.Fourth)) should equal(true)
    quarters(3).isSamePeriod(QuarterRange(startYear + 1, Quarter.First)) should equal(true)
    quarters(4).isSamePeriod(QuarterRange(startYear + 1, Quarter.Second)) should equal(true)
  }

}
