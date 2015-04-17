package debop4s.timeperiod.calendars.seeker

import debop4s.timeperiod.calendars.ICalendarVisitorContext
import debop4s.timeperiod.timerange.DayRange


class DaySeekerContext(val startDay: DayRange,
                       private[this] val _dayCount: Int) extends ICalendarVisitorContext {

  val dayCount = math.abs(_dayCount)

  var remainingDays = dayCount
  var foundDay: DayRange = null

  def isFinished: Boolean = remainingDays == 0

  @inline
  def processDay(day: DayRange) {
    if (isFinished)
      return

    remainingDays -= 1

    if (isFinished)
      foundDay = day
  }

}
