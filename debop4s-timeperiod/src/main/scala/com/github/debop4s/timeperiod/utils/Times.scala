package com.github.debop4s.timeperiod.utils

import com.github.debop4s.core.NotSupportedException
import com.github.debop4s.timeperiod.DayOfWeek.DayOfWeek
import com.github.debop4s.timeperiod.Halfyear.Halfyear
import com.github.debop4s.timeperiod.Month.Month
import com.github.debop4s.timeperiod.PeriodRelation.PeriodRelation
import com.github.debop4s.timeperiod.PeriodUnit.PeriodUnit
import com.github.debop4s.timeperiod.Quarter.Quarter
import com.github.debop4s.timeperiod._
import com.github.debop4s.timeperiod.timerange._
import java.util.Calendar
import org.joda.time.{Duration, DateTimeZone, DateTime}
import org.slf4j.LoggerFactory
import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer
import scala.collection.parallel

/**
 * com.github.debop4s.timeperiod.tools.Times
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 14. 오후 9:09
 */
object Times {

    private lazy val log = LoggerFactory.getLogger(getClass)

    lazy val NullString = "<null>"
    lazy val UnixEpoch = new DateTime(0, DateTimeZone.UTC)

    def now: DateTime = DateTime.now()

    def now(zone: DateTimeZone): DateTime = DateTime.now(zone)

    def nowUtc(): DateTime = DateTime.now(DateTimeZone.UTC)

    def today: DateTime = now.withTimeAtStartOfDay()

    def today(zone: DateTimeZone): DateTime = now(zone).withTimeAtStartOfDay()

    def noon: DateTime = today.plusHours(12)

    def noon(moment: DateTime): DateTime = moment.withTimeAtStartOfDay().plusHours(12)

    def zero: DateTime = UnixEpoch

    def todayUtc(): DateTime = nowUtc().withTimeAtStartOfDay()

    def asLocal(utc: DateTime): DateTime = utc.toDateTime(DateTimeZone.getDefault)

    def asUtc(local: DateTime): DateTime = local.toDateTime(DateTimeZone.UTC)

    /**
     * 지정한 TimeZone이 UTC 로부터 Offset되는 시간을 Milliseconds 단위로 반환합니다.
     * "Asia/Seoul", "Asis/Tokyo" 는 모두 9시간 (32,400,000 msec)을 반환합니다.
     *
     * @param zoneId 지정한 TimeZone Id
     * @return UTC 로부터 TimeZone 까지의 Offset (milliseconds 단위)
     */
    def timeZoneOffset(zoneId: String): Int = timeZoneOffset(DateTimeZone.forID(zoneId))

    /**
     * 지정한 TimeZone이 UTC 로부터 Offset되는 시간을 Milliseconds 단위로 반환합니다.
     * "Asia/Seoul", "Asis/Tokyo" 는 모두 9시간 (32,400,000 msec)을 반환합니다.
     *
     * @param zone 지정한 TimeZone
     * @return UTC 로부터 TimeZone 까지의 Offset (milliseconds 단위)
     */
    def timeZoneOffset(zone: DateTimeZone): Int = zone.getOffset(0)

    def timeZoneForOffsetMillis(millisOffset: Int): DateTimeZone =
        DateTimeZone.forOffsetMillis(millisOffset)

    def availableTimeZone(): Set[DateTimeZone] =
        DateTimeZone.getAvailableIDs.map(id => DateTimeZone.forID(id)).toSet

    def availableOffsetMillis(): Set[Int] =
        availableTimeZone().map(tz => tz.getOffset(0))

    def datepart(moment: DateTime): Datepart = Datepart(moment)

    def timepart(moment: DateTime): Timepart = Timepart(moment)

    def asDate(year: Int, monthOfYear: Int = 1, dayOfMonth: Int = 1): DateTime =
        new DateTime(year, monthOfYear, dayOfMonth, 0, 0)

    def asDate(moment: DateTime): DateTime = moment.withTimeAtStartOfDay()

    def asDateTime(year: Int,
                   monthOfYear: Int = 1,
                   dayOfMonth: Int = 1,
                   hour: Int = 0,
                   minute: Int = 0,
                   second: Int = 0,
                   millis: Int = 0): DateTime =
        new DateTime(year, monthOfYear, dayOfMonth, hour, minute, second, millis)

    def asString(period: ITimePeriod): String =
        if (period == null) NullString else period.toString

    def toDateTime(value: String, defaultValue: DateTime = new DateTime(0)): DateTime = {
        try {
            DateTime.parse(value)
        } catch {
            case t: Throwable => defaultValue
        }
    }

    def toTimePeriodCollection[T <: ITimePeriod](sequence: Iterable[T]): TimePeriodCollection =
        TimePeriodCollection(sequence)

    def yearOf(moment: DateTime): Int = yearOf(moment.getYear, moment.getMonthOfYear)

    def yearOf(moment: DateTime, calendar: ITimeCalendar): Int =
        yearOf(calendar.year(moment), calendar.monthOfYear(moment))

    def yearOf(year: Int, monthOfYear: Int) =
        if (monthOfYear >= 1) year
        else year - 1

    def daysOfYear(year: Int): Int = startTimeOfYear(year + 1).minusMillis(1).getDayOfYear

    def nextHalfyear(startYear: Int, startHalfyear: Halfyear): YearHalfyear =
        addHalfyear(startYear, startHalfyear, 1)

    def prevHalfyear(startYear: Int, startHalfyear: Halfyear): YearHalfyear =
        addHalfyear(startYear, startHalfyear, -1)

    def addHalfyear(startYear: Int, startHalfyear: Halfyear, delta: Int): YearHalfyear = {
        if (delta == 0)
            return YearHalfyear(startYear, startHalfyear)

        val offsetYear = math.abs(delta) / HalfyearsPerYear + 1
        val startHalfyearCount = (startYear + offsetYear) * HalfyearsPerYear + startHalfyear.id - 1
        val targetHalfyearCount = startHalfyearCount + delta

        val year = targetHalfyearCount / HalfyearsPerYear - offsetYear
        val halfyear = Halfyear((targetHalfyearCount % HalfyearsPerYear) + 1)

        val result = YearHalfyear(year, halfyear)

        log.trace(s"startYear=[$startYear], startHalfyear=[$startHalfyear], delta=[$delta], result=[$result]")
        result
    }

    def halfyearOfMonth(monthOfYear: Int): Halfyear = {
        if (monthOfYear <= MonthsPerHalfyear) Halfyear.First
        else Halfyear.Second
    }

    def monthsOfHalfyear(halfyear: Halfyear): Array[Int] =
        if (halfyear == Halfyear.First) FirstHalfyearMonths
        else SecondHalfyearMonths

    def nextQuarter(year: Int, quarter: Quarter): YearQuarter = addQuarter(year, quarter, 1)

    def prevQuarter(year: Int, quarter: Quarter): YearQuarter = addQuarter(year, quarter, -1)

    def addQuarter(year: Int, quarter: Quarter, delta: Int): YearQuarter = {
        if (delta == 0)
            return YearQuarter(year, quarter)

        val offsetYear = math.abs(delta) / QuartersPerYear + 1
        val startQuarters = (year + offsetYear) * QuartersPerYear + quarter.id - 1
        val targetQuarters = startQuarters + delta
        val y = targetQuarters / QuartersPerYear - offsetYear
        val q = (targetQuarters % QuartersPerYear) + 1

        YearQuarter(y, Quarter(q))
    }

