package kr.debop4s.timeperiod.tests.calendars

import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.calendars.DateAdd
import kr.debop4s.timeperiod.tests.AbstractTimePeriodTest
import kr.debop4s.timeperiod.utils.Times._
import kr.debop4s.timeperiod.utils.{Durations, Times}
import org.joda.time.DateTime

/**
 * DateAddTest
 * Created by debop on 2014. 2. 17.
 */
class DateAddTest extends AbstractTimePeriodTest {

    test("no period") {
        val start = Times.asDate(2011, 4, 12)
        val dateAdd = DateAdd()

        dateAdd.add(start, Durations.Zero) should equal(start)
        dateAdd.add(start, Durations.days(1)) should equal(start + 1.day)
        dateAdd.add(start, Durations.days(-1)) should equal(start - 1.day)

        dateAdd.subtract(start, Durations.Zero) should equal(start)
        dateAdd.subtract(start, Durations.days(1)) should equal(start - 1.day)
        dateAdd.subtract(start, Durations.days(-1)) should equal(start + 1.day)
    }

    test("period limits add") {
        val start = asDate(2011, 4, 12)
        val period1 = TimeRange(asDate(2011, 4, 20), asDate(2011, 4, 25))
        val period2 = TimeRange(asDate(2011, 4, 30), null.asInstanceOf[DateTime])

        val dateAdd = DateAdd()

        // 예외기간을 설정합니다. 4월 20일 ~ 4월25일, 4월 30일 이후
        dateAdd.getExcludePeriods.addAll(period1, period2)

        dateAdd.add(start, Durations.Day) should equal(start + 1.day)

        // 4월 12일에 8일을 더하면 4월 20일이지만, 20~25일까지 제외되므로, 4월 25일이 된다.
        dateAdd.add(start, Durations.days(8)) should equal(period1.end)

        // 4월 12에 20일을 더하면 4월 20~25일을 제외한 후 계산하면 4월 30 이후가 된다. (5월 3일).
        // 하지만 4월 30 이후는 모두 제외되므로 결과값은 null이다.
        dateAdd.add(start, Durations.days(20)) should equal(null)

        dateAdd.subtract(start, Durations.days(3)) should equal(start - 3.day)
    }

    test("period limits subtract") {
        val start = asDate(2011, 4, 30)
        val period1 = TimeRange(asDate(2011, 4, 20), asDate(2011, 4, 25))
        val period2 = TimeRange(null, asDate(2011, 4, 6)) // 4월 6일까지

        val dateAdd = DateAdd()

        // 예외기간을 설정합니다. 4월 6일 이전, 4월 20일 ~ 4월 25일
        dateAdd.getExcludePeriods.addAll(period1, period2)

        dateAdd.subtract(start, Durations.Day) should equal(start - 1.day)

        // 4월 30일로부터 5일 전이라면 4월 25일이지만, 제외기간이므로 4월 20일이 된다.
        dateAdd.subtract(start, Durations.days(5)) should equal(period1.start)

        // 4월 30일로부터 20일 전이라면, 5일 전이 4월 20일이므로, 4월 5일이 된다. 근데 4월 6일 이전은 모두 제외 기간이므로 null 을 반환한다.
        dateAdd.subtract(start, Durations.days(20)) should equal(null)
    }

    test("include outside max") {
        val start = asDate(2011, 4, 12)
        val period = TimeRange(asDate(2011, 4, 20), null.asInstanceOf[DateTime])

        val dateAdd = DateAdd()
        dateAdd.includePeriods.add(period)

        dateAdd.add(start, Durations.Zero) should equal(period.start)
        dateAdd.add(start, Durations.days(1)) should equal(period.start + 1.day)

        dateAdd.subtract(start, Durations.Zero) should equal(null)
        dateAdd.subtract(start, Durations.days(1)) should equal(null)
    }

    test("include outside min") {
        val start = asDate(2011, 4, 12)
        val period = TimeRange(null, asDate(2011, 4, 10))

        val dateAdd = DateAdd()
        dateAdd.includePeriods.add(period)

        dateAdd.add(start, Durations.Zero) should equal(null)
        dateAdd.add(start, Durations.days(1)) should equal(null)

        dateAdd.subtract(start, Durations.Zero) should equal(period.end)
        dateAdd.subtract(start, Durations.days(1)) should equal(period.end - 1.day)
    }

