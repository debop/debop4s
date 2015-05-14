package debop4s.timeperiod.timerange

import java.util

import com.google.common.collect.Lists
import debop4s.timeperiod.TimeSpec._
import debop4s.timeperiod._
import org.joda.time.DateTime

import scala.collection.SeqView


/**
 * MonthRangeCollection
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 29. 오후 4:51
 */
@SerialVersionUID(-3955343194292107018L)
class MonthRangeCollection(private[this] val _year: Int,
                           private[this] val _monthOfYear: Int,
                           private[this] val _monthCount: Int,
                           private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
  extends MonthTimeRange(_year, _monthOfYear, _monthCount, _calendar) {

  def this(year: Int, monthOfYear: Int, monthCount: Int) =
    this(year, monthOfYear, monthCount, DefaultTimeCalendar)
  def this(moment: DateTime, monthCount: Int) =
    this(moment.getYear, moment.getMonthOfYear, monthCount, DefaultTimeCalendar)
  def this(moment: DateTime, monthCount: Int, calendar: ITimeCalendar) =
    this(moment.getYear, moment.getMonthOfYear, monthCount, calendar)


  def monthsView: SeqView[MonthRange, Seq[_]] = {
    (0 until monthCount).view.map { m =>
      MonthRange(start.plusMonths(m), calendar)
    }
  }

  def months: util.List[MonthRange] = {
    val results = Lists.newArrayListWithCapacity[MonthRange](monthCount)
    var m = 0
    while (m < monthCount) {
      results add MonthRange(start.plusMonths(m), calendar)
      m += 1
    }
    results
  }
}

object MonthRangeCollection {

  def apply(year: Int, monthOfYear: Int, monthCount: Int): MonthRangeCollection =
    apply(year, monthOfYear, monthCount, DefaultTimeCalendar)

  def apply(year: Int, monthOfYear: Int, monthCount: Int, calendar: ITimeCalendar): MonthRangeCollection =
    new MonthRangeCollection(year, monthOfYear, monthCount, calendar)

  def apply(moment: DateTime, monthCount: Int): MonthRangeCollection = {
    apply(moment, monthCount, DefaultTimeCalendar)
  }

  def apply(moment: DateTime, monthCount: Int, calendar: ITimeCalendar): MonthRangeCollection = {
    new MonthRangeCollection(moment.getYear, moment.getMonthOfYear, monthCount, calendar)
  }

}
