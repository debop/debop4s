package kr.debop4s.timeperiod.timerange

import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

/**
 * kr.debop4s.timeperiod.timerange.MinuteRange
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 27. 오후 7:11
 */
@SerialVersionUID(4111802915947727424L)
class MinuteRange(private val _moment: DateTime,
                  private val _calendar: ITimeCalendar = DefaultTimeCalendar)
    extends MinuteTimeRange(Times.trimToSecond(_moment), 1, _calendar) {

    def this(calendar: ITimeCalendar) {
        this(Times.trimToSecond(Times.now), calendar)
    }

    def this() {
        this(DefaultTimeCalendar)
    }

    def this(year: Int,
             monthOfYear: Int,
             dayOfMonth: Int,
             hourOfDay: Int,
             minuteOfHour: Int,
             calendar: ITimeCalendar) {
        this(new DateTime(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour), calendar)
    }

    def this(year: Int,
             monthOfYear: Int,
             dayOfMonth: Int,
             hourOfDay: Int,
             minuteOfHour: Int) {
        this(new DateTime(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour), DefaultTimeCalendar)
    }

    def year = startYear

    def monthOfYear = startMonthOfYear

    def dayOfMonth = startDayOfMonth

    def hourOfDay = startHourOfDay

    def minuteOfHour = startMinuteOfHour

    def previousMinute: MinuteRange = addMinutes(-1)

    def nextMinute: MinuteRange = addMinutes(1)

    def addMinutes(minutes: Int): MinuteRange = {
        val start = getStart.withTimeAtStartOfDay().withTime(hourOfDay, minuteOfHour, 0, 0)
        new MinuteRange(start.plusMinutes(minutes), calendar)
    }
}
