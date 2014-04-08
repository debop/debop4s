package debop4s.timeperiod.timerange

import debop4s.timeperiod.DayOfWeek.DayOfWeek
import debop4s.timeperiod._
import debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

/**
 * debop4s.timeperiod.timerange.DayRange
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 28. 오후 10:40
 */
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

@SerialVersionUID(7993201574147735665L)
class DayRange(private[this] val _moment: DateTime,
               private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
    extends DayTimeRange(Times.asDate(_moment), 1, _calendar) {

    def year: Int = startYear

    def monthOfYear: Int = startMonthOfYear

    def dayOfMonth: Int = startDayOfMonth

    def dayOfWeek: DayOfWeek = startDayOfWeek

    def addDays(days: Int): DayRange =
        DayRange(Times.asDate(start).plusDays(days), calendar)

    def previousDay: DayRange = addDays(-1)

    def nextDay: DayRange = addDays(1)
}
