package com.github.debop4s.timeperiod.tests.calendars

import com.github.debop4s.timeperiod._
import com.github.debop4s.timeperiod.calendars.CalendarDateAdd
import com.github.debop4s.timeperiod.tests.AbstractTimePeriodTest
import com.github.debop4s.timeperiod.timerange.DayRange
import com.github.debop4s.timeperiod.utils.Times._
import com.github.debop4s.timeperiod.utils.{Durations, Times}
import org.joda.time.DateTime

/**
 * CalendarDateAddTest
 * Created by debop on 2014. 2. 18.
 */
class CalendarDateAddTest extends AbstractTimePeriodTest {

    test("no period") {
        val calendarDateAdd = CalendarDateAdd()
        val now = Times.now

        (-10 until 20).par.foreach(index => {
            val offset = index * 5

            calendarDateAdd.add(now, Durations.days(offset)) should equal(now + offset.day)
            calendarDateAdd.add(now, Durations.days(-offset)) should equal(now - offset.day)

            calendarDateAdd.subtract(now, Durations.days(offset)) should equal(now - offset.day)
            calendarDateAdd.subtract(now, Durations.days(-offset)) should equal(now + offset.day)
        })
    }

    test("period limits add") {
        val start = asDate(2011, 4, 12)
        val period1 = TimeRange(asDate(2011, 4, 20), asDate(2011, 4, 25))
        val period2 = TimeRange(asDate(2011, 4, 30), null.asInstanceOf[DateTime])

        val dateAdd = CalendarDateAdd()

        // 예외기간을 설정합니다. 4월 20일 ~ 4월25일, 4월 30일 이후
        dateAdd.excludePeriods.addAll(period1, period2)

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

        val dateAdd = CalendarDateAdd()

        // 예외기간을 설정합니다. 4월 6일 이전, 4월 20일 ~ 4월 25일
        dateAdd.excludePeriods.addAll(period1, period2)

        dateAdd.subtract(start, Durations.Day) should equal(start - 1.day)

        // 4월 30일로부터 5일 전이라면 4월 25일이지만, 제외기간이므로 4월 20일이 된다.
        dateAdd.subtract(start, Durations.days(5)) should equal(period1.start)

        // 4월 30일로부터 20일 전이라면, 5일 전이 4월 20일이므로, 4월 5일이 된다. 근데 4월 6일 이전은 모두 제외 기간이므로 null 을 반환한다.
        dateAdd.subtract(start, Durations.days(20)) should equal(null)
    }

    test("exclude") {
        val start = asDate(2011, 4, 12)
        val period = TimeRange(asDate(2011, 4, 15), asDate(2011, 4, 20))

        val dateAdd = CalendarDateAdd()

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

        val dateAdd = CalendarDateAdd()
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

    test("calendar date add SeekBoundary mode") {
        val dateAdd = CalendarDateAdd()

        dateAdd.addWorkingWeekDays()
        dateAdd.excludePeriods.add(DayRange(asDate(2011, 4, 4), dateAdd.calendar))
        dateAdd.workingHours += HourRangeInDay(8, 18)

        val start = new DateTime(2011, 4, 1, 9, 0)

        dateAdd.add(start, Durations.hours(29), SeekBoundaryMode.Fill) should equal(new DateTime(2011, 4, 6, 18, 0, 0))
        dateAdd.add(start, Durations.hours(29), SeekBoundaryMode.Next) should equal(new DateTime(2011, 4, 7, 8, 0, 0))
        dateAdd.add(start, Durations.hours(29)) should equal(new DateTime(2011, 4, 7, 8, 0, 0))
    }

    test("calendar dateAdd 1") {
        val dateAdd = CalendarDateAdd()

        dateAdd.addWorkingWeekDays()
        dateAdd.excludePeriods.add(DayRange(2011, 4, 4, dateAdd.calendar))
        dateAdd.workingHours += HourRangeInDay(8, 18)

        val start = new DateTime(2011, 4, 1, 9, 0)

        dateAdd.add(start, Durations.hours(22)) should equal(new DateTime(2011, 4, 6, 11, 0, 0))
        dateAdd.add(start, Durations.hours(22), SeekBoundaryMode.Fill) should equal(new DateTime(2011, 4, 6, 11, 0, 0))

        dateAdd.add(start, Durations.hours(29)) should equal(new DateTime(2011, 4, 7, 8, 0, 0))
        dateAdd.add(start, Durations.hours(29), SeekBoundaryMode.Fill) should equal(new DateTime(2011, 4, 6, 18, 0, 0))
    }

    test("calendar dateAdd 2") {
        val dateAdd = CalendarDateAdd()

        dateAdd.addWorkingWeekDays()
        dateAdd.excludePeriods.add(DayRange(2011, 4, 4, dateAdd.calendar))
        dateAdd.workingHours ++= List(HourRangeInDay(8, 12), HourRangeInDay(13, 18))


        val start = new DateTime(2011, 4, 1, 9, 0)

        dateAdd.add(start, Durations.hours(3)) should equal(new DateTime(2011, 4, 1, 13, 0, 0))
        dateAdd.add(start, Durations.hours(4)) should equal(new DateTime(2011, 4, 1, 14, 0, 0))
        dateAdd.add(start, Durations.hours(8)) should equal(new DateTime(2011, 4, 5, 8, 0, 0))
    }


    test("calendar dateAdd 3") {
        val dateAdd = CalendarDateAdd()

        dateAdd.addWorkingWeekDays()
        dateAdd.excludePeriods.add(DayRange(2011, 4, 4, dateAdd.calendar))
        dateAdd.workingHours ++= List(HourRangeInDay(Timepart(8, 30), Timepart(12)),
                                         HourRangeInDay(Timepart(13, 30), Timepart(18)))


        val start = new DateTime(2011, 4, 1, 9, 0)

        dateAdd.add(start, Durations.hours(3)) should equal(new DateTime(2011, 4, 1, 13, 30, 0))
        dateAdd.add(start, Durations.hours(4)) should equal(new DateTime(2011, 4, 1, 14, 30, 0))
        dateAdd.add(start, Durations.hours(8)) should equal(new DateTime(2011, 4, 5, 9, 0, 0))
    }

    test("empty start week") {
        val dateAdd = CalendarDateAdd()

        dateAdd.addWorkingWeekDays()

        val start = new DateTime(2011, 4, 2, 13, 0, 0)
        val offset = Durations.hours(20)


        // 4월 2일(토), 4월 3일(일) 제외하면 4월 4일 0시부터 20시간
        dateAdd.add(start, Durations.hours(20)) should equal(new DateTime(2011, 4, 4, 20, 0, 0))

        // 4월 2일(토), 4월 3일(일) 제외하면 4월 4일 0시부터 24시간
        dateAdd.add(start, Durations.hours(24)) should equal(new DateTime(2011, 4, 5, 0, 0, 0))

        // 4월 2일(토), 4월 3일(일) 제외하면, 4월 4일부터 5일이면 주말인 4월 9일(토), 4월 10일(일) 제외한 4월 11일!!!
        dateAdd.add(start, Durations.days(5)) should equal(new DateTime(2011, 4, 11, 0, 0, 0))
    }

}
