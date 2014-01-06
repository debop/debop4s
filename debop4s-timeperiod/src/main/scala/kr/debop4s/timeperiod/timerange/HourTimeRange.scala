package kr.debop4s.timeperiod.timerange

import kr.debop4s.core.utils.Hashs
import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime
import scala.collection.mutable.ArrayBuffer

/**
 * kr.debop4s.timeperiod.timerange.HourTimeRange
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 28. 오후 9:32
 */
abstract class HourTimeRange(private val _moment: DateTime,
                             val hourCount: Int = 1,
                             private val _calendar: ITimeCalendar = DefaultTimeCalendar)
    extends CalendarTimeRange(Times.relativeHourPeriod(_moment, hourCount), _calendar) {

    val endHour: Int = getStart.plusHours(hourCount).getHourOfDay

    def getMinutes: Seq[MinuteRange] = {
        val minutes = ArrayBuffer[MinuteRange]()
        for (h <- 0 until hourCount) {
            for (m <- 0 until MinutesPerHour) {
                minutes += new MinuteRange(start.plusHours(h).plusMinutes(m), calendar)
            }
        }
        minutes
    }

    override def hashCode() = Hashs.compute(super.hashCode(), hourCount)

    override protected def buildStringHelper =
        super.buildStringHelper
            .add("hourCount", hourCount)
}