    def quarterOfMonth(monthOfYear: Int): Quarter =
        Quarter((monthOfYear - 1) / MonthsPerQuarter + 1)

    def monthsOfQuarter(quarter: Quarter): Array[Int] = quarter match {
        case Quarter.First => FirstQuarterMonths
        case Quarter.Second => SecondQuarterMonths
        case Quarter.Third => ThirdQuarterMonths
        case Quarter.Fourth => FourthQuarterMonths
        case _ => throw new IllegalArgumentException(s"Invalid parameter. quarter=[$quarter]")
    }

    def nextMonth(year: Int, monthOfYear: Int): YearMonth = addMonth(year, monthOfYear, 1)

    def prevMonth(year: Int, monthOfYear: Int): YearMonth = addMonth(year, monthOfYear, -1)

    def addMonth(year: Int, monthOfYear: Int, count: Int): YearMonth = {
        if (count == 0)
            return YearMonth(year, monthOfYear)

        val offset = math.abs(count) / MonthsPerYear + 1
        val startMonths = (year + offset) * MonthsPerYear + monthOfYear - 1
        val endMonths = startMonths + count
        val y = endMonths / MonthsPerYear - offset
        val m = (endMonths % MonthsPerYear) + 1

        YearMonth(y, m)
    }

    def daysInMonth(year: Int, month: Int): Int =
        asDate(year, month).plusMonths(1).minusDays(1).getDayOfMonth

    @inline
    def startOfWeek(moment: DateTime): DateTime = {
        val day = asDate(moment)
        val dow = day.getDayOfWeek
        day - (dow - 1).day
    }

    /**
    * 해당 시각의 월-주차를 계산합니다.
    */
    def weekOfMonth(moment: DateTime): Int = {
        // moment.getWeekOfWeekyear - startTimeOfMonth(moment).getWeekOfWeekyear + 1
        val calendar = Calendar.getInstance()
        calendar.setTimeInMillis(moment.getMillis)
        calendar.setMinimalDaysInFirstWeek(1)
        calendar.setFirstDayOfWeek(Calendar.MONDAY)
        calendar.get(Calendar.WEEK_OF_MONTH)
    }

    def weekOfYear(moment: DateTime): YearWeek =
        weekOfYear(moment, DefaultTimeCalendar)

    def weekOfYear(moment: DateTime, calendar: ITimeCalendar): YearWeek =
        YearWeek(moment.getWeekyear, moment.getWeekOfWeekyear)

    /** 해당 년도에 주차 수 */
    def weeksOfYear(year: Int): Int =
        weeksOfYear(year, DefaultTimeCalendar)

    /** 해당 년도의 주차 수 */
    @inline
    def weeksOfYear(year: Int, calendar: ITimeCalendar): Int = {
        var lastDay = asDate(year, 12, 31)
        while (lastDay.getWeekyear > year) {
            lastDay = lastDay.minusDays(1)
        }
        lastDay.getWeekOfWeekyear
    }

    def startOfYearWeek(weekyear: Int, weekOfWeekYear: Int, calendar: ITimeCalendar = DefaultTimeCalendar): DateTime =
        new DateTime().withWeekyear(weekyear).withWeekOfWeekyear(weekOfWeekYear)

    def dayStart(moment: DateTime): DateTime = moment.withTimeAtStartOfDay()

    def nextDayOfWeek(day: DayOfWeek): DayOfWeek = addDayOfWeek(day, 1)

    def prevDayOfWeek(day: DayOfWeek): DayOfWeek = addDayOfWeek(day, -1)

    @inline
    def addDayOfWeek(day: DayOfWeek, days: Int): DayOfWeek = {
        if (days == 0) return day

        val weeks = math.abs(days) / DaysPerWeek + 1
        val offset = weeks * DaysPerWeek + day.id - 1 + days
        DayOfWeek((offset % DaysPerWeek) + 1)
    }

    @inline
    def isSameTime(left: DateTime, right: DateTime, unit: PeriodUnit): Boolean = {
        unit match {
            case PeriodUnit.Year => isSameYear(left, right)
            case PeriodUnit.Halfyear => isSameHalfyear(left, right)
            case PeriodUnit.Quarter => isSameQuarter(left, right)
            case PeriodUnit.Month => isSameMonth(left, right)
            case PeriodUnit.Week => isSameWeek(left, right)
            case PeriodUnit.Day => isSameDay(left, right)
            case PeriodUnit.Hour => isSameHour(left, right)
            case PeriodUnit.Minute => isSameMinute(left, right)
            case PeriodUnit.Second => isSameSecond(left, right)

            case PeriodUnit.All => isSameDateTime(left, right)
            case PeriodUnit.Millisecond => isSameDateTime(left, right)

            case _ => isSameDateTime(left, right)
        }
    }

    def isSameYear(left: DateTime, right: DateTime): Boolean = left.getYear == right.getYear

    def isSameHalfyear(left: DateTime, right: DateTime): Boolean =
        isSameYear(left, right) &&
        halfyearOfMonth(left.getMonthOfYear) == halfyearOfMonth(right.getMonthOfYear)

    def isSameQuarter(left: DateTime, right: DateTime): Boolean =
        isSameYear(left, right) &&
        quarterOfMonth(left.getMonthOfYear) == quarterOfMonth(right.getMonthOfYear)

    def isSameMonth(left: DateTime, right: DateTime): Boolean =
        isSameYear(left, right) && left.getMonthOfYear == right.getMonthOfYear

    def isSameWeek(left: DateTime, right: DateTime): Boolean =
        left.weekyear == right.weekyear && left.getWeekOfWeekyear == right.getWeekOfWeekyear

    def isSameDay(left: DateTime, right: DateTime): Boolean =
        isSameYear(left, right) && left.getDayOfYear == right.getDayOfYear

    def isSameHour(left: DateTime, right: DateTime): Boolean =
        isSameDay(left, right) && left.getHourOfDay == right.getHourOfDay

    def isSameMinute(left: DateTime, right: DateTime): Boolean =
        isSameDay(left, right) && left.getMinuteOfDay == right.getMinuteOfDay

    def isSameSecond(left: DateTime, right: DateTime): Boolean =
        isSameDay(left, right) && left.getSecondOfDay == right.getSecondOfDay

    def isSameDateTime(left: DateTime, right: DateTime): Boolean =
        left != null && right != null && left.equals(right)


    def currentYear: DateTime = asDate(now.getYear, 1, 1)

    def currentHalfyear: DateTime = {
        val n = now
        val halfyear = halfyearOfMonth(n.getMonthOfYear)
        val months = monthsOfHalfyear(halfyear)

        asDate(n.getYear, months(0), 1)
    }

    def currentQuarter: DateTime = {
        val n = now
        val q = quarterOfMonth(n.getMonthOfYear)
        val months = monthsOfQuarter(q)

        asDate(n.getYear, months(0), 1)
    }

    def currentMonth: DateTime = trimToDay(now)

    def currentWeek: DateTime = startOfWeek(now)

    def currentDay: DateTime = today

    def currentHour: DateTime = trimToMinute(now)

