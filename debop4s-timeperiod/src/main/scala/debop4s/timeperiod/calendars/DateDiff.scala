package debop4s.timeperiod.calendars

import debop4s.core._
import debop4s.core.utils.{ToStringHelper, Hashs}
import debop4s.timeperiod._
import debop4s.timeperiod.utils.Times
import java.util.Objects
import org.joda.time.{Duration, DateTime}

/**
 * debop4s.timeperiod.calendars.DateDiff
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 5. 오전 3:40
 */
class DateDiff(val start: DateTime,
               val end: DateTime,
               val calendar: ITimeCalendar) {

    val difference = new Duration(start, end)

    lazy val years = calcYears()
    lazy val quarters = calcQuarters()
    lazy val months = calcMonths()
    lazy val weeks = calcWeeks()
    lazy val days = difference.getStandardDays
    lazy val hours = difference.getStandardHours
    lazy val minutes = difference.getStandardMinutes
    lazy val seconds = difference.getStandardSeconds

    lazy val elapsedYears = years
    lazy val elapsedQuarters = quarters
    lazy val elapsedMonths = months - elapsedYears * MonthsPerYear

    lazy val elapsedStartDays = start.plusYears(elapsedYears.toInt).plusMonths(elapsedMonths.toInt)
    lazy val elapsedDays = new Duration(elapsedStartDays, end).getStandardDays.toLong

    lazy val elapsedStartHours =
        start.plusYears(elapsedYears.toInt)
        .plusMonths(elapsedMonths.toInt)
        .plusDays(elapsedDays.toInt)

    lazy val elapsedHours = new Duration(elapsedStartHours, end).getStandardHours.toLong

    lazy val elapsedStartMinutes =
        start.plusYears(elapsedYears.toInt)
        .plusMonths(elapsedMonths.toInt)
        .plusDays(elapsedDays.toInt)
        .plusHours(elapsedHours.toInt)

    lazy val elapsedMinutes = new Duration(elapsedStartMinutes, end).getStandardMinutes.toLong

    lazy val elapsedStartSeconds =
        start.plusYears(elapsedYears.toInt)
        .plusMonths(elapsedMonths.toInt)
        .plusDays(elapsedDays.toInt)
        .plusHours(elapsedHours.toInt)
        .plusMinutes(elapsedMinutes.toInt)

    lazy val elapsedSeconds = new Duration(elapsedStartSeconds, end).getStandardSeconds.toLong

    def isEmpty = difference.isEqual(Duration.ZERO)

    def startYear = calendar.year(start)

    def endYear = calendar.year(end)

    def startMonthOfYear = calendar.monthOfYear(start)

    def endMonthOfYear = calendar.monthOfYear(end)

    @inline
    private def calcYears(): Long = {
        if (Objects.equals(start, end)) return 0
        //        if (start == end)
        //            return 0

        val compareDay = math.min(end.getDayOfMonth, calendar.daysInMonth(startYear, endMonthOfYear))
        var compareDate = Times.asDate(startYear, endMonthOfYear, compareDay).plusMillis(end.getMillisOfDay)

        if (end > start) {
            if (compareDate < start) {
                compareDate = compareDate + 1.year
            }
        } else if (compareDate > start) {
            compareDate = compareDate - 1.year
        }
        endYear - calendar.year(compareDate)
    }

    @inline
    private def calcQuarters(): Long = {
        // if (Objects.equals(start, end))
        if (start == end)
            return 0

        val y1 = Times.yearOf(startYear, startMonthOfYear)
        val q1 = Times.quarterOfMonth(startMonthOfYear)

        val y2 = Times.yearOf(endYear, endMonthOfYear)
        val q2 = Times.quarterOfMonth(endMonthOfYear)

        (y2 * QuartersPerYear + q2.id) - (y1 * QuartersPerYear + q1.id)
    }

    @inline
    private def calcMonths(): Long = {
        // if (Objects.equals(start, end))
        if (start == end)
            return 0

        val compareDay = math.min(end.getDayOfMonth, calendar.daysInMonth(startYear, startMonthOfYear))
        var compareDate = Times.asDate(startYear, startMonthOfYear, compareDay).plusMillis(end.getMillisOfDay)

        if (end > start) {
            if (compareDate < start)
                compareDate = compareDate + 1.month
        } else if (compareDate > start) {
            compareDate = compareDate - 1.month
        }

        (endYear * MonthsPerYear + endMonthOfYear) -
        (calendar.year(compareDate) * MonthsPerYear + calendar.monthOfYear(compareDate))
    }

    @inline
    private def calcWeeks(): Long = {
        // if (Objects.equals(start, end)) 0
        if (start == end)
            return 0

        val w1 = Times.startOfWeek(start)
        val w2 = Times.startOfWeek(end)

        if (w1 == w2) 0
        else (new Duration(w1, w2).getStandardDays / DaysPerWeek).toLong
    }

    @inline
    private def roundEx(n: Double): Double = {
        math.round(n)
        //        if (n >= 0.0) math.round(n)
        //        else -math.round(-n)
    }

    override def hashCode(): Int =
        Hashs.compute(start, end, calendar)

    override def toString: String =
        ToStringHelper(this)
        .add("start", start)
        .add("end", end)
        .add("diffrence", difference)
        .add("calendar", calendar)
        .toString
}


object DateDiff {

    def apply(start: DateTime, end: DateTime): DateDiff =
        apply(start, end, DefaultTimeCalendar)

    def apply(start: DateTime, end: DateTime, calendar: ITimeCalendar): DateDiff =
        new DateDiff(start, end, calendar)

    def apply(moment: DateTime): DateDiff =
        apply(moment, DefaultTimeCalendar)

    def apply(moment: DateTime, calendar: ITimeCalendar): DateDiff =
        apply(moment, Times.now, calendar)
}
