package com.github.debop4s.timeperiod.tests.timelines

import com.github.debop4s.timeperiod.tests.AbstractTimePeriodTest
import com.github.debop4s.timeperiod.tests.samples.SchoolDay
import com.github.debop4s.timeperiod.timeline.TimeGapCalculator
import com.github.debop4s.timeperiod.timerange.{CalendarTimeRange, DayRangeCollection, MonthRange}
import com.github.debop4s.timeperiod.utils.Times._
import com.github.debop4s.timeperiod.utils.{Durations, Times}
import com.github.debop4s.timeperiod.{TimeCalendar, TimeRange, TimePeriodCollection}
import org.joda.time.DateTime

/**
 * com.github.debop4s.timeperiod.tests.timelines.TimeGapCalendarTest
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 12. 오후 7:53
 */
class TimeGapCalendarTest extends AbstractTimePeriodTest {

  val limits = TimeRange(asDate(2011, 3, 1), asDate(2011, 3, 5))
  val calculator = TimeGapCalculator()

  test("no periods") {
    val gaps = calculator.getGaps(TimePeriodCollection(), limits)
    gaps.size shouldBe 1
    gaps(0).isSamePeriod(limits) shouldBe true
  }

  test("period equals limits when excludePeriods has limits") {
    val excludePeriods = TimePeriodCollection(limits)
    val gaps = calculator.getGaps(excludePeriods, limits)
    gaps.size shouldBe 0
  }

  test("period is larger than limits") {
    val excludePeriods = TimePeriodCollection(TimeRange(asDate(2011, 2, 1), asDate(2011, 4, 1)))

    val gaps = calculator.getGaps(excludePeriods, limits)
    gaps.size shouldBe 0
  }

  test("period is outside with limits") {
    val excludePeriods = TimePeriodCollection(TimeRange(asDate(2011, 2, 1), asDate(2011, 2, 5)),
      TimeRange(asDate(2011, 4, 1), asDate(2011, 4, 5)))
    val gaps = calculator.getGaps(excludePeriods, limits)

    gaps.size shouldBe 1
    gaps(0).isSamePeriod(limits) shouldBe true
  }

  test("period is outside touching limits") {
    val limits = new MonthRange(2011, 3)
    val excludePeriods = TimePeriodCollection(TimeRange(asDate(2011, 2, 1), asDate(2011, 3, 5)),
      TimeRange(asDate(2011, 3, 20), asDate(2011, 4, 15)))
    val gaps = calculator.getGaps(excludePeriods, limits)

    gaps.size shouldBe 1
    gaps(0).isSamePeriod(TimeRange(asDate(2011, 3, 5), asDate(2011, 3, 20))) shouldBe true
  }

  test("simple gaps") {
    val limits = TimeRange(asDate(2011, 3, 1), asDate(2011, 3, 20))

    val excludeRange = TimeRange(asDate(2011, 3, 10), asDate(2011, 3, 15))
    val excludePeriods = TimePeriodCollection(excludeRange)

    val gaps = calculator.getGaps(excludePeriods, limits)
    gaps.size should equal(2)
    gaps(0).isSamePeriod(TimeRange(limits.start, excludeRange.start)) should equal(true)
    gaps(1).isSamePeriod(TimeRange(excludeRange.end, limits.end)) should equal(true)
  }

  test("period touching limits start") {
    val limits = TimeRange(asDate(2011, 3, 1), asDate(2011, 3, 20))
    val excludePeriods = TimePeriodCollection(TimeRange(asDate(2011, 3, 1), asDate(2011, 3, 10)))

    val gaps = calculator.getGaps(excludePeriods, limits)
    gaps.size should equal(1)
    gaps(0).isSamePeriod(TimeRange(asDate(2011, 3, 10), asDate(2011, 3, 20))) should equal(true)
  }

  test("period touching limits end") {
    val limits = TimeRange(asDate(2011, 3, 1), asDate(2011, 3, 20))
    val excludePeriods = TimePeriodCollection(TimeRange(asDate(2011, 3, 10), asDate(2011, 3, 20)))

    val gaps = calculator.getGaps(excludePeriods, limits)
    gaps.size should equal(1)
    gaps(0).isSamePeriod(TimeRange(asDate(2011, 3, 1), asDate(2011, 3, 10))) should equal(true)
  }