    def currentMinute: DateTime = trimToSecond(now)

    def currentSecond: DateTime = trimToMillis(now)


    def startTimeOfYear(moment: DateTime): DateTime = startTimeOfYear(moment.getYear)

    def startTimeOfYear(year: Int): DateTime = asDate(year, 1, 1)

    def endTimeOfYear(moment: DateTime): DateTime = endTimeOfYear(moment.getYear)

    def endTimeOfYear(year: Int): DateTime = startTimeOfYear(year + 1).minusMillis(1)

    def startTimeOfLastYear(moment: DateTime): DateTime = startTimeOfYear(moment.getYear - 1)

    def endTimeOfLastYear(moment: DateTime): DateTime = endTimeOfYear(moment.getYear - 1)

    def startTimeOfHalfyear(moment: DateTime): DateTime =
        startTimeOfHalfyear(moment.getYear, moment.getMonthOfYear)

    def startTimeOfHalfyear(year: Int, monthOfYear: Int): DateTime =
        startTimeOfHalfyear(year, halfyearOfMonth(monthOfYear))

    def startTimeOfHalfyear(year: Int, halfyear: Halfyear): DateTime =
        asDate(year, monthsOfHalfyear(halfyear)(0), 1)

    def endTimeOfHalfyear(moment: DateTime): DateTime =
        endTimeOfHalfyear(moment.getYear, moment.getMonthOfYear)

    def endTimeOfHalfyear(year: Int, monthOfYear: Int): DateTime =
        endTimeOfHalfyear(year, halfyearOfMonth(monthOfYear))

    def endTimeOfHalfyear(year: Int, halfyear: Halfyear): DateTime =
        startTimeOfHalfyear(year, halfyear)
        .plusMonths(MonthsPerHalfyear)
        .minus(1)

    def startTimeOfQuarter(moment: DateTime): DateTime =
        startTimeOfQuarter(moment.getYear, moment.getMonthOfYear)

    def startTimeOfQuarter(year: Int, monthOfYear: Int): DateTime =
        startTimeOfQuarter(year, quarterOfMonth(monthOfYear))

    def startTimeOfQuarter(year: Int, quarter: Quarter): DateTime =
        asDate(year, monthsOfQuarter(quarter)(0), 1)

    def endTimeOfQuarter(moment: DateTime): DateTime =
        endTimeOfQuarter(moment.getYear, moment.getMonthOfYear)

    def endTimeOfQuarter(year: Int, monthOfYear: Int): DateTime =
        endTimeOfQuarter(year, quarterOfMonth(monthOfYear))

    def endTimeOfQuarter(year: Int, quarter: Quarter): DateTime =
        startTimeOfQuarter(year, quarter)
        .plusMonths(MonthsPerQuarter)
        .minus(1)

    def startTimeOfLastQuarter(moment: DateTime): DateTime =
        startTimeOfQuarter(moment.minusMonths(MonthsPerQuarter))

    def endTimeOfLastQuarter(moment: DateTime): DateTime =
        endTimeOfQuarter(moment.minusMonths(MonthsPerQuarter))


    def startTimeOfMonth(moment: DateTime): DateTime = asDate(moment.getYear, moment.getMonthOfYear)

    def startTimeOfMonth(year: Int, month: Month): DateTime = asDate(year, month.id)

    def startTimeOfMonth(year: Int, monthOfYear: Int): DateTime = asDate(year, monthOfYear)


    def endTimeOfMonth(moment: DateTime): DateTime =
        startTimeOfMonth(moment).plusMonths(1).minusMillis(1)

    def endTimeOfMonth(year: Int, month: Month): DateTime =
        startTimeOfMonth(year, month).plusMonths(1).minusMillis(1)

    def endTimeOfMonth(year: Int, monthOfYear: Int): DateTime =
        startTimeOfMonth(year, monthOfYear).plusMonths(1).minusMillis(1)

    def startTimeOfLastMonth(moment: DateTime): DateTime = startTimeOfMonth(moment.minusMonths(1))

    def endTimeOfLastMonth(moment: DateTime): DateTime = endTimeOfMonth(moment.minusMonths(1))

    def startTimeOfWeek(moment: DateTime): DateTime = startOfWeek(moment)

    def startTimeOfWeek(weekyear: Int, weekOfWeekYear: Int): DateTime =
        startTimeOfWeek(new DateTime().withWeekyear(weekyear).withWeekOfWeekyear(weekOfWeekYear))

    def endTimeOfWeek(moment: DateTime): DateTime =
        startTimeOfWeek(moment).plusWeeks(1).minus(1)

    def endTimeOfWeek(year: Int, weekOfYear: Int): DateTime =
        startTimeOfWeek(year, weekOfYear).plusWeeks(1).minus(1)


    def startTimeOfLastWeek(moment: DateTime): DateTime =
        startTimeOfWeek(moment).minusWeeks(1)

    def startTimeOfLastWeek(year: Int, weekOfYear: Int): DateTime =
        startTimeOfWeek(year, weekOfYear).minusWeeks(1)

    def endTimeOfLastWeek(moment: DateTime): DateTime =
        startTimeOfWeek(moment).minus(1)

    def endTimeOfLastWeek(year: Int, weekOfYear: Int): DateTime =
        startTimeOfWeek(year, weekOfYear).minus(1)

    def startTimeOfDay(moment: DateTime): DateTime = moment.withTimeAtStartOfDay()

    def endTimeOfDay(moment: DateTime): DateTime = startTimeOfDay(moment).plusDays(1).minus(1)


    def startTimeOfHour(moment: DateTime) = trimToMinute(moment)

    def endTimeOfHour(moment: DateTime) = startTimeOfHour(moment).plusHours(1).minus(1)

    def startTimeOfMinute(moment: DateTime) = trimToSecond(moment)

    def endTimeOfMinute(moment: DateTime) = startTimeOfMinute(moment).plusMinutes(1).minus(1)

    def startTimeOfSecond(moment: DateTime) = trimToMillis(moment)

    def endTimeOfSecond(moment: DateTime) = startTimeOfMinute(moment).plusSeconds(1).minus(1)

    def halfyearOf(monthOfYear: Int): Halfyear = if (monthOfYear < 7) Halfyear.First else Halfyear.Second

    def halfyearOf(moment: DateTime): Halfyear = halfyearOf(moment.getMonthOfYear)

    def startMonthOfQuarter(quarter: Quarter): Int = (quarter.id - 1) * MonthsPerQuarter + 1

    def endMonthOfQuarter(quarter: Quarter): Int = quarter.id * MonthsPerQuarter

    def quarterOf(monthOfYear: Int): Quarter = Quarter((monthOfYear - 1) / MonthsPerQuarter + 1)

    def quarterOf(moment: DateTime): Quarter = quarterOf(moment.getMonthOfYear)

    def previousQuarterOf(moment: DateTime): Quarter =
        prevQuarter(moment.getYear, quarterOf(moment)).quarter

    /** 지정한 일의 다음 주의 같은 요일 */
    def nextDayOfWeek(moment: DateTime): DateTime =
        nextDayOfWeek(moment, DayOfWeek(moment.getDayOfWeek))

