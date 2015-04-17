package debop4s.timeperiod.timerange

import debop4s.timeperiod.TimeSpec._
import debop4s.timeperiod._

@SerialVersionUID(-7922671338410846872L)
class YearCalendarTimeRange(private[this] val _period: ITimePeriod,
                            private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
  extends CalendarTimeRange(_period, _calendar) {

  val yearBaseMonth = 1: Int
  // def yearBaseMonth: Int = 1

  def baseYear: Int = startYear
}
