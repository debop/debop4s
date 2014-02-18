package kr.debop4s.timeperiod.tests.timeranges

import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.tests.AbstractTimePeriodTest
import kr.debop4s.timeperiod.timerange.YearRange
import kr.debop4s.timeperiod.utils.Times
import kr.debop4s.timeperiod.utils.Times._

/**
 * kr.debop4s.timeperiod.tests.timeranges.YearRangeTest 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 17. 오전 10:56
 */
class YearRangeTest extends AbstractTimePeriodTest {

    test("init values") {
        val now = Times.now
        val thisYear = Times.startTimeOfYear(now)
        val nextYear = thisYear + 1.year

        val yearRange = YearRange(now, EmptyOffsetTimeCalendar)

        yearRange.start should equal(thisYear)
        yearRange.end should equal(nextYear)
    }

    test("startYear") {
        val currentYear = Times.currentYear.getYear

        YearRange(2008).year should equal(2008)
        YearRange(currentYear).year should equal(currentYear)

        YearRange(asDate(2014, 2, 14)).year should equal(2014)
    }

    test("year index") {
        val yearIndex = 1994
        val yr = YearRange(yearIndex, EmptyOffsetTimeCalendar)

        yr.isReadonly should equal(true)
        yr.start should equal(Times.startTimeOfYear(yearIndex))
        yr.end should equal(Times.startTimeOfYear(yearIndex + 1))
    }

    test("add years") {
        val now = Times.now
        val startYear = Times.startTimeOfYear(now)
        val yr = YearRange(now)

        yr.previousYear.start should equal(startYear - 1.year)
        yr.nextYear.start should equal(startYear + 1.year)

        (-60 to 120).par.foreach(y => {
            yr.addYears(y).start should equal(startYear + y.year)
        })
    }
}