    @inline
    def nextDayOfWeek(moment: DateTime, dayOfWeek: DayOfWeek): DateTime = {
        val dow = dayOfWeek.id
        var next = moment.plusDays(1)
        while (next.getDayOfWeek != dow) {
            next = next.plusDays(1)
        }
        next
    }

    def prevDayOfWeek(moment: DateTime): DateTime =
        prevDayOfWeek(moment, DayOfWeek(moment.getDayOfWeek))

    @inline
    def prevDayOfWeek(moment: DateTime, dayOfWeek: DayOfWeek): DateTime = {
        val dow = dayOfWeek
        var previous = moment.minusDays(1)
        while (previous.getDayOfWeek != dow.id) {
            previous = previous.minusDays(1)
        }
        previous
    }

    def getDate(moment: DateTime): DateTime = moment.withTimeAtStartOfDay()

    def hasDate(moment: DateTime): Boolean = moment.withTimeAtStartOfDay().getMillis > 0

    def setDate(moment: DateTime, date: DateTime): DateTime =
        Datepart(date).toDateTime(Timepart(moment))

    def setDate(moment: DateTime, year: Int, monthOfYear: Int, dayOfMonth: Int): DateTime =
        setDate(moment, asDate(year, monthOfYear, dayOfMonth))

    def setYear(moment: DateTime, year: Int): DateTime =
        setDate(moment, year, moment.getMonthOfYear, moment.getDayOfMonth)

    def setMonth(moment: DateTime, monthOfYear: Int): DateTime = {
        val day = math.min(moment.getDayOfMonth, daysInMonth(moment.getYear, monthOfYear))
        setDate(moment, moment.getYear, monthOfYear, day)
    }

    def setDay(moment: DateTime, dayOfMonth: Int): DateTime = {
        val day = math.min(moment.getDayOfMonth, dayOfMonth)
        setDate(moment, moment.getYear, moment.getMonthOfYear, day)
    }

    def combine(date: DateTime, time: DateTime): DateTime = setTime(date, time.getMillisOfDay)

    def getTime(moment: DateTime): Duration = new Duration(moment.getMillisOfDay)

    def hasTime(moment: DateTime): Boolean = moment.getMillisOfDay > 0


    def setTime(moment: DateTime, time: DateTime): DateTime = setTime(moment, time.getMillisOfDay)

    def setTime(moment: DateTime, millisOfDay: Int): DateTime =
        moment.withTimeAtStartOfDay().plusMillis(millisOfDay)

    def setTime(moment: DateTime, hourOfDay: Int, minuteOfHour: Int = 0, secondOfMinute: Int = 0, millisOfSecond: Int = 0): DateTime =
        moment.withTime(hourOfDay, minuteOfHour, secondOfMinute, millisOfSecond)

    def setHour(moment: DateTime, hourOfDay: Int): DateTime = moment.withHourOfDay(hourOfDay)

    def setMinute(moment: DateTime, minuteOfHour: Int): DateTime = moment.withMinuteOfHour(minuteOfHour)

    def setSecond(moment: DateTime, secondOfMinute: Int): DateTime = moment.withSecondOfMinute(secondOfMinute)

    def setMillisecond(moment: DateTime, millisOfSecond: Int): DateTime = moment.withMillisOfSecond(millisOfSecond)


    def ago(moment: DateTime, duration: Duration): DateTime = moment.minus(duration)

    def from(moment: DateTime, duration: Duration): DateTime = moment.plus(duration)

    def fromNow(moment: DateTime, duration: Duration): DateTime = from(now, duration)

    def since(moment: DateTime, duration: Duration): DateTime = moment.plus(duration)

    @inline
    def min(a: DateTime, b: DateTime): DateTime = {
        if (a != null && b != null) {if (a < b) a else b}
        else if (a == null) b
        else if (b == null) a
        else null
    }

    @inline
    def max(a: DateTime, b: DateTime): DateTime = {
        if (a != null && b != null) {if (a > b) a else b}
        else if (a == null) b
        else if (b == null) a
        else null
    }

    @inline
    def min(a: Duration, b: Duration): Duration = {
        if (a != null && b != null) {if (a < b) a else b}
        else if (a == null) b
        else if (b == null) a
        else null
    }

    @inline
    def max(a: Duration, b: Duration): Duration = {
        if (a != null && b != null) {if (a > b) a else b}
        else if (a == null) b
        else if (b == null) a
        else null
    }

    def adjustPeriod(start: DateTime, end: DateTime): (DateTime, DateTime) =
        (min(start, end), max(start, end))

    def adjustPeriod(start: DateTime, duration: Duration): (DateTime, Duration) = {
        if (duration.getMillis < 0)
            (start + duration, new Duration(-duration.getMillis))
        else
            (start, duration)
    }

    def timeblock(start: DateTime, duration: Duration): TimeBlock =
        TimeBlock(start, duration, readonly = false)

    def timeBlock(start: DateTime, end: DateTime): TimeBlock =
        TimeBlock(start, end, readonly = false)

    def timeRange(start: DateTime, duration: Duration): TimeRange =
        TimeRange(start, duration, readonly = false)

    def timeRange(start: DateTime, end: DateTime): TimeRange =
        TimeRange(start, end, readonly = false)

    def relativeYearPeriod(start: DateTime, years: Int): TimeRange =
        TimeRange(trimToMonth(start), trimToMonth(start).plusYears(years))

    def relativeMonthPeriod(start: DateTime, months: Int): TimeRange =
        TimeRange(trimToDay(start), trimToDay(start).plusMonths(months))

    def relativeWeekPeriod(start: DateTime, weeks: Int): TimeRange =
        TimeRange(trimToHour(start), trimToHour(start).plusWeeks(weeks))

    def relativeDayPeriod(start: DateTime, days: Int): TimeRange =
        TimeRange(trimToHour(start), trimToHour(start).plusDays(days))

    def relativeHourPeriod(start: DateTime, hours: Int): TimeRange =
        TimeRange(trimToMinute(start), trimToMinute(start).plusHours(hours))

    def relativeMinutePeriod(start: DateTime, minutes: Int): TimeRange =
        TimeRange(trimToSecond(start), trimToSecond(start).plusMinutes(minutes))

    def relativeSecondPeriod(start: DateTime, seconds: Int): TimeRange =
        TimeRange(trimToMillis(start), trimToMillis(start).plusSeconds(seconds))

    @inline
    def periodOf(moment: DateTime,
                 unit: PeriodUnit,
                 calendar: ITimeCalendar = DefaultTimeCalendar): ITimePeriod = {
        unit match {
            case PeriodUnit.All => TimeRange.Anytime
            case PeriodUnit.Year => yearRange(moment, calendar)
            case PeriodUnit.Halfyear => halfyearRange(moment, calendar)
            case PeriodUnit.Quarter => quarterRange(moment, calendar)
            case PeriodUnit.Month => monthRange(moment, calendar)
            case PeriodUnit.Week => weekRange(moment, calendar)
            case PeriodUnit.Day => dayRange(moment, calendar)
            case PeriodUnit.Hour => hourRange(moment, calendar)
            case PeriodUnit.Minute => minuteRange(moment, calendar)
            case PeriodUnit.Second => TimeRange(trimToMillis(moment), Durations.Second)

            case _ => throw new NotSupportedException(s"지원하지 않는 Period 종류입니다. unit=[$unit]")
        }
    }

