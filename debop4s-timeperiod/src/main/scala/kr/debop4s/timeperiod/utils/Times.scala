package kr.debop4s.timeperiod.utils

import kr.debop4s.core.NotSupportedException
import kr.debop4s.core.logging.Logger
import kr.debop4s.timeperiod.DayOfWeek.DayOfWeek
import kr.debop4s.timeperiod.Halfyear.Halfyear
import kr.debop4s.timeperiod.Month.Month
import kr.debop4s.timeperiod.PeriodRelation.PeriodRelation
import kr.debop4s.timeperiod.PeriodUnit.PeriodUnit
import kr.debop4s.timeperiod.Quarter.Quarter
import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.timerange._
import org.joda.time.{Duration, DateTimeZone, DateTime}
import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer
import scala.collection.parallel.ParSeq

/**
 * kr.debop4s.timeperiod.tools.Times
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 14. 오후 9:09
 */
object Times {

    lazy val log = Logger(getClass)

    val NullString = "<null>"
    val UnixEpoch = new DateTime(1970, 1, 1, 0, 0)

    def now: DateTime = DateTime.now()

    def now(zone: DateTimeZone): DateTime = DateTime.now(zone)

    def nowUtc(): DateTime = DateTime.now(DateTimeZone.UTC)

    def today: DateTime = now.withTimeAtStartOfDay()

    def today(zone: DateTimeZone): DateTime = now(zone).withTimeAtStartOfDay()

    def noon: DateTime = today.plusHours(12)

    def noon(moment: DateTime): DateTime = moment.withTimeAtStartOfDay().plusHours(12)

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

    def timeZoneForOffsetMillis(millisOffset: Int): DateTimeZone = DateTimeZone.forOffsetMillis(millisOffset)

    def availableTimeZone(): Set[DateTimeZone] =
        DateTimeZone.getAvailableIDs.map(id => DateTimeZone.forID(id)).toSet

    def availableOffsetMillis(): Set[Int] =
        availableTimeZone().map(tz => tz.getOffset(0))


    def datepart(moment: DateTime): Datepart = Datepart(moment)

    def timepart(moment: DateTime): Timepart = Timepart(moment)

    def asDate(year: Int, monthOfYear: Int = 1, dayOfMonth: Int = 1): DateTime =
        new DateTime(year, monthOfYear, dayOfMonth, 0, 0)

    def asDate(moment: DateTime): DateTime = moment.withTimeAtStartOfDay()

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
        new TimePeriodCollection(sequence)

    def getYearOf(moment: DateTime): Int = getYearOf(moment.getYear, moment.getMonthOfYear)

    def getYearOf(moment: DateTime, calendar: ITimeCalendar): Int =
        getYearOf(calendar.getYear(moment), calendar.getMonthOfYear(moment))

    def getYearOf(year: Int, monthOfYear: Int) =
        if (monthOfYear >= 1) year
        else year - 1

    def getDaysOfYear(year: Int): Int = startTimeOfYear(year + 1).minusMillis(1).getDayOfYear

    def nextHalfyear(startYear: Int, startHalfyear: Halfyear): YearHalfyear =
        addHalfyear(startYear, startHalfyear, 1)

    def previousHalfyear(startYear: Int, startHalfyear: Halfyear): YearHalfyear =
        addHalfyear(startYear, startHalfyear, -1)

    def addHalfyear(startYear: Int, startHalfyear: Halfyear, delta: Int): YearHalfyear = {
        if (delta == 0)
            return YearHalfyear(startYear, startHalfyear)

        val offsetYear = Math.abs(delta) / HalfyearsPerYear + 1
        val startHalfyearCount = (startYear + offsetYear) * HalfyearsPerYear + startHalfyear.id - 1
        val targetHalfyearCount = startHalfyearCount + delta

        val year = targetHalfyearCount / HalfyearsPerYear - offsetYear
        val halfyear = Halfyear((targetHalfyearCount % HalfyearsPerYear) + 1)

        val result = YearHalfyear(year, halfyear)

        log.trace(s"startYear=[$startYear], startHalfyear=[$startHalfyear], delta=[$delta], result=[$result]")
        result
    }

    def getHalfyearOfMonth(monthOfYear: Int): Halfyear = {
        if (monthOfYear <= MonthsPerHalfyear) Halfyear.First
        else Halfyear.Second
    }

    def getMonthsOfHalfyear(halfyear: Halfyear): Array[Int] =
        if (halfyear == Halfyear.First) FirstHalfyearMonths
        else SecondHalfyearMonths

    def nextQuarter(year: Int, quarter: Quarter): YearQuarter = addQuarter(year, quarter, 1)

    def previousQuarter(year: Int, quarter: Quarter): YearQuarter = addQuarter(year, quarter, -1)

