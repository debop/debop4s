package kr.debop4s.timeperiod.tests.calendars

import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.calendars.DateAdd
import kr.debop4s.timeperiod.tests.AbstractTimePeriodTest
import kr.debop4s.timeperiod.timeline.TimeGapCalculator
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
    val dateAdd = new DateAdd()

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

    val dateAdd = new DateAdd()

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

    val dateAdd = new DateAdd()

    // 예외기간을 설정합니다. 4월 6일 이전, 4월 20일 ~ 4월 25일
    dateAdd.getExcludePeriods.addAll(period1, period2)

    dateAdd.subtract(start, Durations.Day) should equal(start - 1.day)

    // 4월 30일로부터 5일 전이라면 4월 25일이지만, 제외기간이므로 4월 20일이 된다.
    dateAdd.subtract(start, Durations.days(5)) should equal(period1.start)

    // 4월 30일로부터 20일 전이라면, 5일 전이 4월 20일이므로, 4월 5일이 된다. 근데 4월 6일 이전은 모두 제외 기간이므로 null 을 반환한다.
    dateAdd.subtract(start, Durations.days(20)) should equal(null)
  }

  test("simple gaps") {
    val limits = TimeRange(asDate(2011, 3, 1), asDate(2011, 3, 20))
    val calculator = new TimeGapCalculator()

    val excludeRange = TimeRange(asDate(2011, 3, 10), asDate(2011, 3, 15))
    val excludePeriods = TimePeriodCollection(excludeRange)

    val gaps = calculator.getGaps(excludePeriods, limits)
    gaps.size should equal(2)
    gaps(0).isSamePeriod(TimeRange(limits.start, excludeRange.start)) should equal(true)
    gaps(1).isSamePeriod(TimeRange(excludeRange.end, limits.end)) should equal(true)
  }

  test("period touching limits start") {
    val limits = TimeRange(asDate(2011, 3, 1), asDate(2011, 3, 20))
    val calculator = TimeGapCalculator()
    val excludePeriods = TimePeriodCollection(TimeRange(asDate(2011, 3, 1), asDate(2011, 3, 10)))

    val gaps = calculator.getGaps(excludePeriods, limits)
    gaps.size should equal(1)
    gaps(0).isSamePeriod(TimeRange(asDate(2011, 3, 10), asDate(2011, 3, 20))) should equal(true)
  }

  test("period touching limits end") {
    val limits = TimeRange(asDate(2011, 3, 1), asDate(2011, 3, 20))
    val calculator = TimeGapCalculator()
    val excludePeriods = TimePeriodCollection(TimeRange(asDate(2011, 3, 10), asDate(2011, 3, 20)))

    val gaps = calculator.getGaps(excludePeriods, limits)
    gaps.size should equal(1)
    gaps(0).isSamePeriod(TimeRange(asDate(2011, 3, 1), asDate(2011, 3, 10))) should equal(true)
  }

  test("moment period") {
    val limits = TimeRange(asDate(2011, 3, 1), asDate(2011, 3, 20))
    val calculator = TimeGapCalculator()
    val excludePeriods = TimePeriodCollection(TimeRange(asDate(2011, 3, 10), asDate(2011, 3, 10)))

    val gaps = calculator.getGaps(excludePeriods, limits)
    gaps.size should equal(1)
    gaps(0).isSamePeriod(limits) should equal(true)
  }

  test("touching periods") {
    val limits = TimeRange(asDate(2011, 3, 29), asDate(2011, 4, 1))
    val calculator = TimeGapCalculator()
    val excludePeriods =
      TimePeriodCollection(TimeRange(new DateTime(2011, 3, 30, 0, 0), new DateTime(2011, 3, 30, 8, 30)),
                            TimeRange(new DateTime(2011, 3, 30, 8, 30), new DateTime(2011, 3, 30, 12, 0)),
                            TimeRange(new DateTime(2011, 3, 30, 10, 0), new DateTime(2011, 3, 31, 0, 0)))

    val gaps = calculator.getGaps(excludePeriods, limits)
    gaps.size should equal(2)
    gaps(0).isSamePeriod(TimeRange(new DateTime(2011, 3, 29, 0, 0), new DateTime(2011, 3, 30, 0, 0))) should equal(true)
    gaps(1).isSamePeriod(TimeRange(new DateTime(2011, 3, 31, 0, 0), new DateTime(2011, 4, 1, 0, 0))) should equal(true)
  }

}