    @inline
    def periodsOf(moment: DateTime,
                  unit: PeriodUnit,
                  periodCount: Int,
                  calendar: ITimeCalendar = DefaultTimeCalendar): CalendarTimeRange = {
        unit match {
            case PeriodUnit.Year => yearRanges(moment, periodCount, calendar)
            case PeriodUnit.Halfyear => halfyearRanges(moment, periodCount, calendar)
            case PeriodUnit.Quarter => quarterRanges(moment, periodCount, calendar)
            case PeriodUnit.Month => monthRanges(moment, periodCount, calendar)
            case PeriodUnit.Week => weekRanges(moment, periodCount, calendar)
            case PeriodUnit.Day => dayRanges(moment, periodCount, calendar)
            case PeriodUnit.Hour => hourRanges(moment, periodCount, calendar)
            case PeriodUnit.Minute => minuteRanges(moment, periodCount, calendar)
            case PeriodUnit.Second =>
                CalendarTimeRange(trimToMillis(moment),
                    trimToMillis(moment).plusSeconds(periodCount),
                    calendar)

            case _ => throw new NotSupportedException(s"지원하지 않는 Period 종류입니다. unit=[$unit]")
        }
    }


    def yearRange(moment: DateTime, calendar: ITimeCalendar = DefaultTimeCalendar) =
        new YearRange(moment.getYear, calendar)

    def yearRanges(moment: DateTime, yearCount: Int, calendar: ITimeCalendar = DefaultTimeCalendar) =
        new YearRangeCollection(moment.getYear, yearCount, calendar)

    def halfyearRange(moment: DateTime, calendar: ITimeCalendar = DefaultTimeCalendar): HalfyearRange =
        HalfyearRange(moment, calendar)

    def halfyearRanges(moment: DateTime, halfyearCount: Int, calendar: ITimeCalendar = DefaultTimeCalendar) =
        HalfyearRangeCollection(moment, halfyearCount, calendar)

    def quarterRange(moment: DateTime, calendar: ITimeCalendar = DefaultTimeCalendar): QuarterRange =
        QuarterRange(moment, calendar)

    def quarterRanges(moment: DateTime, quarterCount: Int, calendar: ITimeCalendar = DefaultTimeCalendar) =
        QuarterRangeCollection(moment, quarterCount, calendar)

    def monthRange(moment: DateTime, calendar: ITimeCalendar = DefaultTimeCalendar): MonthRange =
        MonthRange(moment, calendar)

    def monthRanges(moment: DateTime, monthCount: Int, calendar: ITimeCalendar = DefaultTimeCalendar) =
        MonthRangeCollection(moment, monthCount, calendar)

    def weekRange(moment: DateTime, calendar: ITimeCalendar = DefaultTimeCalendar): WeekRange =
        WeekRange(moment, calendar)

    def weekRanges(moment: DateTime, weekCount: Int, calendar: ITimeCalendar = DefaultTimeCalendar) =
        WeekRangeCollection(moment, weekCount, calendar)

    def dayRange(moment: DateTime, calendar: ITimeCalendar = DefaultTimeCalendar): DayRange =
        DayRange(asDate(moment), calendar)

    def dayRanges(moment: DateTime, dayCount: Int, calendar: ITimeCalendar = DefaultTimeCalendar) =
        DayRangeCollection(asDate(moment), dayCount, calendar)

    def hourRange(moment: DateTime, calendar: ITimeCalendar = DefaultTimeCalendar) =
        HourRange(moment, calendar)

    def hourRanges(moment: DateTime, hourCount: Int, calendar: ITimeCalendar = DefaultTimeCalendar) =
        HourRangeCollection(moment, hourCount, calendar)

    def minuteRange(moment: DateTime, calendar: ITimeCalendar = DefaultTimeCalendar) =
        MinuteRange(moment, calendar)

    def minuteRanges(moment: DateTime, minuteCount: Int, calendar: ITimeCalendar = DefaultTimeCalendar) =
        MinuteRangeCollection(moment, minuteCount, calendar)

    @inline
    def hasInside(period: ITimePeriod, target: DateTime): Boolean = {
        (target >= period.start) && (target <= period.end)
    }

    @inline
    def hasInside(period: ITimePeriod, target: ITimePeriod): Boolean = {
        hasInside(period, target.start) && hasInside(period, target.end)
    }

    def hasPureInside(period: ITimePeriod, target: DateTime): Boolean = {
        target > period.start && target < period.end
    }

    def hasPureInside(period: ITimePeriod, target: ITimePeriod): Boolean = {
        hasPureInside(period, target.start) && hasPureInside(period, target.end)
    }

    def isAnytime(period: ITimePeriod) = period != null && period.isAnytime

    def isNotAnyTime(period: ITimePeriod) = period != null && !period.isAnytime

    @inline
    def relation(period: ITimePeriod, target: ITimePeriod): PeriodRelation = {
        require(period != null)
        require(target != null)

        var relation = PeriodRelation.NoRelation

        if (period.start > target.end) {
            relation = PeriodRelation.After
        } else if (period.end < target.start) {
            relation = PeriodRelation.Before
        } else if (period.start == target.start && period.end == target.end) {
            relation = PeriodRelation.ExactMatch
        } else if (period.start == target.end) {
            relation = PeriodRelation.StartTouching
        } else if (period.end == target.start) {
            relation = PeriodRelation.EndTouching
        } else if (hasInside(period, target)) {
            if (period.start == target.start) {
                relation = PeriodRelation.EnclosingStartTouching
            } else if (period.end == target.end) {
                relation = PeriodRelation.EnclosingEndTouching
            } else {
                relation = PeriodRelation.Enclosing
            }
        } else {
            val insideStart = hasInside(target, period.start)
            val insideEnd = hasInside(target, period.end)

            if (insideStart && insideEnd) {
                if (period.start == target.start) {
                    relation = PeriodRelation.InsideStartTouching
                } else if (period.end == target.end) {
                    relation = PeriodRelation.InsideEndTouching
                } else {
                    relation = PeriodRelation.Inside
                }
            } else if (insideStart) {
                relation = PeriodRelation.StartInside
            } else if (insideEnd) {
                relation = PeriodRelation.EndInside
            }
        }
        log.trace(s"relation=[$relation], period=[$period], target=[$target]")
        relation
    }

    @inline
    def intersectWith(period: ITimePeriod, target: ITimePeriod): Boolean = {
        val isIntersect =
            hasInside(period, target.start) ||
            hasInside(period, target.end) ||
            hasPureInside(target, period)

        log.trace(s"period=[$period], target=[$target]이 교차 구간인가? intersect=[$isIntersect]")

        isIntersect
    }

    lazy val NotOverlapedRelations = Array(PeriodRelation.After,
        PeriodRelation.StartTouching,
        PeriodRelation.EndTouching,
        PeriodRelation.Before)

    @inline
    def overlapsWith(period: ITimePeriod, target: ITimePeriod): Boolean = {
        val r = relation(period, target)
        val isOverlaps = !NotOverlapedRelations.contains(r)

        log.trace(s"isOverlaps=$isOverlaps, period=$period, target=$target")
        isOverlaps
    }

