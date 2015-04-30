package debop4s.timeperiod.timerange

import java.util

import debop4s.timeperiod.TimeSpec._
import debop4s.timeperiod._
import debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

import scala.beans.BeanProperty
import scala.collection.SeqView

/**
 * debop4s.timeperiod.timerange.MonthTimeRange
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 29. 오전 11:41
 */
class MonthTimeRange(private[this] val _year: Int,
                     private[this] val _monthOfYear: Int,
                     @BeanProperty val monthCount: Int,
                     private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
  extends CalendarTimeRange(Times.relativeMonthPeriod(Times.startTimeOfMonth(_year, _monthOfYear), monthCount), _calendar) {

  def daysView: SeqView[DayRange, Seq[_]] = {
    val startMonth = Times.startTimeOfMonth(start)

    for {
      m <- (0 until monthCount).view
      month = startMonth.plusMonths(m)
      dayOfMonth = Times.daysInMonth(month.getYear, month.getMonthOfYear)
      d <- (0 until dayOfMonth).view
    } yield {
      DayRange(month.plusDays(d), calendar)
    }
  }

  @inline
  def getDays: util.List[DayRange] = {
    val startMonth = Times.startTimeOfMonth(start)
    val days = new util.ArrayList[DayRange](monthCount * 31)

    var m = 0
    while (m < monthCount) {
      val month = startMonth.plusMonths(m)
      val dayOfMonth = Times.daysInMonth(month.getYear, month.getMonthOfYear)

      var d = 0
      while (d < dayOfMonth) {
        days add new DayRange(month.plusDays(d), calendar)
        d += 1
      }
      m += 1
    }
    days
  }
}

object MonthTimeRange {

  def apply(year: Int, monthOfYear: Int, monthCount: Int): MonthTimeRange =
    apply(year, monthOfYear, monthCount, DefaultTimeCalendar)

  def apply(year: Int, monthOfYear: Int, monthCount: Int, calendar: ITimeCalendar): MonthTimeRange =
    new MonthTimeRange(year, monthOfYear, monthCount, calendar)

  def apply(moment: DateTime, monthCount: Int): MonthTimeRange =
    apply(moment, monthCount, DefaultTimeCalendar)

  def apply(moment: DateTime, monthCount: Int, calendar: ITimeCalendar): MonthTimeRange =
    new MonthTimeRange(moment.getYear, moment.getMonthOfYear, monthCount, calendar)
}
