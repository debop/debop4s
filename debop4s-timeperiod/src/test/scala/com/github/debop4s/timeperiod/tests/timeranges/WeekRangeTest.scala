package com.github.debop4s.timeperiod.tests.timeranges


import com.github.debop4s.timeperiod._
import com.github.debop4s.timeperiod.tests.AbstractTimePeriodTest
import com.github.debop4s.timeperiod.timerange.WeekRange
import com.github.debop4s.timeperiod.utils.Times

/**
 * com.github.debop4s.timeperiod.tests.timeranges.WeekRangeTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 17. 오전 9:51
 */
class WeekRangeTest extends AbstractTimePeriodTest {

    test("init values") {
        val now = Times.now
        val firstWeek = Times.startTimeOfWeek(now)
        val secondWeek = firstWeek + 1.week

        val wr = WeekRange(now, EmptyOffsetTimeCalendar)
        wr.start should equal(firstWeek)
        wr.end should equal(secondWeek)
    }

    test("default calendar") {
        val yearStart = Times.startTimeOfYear(Times.now)

        (1 until 50).par.foreach(w => {
            val wr = WeekRange(yearStart + w.week)
            wr.weekyear should equal(yearStart.getWeekyear)
            wr.weekOfWeekyear should equal(w + 1)

            wr.unmappedStart should equal(Times.startTimeOfWeek(yearStart + w.week))
            wr.unmappedEnd should equal(Times.startTimeOfWeek(yearStart + (w + 1).week))
        })
    }

    test("getDays") {
        val now = Times.now
        val wr = WeekRange()

        val days = wr.getDays
        var index = 0
        days.foreach(day => {
            day.start should equal(wr.start + index.day)
            day.end should equal(day.calendar.mapEnd(day.start + 1.day))
            index += 1
        })
        index should equal(DaysPerWeek)
    }

    test("add months") {
        val now = Times.now
        val startWeek = Times.startTimeOfWeek(now)
        val wr = WeekRange(now)

        log.debug(s"startWeek=$startWeek, weekRange=$wr")

        wr.previousWeek.start should equal(startWeek - 1.week)
        wr.nextWeek.start should equal(startWeek + 1.week)
        wr.addWeeks(0) should equal(wr)

        (-60 to 120).par.foreach(w => {
            wr.addWeeks(w).start should equal(startWeek + w.week)
        })
    }
}