    test("all excluded") {
        val start = asDate(2011, 4, 12)
        val period = TimeRange(asDate(2011, 4, 10), asDate(2011, 4, 20))

        val dateAdd = DateAdd()
        dateAdd.includePeriods.add(period)
        dateAdd.excludePeriods.add(period)

        dateAdd.add(start, Durations.Zero) should equal(null)
        dateAdd.add(start, Durations.year(2011)) should equal(null)

        dateAdd.subtract(start, Durations.Zero) should equal(null)
        dateAdd.subtract(start, Durations.year(2011)) should equal(null)
    }

    test("all excluded 2") {
        val start = asDate(2011, 4, 12)
        val period1 = TimeRange(asDate(2011, 4, 10), asDate(2011, 4, 20))
        val period2 = TimeRange(asDate(2011, 4, 10), asDate(2011, 4, 15))
        val period3 = TimeRange(asDate(2011, 4, 15), asDate(2011, 4, 20))

        val dateAdd = DateAdd()
        dateAdd.includePeriods.add(period1)
        dateAdd.excludePeriods.addAll(period2, period3)

        dateAdd.add(start, Durations.Zero) should equal(null)
        dateAdd.add(start, Durations.year(2011)) should equal(null)

        dateAdd.subtract(start, Durations.Zero) should equal(null)
        dateAdd.subtract(start, Durations.year(2011)) should equal(null)
    }

    test("all excluded 3") {
        val start = asDate(2011, 4, 12)
        val period1 = TimeRange(asDate(2011, 4, 10), asDate(2011, 4, 20))
        val period2 = TimeRange(asDate(2011, 4, 15), asDate(2011, 4, 20))

        val dateAdd = DateAdd()
        dateAdd.includePeriods.add(period1)
        dateAdd.excludePeriods.add(period2)

        dateAdd.add(start, Durations.Zero) should equal(start)
        dateAdd.add(start, Durations.days(1)) should equal(start + 1.day)
        dateAdd.add(start, Durations.days(2)) should equal(start + 2.day)
        dateAdd.add(start, Durations.days(3)) should equal(null)
    }

    test("period moment") {
        val start = asDate(2011, 4, 12)
        val period = TimeRange(start)

        val dateAdd = DateAdd()

        dateAdd.add(start, Durations.Zero) should equal(start)

        dateAdd.includePeriods.add(period)
        dateAdd.add(start, Durations.Zero) should equal(start)

        dateAdd.excludePeriods.add(period)
        dateAdd.add(start, Durations.Zero) should equal(start)

        dateAdd.includePeriods.clear()
        dateAdd.add(start, Durations.Zero) should equal(start)

        dateAdd.excludePeriods.clear()
        dateAdd.add(start, Durations.Zero) should equal(start)
    }

    test("include") {
        val start = asDate(2011, 4, 12)
        val period = TimeRange(asDate(2011, 4, 1), MaxPeriodTime)

        val dateAdd = DateAdd()

        dateAdd.includePeriods.add(period)

        dateAdd.add(start, Durations.Zero) should equal(start)
        dateAdd.add(start, Durations.days(1)) should equal(start + 1.day)
        dateAdd.add(start, Durations.days(365)) should equal(start + 365.day)
    }

    test("include split") {
        val start = asDate(2011, 4, 12)
        val period1 = TimeRange(asDate(2011, 4, 1), asDate(2011, 4, 15))
        val period2 = TimeRange(asDate(2011, 4, 20), asDate(2011, 4, 24))

        val dateAdd = DateAdd()
        dateAdd.includePeriods.addAll(period1, period2)

        dateAdd.add(start, Durations.Zero) should equal(start)
        dateAdd.add(start, Durations.days(1)) should equal(start + 1.day)
        dateAdd.add(start, Durations.days(3)) should equal(period2.start)
        dateAdd.add(start, Durations.days(5)) should equal(period2.start + 2.day)
        dateAdd.add(start, Durations.days(6)) should equal(period2.start + 3.day)
        dateAdd.add(start, Durations.days(7)) should equal(null)

        // NOTE: SeekboundaryMode에 따라 결과가 달라집니다.
        dateAdd.add(start, Durations.days(7), SeekBoundaryMode.Fill) should equal(period2.end)
        dateAdd.add(start, Durations.days(7), SeekBoundaryMode.Next) should equal(null)
    }

