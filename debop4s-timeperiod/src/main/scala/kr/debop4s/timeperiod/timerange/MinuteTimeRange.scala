package kr.debop4s.timeperiod.timerange

import kr.debop4s.core.utils.{ToStringHelper, Hashs}
import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

/**
 * kr.debop4s.timeperiod.timerange.MinuteTimeRange
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 27. 오후 7:03
 */
@SerialVersionUID(-5669915582907325590L)
abstract class MinuteTimeRange(private val _start: DateTime,
                               val minuteCount: Int = 1,
                               private val _calendar: ITimeCalendar = DefaultTimeCalendar)
  extends CalendarTimeRange(Times.relativeMinutePeriod(_start, minuteCount), _calendar) {

  def this(year: Int,
           monthOfYear: Int,
           dayOfMonth: Int,
           hourOfDay: Int,
           minuteOfHour: Int,
           minuteCount: Int,
           calendar: ITimeCalendar) {
    this(new DateTime(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour), minuteCount, calendar)
  }

  def this(year: Int,
           monthOfYear: Int,
           dayOfMonth: Int,
           hourOfDay: Int,
           minuteOfHour: Int,
           minuteCount: Int) {
    this(new DateTime(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour), minuteCount, DefaultTimeCalendar)
  }

  assert(minuteCount > 0)
  val endMinute = start.plusMinutes(minuteCount).getMinuteOfHour

  override def hashCode(): Int = Hashs.compute(super.hashCode(), endMinute)

  override protected def buildStringHelper: ToStringHelper =
    super.buildStringHelper
    .add("minuteCount", minuteCount)
    .add("endMinute", endMinute)
}

