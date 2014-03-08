package com.github.debop4s.timeperiod.timerange

import com.github.debop4s.timeperiod._
import com.github.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime
import scala.collection.mutable.ArrayBuffer

/**
 * com.github.debop4s.timeperiod.timerange.WeekTimeRange
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 28. 오후 11:05
 */
@SerialVersionUID(-1899389597363540458L)
class WeekTimeRange(private[this] val _moment: DateTime,
                    val weekCount: Int,
                    private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
  extends CalendarTimeRange(WeekTimeRange.getPeriodOf(_moment, weekCount), _calendar) {

  def year: Int = start.getYear

  def weekyear: Int = start.getWeekyear

  def startWeekOfYear: Int = Times.getWeekOfYear(start).weekOfWeekyear

  def endWeekOfYear: Int = Times.getWeekOfYear(end).weekOfWeekyear

  def getDays: Seq[DayRange] = {
    val startDay = startDayStart
    val dayCount = weekCount * DaysPerWeek
    val days = ArrayBuffer[DayRange]()
    for (d <- 0 until dayCount) {
      days += new DayRange(startDay.plusDays(d), calendar)
    }
    days
  }
}

object WeekTimeRange {

  def apply(moment: DateTime, weekCount: Int): WeekTimeRange =
    apply(moment, weekCount, DefaultTimeCalendar)

  def apply(moment: DateTime, weekCount: Int, calendar: ITimeCalendar): WeekTimeRange =
    new WeekTimeRange(moment, weekCount, calendar)

  def apply(year: Int, weekOfYear: Int, weekCount: Int): WeekTimeRange =
    apply(year, weekOfYear, weekCount, DefaultTimeCalendar)

  def apply(year: Int, weekOfYear: Int, weekCount: Int, calendar: ITimeCalendar): WeekTimeRange =
    new WeekTimeRange(Times.startTimeOfWeek(year, weekOfYear), weekCount, calendar)

  def getPeriodOf(moment: DateTime, weekCount: Int): TimeRange = {
    require(weekCount > 0)
    val startWeek = Times.startTimeOfWeek(moment)
    TimeRange(startWeek, startWeek.plusWeeks(weekCount))
  }

  def getPeriodOf(year: Int, weekOfYear: Int, weekCount: Int) {
    require(weekCount > 0)
    val startWeek = Times.startTimeOfWeek(year, weekOfYear)
    TimeRange(startWeek, startWeek.plusWeeks(weekCount))
  }
}
