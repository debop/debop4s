package kr.debop4s.timeperiod.calendars

import kr.debop4s.timeperiod.{MonthRangeInYear, DayRangeInMonth}

/**
 * kr.debop4s.timeperiod.calendars.ICalendarPeriodCollectionFilter
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 3. 오전 10:40
 */
trait ICalendarPeriodCollectorFilter extends ICalendarVisitorFilter {

  def getCollectingMonths: Seq[MonthRangeInYear]

  def getCollectingDays: Seq[DayRangeInMonth]

}
