package debop4s.timeperiod

import debop4s.core.Local

/**
 * debop4s.timeperiod.Periodcontext
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 2. 오후 8:54
 */
object PeriodContext {

  lazy val TIME_CALEMDAR_KEY = this.getClass.getName + ".Current"

  object Current {

    def calendar: ITimeCalendar = {
      val calendar = Local.getOrCreate(TIME_CALEMDAR_KEY, {
        DefaultTimeCalendar
      })
      calendar.getOrElse(DefaultTimeCalendar)
    }

    def calendar_=(calendar: ITimeCalendar) {
      Local.put(TIME_CALEMDAR_KEY, calendar)
    }

    def locale = calendar.getLocale

    def firstDayOfWeek = calendar.firstDayOfWeek
  }

}