    test("exclude") {
        val start = asDate(2011, 4, 12)
        val period = TimeRange(asDate(2011, 4, 15), asDate(2011, 4, 20))

        val dateAdd = DateAdd()

        dateAdd.excludePeriods.add(period)

        dateAdd.add(start, Durations.Zero) should equal(start)
        dateAdd.add(start, Durations.days(1)) should equal(start + 1.day)
        dateAdd.add(start, Durations.days(2)) should equal(start + 2.day)
        dateAdd.add(start, Durations.days(3)) should equal(period.end)
        dateAdd.add(start, Durations.days(3, 0, 0, 0, 1)) should equal(period.end + 1.millis)
        dateAdd.add(start, Durations.days(5)) should equal(period.end + 2.day)
    }

    test("exclude split") {
        val start = asDate(2011, 4, 12)
        val period1 = TimeRange(asDate(2011, 4, 15), asDate(2011, 4, 20))
        val period2 = TimeRange(asDate(2011, 4, 22), asDate(2011, 4, 25))

        val dateAdd = DateAdd()
        dateAdd.excludePeriods.addAll(period1, period2)

        dateAdd.add(start, Durations.Zero) should equal(start)
        dateAdd.add(start, Durations.days(1)) should equal(start + 1.day)
        dateAdd.add(start, Durations.days(2)) should equal(start + 2.day)
        dateAdd.add(start, Durations.days(3)) should equal(period1.end)
        dateAdd.add(start, Durations.days(4)) should equal(period1.end + 1.day)
        dateAdd.add(start, Durations.days(5)) should equal(period2.end)
        dateAdd.add(start, Durations.days(6)) should equal(period2.end + 1.day)
        dateAdd.add(start, Durations.days(7)) should equal(period2.end + 2.day)
    }

    test("include equals exclude") {
        val start = asDate(2011, 3, 5)
        val period1 = TimeRange(asDate(2011, 3, 5), asDate(2011, 3, 10))
        val period2 = TimeRange(asDate(2011, 3, 5), asDate(2011, 3, 10))

        val dateAdd = DateAdd()
        dateAdd.includePeriods.add(period1)
        dateAdd.excludePeriods.add(period2)

        dateAdd.add(start, Durations.Zero) should equal(null)
        dateAdd.add(start, Durations.millis(1)) should equal(null)
        dateAdd.add(start, Durations.millis(-1)) should equal(null)

        dateAdd.subtract(start, Durations.Zero) should equal(null)
        dateAdd.subtract(start, Durations.millis(1)) should equal(null)
        dateAdd.subtract(start, Durations.millis(-1)) should equal(null)
    }

    test("include exclude") {

        val dateAdd = DateAdd()

        dateAdd.includePeriods.add(TimeRange(asDate(2011, 3, 17), asDate(2011, 4, 20)))

        // setup some periods to exclude
        dateAdd.excludePeriods.add(TimeRange(asDate(2011, 3, 22), asDate(2011, 3, 25)))
        dateAdd.excludePeriods.add(TimeRange(asDate(2011, 4, 1), asDate(2011, 4, 7)))
        dateAdd.excludePeriods.add(TimeRange(asDate(2011, 4, 15), asDate(2011, 4, 16)))

        // positive
        val periodStart = asDate(2011, 3, 19)

        dateAdd.add(periodStart, Durations.Hour) should equal(periodStart + 1.hour)
        dateAdd.add(periodStart, Durations.days(4)) should equal(asDate(2011, 3, 26))
        dateAdd.add(periodStart, Durations.days(17)) should equal(asDate(2011, 4, 14))
        dateAdd.add(periodStart, Durations.days(20)) should equal(asDate(2011, 4, 18))

        dateAdd.add(periodStart, Durations.days(22), SeekBoundaryMode.Fill) should equal(asDate(2011, 4, 20))
        dateAdd.add(periodStart, Durations.days(22), SeekBoundaryMode.Next) should equal(null)
        dateAdd.add(periodStart, Durations.days(22)) should equal(null)

        // negative
        val periodEnd = asDate(2011, 4, 18)

        dateAdd.add(periodEnd, Durations.hours(-1)) should equal(periodEnd - 1.hour)
        dateAdd.add(periodEnd, Durations.days(-4)) should equal(asDate(2011, 4, 13))
        dateAdd.add(periodEnd, Durations.days(-17)) should equal(asDate(2011, 3, 22))
        dateAdd.add(periodEnd, Durations.days(-20)) should equal(asDate(2011, 3, 19))

        dateAdd.add(periodEnd, Durations.days(-22), SeekBoundaryMode.Fill) should equal(asDate(2011, 3, 17))
        dateAdd.add(periodEnd, Durations.days(-22), SeekBoundaryMode.Next) should equal(null)
        dateAdd.add(periodEnd, Durations.days(-22)) should equal(null)
    }

