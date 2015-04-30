package debop4s.timeperiod.timerange

import java.util

import debop4s.timeperiod.TimeSpec._
import debop4s.timeperiod._
import debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

import scala.beans.BeanProperty
import scala.collection.SeqView


/**
 * debop4s.timeperiod.timerange.DayTimeRange
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 28. 오후 10:27
 */
class DayTimeRange(private[this] val _start: DateTime,
                   @BeanProperty val dayCount: Int,
                   private[this] var _calendar: ITimeCalendar = DefaultTimeCalendar)
  extends CalendarTimeRange(Times.relativeDayPeriod(_start, dayCount), _calendar) {

  def this(start: DateTime, dayCount: Int) = this(start, dayCount, DefaultTimeCalendar)
  def this(year: Int, monthOfYear: Int, dayOfMonth: Int, dayCount: Int) =
    this(Times.asDate(year, monthOfYear, dayOfMonth), dayCount, DefaultTimeCalendar)

  def this(year: Int, monthOfYear: Int, dayOfMonth: Int, dayCount: Int, calendar: ITimeCalendar) =
    this(Times.asDate(year, monthOfYear, dayOfMonth), dayCount, calendar)

  def startDayOfWeek: DayOfWeek = calendar.dayOfWeek(start)
  def endDayOfWeek: DayOfWeek = calendar.dayOfWeek(end)

  def getStartDayOfWeek = startDayOfWeek
  def getEndDayOfWeek = endDayOfWeek

  def hoursView: SeqView[HourRange, Seq[_]] = {
    val day = startDayStart
    val hours = dayCount * HoursPerDay
    (0 until hours).view.map { h => HourRange(day.plusHours(h), calendar) }
  }

  def getHours: util.List[HourRange] = {
    val day = startDayStart
    val hours = dayCount * HoursPerDay

    val results = new util.ArrayList[HourRange](hours)
    (0 until hours) foreach { h =>
      results add HourRange(day.plusHours(h), calendar)
    }
    results
  }
}

object DayTimeRange {

  def apply(moment: DateTime, dayCount: Int): DayTimeRange =
    apply(moment, dayCount, DefaultTimeCalendar)

  def apply(moment: DateTime, dayCount: Int, calendar: ITimeCalendar): DayTimeRange =
    new DayTimeRange(moment, dayCount, calendar)

  def apply(year: Int, monthOfYear: Int, dayOfMonth: Int, dayCount: Int): DayTimeRange =
    apply(year, monthOfYear, dayOfMonth, dayCount, DefaultTimeCalendar)

  def apply(year: Int, monthOfYear: Int, dayOfMonth: Int, dayCount: Int, calendar: ITimeCalendar): DayTimeRange =
    new DayTimeRange(Times.asDate(year, monthOfYear, dayOfMonth), dayCount, calendar)

}
