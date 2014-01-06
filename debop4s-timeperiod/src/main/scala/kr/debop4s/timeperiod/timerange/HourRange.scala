package kr.debop4s.timeperiod.timerange

import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

/**
 * kr.debop4s.timeperiod.timerange.HourRange
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 28. 오후 9:48
 */
@SerialVersionUID(2876823794105220883L)
class HourRange(moment: DateTime, private val _calendar: ITimeCalendar = DefaultTimeCalendar)
    extends HourTimeRange(moment, 1, _calendar) {

    def this(year: Int, monthOfYear: Int, dayOfMonth: Int, hourOfDay: Int, calendar: ITimeCalendar) {
        this(new DateTime(year, monthOfYear, dayOfMonth, hourOfDay, 0), calendar)
    }

    def this(year: Int, monthOfYear: Int, dayOfMonth: Int, hourOfDay: Int) {
        this(new DateTime(year, monthOfYear, dayOfMonth, hourOfDay, 0), DefaultTimeCalendar)
    }

    def year: Int = startYear

    def monthOfYear: Int = startMonthOfYear

    def dayOfMonth: Int = startDayOfMonth

    def hourOfDay: Int = startHourOfDay

    def previousHour: HourRange = addHours(-1)

    def nextHours: HourRange = addHours(1)

    def addHours(hours: Int): HourRange = {
        val startHour = Times.trimToHour(getStart, hourOfDay)
        new HourRange(startHour.plusHours(hours), calendar)
    }
}