    test("include exclude 2") {

        val dateAdd = DateAdd()

        dateAdd.includePeriods.add(TimeRange(asDate(2011, 3, 1), asDate(2011, 3, 5)))
        dateAdd.includePeriods.add(TimeRange(asDate(2011, 3, 5), asDate(2011, 3, 10)))
        dateAdd.includePeriods.add(TimeRange(asDate(2011, 3, 10), asDate(2011, 3, 15)))
        dateAdd.includePeriods.add(TimeRange(asDate(2011, 3, 15), asDate(2011, 3, 20)))
        dateAdd.includePeriods.add(TimeRange(asDate(2011, 3, 20), asDate(2011, 3, 25)))

        val periodStart = asDate(2011, 3, 1)
        val periodEnd = asDate(2011, 3, 25)

        // add from start
        dateAdd.add(periodStart, Durations.Zero) should equal(periodStart)
        dateAdd.add(periodStart, Durations.days(1)) should equal(asDate(2011, 3, 2))
        dateAdd.add(periodStart, Durations.days(2)) should equal(asDate(2011, 3, 3))
        dateAdd.add(periodStart, Durations.days(3)) should equal(asDate(2011, 3, 4))
        dateAdd.add(periodStart, Durations.days(4)) should equal(asDate(2011, 3, 10))
        dateAdd.add(periodStart, Durations.days(5)) should equal(asDate(2011, 3, 11))
        dateAdd.add(periodStart, Durations.days(8)) should equal(asDate(2011, 3, 14))
        dateAdd.add(periodStart, Durations.days(9)) should equal(asDate(2011, 3, 20))
        dateAdd.add(periodStart, Durations.days(10)) should equal(asDate(2011, 3, 21))

        dateAdd.add(periodStart, Durations.days(14), SeekBoundaryMode.Fill) should equal(asDate(2011, 3, 25))
        dateAdd.add(periodStart, Durations.days(14), SeekBoundaryMode.Next) should equal(null)
        dateAdd.add(periodStart, Durations.days(14)) should equal(null)

        // add from end
        dateAdd.add(periodEnd, Durations.Zero) should equal(periodEnd)
        dateAdd.add(periodEnd, Durations.days(-1)) should equal(periodEnd - 1.day)
        dateAdd.add(periodEnd, Durations.days(-5)) should equal(asDate(2011, 3, 15))
        dateAdd.add(periodEnd, Durations.days(-6)) should equal(asDate(2011, 3, 14))
        dateAdd.add(periodEnd, Durations.days(-10)) should equal(asDate(2011, 3, 5))
        dateAdd.add(periodEnd, Durations.days(-11)) should equal(asDate(2011, 3, 4))

        dateAdd.add(periodEnd, Durations.days(-14), SeekBoundaryMode.Fill) should equal(asDate(2011, 3, 1))
        dateAdd.add(periodEnd, Durations.days(-14), SeekBoundaryMode.Next) should equal(null)
        dateAdd.add(periodEnd, Durations.days(-14)) should equal(null)


        // subtract from end
        dateAdd.subtract(periodEnd, Durations.Zero) should equal(periodEnd)
        dateAdd.subtract(periodEnd, Durations.days(1)) should equal(periodEnd - 1.day)
        dateAdd.subtract(periodEnd, Durations.days(5)) should equal(asDate(2011, 3, 15))
        dateAdd.subtract(periodEnd, Durations.days(6)) should equal(asDate(2011, 3, 14))
        dateAdd.subtract(periodEnd, Durations.days(10)) should equal(asDate(2011, 3, 5))
        dateAdd.subtract(periodEnd, Durations.days(11)) should equal(asDate(2011, 3, 4))

        dateAdd.subtract(periodEnd, Durations.days(14), SeekBoundaryMode.Fill) should equal(asDate(2011, 3, 1))
        dateAdd.subtract(periodEnd, Durations.days(14), SeekBoundaryMode.Next) should equal(null)
        dateAdd.subtract(periodEnd, Durations.days(14)) should equal(null)

        // subtract from start
        dateAdd.subtract(periodStart, Durations.Zero) should equal(periodStart)
        dateAdd.subtract(periodStart, Durations.days(-1)) should equal(periodStart + 1.day)
        dateAdd.subtract(periodStart, Durations.days(-3)) should equal(asDate(2011, 3, 4))
        dateAdd.subtract(periodStart, Durations.days(-4)) should equal(asDate(2011, 3, 10))
        dateAdd.subtract(periodStart, Durations.days(-5)) should equal(asDate(2011, 3, 11))
        dateAdd.subtract(periodStart, Durations.days(-8)) should equal(asDate(2011, 3, 14))
        dateAdd.subtract(periodStart, Durations.days(-9)) should equal(asDate(2011, 3, 20))
        dateAdd.subtract(periodStart, Durations.days(-10)) should equal(asDate(2011, 3, 21))

        dateAdd.subtract(periodStart, Durations.days(-14), SeekBoundaryMode.Fill) should equal(asDate(2011, 3, 25))
        dateAdd.subtract(periodStart, Durations.days(-14), SeekBoundaryMode.Next) should equal(null)
        dateAdd.subtract(periodStart, Durations.days(-14)) should equal(null)
    }

