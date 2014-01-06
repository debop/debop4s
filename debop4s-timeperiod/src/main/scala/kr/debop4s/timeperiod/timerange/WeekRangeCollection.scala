package kr.debop4s.timeperiod.timerange

import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime
import scala.collection.mutable.ArrayBuffer

/**
 * kr.debop4s.timeperiod.timerange.WeekRangeCollection
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 28. 오후 11:25
 */
class WeekRangeCollection(private val _year: Int,
                          private val _weekOfYear: Int,
                          private val _weekCount: Int,
                          private val _calendar: ITimeCalendar = DefaultTimeCalendar)
    extends WeekTimeRange(Times.startTimeOfWeek(_year, _weekOfYear, _calendar), _weekCount, _calendar) {

    def this(moment: DateTime, weekCount: Int, calendar: ITimeCalendar) {
        this(moment.getYear, moment.getWeekOfWeekyear, weekCount, calendar)
    }

    def this(moment: DateTime, weekCount: Int) {
        this(moment.getYear, moment.getWeekOfWeekyear, weekCount, DefaultTimeCalendar)
    }

    def getWeeks: Seq[WeekRange] = {
        val weeks = ArrayBuffer[WeekRange]()
        for (w <- 0 until weekCount) {
            weeks += new WeekRange(start.plusWeeks(w), calendar)
        }
        weeks
    }
}
