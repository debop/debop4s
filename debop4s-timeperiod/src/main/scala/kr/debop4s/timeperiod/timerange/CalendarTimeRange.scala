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
    extends TimeRange(_period.getStart, _period.getEnd, true) {

    def this(start: DateTime, end: DateTime, calendar: ITimeCalendar) {
        this(new TimeRange(start, end, true), calendar)
    }

    def this(start: DateTime, end: DateTime) {
        this(start, end, DefaultTimeCalendar)
    }

    def this(calendar: ITimeCalendar) {
        this(MinPeriodTime, MaxPeriodTime, calendar)
    }

    def this() {
        this(DefaultTimeCalendar)
    }

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