    test("include exclude 3") {

        val dateAdd = DateAdd()

        // setup some periods to exclude
        dateAdd.excludePeriods.add(TimeRange(asDate(2011, 3, 5), asDate(2011, 3, 10)))
        dateAdd.excludePeriods.add(TimeRange(asDate(2011, 3, 10), asDate(2011, 3, 15)))
        dateAdd.excludePeriods.add(TimeRange(asDate(2011, 3, 15), asDate(2011, 3, 20)))

        val start = asDate(2011, 3, 10)

        dateAdd.add(start, Durations.Zero) should equal(start)
        dateAdd.add(start, Durations.days(1)) should equal(start + 1.day)
        dateAdd.add(start, Durations.days(5), SeekBoundaryMode.Fill) should equal(start + 5.day)
        dateAdd.add(start, Durations.days(5), SeekBoundaryMode.Next) should equal(null)
    }

    test("include exclude 4") {
        val dateAdd = DateAdd()

        dateAdd.includePeriods.add(TimeRange(asDate(2011, 3, 10), asDate(2011, 3, 20)))

        dateAdd.excludePeriods.add(TimeRange(asDate(2011, 3, 10), asDate(2011, 3, 15)))
        dateAdd.excludePeriods.add(TimeRange(asDate(2011, 3, 15), asDate(2011, 3, 20)))

        val start = asDate(2011, 3, 10)

        dateAdd.add(start, Durations.Zero) should equal(null)
        dateAdd.add(start, Durations.days(1)) should equal(null)
        dateAdd.add(start, Durations.days(5), SeekBoundaryMode.Fill) should equal(null)
        dateAdd.add(start, Durations.days(5), SeekBoundaryMode.Next) should equal(null)
    }

    test("include exclude 5") {
        val dateAdd = DateAdd()

        dateAdd.includePeriods.add(TimeRange(asDate(2011, 3, 10), asDate(2011, 3, 20)))

        dateAdd.excludePeriods.add(TimeRange(asDate(2011, 3, 5), asDate(2011, 3, 15)))
        dateAdd.excludePeriods.add(TimeRange(asDate(2011, 3, 15), asDate(2011, 3, 30)))

        val start = asDate(2011, 3, 10)

        dateAdd.add(start, Durations.Zero) should equal(null)
        dateAdd.add(start, Durations.days(1)) should equal(null)
        dateAdd.add(start, Durations.days(-1)) should equal(null)

        dateAdd.subtract(start, Durations.Zero) should equal(null)
        dateAdd.subtract(start, Durations.days(1)) should equal(null)
        dateAdd.subtract(start, Durations.days(-1)) should equal(null)
    }

    test("include exclude 6") {
        val dateAdd = DateAdd()

        dateAdd.includePeriods.add(TimeRange(asDate(2011, 3, 10), asDate(2011, 3, 20)))

        dateAdd.excludePeriods.add(TimeRange(asDate(2011, 3, 5), asDate(2011, 3, 12)))
        dateAdd.excludePeriods.add(TimeRange(asDate(2011, 3, 18), asDate(2011, 3, 30)))

        val start = asDate(2011, 3, 10)

        dateAdd.add(start, Durations.Zero) should equal(asDate(2011, 3, 12))
        dateAdd.add(start, Durations.days(1)) should equal(asDate(2011, 3, 13))
    }
}
