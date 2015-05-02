package debop4s.timeperiod.timerange

import java.util

import com.google.common.collect.Lists
import debop4s.timeperiod.TimeSpec._
import debop4s.timeperiod._
import debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

import scala.beans.BeanProperty
import scala.collection.SeqView


@SerialVersionUID(-1899389597363540458L)
class WeekTimeRange(private[this] val _moment: DateTime,
                    @BeanProperty val weekCount: Int,
                    private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
  extends CalendarTimeRange(WeekTimeRange.getPeriodOf(_moment, weekCount), _calendar) {

  def year: Int = start.getYear
  def getYear = year

  def weekyear: Int = start.getWeekyear
  def getWeekYear = weekyear

  def weekOfWeekyear = start.getWeekOfWeekyear
  def getWeekOfWeekyear = weekOfWeekyear

  def startWeekOfYear: Int = Times.weekOfYear(start).weekOfWeekyear
  def getStartWeekOfYear = startWeekOfYear

  def endWeekOfYear: Int = Times.weekOfYear(end).weekOfWeekyear
  def getEndWeekOfYear = endWeekOfYear

  def daysView: SeqView[DayRange, Seq[_]] = {
    val startDay = startDayStart
    val dayCount = weekCount * DaysPerWeek

    (0 until dayCount).view.map { d =>
      DayRange(startDay.plusDays(d), calendar)
    }
  }

  @inline
  def days: util.List[DayRange] = {
    val startDay = startDayStart
    val dayCount = weekCount * DaysPerWeek

    val results = Lists.newArrayListWithCapacity[DayRange](dayCount)
    var d = 0
    while (d < dayCount) {
      results add DayRange(startDay.plusDays(d), calendar)
      d += 1
    }
    results
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

  def getPeriodOf(year: Int, weekOfYear: Int, weekCount: Int): TimeRange = {
    require(weekCount > 0)
    val startWeek = Times.startTimeOfWeek(year, weekOfYear)
    TimeRange(startWeek, startWeek.plusWeeks(weekCount))
  }
}