  test("moment period") {
    val limits = TimeRange(asDate(2011, 3, 1), asDate(2011, 3, 20))
    val excludePeriods = TimePeriodCollection(TimeRange(asDate(2011, 3, 10), asDate(2011, 3, 10)))

    val gaps = calculator.getGaps(excludePeriods, limits)
    gaps.size should equal(1)
    gaps(0).isSamePeriod(limits) should equal(true)
  }

  test("touching periods") {
    val limits = TimeRange(asDate(2011, 3, 29), asDate(2011, 4, 1))

    val excludePeriods =
      TimePeriodCollection(TimeRange(new DateTime(2011, 3, 30, 0, 0), new DateTime(2011, 3, 30, 8, 30)),
        TimeRange(new DateTime(2011, 3, 30, 8, 30), new DateTime(2011, 3, 30, 12, 0)),
        TimeRange(new DateTime(2011, 3, 30, 10, 0), new DateTime(2011, 3, 31, 0, 0)))

    val gaps = calculator.getGaps(excludePeriods, limits)
    gaps.size should equal(2)
    gaps(0).isSamePeriod(TimeRange(new DateTime(2011, 3, 29, 0, 0), new DateTime(2011, 3, 30, 0, 0))) should equal(true)
    gaps(1).isSamePeriod(TimeRange(new DateTime(2011, 3, 31, 0, 0), new DateTime(2011, 4, 1, 0, 0))) should equal(true)
  }

  test("overlapping periods 1") {
    val limits = TimeRange(asDate(2011, 3, 29), asDate(2011, 4, 1))

    val excludePeriods =
      TimePeriodCollection(TimeRange(new DateTime(2011, 3, 30, 0, 0), new DateTime(2011, 3, 31, 0, 0)),
        TimeRange(new DateTime(2011, 3, 30, 0, 0), new DateTime(2011, 3, 30, 12, 0)),
        TimeRange(new DateTime(2011, 3, 30, 12, 0), new DateTime(2011, 3, 31, 0, 0)))

    val gaps = calculator.getGaps(excludePeriods, limits)
    gaps.size should equal(2)
    gaps(0).isSamePeriod(TimeRange(new DateTime(2011, 3, 29, 0, 0), new DateTime(2011, 3, 30, 0, 0))) should equal(true)
    gaps(1).isSamePeriod(TimeRange(new DateTime(2011, 3, 31, 0, 0), new DateTime(2011, 4, 1, 0, 0))) should equal(true)
  }

  test("overlapping periods 2") {
    val limits = TimeRange(asDate(2011, 3, 29), asDate(2011, 4, 1))

    val excludePeriods =
      TimePeriodCollection(TimeRange(new DateTime(2011, 3, 30, 0, 0), new DateTime(2011, 3, 31, 0, 0)),
        TimeRange(new DateTime(2011, 3, 30, 0, 0), new DateTime(2011, 3, 30, 6, 30)),
        TimeRange(new DateTime(2011, 3, 30, 8, 30), new DateTime(2011, 3, 30, 12, 0)),
        TimeRange(new DateTime(2011, 3, 30, 22, 30), new DateTime(2011, 3, 31, 0, 0)))

    val gaps = calculator.getGaps(excludePeriods, limits)
    gaps.size should equal(2)
    gaps(0).isSamePeriod(TimeRange(new DateTime(2011, 3, 29, 0, 0), new DateTime(2011, 3, 30, 0, 0))) should equal(true)
    gaps(1).isSamePeriod(TimeRange(new DateTime(2011, 3, 31, 0, 0), new DateTime(2011, 4, 1, 0, 0))) should equal(true)
  }

  test("overlapping periods 3") {
    val limits = TimeRange(asDate(2011, 3, 29), asDate(2011, 4, 1))

    val excludePeriods =
      TimePeriodCollection(TimeRange(new DateTime(2011, 3, 30, 0, 0), new DateTime(2011, 3, 31, 0, 0)),
        TimeRange(new DateTime(2011, 3, 30, 0, 0), new DateTime(2011, 3, 31, 0, 0)))

    val gaps = calculator.getGaps(excludePeriods, limits)
    gaps.size should equal(2)
    gaps(0).isSamePeriod(TimeRange(new DateTime(2011, 3, 29, 0, 0), new DateTime(2011, 3, 30, 0, 0))) should equal(true)
    gaps(1).isSamePeriod(TimeRange(new DateTime(2011, 3, 31, 0, 0), new DateTime(2011, 4, 1, 0, 0))) should equal(true)
  }

