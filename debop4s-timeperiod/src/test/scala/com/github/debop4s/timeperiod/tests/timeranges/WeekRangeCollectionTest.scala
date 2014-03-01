package com.github.debop4s.timeperiod.tests.timeranges


import com.github.debop4s.timeperiod._
import com.github.debop4s.timeperiod.tests.AbstractTimePeriodTest
import com.github.debop4s.timeperiod.timerange.{WeekRange, WeekRangeCollection}
import com.github.debop4s.timeperiod.utils.{Weeks, Times}

/**
 * WeekRangeCollectionTest
 * Created by debop on 2014. 2. 16.
 */
class WeekRangeCollectionTest extends AbstractTimePeriodTest {

    test("single months") {
        val startYear = 2004
        val startWeek = 22

        val wrs = new WeekRangeCollection(startYear, startWeek, 1)
        wrs.weekCount should equal(1)

        wrs.startYear should equal(startYear)
        wrs.endYear should equal(startYear)
        wrs.startWeekOfYear should equal(startWeek)
        wrs.endWeekOfYear should equal(startWeek)

        val weeks = wrs.getWeeks
        weeks should have size 1
        weeks(0).isSamePeriod(WeekRange(startYear, startWeek)) should equal(true)
    }

    test("calendar weeks") {
        val startYear = 2004
        val startWeek = 22
        val weekCount = 5

        val wrs = WeekRangeCollection(startYear, startWeek, weekCount)

        wrs.weekCount should equal(weekCount)
        wrs.startYear should equal(startYear)
        wrs.startWeekOfYear should equal(startWeek)
        wrs.endYear should equal(startYear)
        wrs.endWeekOfYear should equal(startWeek + weekCount - 1)
    }

    test("weekCounts") {
        val weekCounts = Array(1, 6, 48, 180, 360)

        val now = Times.now
        val today = Times.today

        weekCounts.foreach(weekCount => {
            val wrs = WeekRangeCollection(now, weekCount)

            val startTime = wrs.calendar.mapStart(Times.startTimeOfWeek(today))
            val endTime = wrs.calendar.mapEnd(startTime + weekCount.week)

            wrs.start should equal(startTime)
            wrs.end should equal(endTime)

            val items = wrs.getWeeks

            (0 until weekCount).par.foreach(w => {
                val item = items(w)

                item.start should equal(startTime.plusWeeks(w))
                item.end should equal(wrs.calendar.mapEnd(startTime + (w + 1).week))

                item.unmappedStart should equal(startTime + w.week)
                item.unmappedEnd should equal(startTime + (w + 1).week)

                item.isSamePeriod(WeekRange(wrs.start + w.week)) should equal(true)

                val yw = Weeks.addWeekOfYears(now.getWeekyear, now.getWeekOfWeekyear, w)
                println(s"item=$item, yw=${WeekRange(yw)}")
                item.isSamePeriod(WeekRange(yw)) should equal(true)
            })
        })
    }
}
