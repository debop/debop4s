package kr.debop4s.timeperiod.timerange

import kr.debop4s.core.utils.{ToStringHelper, Hashs}
import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime
import scala.collection.mutable.ArrayBuffer

/**
 * kr.debop4s.timeperiod.timerange.MonthTimeRange
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 29. 오전 11:41
 */
class MonthTimeRange(private[this] val _year: Int,
                     private[this] val _monthOfYear: Int,
                     val monthCount: Int,
                     private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
    extends CalendarTimeRange(Times.relativeMonthPeriod(Times.startTimeOfMonth(_year, _monthOfYear), monthCount), _calendar) {

    def this(moment: DateTime, monthCount: Int, calendar: ITimeCalendar) {
        this(moment.getYear, moment.getMonthOfYear, monthCount, calendar)
    }

    def this(moment: DateTime, monthCount: Int) {
        this(moment.getYear, moment.getMonthOfYear, monthCount, DefaultTimeCalendar)
    }

    def getDays: Seq[DayRange] = {
        val startMonth = Times.startTimeOfMonth(getStart)
        val days = ArrayBuffer[DayRange]()

        for (m <- 0 until monthCount) {
            val currentMonth = startMonth.plusMonths(m)
            val daysOfMonth = Times.getDaysInMonth(currentMonth.getYear, currentMonth.getMonthOfYear)
            for (d <- 0 until daysOfMonth) {
                days += new DayRange(currentMonth.plusDays(d), calendar)
            }
        }
        days
    }

    override def hashCode() = Hashs.compute(super.hashCode(), monthCount)

    override protected def buildStringHelper: ToStringHelper =
        ToStringHelper(this)
            .add("year", startYear)
            .add("monthOfYear", startMonthOfYear)
            .add("monthCount", monthCount)
            .add("calendar", calendar)
}
