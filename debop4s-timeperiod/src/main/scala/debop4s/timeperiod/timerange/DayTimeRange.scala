package debop4s.timeperiod.timerange

import debop4s.core.jodatime._
import debop4s.timeperiod.DayOfWeek._
import debop4s.timeperiod._
import debop4s.timeperiod.utils.Times
import org.joda.time.DateTime
import scala.collection.SeqView

/**
 * debop4s.timeperiod.timerange.DayTimeRange
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 28. 오후 10:27
 */
class DayTimeRange(private[this] val _start: DateTime,
                   val dayCount: Int,
                   private[this] var _calendar: ITimeCalendar = DefaultTimeCalendar)
    extends CalendarTimeRange(Times.relativeDayPeriod(_start, dayCount), _calendar) {

    def startDayOfWeek: DayOfWeek = calendar.dayOfWeek(start)

    def endDayOfWeek: DayOfWeek = calendar.dayOfWeek(end)

    @inline
    def hours: SeqView[HourRange, Seq[_]] = {
        val day = startDayStart
        val hours = dayCount * HoursPerDay
        (0 until hours).view.map { h => HourRange(day + h.hour, calendar) }
    }
}

object DayTimeRange {

    def apply(moment: DateTime, dayCount: Int): DayTimeRange =
        apply(moment, dayCount, DefaultTimeCalendar)

    def apply(moment: DateTime, dayCount: Int, calendar: ITimeCalendar): DayTimeRange =
        new DayTimeRange(moment, dayCount, calendar)

    def apply(year: Int, monthOfYear: Int, dayOfMonth: Int, dayCount: Int): DayTimeRange =
        apply(year, monthOfYear, dayOfMonth, dayCount, DefaultTimeCalendar)

    def apply(year: Int, monthOfYear: Int, dayOfMonth: Int, dayCount: Int, calendar: ITimeCalendar): DayTimeRange =
        new DayTimeRange(Times.asDate(year, monthOfYear, dayOfMonth), dayCount, calendar)

}
