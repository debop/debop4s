package debop4s.timeperiod.timerange

import java.util

import debop4s.timeperiod.TimeSpec._
import debop4s.timeperiod._
import debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

import scala.collection.SeqView


/**
 * debop4s.timeperiod.timerange.DayRangeCollection
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 28. 오후 10:45
 */
class DayRangeCollection(private[this] val _moment: DateTime,
                         private[this] val _dayCount: Int,
                         private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
  extends DayTimeRange(Times.asDate(_moment), _dayCount, _calendar) {

  def this(moment: DateTime, dayCount: Int) =
    this(moment, dayCount, DefaultTimeCalendar)

  def this(year: Int, monthOfYear: Int, dayOfMonth: Int, dayCount: Int) =
    this(Times.asDate(year, monthOfYear, dayOfMonth), dayCount, DefaultTimeCalendar)

  def this(year: Int, monthOfYear: Int, dayOfMonth: Int, dayCount: Int, calendar: ITimeCalendar) =
    this(Times.asDate(year, monthOfYear, dayOfMonth), dayCount, calendar)


  @inline
  def days: SeqView[DayRange, Seq[_]] = {
    val startDay = Times.asDate(start)

    (0 until dayCount).view.map { d =>
      DayRange(startDay.plusDays(d), calendar)
    }
  }

  def getDays: util.List[DayRange] = {
    val startDay = Times.asDate(start)
    val results = new util.ArrayList[DayRange](dayCount)
    var d = 0
    while (d < dayCount) {
      results add DayRange(startDay.plusDays(d), calendar)
      d += 1
    }
    results
  }
}

object DayRangeCollection {

  def apply(moment: DateTime, dayCount: Int): DayRangeCollection =
    apply(moment, dayCount, DefaultTimeCalendar)

  def apply(moment: DateTime, dayCount: Int, calendar: ITimeCalendar): DayRangeCollection =
    new DayRangeCollection(moment, dayCount, calendar)

  def apply(year: Int, monthOfYear: Int, dayOfMonth: Int, dayCount: Int): DayRangeCollection =
    apply(year, monthOfYear, dayOfMonth, dayCount, DefaultTimeCalendar)

  def apply(year: Int, monthOfYear: Int, dayOfMonth: Int, dayCount: Int, calendar: ITimeCalendar): DayRangeCollection =
    new DayRangeCollection(Times.asDate(year, monthOfYear, dayOfMonth), dayCount, calendar)

}
