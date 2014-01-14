package kr.debop4s.timeperiod.calendars

import java.util.Objects
import kr.debop4s.core.ValueObject
import kr.debop4s.core.utils.Hashs
import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.utils.Times
import org.joda.time.{Duration, DateTime}

/**
 * kr.debop4s.timeperiod.calendars.DateDiff
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 5. 오전 3:40
 */
@SerialVersionUID(3415272759108830763L)
class DateDiff(val start: DateTime,
               val end: DateTime,
               val calendar: ITimeCalendar) extends ValueObject {

    def this(moment: DateTime, calendar: ITimeCalendar) {
        this(moment, Times.now, calendar)
    }

    def this(moment: DateTime) {
        this(moment, DefaultTimeCalendar)
    }

    def this(start: DateTime, end: DateTime) {
        this(start, end, DefaultTimeCalendar)
    }

    val difference = new Duration(start, end)

    lazy val years = calcYears()
    lazy val quarters = calcQuarters()
    lazy val months = calcMonths()
    lazy val weeks = calcWeeks()
    lazy val days = Math.round(roundEx(difference.getStandardDays)).toInt
    lazy val hours = Math.round(roundEx(difference.getStandardHours)).toInt
    lazy val minutes = Math.round(roundEx(difference.getStandardMinutes)).toInt
    lazy val seconds = Math.round(roundEx(difference.getStandardSeconds)).toInt

    lazy val elapsedYears = years
    lazy val elapsedQuarters = quarters
    lazy val elapsedMonths = months - elapsedYears * MonthsPerYear

    lazy val elapsedStartDays = start.plusYears(elapsedYears).plusMonths(elapsedMonths)
    lazy val elapsedDays = new Duration(elapsedStartDays, end).getStandardDays.toInt

    lazy val elapsedStartHours = start.plusYears(elapsedYears).plusMonths(elapsedMonths).plusDays(elapsedDays)
    lazy val elapsedHours = new Duration(elapsedStartHours, end).getStandardHours.toInt

    lazy val elapsedStartMinutes =
        start.plusYears(elapsedYears).plusMonths(elapsedMonths).plusDays(elapsedDays).plusHours(elapsedHours)
    lazy val elapsedMinutes = new Duration(elapsedStartMinutes, end).getStandardMinutes.toInt

    lazy val elapsedStartSeconds =
        start.plusYears(elapsedYears).plusMonths(elapsedMonths).plusDays(elapsedDays)
            .plusHours(elapsedHours).plusMinutes(elapsedMinutes)
    lazy val elapsedSeconds = new Duration(elapsedStartSeconds, end).getStandardSeconds.toInt

    def isEmpty = difference.isEqual(Duration.ZERO)

    def startYear = calendar.getYear(start)

    def endYear = calendar.getYear(end)

    def startMonthOfYear = calendar.getMonthOfYear(start)

    def endMonthOfYear = calendar.getMonthOfYear(end)

    private def calcYears(): Int = {
        if (Objects.equals(start, end)) 0

        val compareDay = Math.min(end.getDayOfMonth, calendar.getDaysInMonth(startYear, endMonthOfYear))
        var compareDate = Times.asDate(startYear, endMonthOfYear, compareDay).plusMillis(end.getMillisOfDay)
        if (end > start) {
            if (compareDate < start)
                compareDate = compareDate + 1.year
        } else if (compareDate > start) {
            compareDate = compareDate - 1.year
        }
        endYear - calendar.getYear(compareDate)
    }

    private def calcQuarters(): Int = {
        if (Objects.equals(start, end)) 0

        val y1 = Times.getYearOf(startYear, startMonthOfYear)
        val q1 = Times.getQuarterOfMonth(startMonthOfYear)

        val y2 = Times.getYearOf(endYear, endMonthOfYear)
        val q2 = Times.getQuarterOfMonth(endMonthOfYear)

        (y2 * QuartersPerYear + q2.id) - (y1 * QuartersPerYear + q1.id)
    }

    private def calcMonths(): Int = {
        if (Objects.equals(start, end)) 0

        val compareDay = Math.min(end.getDayOfMonth, calendar.getDaysInMonth(startYear, startMonthOfYear))
        var compareDate = Times.asDate(startYear, startMonthOfYear, compareDay).plusMillis(end.getMillisOfDay)

        if (end > start) {
            if (compareDate < start)
                compareDate = compareDate + 1.month
        } else if (compareDate > start) {
            compareDate = compareDate - 1.month
        }

        (endYear * MonthsPerYear + endMonthOfYear) -
        (calendar.getYear(compareDate) * MonthsPerYear + calendar.getMonthOfYear(compareDate))
    }

    private def calcWeeks(): Int = {
        if (Objects.equals(start, end)) 0

        val w1 = Times.getStartOfWeek(start)
        val w2 = Times.getStartOfWeek(end)

        if (Objects.equals(w1, w2)) 0
        else (new Duration(w1, w2).getStandardDays / DaysPerWeek).toInt
    }

    private def roundEx(n: Double): Double =
        if (n >= 0.0) Math.round(n) else -Math.round(-n)

    override def hashCode(): Int =
        Hashs.compute(start, end, difference, calendar)

    override protected def buildStringHelper =
        super.buildStringHelper
            .add("start", start)
            .add("end", end)
            .add("diffrence", difference)
            .add("calendar", calendar)
}
