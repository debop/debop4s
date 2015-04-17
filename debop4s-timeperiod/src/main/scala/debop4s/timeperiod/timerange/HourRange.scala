package debop4s.timeperiod.timerange

import debop4s.timeperiod.TimeSpec._
import debop4s.timeperiod._
import debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

@SerialVersionUID(2876823794105220883L)
class HourRange(private[this] val _moment: DateTime,
                private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
  extends HourTimeRange(_moment, 1, _calendar) {

  def this() = this(Times.now, DefaultTimeCalendar)
  def this(calendar: ITimeCalendar) = this(Times.now, calendar)
  def this(moment: DateTime) = this(moment, DefaultTimeCalendar)
  def this(year: Int, monthOfYear: Int, dayOfMonth: Int, hourOfDay: Int) =
    this(new DateTime(year, monthOfYear, dayOfMonth, hourOfDay, 0), DefaultTimeCalendar)
  def this(year: Int, monthOfYear: Int, dayOfMonth: Int, hourOfDay: Int, calendar: ITimeCalendar) =
    this(new DateTime(year, monthOfYear, dayOfMonth, hourOfDay, 0), calendar)

  def year: Int = startYear
  def getYear = year

  def monthOfYear: Int = startMonthOfYear
  def getMonthOfYear = monthOfYear

  def dayOfMonth: Int = startDayOfMonth
  def getDayOfMonth = dayOfMonth

  def hourOfDay: Int = startHourOfDay
  def getHourOfDay = hourOfDay

  def previousHour: HourRange = addHours(-1)

  def nextHour: HourRange = addHours(1)

  def addHours(hours: Int): HourRange = {
    val startHour = Times.trimToHour(start, hourOfDay)
    HourRange(startHour.plusHours(hours), calendar)
  }
}

object HourRange {

  def apply(): HourRange = apply(Times.now, DefaultTimeCalendar)

  def apply(calendar: ITimeCalendar): HourRange = apply(Times.today, calendar)

  def apply(moment: DateTime): HourRange = apply(moment, DefaultTimeCalendar)

  def apply(moment: DateTime, calendar: ITimeCalendar): HourRange = {
    new HourRange(moment, calendar)
  }

  def apply(year: Int, monthOfYear: Int, dayOfMonth: Int, hourOfDay: Int): HourRange = {
    apply(year, monthOfYear, dayOfMonth, hourOfDay, DefaultTimeCalendar)
  }

  def apply(year: Int, monthOfYear: Int, dayOfMonth: Int, hourOfDay: Int, calendar: ITimeCalendar): HourRange = {
    new HourRange(new DateTime(year, monthOfYear, dayOfMonth, hourOfDay, 0), calendar)
  }
}