    @inline
    def intersectBlock(period: ITimePeriod, target: ITimePeriod): TimeBlock = {
        var intersection: TimeBlock = null
        if (intersectWith(period, target)) {
            val start = max(period.start, target.start)
            val end = min(period.end, target.end)

            intersection = TimeBlock(start, end, period.isReadonly)

        }
        log.trace(s"기간의 교집합. period=[$period], target=[$target], result=[$intersection]")
        intersection
    }

    @inline
    def unionBlock(period: ITimePeriod, target: ITimePeriod): TimeBlock = {
        val start = min(period.start, target.start)
        val end = max(period.end, target.end)

        TimeBlock(start, end, period.isReadonly)
    }

    @inline
    def intersectRange(period: ITimePeriod, target: ITimePeriod): TimeRange = {
        require(period != null)
        require(target != null)

        var intersection: TimeRange = null

        if (intersectWith(period, target)) {
            val start = max(period.start, target.start)
            val end = min(period.end, target.end)

            intersection = TimeRange(start, end, period.isReadonly)
        }
        log.trace(s"기간의 교집합. period=[$period], target=[$target], result=[$intersection]")
        intersection
    }

    @inline
    def unionRange(period: ITimePeriod, target: ITimePeriod): TimeRange = {
        val start = min(period.start, target.start)
        val end = max(period.end, target.end)

        TimeRange(start, end, period.isReadonly)
    }


    def trimToYear(moment: DateTime): DateTime =
        asDate(moment.getYear)

    def trimToMonth(moment: DateTime, monthOfYear: Int = 1) =
        asDate(moment.getYear, monthOfYear)

    def trimToDay(moment: DateTime, dayOfMonth: Int = 1) =
        asDate(moment.getYear, moment.getMonthOfYear, dayOfMonth)

    def trimToHour(moment: DateTime, hourOfDay: Int = 0) =
        asDate(moment).withHourOfDay(hourOfDay)

    def trimToMinute(m: DateTime, minuteOfHour: Int = 0) =
        asDate(m).withTime(m.getHourOfDay, minuteOfHour, 0, 0)

    //trimToHour(moment, moment.getHourOfDay).withMinuteOfHour(minuteOfHour)

    def trimToSecond(m: DateTime, secondOfMinute: Int = 0) =
        asDate(m).withTime(m.getHourOfDay, m.getMinuteOfHour, secondOfMinute, 0)

    // trimToMinute(moment, moment.getMinuteOfHour).withSecondOfMinute(secondOfMinute)

    def trimToMillis(m: DateTime, millisOfSecond: Int = 0) =
        m.withMillisOfSecond(millisOfSecond)

    def assertValidPeriod(start: DateTime, end: DateTime) {
        if (start != null && end != null) {
            assert(start <= end, s"시작시각이 완료시각보다 이전이어야 합니다. start=[$start], end=[$end]")
        }
    }

    def assertMutable(period: ITimePeriod) {
        assert(period != null)
        assert(!period.isReadonly, s"TimePeriod가 읽기 전용입니다. period=[$period]")
    }

    def allItemsAreEqual(left: Iterable[_ <: ITimePeriod], right: Iterable[_ <: ITimePeriod]): Boolean = {
        require(left != null)
        require(right != null)

        if (left.size != right.size) false
        else left.sameElements(right)
    }

    def isWeekday(dayOfWeek: DayOfWeek): Boolean = Weekdays.contains(dayOfWeek)

    def isWeekend(dayOfWeek: DayOfWeek): Boolean = Weekends.contains(dayOfWeek)


    def foreachPeriods(period: ITimePeriod, unit: PeriodUnit) = {
        unit match {
            case PeriodUnit.Year => foreachYears(period)
            case PeriodUnit.Halfyear => foreachHalfyears(period)
            case PeriodUnit.Quarter => foreachQuarters(period)
            case PeriodUnit.Month => foreachMonths(period)
            case PeriodUnit.Week => foreachWeeks(period)
            case PeriodUnit.Day => foreachDays(period)
            case PeriodUnit.Hour => foreachHours(period)
            case PeriodUnit.Minute => foreachMinutes(period)
            case _ => throw new NotSupportedException(s"지원하지 않는 PeriodUnit 입니다. unit=[$unit]")
        }
    }

    @inline
    def foreachYears(period: ITimePeriod): ArrayBuffer[ITimePeriod] = {
        require(period != null)

        val years = ArrayBuffer[ITimePeriod]()
        if (period.isAnytime)
            return years

        if (isSameYear(period.start, period.end)) {
            years += TimeRange(period)
            return years
        }

        years += TimeRange(period.start, endTimeOfYear(period.start))

        var current = startTimeOfYear(period.start).plusYears(1)
        val endYear = period.end.getYear
        val calendar = DefaultTimeCalendar

        while (current.getYear < endYear) {
            years += yearRange(current, calendar)
            current = current.plusYears(1)
        }

        if (current < period.end) {
            years += TimeRange(startTimeOfYear(current), period.end)
        }

        years
    }

    @inline
    def foreachHalfyears(period: ITimePeriod): ArrayBuffer[ITimePeriod] = {
        require(period != null)

        val halfyears = ArrayBuffer[ITimePeriod]()
        if (period.isAnytime)
            return halfyears

        assertHasPeriod(period)

        if (isSameHalfyear(period.start, period.end)) {
            halfyears :+ TimeRange(period)
            return halfyears
        }

        var current = endTimeOfHalfyear(period.start)
        halfyears += TimeRange(period.start, current)

        val endHashcode = period.end.getYear * 10 + halfyearOf(period.end).id
        current = current.plusDays(1)
        val calendar = DefaultTimeCalendar

        while (current.getYear * 10 + halfyearOf(current).id < endHashcode) {
            halfyears += halfyearRange(current, calendar)
            current = current.plusMonths(MonthsPerHalfyear)
        }

        if (current < period.end) {
            halfyears += TimeRange(startTimeOfHalfyear(current), period.end)
        }

        halfyears
    }

    @inline
    def foreachQuarters(period: ITimePeriod): ArrayBuffer[ITimePeriod] = {
        require(period != null)

        val quarters = ArrayBuffer[ITimePeriod]()
        if (period.isAnytime)
            return quarters

        assertHasPeriod(period)

        if (isSameQuarter(period.start, period.end)) {
            quarters += TimeRange(period)
            return quarters
        }

        var current = endTimeOfQuarter(period.start)
        quarters += TimeRange(period.start, current)

        val endHashcode = period.end.getYear * 10 + quarterOf(period.end).id
        current = current + 1.days
        val calendar = DefaultTimeCalendar

        while (current.getYear * 10 + quarterOf(current).id < endHashcode) {
            quarters += quarterRange(current, calendar)
            current = current.plusMonths(MonthsPerQuarter)
        }

        if (current < period.end) {
            quarters += TimeRange(startTimeOfQuarter(current), period.end)
        }

        quarters
    }

    @inline
    def foreachMonths(period: ITimePeriod): ArrayBuffer[ITimePeriod] = {
        require(period != null)

        val months = ArrayBuffer[ITimePeriod]()
        if (period.isAnytime)
            return months

        assertHasPeriod(period)

        if (isSameMonth(period.start, period.end)) {
            months += TimeRange(period)
            return months
        }

        var current = endTimeOfMonth(period.start)
        months += TimeRange(period.start, current)

        val monthEnd = startTimeOfMonth(period.end)
        val calendar = DefaultTimeCalendar

        current = current.plusDays(1)
        while (current < monthEnd) {
            months += monthRange(current, calendar)
            current = current.plusMonths(1)
        }

        current = startTimeOfMonth(current)
        if (current < period.end) {
            months += TimeRange(current, period.end)
        }
        months
    }

