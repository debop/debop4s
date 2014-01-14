package kr.debop4s.timeperiod.tests.timeranges

import kr.debop4s.timeperiod.tests.AbstractTimePeriodTest
import kr.debop4s.timeperiod.utils.Times
import kr.debop4s.timeperiod.timerange.DayRangeCollection

/**
 * kr.debop4s.timeperiod.tests.timeranges.DayRangeCollectionTest 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 14. 오후 3:23
 */
class DayRangeCollectionTest extends AbstractTimePeriodTest {

    test("single days") {
        val start = Times.asDate(2004, 2, 22)
        val days = new DayRangeCollection(start, 1)

        days.dayCount should equal (1)

        days.startYear should equal (start.getYear)
        days.startMonthOfYear should equal (start.getMonthOfYear)
        days.startDayOfMonth should equal (start.dayOfMonth())
    }

}
