package kr.debop4s.timeperiod.calendars

import kr.debop4s.timeperiod.calendars.CollectKind.CollectKind

/**
 * kr.debop4s.timeperiod.calendars.CalendarPeriodCollectContext
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 5. 오전 12:51
 */
class CalendarPeriodCollectorContext extends ICalendarVisitorContext {

  var scope: CollectKind = _
}

object CollectKind extends Enumeration {
  type CollectKind = Value

  val Year = Value(0, "Year")
  val Month = Value(1, "Month")
  val Day = Value(2, "Day")
  val Hour = Value(3, "Hour")
  val Minute = Value(4, "Minute")
}
