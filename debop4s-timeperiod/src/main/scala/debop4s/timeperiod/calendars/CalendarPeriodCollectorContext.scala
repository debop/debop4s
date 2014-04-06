package debop4s.timeperiod.calendars

import debop4s.timeperiod.calendars.CollectKind.CollectKind

/**
 * debop4s.timeperiod.calendars.CalendarPeriodCollectContext
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 5. 오전 12:51
 */
class CalendarPeriodCollectorContext(val scope: CollectKind) extends ICalendarVisitorContext {
  override def toString: String = s"scope=$scope"
}

object CollectKind extends Enumeration {
  type CollectKind = Value

  val Year = Value(0, "Year")
  val Month = Value(1, "Month")
  val Day = Value(2, "Day")
  val Hour = Value(3, "Hour")
  val Minute = Value(4, "Minute")
}
