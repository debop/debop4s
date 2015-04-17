package debop4s.timeperiod.calendars

import debop4s.timeperiod._


trait ICalendarPeriodCollectorFilter extends ICalendarVisitorFilter {

  def collectingMonths: Seq[MonthRangeInYear]

  def collectingDays: Seq[DayRangeInMonth]

}