    def addQuarter(year: Int, quarter: Quarter, delta: Int): YearQuarter = {
        if (delta == 0)
            return YearQuarter(year, quarter)

        val offsetYear = Math.abs(delta) / QuartersPerYear + 1
        val startQuarters = (year + offsetYear) * QuartersPerYear + quarter.id - 1
        val targetQuarters = startQuarters + delta
        val y = targetQuarters / QuartersPerYear - offsetYear
        val q = (targetQuarters % QuartersPerYear) + 1

        YearQuarter(y, Quarter(q))
    }

    def getQuarterOfMonth(monthOfYear: Int): Quarter =
        Quarter((monthOfYear - 1) / MonthsPerQuarter + 1)

    def getMonthsOfQuarter(quarter: Quarter) = quarter match {
        case Quarter.First => FirstQuarterMonths
        case Quarter.Second => SecondQuarterMonths
        case Quarter.Third => ThirdQuarterMonths
        case Quarter.Fourth => FourthQuarterMonths
        case _ => throw new IllegalArgumentException(s"Invalid parameter. quarter=[$quarter]")
    }

    def nextMonth(year: Int, monthOfYear: Int): YearMonth = addMonth(year, monthOfYear, 1)

    def previousMonth(year: Int, monthOfYear: Int): YearMonth = addMonth(year, monthOfYear, -1)

    def addMonth(year: Int, monthOfYear: Int, count: Int): YearMonth = {
        if (count == 0)
            YearMonth(year, monthOfYear)

        val offset = Math.abs(count) / MonthsPerYear + 1
        val startMonths = (year + offset) * MonthsPerYear + monthOfYear - 1
        val endMonths = startMonths + count
        val y = endMonths / MonthsPerYear - offset
        val m = (endMonths % MonthsPerYear) + 1

        YearMonth(y, m)
    }

    def getDaysInMonth(year: Int, month: Int): Int =
        asDate(year, month).plusMonths(1).minusDays(1).getDayOfMonth

    def getStartOfWeek(moment: DateTime): DateTime = {
        val dow = moment.withTimeAtStartOfDay().getDayOfWeek
        moment.minusDays(dow - 1)
    }

    def getWeekOfMonth(moment: DateTime): Int =
        moment.getWeekOfWeekyear - startTimeOfMonth(moment).getWeekOfWeekyear + 1

    def getWeekOfYear(moment: DateTime): YearWeek = getWeekOfYear(moment, DefaultTimeCalendar)

    def getWeekOfYear(moment: DateTime, calendar: ITimeCalendar): YearWeek =
        YearWeek(moment.getWeekyear, moment.getWeekOfWeekyear)

    def getWeeksOfYear(year: Int): Int = getWeeksOfYear(year, DefaultTimeCalendar)

    def getWeeksOfYear(year: Int, calendar: ITimeCalendar): Int = {
        var lastDay = asDate(year, 12, 31)
        while (lastDay.getWeekyear > year) {
            lastDay = lastDay.minusDays(1)
        }
        lastDay.getWeekOfWeekyear
    }

    def getStartOfYearWeek(year: Int, weekOfYear: Int, calendar: ITimeCalendar = DefaultTimeCalendar): DateTime =
        new DateTime().withYear(year).withWeekOfWeekyear(weekOfYear)

    def dayStart(moment: DateTime): DateTime = moment.withTimeAtStartOfDay()

    def nextDayOfWeek(day: DayOfWeek): DayOfWeek = addDayOfWeek(day, 1)

    def previousDayOfWeek(day: DayOfWeek): DayOfWeek = addDayOfWeek(day, -1)

    def addDayOfWeek(day: DayOfWeek, days: Int): DayOfWeek = {
        if (days == 0) day

        val weeks = Math.abs(days) / DaysPerWeek + 1
        val offset = weeks * DaysPerWeek + day.id - 1 + days
        DayOfWeek((offset % DaysPerWeek) + 1)
    }

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
        getHalfyearOfMonth(left.getMonthOfYear) == getHalfyearOfMonth(right.getMonthOfYear)

    def isSameQuarter(left: DateTime, right: DateTime): Boolean =
        isSameYear(left, right) &&
        getQuarterOfMonth(left.getMonthOfYear) == getQuarterOfMonth(right.getMonthOfYear)

    def isSameMonth(left: DateTime, right: DateTime): Boolean =
        isSameYear(left, right) && left.getMonthOfYear == right.getMonthOfYear

    def isSameWeek(left: DateTime, right: DateTime): Boolean =
        isSameYear(left, right) && left.getWeekOfWeekyear == right.getWeekOfWeekyear

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
        val halfyear = getHalfyearOfMonth(n.getMonthOfYear)
        val months = getMonthsOfHalfyear(halfyear)

