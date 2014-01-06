package kr.debop4s.timeperiod.timerange

import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

/**
 * kr.debop4s.timeperiod.timerange.MonthRange
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 29. 오후 4:31
 */
@SerialVersionUID(6337203416072219224L)
class MonthRange(private[this] val _year: Int,
                 private[this] val _monthOfYear: Int,
                 private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
    extends MonthTimeRange(_year, _monthOfYear, 1, _calendar) {

    val daysInMonth = Times.getDaysInMonth(_year, _monthOfYear)

    def this(moment: DateTime, calendar: ITimeCalendar) {
        this(moment.getYear, moment.getMonthOfYear, calendar)
    }

    def this(moment: DateTime) {
        this(moment.getYear, moment.getMonthOfYear, DefaultTimeCalendar)
    }

    def year: Int = startYear

    def monthOfYear: Int = startMonthOfYear

    def addMonths(months: Int): MonthRange =
        new MonthRange(Times.startTimeOfMonth(getStart).plusMonths(months), calendar)

    def nextMonth: MonthRange = addMonths(1)

    def previousMonth: MonthRange = addMonths(-1)
}