  test("get gap") {
    val now = Times.now
    val schoolDay = new SchoolDay(now)

    val excludePeriods = TimePeriodCollection()
    excludePeriods.addAll(schoolDay)

    calculator.getGaps(excludePeriods).size should equal(0)
    calculator.getGaps(excludePeriods, schoolDay).size should equal(0)

    excludePeriods.clear()
    excludePeriods.add(schoolDay.lesson1)
    excludePeriods.add(schoolDay.lesson2)
    excludePeriods.add(schoolDay.lesson3)
    excludePeriods.add(schoolDay.lesson4)

    val gap2 = calculator.getGaps(excludePeriods)
    gap2.size should equal(3)
    gap2(0).isSamePeriod(schoolDay.break1) should equal(true)
    gap2(1).isSamePeriod(schoolDay.break2) should equal(true)
    gap2(2).isSamePeriod(schoolDay.break3) should equal(true)

    val testRange3 = TimeRange(schoolDay.lesson1.start, schoolDay.lesson4.end)
    val gap3 = calculator.getGaps(excludePeriods, testRange3)
    gap3.size should equal(3)
    gap3(0).isSamePeriod(schoolDay.break1) should equal(true)
    gap3(1).isSamePeriod(schoolDay.break2) should equal(true)
    gap3(2).isSamePeriod(schoolDay.break3) should equal(true)

    val testRange4 = TimeRange(schoolDay.start.minusHours(1), schoolDay.end.plusHours(1))
    val gap4 = calculator.getGaps(excludePeriods, testRange4)
    gap4.size should equal(5)
    gap4(0).isSamePeriod(TimeRange(testRange4.start, schoolDay.start)) should equal(true)
    gap4(1).isSamePeriod(schoolDay.break1) should equal(true)
    gap4(2).isSamePeriod(schoolDay.break2) should equal(true)
    gap4(3).isSamePeriod(schoolDay.break3) should equal(true)
    gap4(4).isSamePeriod(TimeRange(schoolDay.end, testRange4.end)) should equal(true)

    excludePeriods.clear()
    excludePeriods.add(schoolDay.lesson1)
    val gap8 = calculator.getGaps(excludePeriods, schoolDay.lesson1)
    gap8.size should equal(0)

    excludePeriods.clear()
    excludePeriods.add(schoolDay.lesson1)

    val testRange9 = TimeRange(schoolDay.lesson1.start.minus(1), schoolDay.lesson1.end.plus(1))
    val gap9 = calculator.getGaps(excludePeriods, testRange9)

    gap9.size should equal(2)
    gap9(0).duration should equal(Durations.Millisecond)
    gap9(1).duration should equal(Durations.Millisecond)
  }

  test("calendar get gap") {
    val calendars = Array(TimeCalendar.getDefault, TimeCalendar.getEmptyOffset)

    calendars.foreach(calendar => {

      // simulation of same reservations
      val excludePeriods = TimePeriodCollection(DayRangeCollection(2011, 3, 7, 2, calendar),
        DayRangeCollection(2011, 3, 16, 2, calendar))

      // overall search ranges
      val limits = CalendarTimeRange(asDate(2011, 3, 4), asDate(2011, 3, 21), calendar)
      val days = DayRangeCollection(limits.start, limits.duration.getStandardDays.toInt + 1, calendar)

      // limits의 내부이고, 주말인 DayRange를 제외목록에 추가합니다.
      days.getDays.foreach(day => {
        if (limits.hasInside(day) && Times.isWeekend(day.dayOfWeek)) {
          excludePeriods.add(day)
        }
      })

      val calculator = TimeGapCalculator(calendar)
      val gaps = calculator.getGaps(excludePeriods, limits)

      gaps.foreach(gap => log.trace(s"$gap"))

      gaps.size should equal(4)
      gaps(0).isSamePeriod(TimeRange(asDate(2011, 3, 4), Durations.days(1))) should equal(true)
      gaps(1).isSamePeriod(TimeRange(asDate(2011, 3, 9), Durations.days(3))) should equal(true)
      gaps(2).isSamePeriod(TimeRange(asDate(2011, 3, 14), Durations.days(2))) should equal(true)
      gaps(3).isSamePeriod(TimeRange(asDate(2011, 3, 18), Durations.days(1))) should equal(true)

    })
  }
}
