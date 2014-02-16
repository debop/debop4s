package kr.debop4s.timeperiod.tests.timeranges

import kr.debop4s.timeperiod.tests.AbstractTimePeriodTest
import kr.debop4s.timeperiod.timerange.{MonthRange, MonthRangeCollection}

/**
 * MonthRangeCollectionTest
 * Created by debop on 2014. 2. 16.
 */
class MonthRangeCollectionTest extends AbstractTimePeriodTest {

  test("single month") {
    val startYear = 2004
    val startMonth = 6

    val mrs = MonthRangeCollection(startYear, startMonth, 1)
    mrs.monthCount should equal(1)

    val months = mrs.getMonths
    months.size should equal(1)
    months(0).isSamePeriod(new MonthRange(startYear, startMonth)) should equal(true)

    mrs.startYear should equal(startYear)
    mrs.endYear should equal(startYear)
    mrs.startMonthOfYear should equal(startMonth)
    mrs.endMonthOfYear should equal(startMonth)
  }
}
