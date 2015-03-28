package debop4s.timeperiod.timerange

import debop4s.core.jodatime._
import debop4s.timeperiod._
import debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

/**
 * debop4s.timeperiod.timerange.MonthTimeRange
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 29. 오전 11:41
 */
class MonthTimeRange(private[this] val _year: Int,
                     private[this] val _monthOfYear: Int,
                     val monthCount: Int,
                     private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
  extends CalendarTimeRange(Times.relativeMonthPeriod(Times.startTimeOfMonth(_year, _monthOfYear), monthCount), _calendar) {

  @inline
  def days = {
    val startMonth = Times.startTimeOfMonth(start)

    for {
      m <- ( 0 until monthCount ).view
      month = startMonth + m.month
      dayOfMonth = Times.daysInMonth(month.getYear, month.getMonthOfYear)
      d <- ( 0 until dayOfMonth ).view
    } yield {
      DayRange(month + d.day, calendar)
    }
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
