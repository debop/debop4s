package debop4s.timeperiod.timerange

import debop4s.timeperiod.TimeSpec._
import debop4s.timeperiod._
import debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

@SerialVersionUID(709289105887324670L)
class YearRange(private[this] val _year: Int,
                private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
  extends YearTimeRange(_year, 1, _calendar) {

  def this() = this(Times.currentYear.getYear, DefaultTimeCalendar)
  def this(year: Int) = this(year, DefaultTimeCalendar)
  def this(moment: DateTime) = this(moment.getYear, DefaultTimeCalendar)
  def this(moment: DateTime, calendar: ITimeCalendar) = this(moment.getYear, calendar)

  def year = startYear
  def getYear = year

  def addYears(years: Int): YearRange = {
    new YearRange(startYear + years, calendar)
  }

  def nextYear: YearRange = addYears(1)

  def previousYear: YearRange = addYears(-1)
}

object YearRange {

  def apply(): YearRange = apply(Times.currentYear, DefaultTimeCalendar)

  def apply(year: Int): YearRange = apply(year, DefaultTimeCalendar)

  def apply(year: Int, calendar: ITimeCalendar): YearRange =
    new YearRange(year, calendar)

  def apply(moment: DateTime): YearRange =
    apply(moment, DefaultTimeCalendar)

  def apply(moment: DateTime, calendar: ITimeCalendar): YearRange =
    new YearRange(moment.getYear, calendar)
}
