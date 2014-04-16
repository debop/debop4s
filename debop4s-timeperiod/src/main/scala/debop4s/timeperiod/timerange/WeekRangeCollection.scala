package debop4s.timeperiod.timerange

import debop4s.timeperiod._
import debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

/**
 * debop4s.timeperiod.timerange.WeekRangeCollection
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 28. 오후 11:25
 */
class WeekRangeCollection(private[this] val year: Int,
                          private[this] val weekOfYear: Int,
                          private[this] val weekCount: Int,
                          private[this] val calendar: ITimeCalendar = DefaultTimeCalendar)
    extends WeekTimeRange(Times.startTimeOfWeek(year, weekOfYear), weekCount, calendar) {

    @inline
    def getWeeks = {
        (0 until weekCount).view.map { w =>
            WeekRange(start.plusWeeks(w), calendar)
        }
    }
}

object WeekRangeCollection {

    def apply(year: Int, weekOfYear: Int, weekCount: Int): WeekRangeCollection =
        apply(year, weekOfYear, weekCount, DefaultTimeCalendar)

    def apply(year: Int, weekOfYear: Int, weekCount: Int, calendar: ITimeCalendar): WeekRangeCollection =
        new WeekRangeCollection(year, weekOfYear, weekCount, calendar)

    def apply(moment: DateTime, weekCount: Int): WeekRangeCollection =
        apply(moment, weekCount, DefaultTimeCalendar)

    def apply(moment: DateTime, weekCount: Int, calendar: ITimeCalendar): WeekRangeCollection =
        new WeekRangeCollection(moment.getYear, moment.getWeekOfWeekyear, weekCount, calendar)


}
