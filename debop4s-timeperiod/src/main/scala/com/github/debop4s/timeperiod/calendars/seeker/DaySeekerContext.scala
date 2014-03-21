package com.github.debop4s.timeperiod.calendars.seeker

import com.github.debop4s.timeperiod.calendars.ICalendarVisitorContext
import com.github.debop4s.timeperiod.timerange.DayRange

/**
 * com.github.debop4s.timeperiod.calendars.seeker.DaySeekerContext
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 5. 오후 8:17
 */
class DaySeekerContext(val startDay: DayRange,
                       private[this] val _dayCount: Int) extends ICalendarVisitorContext {

    val dayCount = math.abs(_dayCount)

    var remainingDays = dayCount
    var foundDay: DayRange = null

    def isFinished: Boolean = remainingDays == 0

    @inline
    def processDay(day: DayRange) {
        if (isFinished)
            return

        remainingDays -= 1

        if (isFinished)
            foundDay = day
    }

}
