package kr.debop4s.timeperiod.timerange

import kr.debop4s.core.utils.{ToStringHelper, Hashs}
import kr.debop4s.timeperiod.DayOfWeek.DayOfWeek
import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime
import scala.collection.mutable.ArrayBuffer

/**
 * kr.debop4s.timeperiod.timerange.DayTimeRange
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 28. 오후 10:27
 */
abstract class DayTimeRange(private[this] val _start: DateTime,
                            val dayCount: Int,
                            private[this] var _calendar: ITimeCalendar = DefaultTimeCalendar)
  extends CalendarTimeRange(Times.relativeDayPeriod(_start, dayCount), _calendar) {

  def this(year: Int, monthOfYear: Int, dayOfMonth: Int, dayCount: Int, calendar: ITimeCalendar) {
    this(Times.asDate(year, monthOfYear, dayOfMonth), dayCount, calendar)
  }

  def this(year: Int, monthOfYear: Int, dayOfMonth: Int, dayCount: Int) {
    this(Times.asDate(year, monthOfYear, dayOfMonth), dayCount, DefaultTimeCalendar)
  }

  def startDayOfWeek: DayOfWeek = calendar.getDayOfWeek(start)

  def endDayOfWeek: DayOfWeek = calendar.getDayOfWeek(end)

  def getHours: Seq[HourRange] = {
    val day = startDayStart
    val hours = ArrayBuffer[HourRange]()

    for (d <- 0 until dayCount) {
      for (h <- 0 until HoursPerDay) {
        hours += new HourRange(day.plusHours(d * HoursPerDay + h), calendar)
      }
    }
    hours
  }

  override def hashCode() = Hashs.compute(super.hashCode(), dayCount)

  override protected def buildStringHelper =
    ToStringHelper(this)
    .add("start", start)
    .add("end", end)
    .add("dayCount", dayCount)
    .add("calendar", calendar)
}
