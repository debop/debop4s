package debop4s.timeperiod.timerange

import java.util

import debop4s.timeperiod.TimeSpec._
import debop4s.timeperiod._
import debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

import scala.beans.BeanProperty
import scala.collection.SeqView


/**
 * debop4s.timeperiod.timerange.HourTimeRange
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 28. 오후 9:32
 */
class HourTimeRange(private[this] val _moment: DateTime,
                    @BeanProperty val hourCount: Int = 1,
                    private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
  extends CalendarTimeRange(Times.relativeHourPeriod(_moment, hourCount), _calendar) {

  val endHour: Int = start.plusHours(hourCount).getHourOfDay
  def getEndHour = endHour

  def minutes: SeqView[MinuteRange, Seq[_]] = {
    val count = hourCount * MinutesPerHour
    (0 until count).view.map { m =>
      MinuteRange(start.plusMinutes(m), calendar)
    }
  }

  def getMinutes: util.List[MinuteRange] = {
    val count = hourCount * MinutesPerHour

    val results = new util.ArrayList[MinuteRange](count)
    (0 until count) foreach { m =>
      results add MinuteRange(start.plusMinutes(m), calendar)
    }
    results
  }
}

object HourTimeRange {

  def apply(moment: DateTime, hourCount: Int): HourTimeRange =
    apply(moment, hourCount, DefaultTimeCalendar)

  def apply(moment: DateTime, hourCount: Int, calendar: ITimeCalendar): HourTimeRange =
    new HourTimeRange(moment, hourCount, calendar)
}
