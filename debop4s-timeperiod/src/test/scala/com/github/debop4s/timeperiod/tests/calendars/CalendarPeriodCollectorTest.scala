package com.github.debop4s.timeperiod.tests.calendars

import com.github.debop4s.timeperiod._
import com.github.debop4s.timeperiod.calendars.{CalendarPeriodCollector, CalendarPeriodCollectorFilter}
import com.github.debop4s.timeperiod.tests.AbstractTimePeriodTest
import com.github.debop4s.timeperiod.timerange._
import com.github.debop4s.timeperiod.utils.Times._
import com.github.debop4s.timeperiod.{Timepart, HourRangeInDay, DayOfWeek, Month}

/**
 * CalendarPeriodCollectorTest
 * Created by debop on 2014. 2. 18.
 */
class CalendarPeriodCollectorTest extends AbstractTimePeriodTest {

    test("collect years") {
        val filter = new CalendarPeriodCollectorFilter()

        filter.years ++= List(2006, 2007, 2012)
        val limits = CalendarTimeRange(asDate(2001, 1, 1), asDate(2019, 12, 31))
        val collector = new CalendarPeriodCollector(filter, limits)

        collector.collectYears()

        log.trace(s"Collect years... periods=${collector.periods }")

        var i = 0
        collector.periods.foreach { period =>
            log.trace(s"period=$period")
            period.isSamePeriod(YearRange(filter.years(i))) should equal(true)
            i += 1
        }
    }

    test("collect months") {
        val filter = new CalendarPeriodCollectorFilter()

        filter.monthOfYears += Month.January.id
        val limits = CalendarTimeRange(asDate(2010, 1, 1), asDate(2011, 12, 31))
        val collector = new CalendarPeriodCollector(filter, limits)

        collector.collectMonths()
        log.trace(s"Collect months... periods=${collector.periods }")

        collector.periods.size should equal(2)
        collector.periods(0).isSamePeriod(MonthRange(2010, 1)) should equal(true)
        collector.periods(1).isSamePeriod(MonthRange(2011, 1)) should equal(true)
    }

    test("collect days") {
        val filter = new CalendarPeriodCollectorFilter()

        // 1월의 금요일만 추출
        filter.monthOfYears += Month.January.id
        filter.weekDays += DayOfWeek.Friday

        val limits = CalendarTimeRange(asDate(2010, 1, 1), asDate(2011, 12, 31))
        val collector = new CalendarPeriodCollector(filter, limits)

        collector.collectDays()

        collector.periods.foreach(period => log.trace(s"Day=$period}"))

        collector.periods.size should equal(9)

        collector.periods(0).isSamePeriod(DayRange(2010, 1, 1)) should equal(true)
        collector.periods(1).isSamePeriod(DayRange(2010, 1, 8)) should equal(true)
        collector.periods(2).isSamePeriod(DayRange(2010, 1, 15)) should equal(true)
        collector.periods(3).isSamePeriod(DayRange(2010, 1, 22)) should equal(true)
        collector.periods(4).isSamePeriod(DayRange(2010, 1, 29)) should equal(true)

        collector.periods(5).isSamePeriod(DayRange(2011, 1, 7)) should equal(true)
        collector.periods(6).isSamePeriod(DayRange(2011, 1, 14)) should equal(true)
        collector.periods(7).isSamePeriod(DayRange(2011, 1, 21)) should equal(true)
        collector.periods(8).isSamePeriod(DayRange(2011, 1, 28)) should equal(true)
    }

    test("collect hours") {
        val filter = new CalendarPeriodCollectorFilter()

        // 1월의 금요일의 08:00~18:00 추출
        filter.monthOfYears += Month.January.id
        filter.weekDays += DayOfWeek.Friday
        filter.collectingHours += HourRangeInDay(8, 18)

        val limits = CalendarTimeRange(asDate(2010, 1, 1), asDate(2011, 12, 31))
        val collector = new CalendarPeriodCollector(filter, limits)

        collector.collectHours()

        collector.periods.foreach(period => log.trace(s"Hours=$period"))

        collector.periods.size should equal(9)

        collector.periods(0).isSamePeriod(HourRangeCollection(2010, 1, 1, 8, 10)) should equal(true)
        collector.periods(1).isSamePeriod(HourRangeCollection(2010, 1, 8, 8, 10)) should equal(true)
        collector.periods(2).isSamePeriod(HourRangeCollection(2010, 1, 15, 8, 10)) should equal(true)
        collector.periods(3).isSamePeriod(HourRangeCollection(2010, 1, 22, 8, 10)) should equal(true)
        collector.periods(4).isSamePeriod(HourRangeCollection(2010, 1, 29, 8, 10)) should equal(true)

        collector.periods(5).isSamePeriod(HourRangeCollection(2011, 1, 7, 8, 10)) should equal(true)
        collector.periods(6).isSamePeriod(HourRangeCollection(2011, 1, 14, 8, 10)) should equal(true)
        collector.periods(7).isSamePeriod(HourRangeCollection(2011, 1, 21, 8, 10)) should equal(true)
        collector.periods(8).isSamePeriod(HourRangeCollection(2011, 1, 28, 8, 10)) should equal(true)
    }

