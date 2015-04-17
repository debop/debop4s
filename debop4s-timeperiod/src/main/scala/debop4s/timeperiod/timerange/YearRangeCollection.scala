package debop4s.timeperiod.timerange

import java.util

import com.google.common.collect.Lists
import debop4s.timeperiod.TimeSpec._
import debop4s.timeperiod._
import org.joda.time.DateTime

import scala.collection.SeqView

@SerialVersionUID(6717411713272815855L)
class YearRangeCollection(private[this] val _year: Int,
                          private[this] val _yearCount: Int,
                          private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
  extends YearTimeRange(_year, _yearCount, _calendar) {

  def this(year: Int, yearCount: Int) =
    this(year, yearCount, DefaultTimeCalendar)

  def this(moment: DateTime, yearCount: Int) =
    this(moment.getYear, yearCount, DefaultTimeCalendar)

  def this(moment: DateTime, yearCount: Int, calendar: ITimeCalendar) =
    this(moment.getYear, yearCount, calendar)

  @inline
  def years: SeqView[YearRange, Seq[_]] = {
    (0 until yearCount).view.map { y =>
      YearRange(startYear + y, calendar)
    }
  }

  def getYears: util.List[YearRange] = {
    val years = Lists.newArrayListWithCapacity[YearRange](yearCount)
    var i = 0
    while (i < yearCount) {
      years add YearRange(startYear + i, calendar)
      i += 1
    }
    years
  }
}

object YearRangeCollection {

  def apply(year: Int, yearCount: Int): YearRangeCollection =
    apply(year, yearCount, DefaultTimeCalendar)

  def apply(year: Int, yearCount: Int, calendar: ITimeCalendar): YearRangeCollection =
    new YearRangeCollection(year, yearCount, calendar)

  def apply(moment: DateTime, yearCount: Int): YearRangeCollection =
    apply(moment, yearCount, DefaultTimeCalendar)

  def apply(moment: DateTime, yearCount: Int, calendar: ITimeCalendar): YearRangeCollection =
    new YearRangeCollection(moment.getYear, yearCount, calendar)

}
