package kr.debop4s.timeperiod.timerange

import kr.debop4s.core.utils.{ToStringHelper, Hashs}
import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.utils.Times
import org.joda.time.{DateTime, Duration}

/**
 * kr.debop4s.timeperiod.timerange.CalendarTimeRange
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 27. 오후 4:16
 */
@SerialVersionUID(-422889827258277497L)
class CalendarTimeRange(private val _period: ITimePeriod, val calendar: ITimeCalendar = DefaultTimeCalendar)
  extends TimeRange(calendar.mapStart(_period.getStart), calendar.mapEnd(_period.getEnd), true) {

  private val _mappedStart = calendar.mapStart(_period.start)
  private val _mappedEnd = calendar.mapEnd(_period.end)
  Times.assertValidPeriod(_mappedStart, _mappedEnd)

  def startYear: Int = start.getYear

  def startMonthOfYear: Int = start.getMonthOfYear

  def startDayOfMonth: Int = start.getDayOfMonth

  def startHourOfDay: Int = start.getHourOfDay

  def startMinuteOfHour: Int = start.getMinuteOfHour

  def endYear: Int = end.getYear

  def endMonthOfYear: Int = end.getMonthOfYear

  def endDayOfMonth: Int = end.getDayOfMonth

  def endHourOfDay: Int = end.getHourOfDay

  def endMinuteOfHour: Int = end.getMinuteOfHour

  def mappedStart = calendar.mapStart(start)

  def mappedEnd = calendar.mapEnd(end)

  def unmappedStart = calendar.unmapStart(start)

  def unmappedEnd = calendar.unmapEnd(end)

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

  override def copy(offset: Duration): CalendarTimeRange = {
    new CalendarTimeRange(super.copy(offset), calendar)
  }

  override def hashCode(): Int = Hashs.compute(super.hashCode(), calendar)

  override protected def buildStringHelper: ToStringHelper =
    super.buildStringHelper
    .add("calendar", calendar)
}

object CalendarTimeRange {

  def apply(): CalendarTimeRange = apply(DefaultTimeCalendar)

  def apply(calendar: ITimeCalendar): CalendarTimeRange = apply(MinPeriodTime, MaxPeriodTime, calendar)

  def apply(start: DateTime, end: DateTime): CalendarTimeRange =
    apply(start, end, DefaultTimeCalendar)

  def apply(start: DateTime, end: DateTime, calendar: ITimeCalendar): CalendarTimeRange =
    new CalendarTimeRange(TimeRange(start, end), calendar)
}