    test("collect minutes") {
        val filter = new CalendarPeriodCollectorFilter()

        // 1월의 금요일의 08:00~18:00 추출
        filter.monthOfYears += Month.January.id
        filter.weekDays += DayOfWeek.Friday
        filter.collectingHours += HourRangeInDay(Timepart(8, 30), Timepart(18, 50))

        val limits = CalendarTimeRange(asDate(2010, 1, 1), asDate(2011, 12, 31))
        val collector = new CalendarPeriodCollector(filter, limits)

        collector.collectHours()

        collector.periods.foreach(period => log.trace(s"Hours=$period"))

        collector.periods.size should equal(9)

        collector.periods(0).isSamePeriod(CalendarTimeRange(asDateTime(2010, 1, 1, 8, 30), asDateTime(2010, 1, 1, 18, 50))) should equal(true)
        collector.periods(1).isSamePeriod(CalendarTimeRange(asDateTime(2010, 1, 8, 8, 30), asDateTime(2010, 1, 8, 18, 50))) should equal(true)
        collector.periods(2).isSamePeriod(CalendarTimeRange(asDateTime(2010, 1, 15, 8, 30), asDateTime(2010, 1, 15, 18, 50))) should equal(true)
        collector.periods(3).isSamePeriod(CalendarTimeRange(asDateTime(2010, 1, 22, 8, 30), asDateTime(2010, 1, 22, 18, 50))) should equal(true)
        collector.periods(4).isSamePeriod(CalendarTimeRange(asDateTime(2010, 1, 29, 8, 30), asDateTime(2010, 1, 29, 18, 50))) should equal(true)

        collector.periods(5).isSamePeriod(CalendarTimeRange(asDateTime(2011, 1, 7, 8, 30), asDateTime(2011, 1, 7, 18, 50))) should equal(true)
        collector.periods(6).isSamePeriod(CalendarTimeRange(asDateTime(2011, 1, 14, 8, 30), asDateTime(2011, 1, 14, 18, 50))) should equal(true)
        collector.periods(7).isSamePeriod(CalendarTimeRange(asDateTime(2011, 1, 21, 8, 30), asDateTime(2011, 1, 21, 18, 50))) should equal(true)
        collector.periods(8).isSamePeriod(CalendarTimeRange(asDateTime(2011, 1, 28, 8, 30), asDateTime(2011, 1, 28, 18, 50))) should equal(true)
    }

    test("collect exclude period") {

        val workingDays2011 = 365 - 2 - (51 * 2) - 1
        val workingDaysMarch2011 = 31 - 8

        val year2011 = YearRange(2011)
        val filter1 = new CalendarPeriodCollectorFilter()
        filter1.addWorkingWeekdays()

        val collector1 = new CalendarPeriodCollector(filter1, year2011)
        collector1.collectDays()
        collector1.periods.size should equal(workingDays2011)

        // 3월 제외 (23일 제외)
        val filter2 = new CalendarPeriodCollectorFilter()
        filter2.addWorkingWeekdays()
        filter2.excludePeriods.add(MonthRange(2011, 3))

        val collector2 = new CalendarPeriodCollector(filter2, year2011)
        collector2.collectDays()
        collector2.periods.size should equal(workingDays2011 - workingDaysMarch2011)

        // 2011년 26주차 ~ 27주차 (여름휴가)
        val filter3 = new CalendarPeriodCollectorFilter()
        filter3.addWorkingWeekdays()
        filter3.excludePeriods.add(MonthRange(2011, 3))
        filter3.excludePeriods.add(WeekRangeCollection(2011, 26, 2))

        val collector3 = new CalendarPeriodCollector(filter3, year2011)
        collector3.collectDays()
        collector3.periods.size should equal(workingDays2011 - workingDaysMarch2011 - 2 * WeekDaysPerWeek)

    }
}
