package debop4s.timeperiod.timerange

import debop4s.timeperiod.TimeSpec._
import debop4s.timeperiod._
import debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

/**
 * 특정 한 주(Week) 의 기간을 표현합니다.
 *
 * @author sunghyouk.bae@gmail.com
 * @since  2013. 12. 28. 오후 11:18
 */
class WeekRange(private[this] val _weekyear: Int,
                private[this] val _weekOfWeekyear: Int,
                private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
  extends WeekTimeRange(Times.startTimeOfWeek(_weekyear, _weekOfWeekyear), 1, _calendar) {

  def this() = this(Times.today.getWeekyear, Times.today.getWeekOfWeekyear, DefaultTimeCalendar)
  def this(weekyear: Int, weekOfWeekyear: Int) = this(weekyear, weekOfWeekyear, DefaultTimeCalendar)
  def this(moment: DateTime) = this(moment.getWeekyear, moment.getWeekOfWeekyear, DefaultTimeCalendar)
  def this(moment: DateTime, calendar: ITimeCalendar) = this(moment.getWeekyear, moment.getWeekOfWeekyear, calendar)

  def firstDayOfWeek: DateTime = start
  def getFirstDayOfWeek = firstDayOfWeek

  def lastDayOfWeek: DateTime = firstDayOfWeek.plusDays(6)
  def getLastDayOfWeek = lastDayOfWeek

  def isMultipleCalendarYears: Boolean =
    calendar.year(firstDayOfWeek) != calendar.year(lastDayOfWeek)

  def previousWeek: WeekRange = addWeeks(-1)

  def nextWeek: WeekRange = addWeeks(1)

  def addWeeks(weeks: Int): WeekRange = {
    WeekRange(Times.startOfYearweek(weekyear, weekOfWeekyear, calendar).plusWeeks(weeks), calendar)
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

  def apply(yw: YearWeek): WeekRange =
    apply(yw.weekyear, yw.weekOfWeekyear, DefaultTimeCalendar)

  def apply(yw: YearWeek, calendar: ITimeCalendar): WeekRange =
    apply(yw.weekyear, yw.weekOfWeekyear, calendar)

}