        asDate(n.getYear, months(0), 1)
    }

    def currentQuarter: DateTime = {
        val n = now
        val q = getQuarterOfMonth(n.getMonthOfYear)
        val months = getMonthsOfQuarter(q)

        asDate(n.getYear, months(0), 1)
    }

    def currentMonth: DateTime = trimToDay(now)

    def currentWeek: DateTime = getStartOfWeek(now)

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
        startTimeOfHalfyear(year, getHalfyearOfMonth(monthOfYear))

    def startTimeOfHalfyear(year: Int, halfyear: Halfyear): DateTime =
        asDate(year, getMonthsOfHalfyear(halfyear)(0), 1)

    def endTimeOfHalfyear(moment: DateTime): DateTime =
        endTimeOfHalfyear(moment.getYear, moment.getMonthOfYear)

    def endTimeOfHalfyear(year: Int, monthOfYear: Int): DateTime =
        endTimeOfHalfyear(year, getHalfyearOfMonth(monthOfYear))

    def endTimeOfHalfyear(year: Int, halfyear: Halfyear): DateTime =
        startTimeOfHalfyear(year, halfyear)
            .plusMonths(MonthsPerHalfyear)
            .minus(1)

    def startTimeOfQuarter(moment: DateTime): DateTime =
        startTimeOfQuarter(moment.getYear, moment.getMonthOfYear)

    def startTimeOfQuarter(year: Int, monthOfYear: Int): DateTime =
        startTimeOfQuarter(year, getQuarterOfMonth(monthOfYear))

    def startTimeOfQuarter(year: Int, quarter: Quarter): DateTime =
        asDate(year, getMonthsOfQuarter(quarter)(0), 1)

    def endTimeOfQuarter(moment: DateTime): DateTime =
        endTimeOfQuarter(moment.getYear, moment.getMonthOfYear)

    def endTimeOfQuarter(year: Int, monthOfYear: Int): DateTime =
        endTimeOfQuarter(year, getQuarterOfMonth(monthOfYear))

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

    def startTimeOfWeek(moment: DateTime): DateTime = getStartOfWeek(moment)

    def startTimeOfWeek(year: Int, weekOfYear: Int, calendar: ITimeCalendar = DefaultTimeCalendar): DateTime = {
        var current = startTimeOfYear(year).minusWeeks(1)
        while (current.getYear < year + 2) {
            if (current.getWeekyear == year && current.getWeekOfWeekyear == weekOfYear)
                return current
            current = current.plusDays(1)
        }
        current
    }

    def endTimeOfWeek(moment: DateTime): DateTime = startTimeOfWeek(moment).plusWeeks(1).minus(1)

    def endTimeOfWeek(year: Int, weekOfYear: Int): DateTime = startTimeOfWeek(year, weekOfYear).plusWeeks(1).minus(1)


    def startTimeOfLastWeek(moment: DateTime): DateTime = startTimeOfWeek(moment).minusWeeks(1)

    def startTimeOfLastWeek(year: Int, weekOfYear: Int): DateTime = startTimeOfWeek(year, weekOfYear).minusWeeks(1)

    def endTimeOfLastWeek(moment: DateTime): DateTime = startTimeOfWeek(moment).minus(1)

    def endTimeOfLastWeek(year: Int, weekOfYear: Int): DateTime = startTimeOfWeek(year, weekOfYear).minus(1)


    def startTimeOfDay(moment: DateTime): DateTime = moment.withTimeAtStartOfDay()

    def endTimeOfDay(moment: DateTime): DateTime = startTimeOfDay(moment).plusDays(1).minus(1)


    def startTimeOfHour(moment: DateTime): DateTime = trimToMinute(moment)

    def endTimeOfHour(moment: DateTime): DateTime = startTimeOfHour(moment).plusHours(1).minus(1)

    def startTimeOfMinute(moment: DateTime): DateTime = trimToSecond(moment)

    def endTimeOfMinute(moment: DateTime): DateTime = startTimeOfMinute(moment).plusMinutes(1).minus(1)

    def startTimeOfSecond(moment: DateTime): DateTime = trimToMillis(moment)

    def endTimeOfSecond(moment: DateTime): DateTime = startTimeOfMinute(moment).plusSeconds(1).minus(1)

    def halfyearOf(monthOfYear: Int): Halfyear = if (monthOfYear < 7) Halfyear.First else Halfyear.Second

    def halfyearOf(moment: DateTime): Halfyear = halfyearOf(moment.getMonthOfYear)

    def startMonthOfQuarter(quarter: Quarter): Int = (quarter.id - 1) * MonthsPerQuarter + 1

    def endMonthOfQuarter(quarter: Quarter): Int = quarter.id * MonthsPerQuarter

    def quarterOf(monthOfYear: Int): Quarter = Quarter((monthOfYear - 1) / MonthsPerQuarter + 1)

    def quarterOf(moment: DateTime): Quarter = quarterOf(moment.getMonthOfYear)

    def previousQuarterOf(moment: DateTime): Quarter =
        previousQuarter(moment.getYear, quarterOf(moment)).quarter

    /** 지정한 일의 다음 주의 같은 요일 */
    def nextDayOfWeek(moment: DateTime): DateTime =
        nextDayOfWeek(moment, DayOfWeek(moment.getDayOfWeek))

    def nextDayOfWeek(moment: DateTime, dayOfWeek: DayOfWeek): DateTime = {
        val dow = dayOfWeek.id
        var next = moment.plusDays(1)
        while (next.getDayOfWeek != dow) {
            next = next.plusDays(1)
        }
        next
    }

    def previousDayOfWeek(moment: DateTime): DateTime =
        previousDayOfWeek(moment, DayOfWeek(moment.getDayOfWeek))

    def previousDayOfWeek(moment: DateTime, dayOfWeek: DayOfWeek): DateTime = {
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
        Datepart(date).getDateTime(Timepart(moment))

    def setDate(moment: DateTime, year: Int, monthOfYear: Int, dayOfMonth: Int): DateTime =
        setDate(moment, asDate(year, monthOfYear, dayOfMonth))

    def setYear(moment: DateTime, year: Int): DateTime =
        setDate(moment, year, moment.getMonthOfYear, moment.getDayOfMonth)

    def setMonth(moment: DateTime, monthOfYear: Int): DateTime = {
        val day = Math.min(moment.getDayOfMonth, getDaysInMonth(moment.getYear, monthOfYear))
        setDate(moment, moment.getYear, monthOfYear, day)
    }

    def setDay(moment: DateTime, dayOfMonth: Int): DateTime = {
        val day = Math.min(moment.getDayOfMonth, dayOfMonth)
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

    def min(a: DateTime, b: DateTime): DateTime = {
        if (a != null && b != null) {
            if (a.compareTo(b) < 0) a else b
        } else {
            null
        }
    }

    def max(a: DateTime, b: DateTime): DateTime = {
        if (a != null && b != null) {
            if (a.compareTo(b) > 0) a else b
        } else {
            null
        }
    }

    def min(a: Duration, b: Duration): Duration = {
        if (a != null && b != null) {
            if (a.compareTo(b) < 0) a else b
        } else {
            null
        }
    }

    def max(a: Duration, b: Duration): Duration = {
        if (a != null && b != null) {
            if (a.compareTo(b) > 0) a else b
        } else {
            null
        }
    }

    def adjustPeriod(start: DateTime, end: DateTime): (DateTime, DateTime) =
        (min(start, end), max(start, end))

    def adjustPeriod(start: DateTime, duration: Duration): (DateTime, DateTime) =
        adjustPeriod(start, start.plus(duration))

    def relativeYearPeriod(start: DateTime, years: Int): TimeRange = TimeRange(trimToMonth(start), start.plusYears(years))

    def relativeMonthPeriod(start: DateTime, months: Int): TimeRange = TimeRange(trimToDay(start), start.plusMonths(months))

    def relativeWeekPeriod(start: DateTime, weeks: Int): TimeRange = TimeRange(trimToHour(start), start.plusWeeks(weeks))

    def relativeDayPeriod(start: DateTime, days: Int): TimeRange = TimeRange(trimToHour(start), start.plusDays(days))

    def relativeHourPeriod(start: DateTime, hours: Int): TimeRange = TimeRange(trimToMinute(start), start.plusHours(hours))

    def relativeMinutePeriod(start: DateTime, minutes: Int): TimeRange = TimeRange(trimToSecond(start), start.plusMinutes(minutes))

    def relativeSecondPeriod(start: DateTime, seconds: Int): TimeRange = TimeRange(trimToMillis(start), start.plusSeconds(seconds))

    def getPeriodOf(moment: DateTime, unit: PeriodUnit, calendar: ITimeCalendar = DefaultTimeCalendar): ITimePeriod = {
        log.trace(s"일자[$moment]가 속한 기간 종류[$unit]의 기간을 구합니다.")

        unit match {
            case PeriodUnit.All => TimeRange.Anytime
            case PeriodUnit.Year => getYearRange(moment, calendar)
            case PeriodUnit.Halfyear => getHalfyearRange(moment, calendar)
            case PeriodUnit.Quarter => getQuarterRange(moment, calendar)
            case PeriodUnit.Month => getMonthRange(moment, calendar)
            case PeriodUnit.Week => getWeekRange(moment, calendar)
            case PeriodUnit.Day => getDayRange(moment, calendar)
            case PeriodUnit.Hour => getHourRange(moment, calendar)
            case PeriodUnit.Minute => getMinuteRange(moment, calendar)

            case _ => throw new NotSupportedException(s"지원하지 않는 Period 종류입니다. unit=[$unit]")
        }
    }

    def getPeriodsOf(moment: DateTime,
                     unit: PeriodUnit,
                     periodCount: Int,
                     calendar: ITimeCalendar = DefaultTimeCalendar): CalendarTimeRange = {
        log.trace(s"일자[$moment]가 속한 기간 종류[$unit]의 기간을 구합니다.")

        unit match {
            case PeriodUnit.Year => getYearRanges(moment, periodCount, calendar)
            case PeriodUnit.Halfyear => getHalfyearRanges(moment, periodCount, calendar)
            case PeriodUnit.Quarter => getQuarterRanges(moment, periodCount, calendar)
            case PeriodUnit.Month => getMonthRanges(moment, periodCount, calendar)
            case PeriodUnit.Week => getWeekRanges(moment, periodCount, calendar)
            case PeriodUnit.Day => getDayRanges(moment, periodCount, calendar)
            case PeriodUnit.Hour => getHourRanges(moment, periodCount, calendar)
            case PeriodUnit.Minute => getMinuteRanges(moment, periodCount, calendar)
            case PeriodUnit.Second =>
                new CalendarTimeRange(trimToMillis(moment),
                                         trimToMillis(moment).plusSeconds(periodCount),
                                         calendar)

            case _ => throw new NotSupportedException(s"지원하지 않는 Period 종류입니다. unit=[$unit]")
        }
    }


    def getYearRange(moment: DateTime, calendar: ITimeCalendar = DefaultTimeCalendar): YearRange =
        new YearRange(moment.getYear, calendar)

    def getYearRanges(moment: DateTime, yearCount: Int, calendar: ITimeCalendar = DefaultTimeCalendar): YearRangeCollection =
        new YearRangeCollection(moment.getYear, yearCount, calendar)

    def getHalfyearRange(moment: DateTime, calendar: ITimeCalendar = DefaultTimeCalendar): HalfyearRange =
        new HalfyearRange(moment, calendar)

    def getHalfyearRanges(moment: DateTime, halfyearCount: Int, calendar: ITimeCalendar = DefaultTimeCalendar): HalfyearRangeCollection =
        new HalfyearRangeCollection(moment, halfyearCount, calendar)

    def getQuarterRange(moment: DateTime, calendar: ITimeCalendar = DefaultTimeCalendar): QuarterRange =
        new QuarterRange(moment, calendar)

    def getQuarterRanges(moment: DateTime, quarterCount: Int, calendar: ITimeCalendar = DefaultTimeCalendar): QuarterRangeCollection =
        new QuarterRangeCollection(moment, quarterCount, calendar)

    def getMonthRange(moment: DateTime, calendar: ITimeCalendar = DefaultTimeCalendar): MonthRange =
        new MonthRange(moment, calendar)

    def getMonthRanges(moment: DateTime, monthCount: Int, calendar: ITimeCalendar = DefaultTimeCalendar): MonthRangeCollection =
        new MonthRangeCollection(moment, monthCount, calendar)

    def getWeekRange(moment: DateTime, calendar: ITimeCalendar = DefaultTimeCalendar): WeekRange =
        new WeekRange(moment, calendar)

    def getWeekRanges(moment: DateTime, weekCount: Int, calendar: ITimeCalendar = DefaultTimeCalendar): WeekRangeCollection =
        new WeekRangeCollection(moment, weekCount, calendar)

    def getDayRange(moment: DateTime, calendar: ITimeCalendar = DefaultTimeCalendar): DayRange =
        new DayRange(asDate(moment), calendar)

    def getDayRanges(moment: DateTime, dayCount: Int, calendar: ITimeCalendar = DefaultTimeCalendar): DayRangeCollection =
        new DayRangeCollection(asDate(moment), dayCount, calendar)

    def getHourRange(moment: DateTime, calendar: ITimeCalendar = DefaultTimeCalendar): HourRange =
        new HourRange(moment, calendar)

    def getHourRanges(moment: DateTime, hourCount: Int, calendar: ITimeCalendar = DefaultTimeCalendar): HourRangeCollection =
        new HourRangeCollection(moment, hourCount, calendar)

    def getMinuteRange(moment: DateTime, calendar: ITimeCalendar = DefaultTimeCalendar): MinuteRange =
        new MinuteRange(moment, calendar)

    def getMinuteRanges(moment: DateTime, minuteCount: Int, calendar: ITimeCalendar = DefaultTimeCalendar): MinuteRangeCollection =
        new MinuteRangeCollection(moment, minuteCount, calendar)


    def hasInside(period: ITimePeriod, target: DateTime): Boolean = {
        val isInside = target.compareTo(period.getStart) >= 0 && target.compareTo(period.getEnd) <= 0
        log.trace(s"기간[$period]에 target[$target]이 포함되는가? isInside=$isInside")
        isInside
    }

    def hasInside(period: ITimePeriod, target: ITimePeriod): Boolean = {
        val isInside = hasInside(period, target.getStart) && hasInside(period, target.getEnd)
        log.trace(s"기간[$period]에 target[$target]이 포함되는가? isInside=$isInside")
        isInside
    }

    def hasPureInside(period: ITimePeriod, target: DateTime): Boolean = {
        val isInside = target.compareTo(period.getStart) > 0 && target.compareTo(period.getEnd) < 0
        log.trace(s"기간[$period]에 target[$target]이 포함되는가? isInside=$isInside")
        isInside
    }

    def hasPureInside(period: ITimePeriod, target: ITimePeriod): Boolean = {
        val isInside = hasPureInside(period, target.getStart) && hasPureInside(period, target.getEnd)
        log.trace(s"기간[$period]에 target[$target]이 포함되는가? isInside=$isInside")
        isInside
    }

    def isAnytime(period: ITimePeriod) = period != null && period.isAnytime

    def isNotAnyTime(period: ITimePeriod) = period != null && !period.isAnytime

    def getRelation(period: ITimePeriod, target: ITimePeriod): PeriodRelation = {
        assert(period != null)
        assert(target != null)

        var relation = PeriodRelation.NoRelation

        if (period.getStart.compareTo(target.getEnd) > 0) {
            relation = PeriodRelation.After
        } else if (period.getEnd.compareTo(target.getStart) < 0) {
            relation = PeriodRelation.Before
        } else if (period.getStart.equals(target.getStart) && period.getEnd.equals(target.getEnd)) {
            relation = PeriodRelation.ExactMatch
        } else if (period.getStart.equals(target.getEnd)) {
            relation = PeriodRelation.StartTouching
        } else if (period.getEnd.equals(target.getEnd)) {
            relation = PeriodRelation.EndTouching
        } else if (hasInside(period, target)) {
            if (period.getStart.equals(target.getStart)) {
                relation = PeriodRelation.EnclosingStartTouching
            } else if (period.getEnd.equals(target.getEnd)) {
                relation = PeriodRelation.EnclosingEndTouching
            } else {
                relation = PeriodRelation.Enclosing
            }
        } else {
            val insideStart = hasInside(target, period.getStart)
            val insideEnd = hasInside(target, period.getEnd)

            if (insideStart && insideEnd) {
                if (period.getStart.equals(target.getStart)) {
                    relation = PeriodRelation.InsideStartTouching
                } else if (period.getEnd.equals(target.getEnd)) {
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
        log.debug(s"Period=[$period], target=[$target], relation=[$relation]")
        relation
    }

    def intersectWith(period: ITimePeriod, target: ITimePeriod): Boolean = {
        assert(period != null)
        assert(target != null)

        val isIntersect = hasInside(period, target.getStart) ||
                          hasInside(period, target.getEnd) ||
                          hasPureInside(target, period)
        log.trace(s"period=[$period], target=[$target]이 교차 구간인가? intersect=[$isIntersect]")
        isIntersect
    }

    val NotOverlapedRelations = Array(PeriodRelation.After,
                                         PeriodRelation.StartTouching,
                                         PeriodRelation.EndTouching,
                                         PeriodRelation.Before)

    def overlapsWith(period: ITimePeriod, target: ITimePeriod): Boolean = {
        assert(period != null)
        assert(target != null)


        val relation = getRelation(period, target)

        !NotOverlapedRelations.contains(relation)
    }

    def getIntersectionBlock(period: ITimePeriod, target: ITimePeriod): TimeBlock = {
        if (intersectWith(period, target)) {
            val start = max(period.getStart, target.getStart)
            val end = min(period.getEnd, target.getEnd)

            TimeBlock(start, end, period.isReadonly)
        }
        null.asInstanceOf[TimeBlock]
    }

    def getUnionBlock(period: ITimePeriod, target: ITimePeriod): TimeBlock = {
        val start = min(period.getStart, target.getStart)
        val end = max(period.getEnd, target.getEnd)

        TimeBlock(start, end, period.isReadonly)
    }

    def getIntersectionRange(period: ITimePeriod, target: ITimePeriod): TimeRange = {
        if (intersectWith(period, target)) {
            val start = max(period.getStart, target.getStart)
            val end = min(period.getEnd, target.getEnd)

            TimeRange(start, end, period.isReadonly)
        }
        null.asInstanceOf[TimeRange]
    }

    def getUnionRange(period: ITimePeriod, target: ITimePeriod): TimeRange = {
        val start = min(period.getStart, target.getStart)
        val end = max(period.getEnd, target.getEnd)

        TimeRange(start, end, period.isReadonly)
    }


    def trimToYear(moment: DateTime): DateTime =
        asDate(moment.getYear)

    def trimToMonth(moment: DateTime, monthOfYear: Int = 1): DateTime =
        asDate(moment.getYear, monthOfYear)

    def trimToDay(moment: DateTime, dayOfMonth: Int = 1): DateTime =
        asDate(moment.getYear, moment.getMonthOfYear, dayOfMonth)

    def trimToHour(moment: DateTime, hourOfDay: Int = 0): DateTime =
        getDate(moment).withHourOfDay(hourOfDay)

    def trimToMinute(moment: DateTime, minuteOfHour: Int = 0): DateTime =
        trimToHour(moment, moment.getHourOfDay).withMinuteOfHour(minuteOfHour)

    def trimToSecond(moment: DateTime, secondOfMinute: Int = 0): DateTime =
        trimToMinute(moment, moment.getMinuteOfHour).withSecondOfMinute(secondOfMinute)

    def trimToMillis(moment: DateTime, millisOfSecond: Int = 0): DateTime =
        moment.withMillisOfSecond(millisOfSecond)


    def assertValidPeriod(start: DateTime, end: DateTime) {
        if (start != null && end != null) {
            assert(start.compareTo(end) <= 0,
                      s"시작시각이 완료시각보다 이전이어야 합니다. start=[$start], end=[$end]")
        }
    }

    def assertMutable(period: ITimePeriod) {
        assert(period != null)
        assert(period.isReadonly, s"TimePeriod가 읽기 전용입니다. period=[$period]")
    }

    def allItemsAreEqual(left: Iterable[_ <: ITimePeriod], right: Iterable[_ <: ITimePeriod]): Boolean = {
        assert(left != null)
        assert(right != null)

        if (left.size != right.size)
            false

        for (x <- left; y <- right) {
            if (x != y)
                return false
        }
        true
    }

    def isWeekday(dayOfWeek: DayOfWeek): Boolean = Weekdays.contains(dayOfWeek)

    def isWeekend(dayOfWeek: DayOfWeek): Boolean = Weekends.contains(dayOfWeek)


    def foreachPeriods(period: ITimePeriod, unit: PeriodUnit): Seq[ITimePeriod] = {
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

    def foreachYears(period: ITimePeriod): Seq[ITimePeriod] = {
        assert(period != null)
        log.debug(s"기간[$period]에 대해 Year 단위로 열거합니다...")

        val years = ArrayBuffer[ITimePeriod]()

        if (period.isAnytime)
            return years

        if (isSameYear(period.getStart, period.getEnd)) {
            years += TimeRange(period)
            return years
        }

        years += TimeRange(period.getStart, endTimeOfYear(period.getStart))

        var current = startTimeOfYear(period.getStart).plusYears(1)
        val endYear = period.getEnd.getYear
        val calendar = DefaultTimeCalendar

        while (current.getYear < endYear) {
            years += getYearRange(current, calendar)
            current = current.plusYears(1)
        }

        if (current.compareTo(period.getEnd) < 0) {
            years += TimeRange(startTimeOfYear(current), period.getEnd)
        }

        years
    }

    def foreachHalfyears(period: ITimePeriod): Seq[ITimePeriod] = {
        assert(period != null)
        log.debug(s"기간[$period]에 대해 HalfYear 단위로 열거합니다...")

        val halfyears = ArrayBuffer[ITimePeriod]()

        if (period.isAnytime)
            return halfyears

        assertHasPeriod(period)

        if (isSameHalfyear(period.getStart, period.getEnd)) {
            halfyears :+ TimeRange(period)
            return halfyears
        }

        var current = endTimeOfHalfyear(period.getStart)
        halfyears += TimeRange(period.getStart, current)

        val endHashcode = period.getEnd.getYear * 10 + halfyearOf(period.getEnd).id
        current = current.plusDays(1)
        val calendar = DefaultTimeCalendar

        while (current.getYear * 10 + halfyearOf(current).id < endHashcode) {
            halfyears += getHalfyearRange(current, calendar)
            current = current.plusMonths(MonthsPerHalfyear)
        }

        if (current.compareTo(period.getEnd) < 0) {
            halfyears += TimeRange(startTimeOfHalfyear(current), period.getEnd)
        }

        halfyears
    }

    def foreachQuarters(period: ITimePeriod): Seq[ITimePeriod] = {
        assert(period != null)
        log.debug(s"기간[$period]에 대해 Quarter 단위로 열거합니다...")

        val quarters = ArrayBuffer[ITimePeriod]()

        if (period.isAnytime)
            return quarters

        assertHasPeriod(period)

        if (isSameQuarter(period.getStart, period.getEnd)) {
            quarters += TimeRange(period)
            return quarters
        }

        var current = endTimeOfQuarter(period.getStart)
        quarters += TimeRange(period.getStart, current)

        val endHashcode = period.getEnd.getYear * 10 + quarterOf(period.getEnd).id
        current = current.plusDays(1)
        val calendar = DefaultTimeCalendar

        while (current.getYear * 10 + quarterOf(current).id < endHashcode) {
            quarters += getQuarterRange(current, calendar)
            current = current.plusMonths(MonthsPerQuarter)
        }

        if (current.compareTo(period.getEnd) < 0) {
            quarters += TimeRange(startTimeOfQuarter(current), period.getEnd)
        }

        quarters
    }

    def foreachMonths(period: ITimePeriod): Seq[ITimePeriod] = {
        assert(period != null)
        log.trace(s"기간[$period]에 대해 월(Month) 단위로 열거합니다...")

        val months = ArrayBuffer[ITimePeriod]()
        if (period.isAnytime)
            return months

        assertHasPeriod(period)

        if (isSameMonth(period.getStart, period.getEnd)) {
            months += TimeRange(period)
            return months
        }

        var current = endTimeOfMonth(period.getStart)
        months += TimeRange(period.getStart, current)

        val monthEnd = startTimeOfMonth(period.getEnd)
        val calendar = DefaultTimeCalendar

        current = current.plusDays(1)
        while (current.compareTo(monthEnd) < 0) {
            months += getMonthRange(current, calendar)
            current = current.plusMonths(1)
        }

        current = startTimeOfMonth(current)
        if (current.compareTo(period.getEnd) < 0) {
            months += TimeRange(current, period.getEnd)
        }
        months
    }

    def foreachWeeks(period: ITimePeriod): Seq[ITimePeriod] = {
        assert(period != null)
        log.trace(s"기간[$period]에 대해 주(Week) 단위로 열거합니다...")

        val weeks = ArrayBuffer[ITimePeriod]()
        if (period.isAnytime)
            return weeks

        assertHasPeriod(period)

        if (isSameWeek(period.getStart, period.getEnd)) {
            weeks :+ TimeRange(period)
            return weeks
        }

        var current = period.getStart
        val endWeek = endTimeOfWeek(current)
        if (endWeek.compareTo(period.getEnd) >= 0) {
            weeks += TimeRange(current, period.getEnd)
            return weeks
        }

        weeks += TimeRange(current, endWeek)
        current = endWeek.plusWeeks(1)
        val calendar = DefaultTimeCalendar

        while (current.compareTo(period.getEnd) < 0) {
            weeks += getWeekRange(current, calendar)
            current = current.plusWeeks(1)
        }

        current = startTimeOfWeek(current)
        if (current.compareTo(period.getEnd) < 0) {
            weeks += TimeRange(current, period.getEnd)
        }
        weeks
    }

    def foreachDays(period: ITimePeriod): Seq[ITimePeriod] = {
        assert(period != null)
        log.trace(s"기간[$period]에 대해 일(Day) 단위로 열거합니다...")

        val days = ArrayBuffer[ITimePeriod]()
        if (period.isAnytime)
            return days

        assertHasPeriod(period)

        if (isSameDay(period.getStart, period.getEnd)) {
            days += TimeRange(period)
            return days
        }

        days += TimeRange(period.getStart, endTimeOfDay(period.getStart))
        val endDay = period.getEnd.withTimeAtStartOfDay()
        var current = period.getStart.withTimeAtStartOfDay().plusDays(1)

        while (current.compareTo(endDay) < 0) {
            days += getDayRange(current, DefaultTimeCalendar)
            current = current.plusDays(1)
        }

        if (period.getEnd.getMillisOfDay > 0)
            days += TimeRange(endDay, period.getEnd)

        days
    }

    def foreachHours(period: ITimePeriod): Seq[ITimePeriod] = {
        assert(period != null)
        log.trace(s"기간[$period]에 대해 시간(Hour) 단위로 열거합니다...")

        val hours = ArrayBuffer[ITimePeriod]()
        if (period.isAnytime)
            return hours

        assertHasPeriod(period)

        if (isSameHour(period.getStart, period.getEnd)) {
            hours += TimeRange(period)
            return hours
        }

        hours += TimeRange(period.getStart, endTimeOfHour(period.getStart))

        val endHour = period.getEnd
        var current = trimToHour(period.getStart, period.getStart.getHourOfDay + 1)
        val maxHour = endHour.minusHours(1)

        while (current.compareTo(maxHour) <= 0) {
            hours += getHourRange(current, DefaultTimeCalendar)
            current = current.plusHours(1)
        }

        if (endHour.minusHours(endHour.getHourOfDay).getMillisOfDay > 0) {
            hours += TimeRange(startTimeOfHour(endHour), endHour)
        }
        hours
    }

    def foreachMinutes(period: ITimePeriod): Seq[ITimePeriod] = {
        assert(period != null)
        log.trace(s"기간[$period]에 대해 분(Minute) 단위로 열거합니다...")

        val minutes = ArrayBuffer[ITimePeriod]()
        if (period.isAnytime)
            return minutes

        assertHasPeriod(period)

        if (isSameMinute(period.getStart, period.getEnd)) {
            minutes += TimeRange(period)
            return minutes
        }

        minutes += TimeRange(period.getStart, endTimeOfMinute(period.getStart))

        val endMin = period.getEnd
        var current = trimToMinute(period.getStart, period.getStart.getMinuteOfHour + 1)
        val maxMin = endMin.minusMinutes(1)

        while (current.compareTo(maxMin) <= 0) {
            minutes += getMinuteRange(current, DefaultTimeCalendar)
            current = current.plusMinutes(1)
        }

        if (endMin.minusMinutes(endMin.getMinuteOfHour).getMillisOfDay > 0) {
            minutes += TimeRange(startTimeOfMinute(endMin), period.getEnd)
        }

        minutes
    }


    def assertHasPeriod(period: ITimePeriod) {
        assert(period != null && period.hasPeriod, s"기간이 설정되지 않았습니다. period=$period")
    }

    def runPeriods[T](period: ITimePeriod, unit: PeriodUnit)(func: ITimePeriod => T): Seq[T] = {
        assert(period != null)
        assert(func != null)

        foreachPeriods(period, unit).map(p => func(p))
    }

    def runPeriodsAsParallel[T](period: ITimePeriod, unit: PeriodUnit)(func: ITimePeriod => T): ParSeq[(ITimePeriod, T)] = {
        assert(period != null)
        assert(func != null)

        foreachPeriods(period, unit).par.map(p => (p, func(p)))
    }

    def getOrDefaultTimeCalendar(calendar: ITimeCalendar) =
        if (calendar != null) calendar else DefaultTimeCalendar
}
