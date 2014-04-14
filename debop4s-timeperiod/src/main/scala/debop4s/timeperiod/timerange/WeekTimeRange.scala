package debop4s.timeperiod.timerange

import debop4s.timeperiod._
import debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

/**
 * debop4s.timeperiod.timerange.WeekTimeRange
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 28. 오후 11:05
 */
@SerialVersionUID(-1899389597363540458L)
class WeekTimeRange(private[this] val _moment: DateTime,
                    val weekCount: Int,
                    private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
    extends CalendarTimeRange(WeekTimeRange.getPeriodOf(_moment, weekCount), _calendar) {

    def year: Int = start.getYear

    def weekyear: Int = start.getWeekyear

    def startWeekOfYear: Int = Times.weekOfYear(start).weekOfWeekyear

    def endWeekOfYear: Int = Times.weekOfYear(end).weekOfWeekyear

    @inline
    def days = {
        val startDay = startDayStart
        val dayCount = weekCount * DaysPerWeek

        (0 until dayCount).view.map {
            d =>
                DayRange(startDay.plusDays(d), calendar)
        }
    }
}

object WeekTimeRange {

    def apply(moment: DateTime, weekCount: Int): WeekTimeRange =
        apply(moment, weekCount, DefaultTimeCalendar)

    def apply(moment: DateTime, weekCount: Int, calendar: ITimeCalendar): WeekTimeRange =
        new WeekTimeRange(moment, weekCount, calendar)

    def apply(year: Int, weekOfYear: Int, weekCount: Int): WeekTimeRange =
        apply(year, weekOfYear, weekCount, DefaultTimeCalendar)

    def apply(year: Int, weekOfYear: Int, weekCount: Int, calendar: ITimeCalendar): WeekTimeRange =
        new WeekTimeRange(Times.startTimeOfWeek(year, weekOfYear), weekCount, calendar)

    def getPeriodOf(moment: DateTime, weekCount: Int): TimeRange = {
        require(weekCount > 0)
        val startWeek = Times.startTimeOfWeek(moment)
        TimeRange(startWeek, startWeek.plusWeeks(weekCount))
    }

    def getPeriodOf(year: Int, weekOfYear: Int, weekCount: Int) {
        require(weekCount > 0)
        val startWeek = Times.startTimeOfWeek(year, weekOfYear)
        TimeRange(startWeek, startWeek.plusWeeks(weekCount))
    }
}