    @inline
    def foreachWeeks(period: ITimePeriod): ArrayBuffer[ITimePeriod] = {
        require(period != null)

        val weeks = ArrayBuffer[ITimePeriod]()
        if (period.isAnytime)
            return weeks

        assertHasPeriod(period)

        if (isSameWeek(period.start, period.end)) {
            weeks += TimeRange(period)
            return weeks
        }

        var current = period.start
        val endWeek = endTimeOfWeek(current)
        if (endWeek >= period.end) {
            weeks += TimeRange(current, period.end)
            return weeks
        }

        weeks += TimeRange(current, endWeek)
        current = endWeek.plusWeeks(1)
        val calendar = DefaultTimeCalendar

        while (current < period.end) {
            weeks += weekRange(current, calendar)
            current = current.plusWeeks(1)
        }

        current = startTimeOfWeek(current)
        if (current < period.end) {
            weeks += TimeRange(current, period.end)
        }
        weeks
    }

    @inline
    def foreachDays(period: ITimePeriod): ArrayBuffer[ITimePeriod] = {
        require(period != null)

        val days = ArrayBuffer[ITimePeriod]()
        if (period.isAnytime)
            return days

        assertHasPeriod(period)

        if (isSameDay(period.start, period.end)) {
            days += TimeRange(period)
            return days
        }

        days += TimeRange(period.start, endTimeOfDay(period.start))
        val endDay = period.end.withTimeAtStartOfDay()
        var current = period.start.withTimeAtStartOfDay().plusDays(1)

        while (current < endDay) {
            days += dayRange(current, DefaultTimeCalendar)
            current = current.plusDays(1)
        }

        if (period.end.getMillisOfDay > 0)
            days += TimeRange(endDay, period.end)

        days
    }

    @inline
    def foreachHours(period: ITimePeriod): ArrayBuffer[ITimePeriod] = {
        require(period != null)

        val hours = ArrayBuffer[ITimePeriod]()
        if (period.isAnytime)
            return hours

        assertHasPeriod(period)

        if (isSameHour(period.start, period.end)) {
            hours += TimeRange(period)
            return hours
        }

        hours += TimeRange(period.start, endTimeOfHour(period.start))

        val endHour = period.end
        var current = trimToHour(period.start, period.start.getHourOfDay + 1)
        val maxHour = endHour.minusHours(1)

        while (current <= maxHour) {
            hours += hourRange(current, DefaultTimeCalendar)
            current = current.plusHours(1)
        }

        if (endHour.minusHours(endHour.getHourOfDay).getMillisOfDay > 0) {
            hours += TimeRange(startTimeOfHour(endHour), endHour)
        }
        hours
    }

    @inline
    def foreachMinutes(period: ITimePeriod): ArrayBuffer[ITimePeriod] = {
        require(period != null)

        val minutes = ArrayBuffer[ITimePeriod]()
        if (period.isAnytime)
            return minutes

        assertHasPeriod(period)

        if (isSameMinute(period.start, period.end)) {
            minutes += TimeRange(period)
            return minutes
        }

        minutes += TimeRange(period.start, endTimeOfMinute(period.start))

        val endMin = period.end
        var current = trimToMinute(period.start, period.start.getMinuteOfHour + 1)
        val maxMin = endMin - 1.minutes

        while (current <= maxMin) {
            minutes += minuteRange(current, DefaultTimeCalendar)
            current = current.plusMinutes(1)
        }

        if (endMin.minusMinutes(endMin.getMinuteOfHour).getMillisOfDay > 0) {
            minutes += TimeRange(startTimeOfMinute(endMin), period.end)
        }

        minutes
    }

    def periodsStream(period: ITimePeriod, unit: PeriodUnit): Stream[ITimePeriod] = {
        unit match {
            case PeriodUnit.Year => yearsStream(period)
            case PeriodUnit.Halfyear => halfyearsStream(period)
            case PeriodUnit.Quarter => quartersStream(period)
            case PeriodUnit.Month => monthsStream(period)
            case PeriodUnit.Week => weeksStream(period)
            case PeriodUnit.Day => daysStream(period)
            case PeriodUnit.Hour => hoursStream(period)
            case PeriodUnit.Minute => minutesStream(period)
            case _ => throw new NotSupportedException(s"지원하지 않는 PeriodUnit 입니다. unit=[$unit]")
        }
    }

    @inline
    def yearsStream(period: ITimePeriod): Stream[ITimePeriod] = {
        require(period != null)

        if (period.isAnytime)
            Stream.empty[ITimePeriod]

        if (isSameYear(period.start, period.end)) {
            return TimeRange(period) #:: Stream.empty[ITimePeriod]
        }

        val head = TimeRange(period.start, endTimeOfYear(period.start))

        val current = startTimeOfYear(period.start).plusYears(1)
        val endYear = period.end.getYear
        val calendar = DefaultTimeCalendar

        def nextYears(current: DateTime): Stream[ITimePeriod] = {
            if (current.getYear < endYear) {
                yearRange(current, calendar) #:: nextYears(current.plusYears(1))
            } else if (current < period.end) {
                TimeRange(startTimeOfYear(current), period.end) #:: Stream.empty[ITimePeriod]
            } else {
                Stream.empty[ITimePeriod]
            }
        }

        head #:: nextYears(current)
    }

    @inline
    def halfyearsStream(period: ITimePeriod): Stream[ITimePeriod] = {
        require(period != null)

        if (period.isAnytime)
            return Stream.empty[ITimePeriod]

        assertHasPeriod(period)

        if (isSameHalfyear(period.start, period.end)) {
            return TimeRange(period) #:: Stream.empty[ITimePeriod]
        }

        var current = endTimeOfHalfyear(period.start)
        val head = TimeRange(period.start, current)

        val endHashcode = period.end.getYear * 10 + halfyearOf(period.end).id
        val calendar = DefaultTimeCalendar
        current = current.plusDays(1)

        def nextHalfyears(current: DateTime): Stream[ITimePeriod] = {
            if (current.getYear * 10 + halfyearOf(current).id < endHashcode) {
                halfyearRange(current, calendar) #:: nextHalfyears(current.plusMonths(MonthsPerHalfyear))
            } else if (current < period.end) {
                TimeRange(startTimeOfHalfyear(current), period.end) #:: Stream.empty[ITimePeriod]
            } else {
                Stream.empty[ITimePeriod]
            }
        }

        head #:: nextHalfyears(current)
    }

