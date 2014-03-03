package com.github.debop4s.timeperiod.timerange

import com.github.debop4s.timeperiod._
import com.github.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

/**
 * com.github.debop4s.timeperiod.timerange.WeekRange
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 28. 오후 11:18
 */
class WeekRange(private[this] val _weekyear: Int,
                private[this] val _weekOfWeekyear: Int,
                private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
  extends WeekTimeRange(Times.startTimeOfWeek(_weekyear, _weekOfWeekyear), 1, _calendar) {

  def weekOfWeekyear = start.getWeekOfWeekyear

  def firstDayOfWeek: DateTime = start

  def lastDayOfWeek: DateTime = firstDayOfWeek.plusDays(6)

  def isMultipleCalendarYears: Boolean =
    calendar.getYear(firstDayOfWeek) != calendar.getYear(lastDayOfWeek)

  def previousWeek: WeekRange = addWeeks(-1)

  def nextWeek: WeekRange = addWeeks(1)

  def addWeeks(weeks: Int): WeekRange = {
    WeekRange(Times.getStartOfYearWeek(weekyear, weekOfWeekyear, calendar).plusWeeks(weeks), calendar)
  }
}

object WeekRange {

  def apply(): WeekRange = apply(Times.today)

  def apply(calendar: ITimeCalendar): WeekRange = apply(Times.today, calendar)

  def apply(weekyear: Int, weekOfYear: Int): WeekRange =
    apply(weekyear, weekOfYear, DefaultTimeCalendar)

  def apply(weekyear: Int, weekOfYear: Int, calendar: ITimeCalendar): WeekRange =
    new WeekRange(weekyear, weekOfYear, calendar)

  def apply(moment: DateTime): WeekRange = apply(moment, DefaultTimeCalendar)

  def apply(moment: DateTime, calendar: ITimeCalendar): WeekRange =
    new WeekRange(moment.getWeekyear, moment.getWeekOfWeekyear, calendar)

  def apply(yw: YearWeek): WeekRange = apply(yw.weekyear, yw.weekOfWeekyear)

}
