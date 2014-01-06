package kr.debop4s.timeperiod.timerange

import kr.debop4s.core.utils.Hashs
import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime
import scala.collection.mutable.ArrayBuffer

/**
 * kr.debop4s.timeperiod.timerange.WeekTimeRange
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 28. 오후 11:05
 */
@SerialVersionUID(-1899389597363540458L)
abstract class WeekTimeRange(private val _moment: DateTime,
                             val weekCount: Int,
                             private val _calendar: ITimeCalendar = DefaultTimeCalendar)
    extends CalendarTimeRange(Times.relativeWeekPeriod(_moment, weekCount), _calendar) {

    def this(year: Int, weekOfYear: Int, weekCount: Int, calendar: ITimeCalendar) {
        this(Times.startTimeOfWeek(year, weekOfYear, calendar), weekCount, calendar)
    }

    def this(year: Int, weekOfYear: Int, weekCount: Int) {
        this(year, weekOfYear, weekCount, DefaultTimeCalendar)
    }

    def year: Int = getStart.getYear

    def startWeekOfYear: Int = Times.getWeekOfYear(getStart).weekOfYear

    def endWeekOfYear: Int = Times.getWeekOfYear(getEnd).weekOfYear

    def getDays: Seq[DayRange] = {
        val startDay = startDayStart
        val dayCount = weekCount * DaysPerWeek
        val days = ArrayBuffer[DayRange]()
        for (d <- 0 until dayCount) {
            days += new DayRange(startDay.plusDays(d), calendar)
        }
        days
    }

    override def hashCode() = Hashs.compute(super.hashCode(), weekCount)

    override protected def buildStringHelper =
        super.buildStringHelper
            .add("weekCount", weekCount)
}
