package debop4s.timeperiod.timerange

import debop4s.timeperiod._
import org.joda.time.DateTime

/**
 * debop4s.timeperiod.timerange.YearRangeCollection
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 30. 오전 10:36
 */
@SerialVersionUID(6717411713272815855L)
class YearRangeCollection(private[this] val _year: Int,
                          private[this] val _yearCount: Int,
                          private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
  extends YearTimeRange(_year, _yearCount, _calendar) {

  @inline
  def years = {
    (0 until yearCount).view.map {
      y =>
        YearRange(startYear + y, calendar)
    }
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