    @inline
    def quartersStream(period: ITimePeriod): Stream[ITimePeriod] = {
        require(period != null)

        if (period.isAnytime)
            return Stream.empty[ITimePeriod]

        assertHasPeriod(period)

        if (isSameQuarter(period.start, period.end)) {
            TimeRange(period) #:: Stream.empty[ITimePeriod]
        }

        var current = endTimeOfQuarter(period.start)
        val head = TimeRange(period.start, current)

        val endHashcode = period.end.getYear * 10 + quarterOf(period.end).id
        current = current + 1.days
        val calendar = DefaultTimeCalendar

        def nextQuarters(current: DateTime): Stream[ITimePeriod] = {
            if (current.getYear * 10 + quarterOf(current).id < endHashcode) {
                quarterRange(current, calendar) #:: nextQuarters(current.plusMonths(MonthsPerQuarter))
            } else if (current < period.end) {
                TimeRange(startTimeOfQuarter(current), period.end) #:: Stream.empty[ITimePeriod]
            } else {
                Stream.empty[ITimePeriod]
            }
        }

        head #:: nextQuarters(current)
    }

    @inline
    def monthsStream(period: ITimePeriod): Stream[ITimePeriod] = {
        require(period != null)

        if (period.isAnytime)
            return Stream.empty[ITimePeriod]

        assertHasPeriod(period)

        if (isSameMonth(period.start, period.end)) {
            return TimeRange(period) #:: Stream.empty[ITimePeriod]
        }

        var current = endTimeOfMonth(period.start)
        val head = TimeRange(period.start, current)

        val monthEnd = startTimeOfMonth(period.end)
        val calendar = DefaultTimeCalendar

        current = current.plusDays(1)

        def nextMonth(current: DateTime): Stream[ITimePeriod] = {
            if (current < monthEnd) {
                monthRange(current, calendar) #:: nextMonth(current.plusMonths(1))
            } else {
                val last = startTimeOfMonth(current)
                if (last < period.end) {
                    TimeRange(current, period.end) #:: Stream.empty[ITimePeriod]
                } else {
                    Stream.empty[ITimePeriod]
                }
            }
        }
        head #:: nextMonth(current)
    }

    @inline
    def weeksStream(period: ITimePeriod): Stream[ITimePeriod] = {
        require(period != null)

        if (period.isAnytime)
            return Stream.empty[ITimePeriod]

        assertHasPeriod(period)

        if (isSameWeek(period.start, period.end)) {
            return TimeRange(period) #:: Stream.empty[ITimePeriod]
        }

        var current = period.start
        val endWeek = endTimeOfWeek(current)
        if (endWeek >= period.end) {
            return TimeRange(current, period.end) #:: Stream.empty[ITimePeriod]
        }

        val head = TimeRange(current, endWeek)
        current = endWeek.plusWeeks(1)
        val calendar = DefaultTimeCalendar

        def nextWeeks(current: DateTime): Stream[ITimePeriod] = {
            if (current < period.end) {
                weekRange(current, calendar) #:: nextWeeks(current.plusWeeks(1))
            } else {
                val last = startTimeOfWeek(current)
                if (last < period.end) {
                    TimeRange(current, period.end) #:: Stream.empty[ITimePeriod]
                } else {
                    Stream.empty[ITimePeriod]
                }
            }
        }
        head #:: nextWeeks(current)
    }

    /**
     * 배열로 전체 정보를 가지는 게 아니라 Stream 을 이용하여 지연된 작업을 수행합니다.
     */
    @inline
    def daysStream(period: ITimePeriod): Stream[ITimePeriod] = {
        require(period != null)

        if (period.isAnytime)
            return Stream.empty[ITimePeriod]

        assertHasPeriod(period)

        if (isSameDay(period.start, period.end)) {
            return TimeRange(period) #:: Stream.empty[ITimePeriod]
        }

        val endDay = period.end.withTimeAtStartOfDay()
        val current = period.start.withTimeAtStartOfDay().plusDays(1)

        val head = TimeRange(period.start, endTimeOfDay(period.start))

        def nextDays(current: DateTime): Stream[ITimePeriod] = {
            if (current < endDay) {
                dayRange(current, DefaultTimeCalendar) #:: nextDays(current.plusDays(1))
            } else if (period.end.getMillisOfDay > 0) {
                TimeRange(endDay, period.end) #:: Stream.empty[ITimePeriod]
            } else {
                Stream.empty[ITimePeriod]
            }
        }

        head #:: nextDays(current)
    }

    /**
     * 배열로 전체 정보를 가지는 게 아니라 Stream 을 이용하여 지연된 작업을 수행합니다.
     */
    @inline
    def hoursStream(period: ITimePeriod): Stream[ITimePeriod] = {
        require(period != null)

        if (period.isAnytime)
            return Stream.empty[ITimePeriod]

        assertHasPeriod(period)

        if (isSameHour(period.start, period.end)) {
            return TimeRange(period) #:: Stream.empty[ITimePeriod]
        }

        val endHour = period.end
        val current = trimToHour(period.start, period.start.getHourOfDay + 1)
        val maxHour = endHour.minusHours(1)


        val head = TimeRange(period.start, endTimeOfHour(period.start))

        def nextHours(current: DateTime): Stream[ITimePeriod] = {
            if (current <= maxHour) {
                hourRange(current, DefaultTimeCalendar) #:: nextHours(current.plusHours(1))
            } else if (endHour.minusHours(endHour.getHourOfDay).getMillisOfDay > 0) {
                TimeRange(startTimeOfHour(endHour), endHour) #:: Stream.empty[ITimePeriod]
            } else {
                Stream.empty
            }
        }

        head #:: nextHours(current)
    }

    /**
    * 배열로 전체 정보를 가지는 게 아니라 Stream 을 이용하여 지연된 작업을 수행합니다.
    */
    @inline
    def minutesStream(period: ITimePeriod): Stream[ITimePeriod] = {
        require(period != null)

        if (period.isAnytime)
            return Stream.empty

        assertHasPeriod(period)

        val endMin = period.end
        val current = trimToMinute(period.start, period.start.getMinuteOfHour + 1)
        val maxMin = endMin - 1.minutes

        if (isSameMinute(period.start, period.end)) {
            return TimeRange(period) #:: Stream.empty[ITimePeriod]
        }

        val head = TimeRange(period.start, endTimeOfMinute(period.start))

        def nextMinutes(current: DateTime): Stream[ITimePeriod] = {
            if (current <= maxMin) {
                Stream.cons(minuteRange(current, DefaultTimeCalendar), nextMinutes(current.plusMinutes(1)))
            } else if (endMin.minusMinutes(endMin.getMinuteOfHour).getMillisOfDay > 0) {
                Stream.cons(TimeRange(startTimeOfMinute(endMin), period.end), Stream.empty)
            } else {
                Stream.empty
            }
        }

        head #:: nextMinutes(current)
    }

    def assertHasPeriod(period: ITimePeriod) {
        assert(period != null && period.hasPeriod, s"기간이 설정되지 않았습니다. period=$period")
    }

    def runPeriods[T](period: ITimePeriod, unit: PeriodUnit)(func: ITimePeriod => T): Iterable[T] = {
        require(period != null)
        require(func != null)

        foreachPeriods(period, unit).map(p => func(p))
    }

    def runPeriodsAsParallel[T](period: ITimePeriod, unit: PeriodUnit)(func: ITimePeriod => T): parallel.ParIterable[(ITimePeriod, T)] = {
        require(period != null)
        require(func != null)

        foreachPeriods(period, unit).par.map(p => (p, func(p))).toIterable
    }

    def getOrDefaultTimeCalendar(calendar: ITimeCalendar) =
        if (calendar != null) calendar else DefaultTimeCalendar
}
