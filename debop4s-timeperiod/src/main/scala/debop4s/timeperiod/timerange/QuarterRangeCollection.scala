package debop4s.timeperiod.timerange

import java.util

import com.google.common.collect.Lists
import debop4s.timeperiod.TimeSpec._
import debop4s.timeperiod._
import debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

import scala.collection.SeqView


@SerialVersionUID(-1191375103809489196L)
class QuarterRangeCollection(private[this] val _year: Int,
                             private[this] val _quarter: Quarter,
                             private[this] val _quarterCount: Int,
                             private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
  extends QuarterTimeRange(_year, _quarter, _quarterCount, _calendar) {

  def this(year: Int, quarter: Quarter, quarterCount: Int) =
    this(year, quarter, quarterCount, DefaultTimeCalendar)

  def this(moment: DateTime, quarterCount: Int) =
    this(moment.getYear, Times.quarterOfMonth(moment.getMonthOfYear), quarterCount, DefaultTimeCalendar)

  def this(moment: DateTime, quarterCount: Int, calendar: ITimeCalendar) =
    this(moment.getYear, Times.quarterOfMonth(moment.getMonthOfYear), quarterCount, calendar)

  def quartersView: SeqView[QuarterRange, Seq[_]] = {
    (0 until quarterCount).view.map { q =>
      QuarterRange(start.plusMonths(q * MonthsPerQuarter), calendar)
    }
  }

  @inline
  def getQuarters: util.List[QuarterRange] = {
    val results = Lists.newArrayListWithCapacity[QuarterRange](quarterCount)
    var q = 0
    while (q < quarterCount) {
      results add QuarterRange(start.plusMonths(q * MonthsPerQuarter), calendar)
      q += 1
    }
    results
  }
}

object QuarterRangeCollection {

  def apply(year: Int, quarter: Quarter, quarterCount: Int): QuarterRangeCollection =
    apply(year, quarter, quarterCount, DefaultTimeCalendar)

  def apply(year: Int, quarter: Quarter, quarterCount: Int, calendar: ITimeCalendar): QuarterRangeCollection =
    new QuarterRangeCollection(year, quarter, quarterCount, calendar)

  def apply(moment: DateTime, quarterCount: Int): QuarterRangeCollection =
    apply(moment, quarterCount, DefaultTimeCalendar)

  def apply(moment: DateTime, quarterCount: Int, calendar: ITimeCalendar): QuarterRangeCollection =
    new QuarterRangeCollection(moment.getYear, Times.quarterOfMonth(moment.getMonthOfYear), quarterCount, calendar)


}
