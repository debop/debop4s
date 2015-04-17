package debop4s.timeperiod.timerange

import debop4s.timeperiod.TimeSpec._
import debop4s.timeperiod._
import debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

@SerialVersionUID(7993201574147735665L)
class DayRange(private[this] val _moment: DateTime,
               private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
  extends DayTimeRange(Times.asDate(_moment), 1, _calendar) {

  def this() = this(Times.today, DefaultTimeCalendar)
  def this(moment: DateTime) = this(moment, DefaultTimeCalendar)
  def this(year: Int, monthOfYear: Int, dayOfMonth: Int) =
    this(Times.asDate(year, monthOfYear, dayOfMonth), DefaultTimeCalendar)
  def this(year: Int, monthOfYear: Int, dayOfMonth: Int, calendar: ITimeCalendar) =
    this(Times.asDate(year, monthOfYear, dayOfMonth), calendar)

  def year: Int = startYear
  def getYear = year

  def monthOfYear: Int = startMonthOfYear
  def getMonthOfYear = monthOfYear

  def dayOfMonth: Int = startDayOfMonth
  def getDayOfMonth = dayOfMonth

  def dayOfWeek: DayOfWeek = startDayOfWeek
  def getDayOfWeek = dayOfWeek

  def addDays(days: Int): DayRange =
    DayRange(Times.asDate(start).plusDays(days), calendar)

  def previousDay: DayRange = addDays(-1)

  def nextDay: DayRange = addDays(1)
}

object DayRange {

  def apply(): DayRange = new DayRange(Times.today, DefaultTimeCalendar)

  def apply(calendar: TimeCalendar): DayRange = new DayRange(Times.today, calendar)

  def apply(moment: DateTime): DayRange = new DayRange(moment, DefaultTimeCalendar)

  def apply(moment: DateTime = Times.today, calendar: ITimeCalendar): DayRange =
    new DayRange(moment, calendar)

  def apply(year: Int, monthOfYear: Int, dayOfMonth: Int): DayRange =
    new DayRange(Times.asDate(year, monthOfYear, dayOfMonth), DefaultTimeCalendar)

  def apply(year: Int, monthOfYear: Int, dayOfMonth: Int, calendar: ITimeCalendar): DayRange =
    new DayRange(Times.asDate(year, monthOfYear, dayOfMonth), calendar)

}
