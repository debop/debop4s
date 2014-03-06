package com.github.debop4s.timeperiod.tests.calendars

import com.github.debop4s.timeperiod._
import com.github.debop4s.timeperiod.tests.AbstractTimePeriodTest
import com.github.debop4s.timeperiod.timerange._
import com.github.debop4s.timeperiod.utils.Times

/**
 * BusinessCaseTest
 * Created by debop on 2014. 2. 18.
 */
class BusinessCaseTest extends AbstractTimePeriodTest {

    test("time range calendar") {

        val now = Times.now

        (0 until 500).par.foreach(i => {
            val current = now + i.day
            val currentFiveSeconds = TimeRange(Times.trimToSecond(current, 15), Times.trimToSecond(current, 20))

            YearRange(current).hasInside(currentFiveSeconds) should equal(true)
            HalfyearRange(current).hasInside(currentFiveSeconds) should equal(true)
            QuarterRange(current).hasInside(currentFiveSeconds) should equal(true)
            MonthRange(current).hasInside(currentFiveSeconds) should equal(true)
            WeekRange(current).hasInside(currentFiveSeconds) should equal(true)
            DayRange(current).hasInside(currentFiveSeconds) should equal(true)
            HourRange(current).hasInside(currentFiveSeconds) should equal(true)
            MinuteRange(current).hasInside(currentFiveSeconds) should equal(true)

        })

        val anytime = TimeRange()

        YearRange().hasInside(anytime) should equal(false)
        HalfyearRange().hasInside(anytime) should equal(false)
        QuarterRange().hasInside(anytime) should equal(false)
        MonthRange().hasInside(anytime) should equal(false)
        WeekRange().hasInside(anytime) should equal(false)
        DayRange().hasInside(anytime) should equal(false)
        HourRange().hasInside(anytime) should equal(false)
        MinuteRange().hasInside(anytime) should equal(false)
    }

}
