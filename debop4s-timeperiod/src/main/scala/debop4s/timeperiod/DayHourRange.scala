package debop4s.timeperiod

import debop4s.core.utils.Hashs

@SerialVersionUID(2595125864993419600L)
class DayHourRange(val dayOfWeek: DayOfWeek,
                   private[this] val _startHourOfDay: Int,
                   private[this] val _endHourOfDay: Int)
  extends HourRangeInDay(Timepart(_startHourOfDay), Timepart(_endHourOfDay)) {

  override def hashCode = Hashs.compute(dayOfWeek, start, end)

  override protected def buildStringHelper =
    super.buildStringHelper
    .add("dayOfWeek", dayOfWeek)
}

object DayHourRange {

  def apply(dayOfWeek: DayOfWeek, startHourOfDay: Int = 0, endHourOfDay: Int = 23): DayHourRange = {
    val startHour = (startHourOfDay min endHourOfDay) max 0
    val endHour = (startHourOfDay max endHourOfDay) min 23
    new DayHourRange(dayOfWeek, startHour, endHour)
  }
}
