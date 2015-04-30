package debop4s.timeperiod.timerange

import java.util

import debop4s.timeperiod.TimeSpec._
import debop4s.timeperiod._
import debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

import scala.collection.SeqView


/**
 * debop4s.timeperiod.timerange.HourCollectionRange
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 28. 오후 10:06
 */
@SerialVersionUID(8973240176036662074L)
class HourRangeCollection(private[this] val _moment: DateTime,
                          private[this] val _hourCount: Int,
                          private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
  extends HourTimeRange(Times.trimToSecond(_moment), _hourCount, _calendar) {

  def this(moment: DateTime, hourCount: Int) = this(moment, hourCount, DefaultTimeCalendar)
  def this(year: Int, monthOfYear: Int, dayOfMonth: Int, hourOfDay: Int, hourCount: Int) =
    this(new DateTime(year, monthOfYear, dayOfMonth, hourOfDay, 0), hourCount, DefaultTimeCalendar)
  def this(year: Int, monthOfYear: Int, dayOfMonth: Int, hourOfDay: Int, hourCount: Int, calendar: ITimeCalendar) =
    this(new DateTime(year, monthOfYear, dayOfMonth, hourOfDay, 0), hourCount, calendar)

  def hoursView: SeqView[HourRange, Seq[_]] = {
    val startHour = Times.trimToMinute(start)

    (0 until hourCount).view.map { h =>
      HourRange(startHour.plusHours(h), calendar)
    }
  }

  @inline
  def getHours: util.List[HourRange] = {
    val startHour = Times.trimToMinute(start)

    val results = new util.ArrayList[HourRange](hourCount)
    var h = 0
    while (h < hourCount) {
      results add HourRange(startHour.plusHours(h), calendar)
      h += 1
    }
    results
  }
}

object HourRangeCollection {

  def apply(moment: DateTime, hourCount: Int): HourRangeCollection = {
    new HourRangeCollection(moment, hourCount, DefaultTimeCalendar)
  }

  def apply(moment: DateTime, hourCount: Int, calendar: ITimeCalendar): HourRangeCollection = {
    new HourRangeCollection(moment, hourCount, calendar)
  }

  def apply(year: Int,
            monthOfYear: Int,
            dayOfMonth: Int,
            hourOfDay: Int,
            hourCount: Int): HourRangeCollection = {
    apply(year, monthOfYear, dayOfMonth, hourOfDay, hourCount, DefaultTimeCalendar)
  }

  def apply(year: Int,
            monthOfYear: Int,
            dayOfMonth: Int,
            hourOfDay: Int,
            hourCount: Int,
            calendar: ITimeCalendar): HourRangeCollection = {
    new HourRangeCollection(new DateTime(year, monthOfYear, dayOfMonth, hourOfDay, 0), hourCount, calendar)
  }
}
