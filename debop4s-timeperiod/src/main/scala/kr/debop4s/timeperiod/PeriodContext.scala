package kr.debop4s.timeperiod

import kr.debop4s.core.Local

/**
 * kr.debop4s.timeperiod.Periodcontext
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 2. 오후 8:54
 */
object PeriodContext {

    lazy val TIME_CALEMDAR_KEY = this.getClass.getName + ".Current"

    object Current {

        def calendar: ITimeCalendar = {
            var calendar = Local.get(TIME_CALEMDAR_KEY).asInstanceOf[ITimeCalendar]
            if (calendar == null) {
                calendar = DefaultTimeCalendar
                Local.put(TIME_CALEMDAR_KEY, calendar)
            }
            calendar
        }

        def calendar_=(calendar: ITimeCalendar) {
            Local.put(TIME_CALEMDAR_KEY, calendar)
        }

        def getTimeCalendar: ITimeCalendar = calendar

        def setTimeCalendar(cal: ITimeCalendar) = { calendar = cal }

        def locale = calendar.getLocale

        def firstDayOfWeek = calendar.getFirstDayOfWeek
    }

}
