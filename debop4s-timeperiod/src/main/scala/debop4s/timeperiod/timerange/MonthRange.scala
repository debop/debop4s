package debop4s.timeperiod.timerange

import debop4s.timeperiod.TimeSpec._
import debop4s.timeperiod._
import debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

/**
 * kr.hconnect.timeperiod.timerange.MonthRange
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 29. 오후 4:31
 */
@SerialVersionUID(6337203416072219224L)
class MonthRange(private[this] val _year: Int,
                 private[this] val _monthOfYear: Int,
                 private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
  extends MonthTimeRange(_year, _monthOfYear, 1, _calendar) {

  def this() = this(Times.today.getYear, Times.today.getMonthOfYear, DefaultTimeCalendar)
  def this(moment: DateTime) = this(moment.getYear, moment.getMonthOfYear, DefaultTimeCalendar)
  def this(moment: DateTime, calendar: ITimeCalendar) = this(moment.getYear, moment.getMonthOfYear, calendar)
  def this(year: Int, monthOfYear: Int) = this(year, monthOfYear, DefaultTimeCalendar)

  val daysInMonth = Times.daysInMonth(_year, _monthOfYear)

  def year: Int = startYear

  def getYear = year

  def monthOfYear: Int = startMonthOfYear

  def getMonthOfYear = monthOfYear

  def addMonths(months: Int): MonthRange =
    MonthRange(Times.startTimeOfMonth(start).plusMonths(months), calendar)

  def nextMonth: MonthRange = addMonths(1)

  def previousMonth: MonthRange = addMonths(-1)
}

object MonthRange {

  def apply(): MonthRange = apply(Times.now, DefaultTimeCalendar)

  def apply(calendar: ITimeCalendar): MonthRange = apply(Times.now, calendar)

  def apply(year: Int, monthOfYear: Int): MonthRange = apply(year, monthOfYear, DefaultTimeCalendar)

  def apply(year: Int, monthOfYear: Int, calendar: ITimeCalendar): MonthRange =
    new MonthRange(year, monthOfYear, calendar)

  def apply(moment: DateTime): MonthRange =
    apply(moment, DefaultTimeCalendar)

  def apply(moment: DateTime, calendar: ITimeCalendar): MonthRange =
    new MonthRange(moment.getYear, moment.getMonthOfYear, calendar)
}
