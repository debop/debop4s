package debop4s.timeperiod.calendars

/**
 * kr.hconnect.timeperiod.calendars.Calendars
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 3. 오전 10:32
 */
object Calendars {

  def asString(filter: ICalendarVisitorFilter): String = {
    if (filter == null)
      "Filter is null."

    val builder = new StringBuilder()
    builder.append("CalendarVisitorFilter#\n")
    builder.append("----------------------")
    builder.append("\n\t years=").append(filter.years.mkString)
    builder.append("\n\t monthOfYears=").append(filter.monthOfYears.mkString)
    builder.append("\n\t monthOfYears=").append(filter.monthOfYears.mkString)
    builder.append("\n\t dayOfMonths=").append(filter.dayOfMonths.mkString)
    builder.append("\n\t hourOfDays=").append(filter.hourOfDays.mkString)
    builder.append("\n\t minuteOfHours=").append(filter.minuteOfHours.mkString)
    builder.append("\n\t dayOfWeeks=").append(filter.weekDays.mkString)
    builder.append("\n\t exclude periods=").append(filter.excludePeriods)
    builder.append("----------------------")
    builder.toString()
  }

}
