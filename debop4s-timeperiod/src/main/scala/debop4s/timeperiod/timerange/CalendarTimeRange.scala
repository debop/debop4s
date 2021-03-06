package debop4s.timeperiod.timerange

import debop4s.core.utils.Hashs
import debop4s.timeperiod.TimeSpec._
import debop4s.timeperiod._
import debop4s.timeperiod.utils.Times
import org.joda.time.{DateTime, Duration}

/**
 * debop4s.timeperiod.timerange.CalendarTimeRange
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 27. 오후 4:16
 */
@SerialVersionUID(-422889827258277497L)
class CalendarTimeRange(private[this] val _period: ITimePeriod,
                        val calendar: ITimeCalendar = DefaultTimeCalendar)
  extends TimeRange(calendar.mapStart(_period.start),
                    calendar.mapEnd(_period.end),
                    true) {

  def this(period: ITimePeriod) = this(period, DefaultTimeCalendar)
  def this(start: DateTime, end: DateTime) = this(TimeRange(start, end), DefaultTimeCalendar)
  def this(start: DateTime, end: DateTime, calendar: ITimeCalendar) = this(TimeRange(start, end), calendar)

  private[this] val _mappedStart = calendar.mapStart(_period.start)
  private[this] val _mappedEnd = calendar.mapEnd(_period.end)
  Times.assertValidPeriod(_mappedStart, _mappedEnd)

  def getTimeCalendar: ITimeCalendar = calendar

  def startYear: Int = start.getYear
  def getStartYear = startYear

  def startMonthOfYear: Int = start.getMonthOfYear
  def getStartMonthOfYear = startMonthOfYear

  def startDayOfMonth: Int = start.getDayOfMonth
  def getStartDayOfMonth = startDayOfMonth

  def startHourOfDay: Int = start.getHourOfDay
  def getStartHourOfDay = startHourOfDay

  def startMinuteOfHour: Int = start.getMinuteOfHour
  def getStartMinuteOfHour = startMinuteOfHour

  def endYear: Int = end.getYear
  def getEndYear = endYear
  def endMonthOfYear: Int = end.getMonthOfYear
  def getEndMonthOfYear = endMonthOfYear
  def endDayOfMonth: Int = end.getDayOfMonth
  def getEndDayOfMonth = endDayOfMonth
  def endHourOfDay: Int = end.getHourOfDay
  def getEndHourOfDay = endHourOfDay
  def endMinuteOfHour: Int = end.getMinuteOfHour
  def getEndMinuteOfHour = endMinuteOfHour

  def mappedStart = calendar.mapStart(start)
  def getMappedStart = mappedStart

  def mappedEnd = calendar.mapEnd(end)
  def getMappedEnd = mappedEnd

  def unmappedStart = calendar.unmapStart(start)
  def getUnmappedStart = unmappedStart

  def unmappedEnd = calendar.unmapEnd(end)
  def getUnmappedEnd = unmappedEnd

  def startMonthStart = Times.trimToDay(start)

  def endMonthStart = Times.trimToDay(end)

  def startDayStart = Times.trimToHour(start)

  def endDayStart = Times.trimToHour(end)

  def startHourStart = Times.trimToMinute(start)

  def endHourStart = Times.trimToMinute(end)

  def startMinuteStart = Times.trimToSecond(start)

  def endMinuteStart = Times.trimToSecond(end)

  def startSecondStart = Times.trimToMillis(start)

  def endSecondEnd = Times.trimToMillis(end)

  override def copy(offset: Duration) = {
    CalendarTimeRange(super.copy(offset), calendar)
  }

  override def hashCode: Int = Hashs.compute(super.hashCode(), calendar)

  //    override protected def buildStringHelper: ToStringHelper =
  //        super.buildStringHelper
  //        .add("calendar", calendar)
}

object CalendarTimeRange {

  def apply(): CalendarTimeRange = apply(DefaultTimeCalendar)

  def apply(calendar: ITimeCalendar): CalendarTimeRange = apply(MinPeriodTime, MaxPeriodTime, calendar)

  def apply(start: DateTime, end: DateTime): CalendarTimeRange =
    apply(start, end, DefaultTimeCalendar)

  def apply(start: DateTime, end: DateTime, calendar: ITimeCalendar): CalendarTimeRange =
    new CalendarTimeRange(TimeRange(start, end), calendar)

  def apply(period: ITimePeriod): CalendarTimeRange =
    apply(period, DefaultTimeCalendar)

  def apply(period: ITimePeriod, calendar: ITimeCalendar): CalendarTimeRange =
    new CalendarTimeRange(period, calendar)
}
