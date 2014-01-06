package kr.debop4s.timeperiod.timerange

import kr.debop4s.timeperiod.DayOfWeek.DayOfWeek
import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

/**
 * kr.debop4s.timeperiod.timerange.DayRange
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 28. 오후 10:40
 */
@SerialVersionUID(7993201574147735665L)
class DayRange(private[this] val _moment: DateTime,
               private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
    extends DayTimeRange(Times.asDate(_moment), 1, _calendar) {

    def this(year: Int, monthOfYear: Int, dayOfMonth: Int, calendar: ITimeCalendar) {
        this(Times.asDate(year, monthOfYear, dayOfMonth), calendar)
    }

    def this(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        this(year, monthOfYear, dayOfMonth, DefaultTimeCalendar)
    }

    def this(calendar: ITimeCalendar) {
        this(Times.today, calendar)
    }

    def this() {
        this(DefaultTimeCalendar)
    }

    def year: Int = startYear

    def monthOfYear: Int = startMonthOfYear

    def dayOfMonth: Int = startDayOfMonth

    def dayOfWeek: DayOfWeek = startDayOfWeek

    def addDays(days: Int): DayRange = {
        new DayRange(Times.asDate(getStart).plusDays(days), calendar)
    }

    def previousDay: DayRange = addDays(-1)

    def nextDay: DayRange = addDays(1)


}
