package kr.debop4s.timeperiod

import kr.debop4s.core.utils.Hashs
import kr.debop4s.timeperiod.DayOfWeek.DayOfWeek

/**
 * kr.debop4s.timeperiod.DayHourRange
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 4. 오후 11:30
 */
@SerialVersionUID(2595125864993419600L)
class DayHourRange(val dayOfWeek: DayOfWeek,
                   private[this] val _startHourOfDay: Int,
                   private[this] val _endHourOfDay: Int)
  extends HourRangeInDay(_startHourOfDay, _endHourOfDay) {

  override def hashCode() = Hashs.compute(dayOfWeek, start, end)

  override protected def buildStringHelper =
    super.buildStringHelper
    .add("dayOfWeek", dayOfWeek)
}
