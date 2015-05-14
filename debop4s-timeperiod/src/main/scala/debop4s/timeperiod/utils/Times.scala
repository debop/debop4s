package debop4s.timeperiod.utils

import java.lang.{ Iterable => JIterable }
import java.util
import java.util.{ Calendar, Date, List => JList, Set => JSet }

import debop4s.core._
import debop4s.core.conversions.jodatime._
import debop4s.core.utils.Strings
import debop4s.timeperiod.TimeSpec._
import debop4s.timeperiod._
import debop4s.timeperiod.timerange._
import org.joda.time.{ DateTime, DateTimeZone, Duration }
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
import scala.util.control.NonFatal

/**
 * Time 과 관련된 Helper class 입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 14. 오후 9:09
 */
object Times {

  private[this] lazy val log = LoggerFactory.getLogger(getClass)

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

  def availableTimeZone(): JSet[DateTimeZone] =
    DateTimeZone.getAvailableIDs.asScala.map(id => DateTimeZone.forID(id)).asJava

  def availableOffsetMillis(): JSet[Int] =
    availableTimeZone().asScala.map(tz => tz.getOffset(0)).asJava

  def datepart(moment: DateTime): Datepart = Datepart(moment)

  def timepart(moment: DateTime): Timepart = Timepart(moment)

  def asDate(year: Int, monthOfYear: Int = 1, dayOfMonth: Int = 1): DateTime =
    new DateTime(year, monthOfYear, dayOfMonth, 0, 0)

  def asDate(moment: DateTime): DateTime = moment.withTimeAtStartOfDay()

  def asDate(date: Date): DateTime = toDateTime(date).withTimeAtStartOfDay()

  def asDateTime(year: Int, monthOfYear: Int, dayOfMonth: Int): DateTime =
    new DateTime(year, monthOfYear, dayOfMonth, 0, 0)

  def asDateTime(year: Int,
                 monthOfYear: Int = 1,
                 dayOfMonth: Int = 1,
                 hour: Int = 0,
                 minute: Int = 0,
                 second: Int = 0,
                 millis: Int = 0): DateTime =
    new DateTime(year, monthOfYear, dayOfMonth, hour, minute, second, millis)

  def asDateTime(timestamp: Long): DateTime = asDateTime(timestamp, DateTimeZone.getDefault)
  def asDateTime(timestamp: Long, timezone: DateTimeZone): DateTime = {
    if (timezone != null)
      new DateTime(timestamp, timezone)
    else
      new DateTime(timestamp, DateTimeZone.getDefault)
  }
  def asDateTime(timestamp: Long, timezoneId: String): DateTime = {
    if (Strings.isEmpty(timezoneId)) asDateTime(timestamp)
    else asDateTime(timestamp, DateTimeZone.forID(timezoneId))
  }

  def asString(period: ITimePeriod): String =
    if (period == null) NullString else period.toString

  def toDateTime(date: Date): DateTime = new DateTime(date)

  def toDateTime(value: String): DateTime = toDateTime(value, new DateTime(0))

  def toDateTime(value: String, defaultValue: DateTime): DateTime = {
    try {
      DateTime.parse(value)
    } catch {
      case NonFatal(e) => defaultValue
    }
  }

  def toTimePeriodCollection[T <: ITimePeriod](sequence: JIterable[T]): TimePeriodCollection =
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
    val startHalfyearCount = (startYear + offsetYear) * HalfyearsPerYear + startHalfyear.getValue - 1
    val targetHalfyearCount = startHalfyearCount + delta

    val year = targetHalfyearCount / HalfyearsPerYear - offsetYear
    val halfyear = Halfyear.valueOf((targetHalfyearCount % HalfyearsPerYear) + 1)

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
    val startQuarters = (year + offsetYear) * QuartersPerYear + quarter.getValue - 1
    val targetQuarters = startQuarters + delta
    val y = targetQuarters / QuartersPerYear - offsetYear
    val q = (targetQuarters % QuartersPerYear) + 1

    YearQuarter(y, Quarter.valueOf(q))
  }

  def quarterOfMonth(monthOfYear: Int): Quarter =
    Quarter.valueOf((monthOfYear - 1) / MonthsPerQuarter + 1)

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
  def weeksOfYear(year: Int, calendar: ITimeCalendar): Int = {
    var lastDay = asDate(year, 12, 31)
    while (lastDay.getWeekyear > year) {
      lastDay = lastDay.minusDays(1)
    }
    lastDay.getWeekOfWeekyear
  }

  def startOfYearweek(weekyear: Int, weekOfWeekyear: Int): DateTime =
    startOfYearweek(weekyear, weekOfWeekyear, DefaultTimeCalendar)
  def startOfYearweek(weekyear: Int, weekOfWeekyear: Int, calendar: ITimeCalendar = DefaultTimeCalendar): DateTime =
    new DateTime().withWeekyear(weekyear).withWeekOfWeekyear(weekOfWeekyear)

  def dayStart(moment: DateTime): DateTime = moment.withTimeAtStartOfDay()

  def nextDayOfWeek(day: DayOfWeek): DayOfWeek = addDayOfWeek(day, 1)

  def prevDayOfWeek(day: DayOfWeek): DayOfWeek = addDayOfWeek(day, -1)


  def addDayOfWeek(day: DayOfWeek, days: Int): DayOfWeek = {
    if (days == 0) return day

    val weeks = math.abs(days) / DaysPerWeek + 1
    val offset = weeks * DaysPerWeek + day.getValue - 1 + days
    DayOfWeek.valueOf((offset % DaysPerWeek) + 1)
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

  def startTimeOfMonth(year: Int, month: Month): DateTime = asDate(year, month.getValue)

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

  def startMonthOfQuarter(quarter: Quarter): Int = (quarter.getValue - 1) * MonthsPerQuarter + 1

  def endMonthOfQuarter(quarter: Quarter): Int = quarter.getValue * MonthsPerQuarter

  def quarterOf(monthOfYear: Int): Quarter = Quarter.valueOf((monthOfYear - 1) / MonthsPerQuarter + 1)

  def quarterOf(moment: DateTime): Quarter = quarterOf(moment.getMonthOfYear)

  def previousQuarterOf(moment: DateTime): Quarter =
    prevQuarter(moment.getYear, quarterOf(moment)).quarter

  /** 지정한 일의 다음 주의 같은 요일 */
  def nextDayOfWeek(moment: DateTime): DateTime =
    nextDayOfWeek(moment, DayOfWeek.valueOf(moment.getDayOfWeek))

  def nextDayOfWeek(moment: DateTime, dayOfWeek: DayOfWeek): DateTime = {
    val dow = dayOfWeek.getValue
    var next = moment.plusDays(1)
    while (next.getDayOfWeek != dow) {
      next = next.plusDays(1)
    }
    next
  }

  def prevDayOfWeek(moment: DateTime): DateTime =
    prevDayOfWeek(moment, DayOfWeek.valueOf(moment.getDayOfWeek))

  def prevDayOfWeek(moment: DateTime, dayOfWeek: DayOfWeek): DateTime = {
    val dow = dayOfWeek
    var previous = moment.minusDays(1)
    while (previous.getDayOfWeek != dow.getValue) {
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

  def combine(date: DateTime, time: DateTime): DateTime =
    setTime(date, time.getMillisOfDay)

  def getTime(moment: DateTime): Duration =
    new Duration(moment.getMillisOfDay)

  def hasTime(moment: DateTime): Boolean = moment.getMillisOfDay > 0


  def setTime(moment: DateTime, time: DateTime): DateTime = moment.withMillisOfDay(time.getMillisOfDay)

  def setTime(moment: DateTime, hourOfDay: Int): DateTime =
    setTime(moment, hourOfDay, 0, 0, 0)

  def setTime(moment: DateTime, hourOfDay: Int, minuteOfHour: Int): DateTime =
    setTime(moment, hourOfDay, minuteOfHour, 0, 0)

  def setTime(moment: DateTime, hourOfDay: Int, minuteOfHour: Int, secondOfMinute: Int): DateTime =
    setTime(moment, hourOfDay, minuteOfHour, secondOfMinute, 0)

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
    a min b
    //        if (a != null && b != null) {
    //            if (a < b) a else b
    //        }
    //        else if (a == null) b
    //        else if (b == null) a
    //        else null
  }

  def max(a: DateTime, b: DateTime): DateTime = {
    a max b
    //        if (a != null && b != null) {
    //            if (a > b) a else b
    //        }
    //        else if (a == null) b
    //        else if (b == null) a
    //        else null
  }

  def min(a: Duration, b: Duration): Duration = {
    a min b
    //        if (a != null && b != null) {
    //            if (a < b) a else b
    //        }
    //        else if (a == null) b
    //        else if (b == null) a
    //        else null
  }


  def max(a: Duration, b: Duration): Duration = {
    a max b
    //        if (a != null && b != null) {
    //            if (a > b) a else b
    //        }
    //        else if (a == null) b
    //        else if (b == null) a
    //        else null
  }

  def adjustPeriod(start: DateTime, end: DateTime): (DateTime, DateTime) =
    (start min end, start max end)

  def adjustPeriod(start: DateTime, duration: Duration): (DateTime, Duration) = {
    if (duration.getMillis < 0)
      (start + duration, new Duration(-duration.getMillis))
    else
      (start, duration)
  }

  def timeBlock(start: DateTime, duration: Duration): TimeBlock =
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


  def periodOf(moment: DateTime,
               unit: PeriodUnit): ITimePeriod =
    periodOf(moment, unit, DefaultTimeCalendar)


  def periodOf(moment: DateTime,
               unit: PeriodUnit,
               calendar: ITimeCalendar): ITimePeriod = {
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


  def hasInside(period: ITimePeriod, target: DateTime): Boolean = {
    (target >= period.start) && (target <= period.end)
  }


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


  def overlapsWith(period: ITimePeriod, target: ITimePeriod): Boolean = {
    val r = relation(period, target)
    val isOverlaps = !NotOverlapedRelations.contains(r)

    log.trace(s"isOverlaps=$isOverlaps, period=$period, target=$target")
    isOverlaps
  }


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


  def unionBlock(period: ITimePeriod, target: ITimePeriod): TimeBlock = {
    val start = min(period.start, target.start)
    val end = max(period.end, target.end)

    TimeBlock(start, end, period.isReadonly)
  }


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


  def unionRange(period: ITimePeriod, target: ITimePeriod): TimeRange = {
    val start = min(period.start, target.start)
    val end = max(period.end, target.end)

    TimeRange(start, end, period.isReadonly)
  }


  def trimToYear(moment: DateTime): DateTime =
    asDate(moment.getYear)

  def trimToMonth(m: DateTime): DateTime = trimToMonth(m, 1)
  def trimToMonth(m: DateTime, monthOfYear: Int): DateTime =
    asDate(m.getYear, monthOfYear)

  def trimToDay(m: DateTime): DateTime = trimToDay(m, 1)
  def trimToDay(m: DateTime, dayOfMonth: Int): DateTime =
    asDate(m.getYear, m.getMonthOfYear, dayOfMonth)

  def trimToHour(m: DateTime): DateTime = trimToHour(m, 0)
  def trimToHour(m: DateTime, hourOfDay: Int): DateTime =
    asDate(m).withHourOfDay(hourOfDay)

  def trimToMinute(m: DateTime): DateTime = trimToMinute(m, 0)
  def trimToMinute(m: DateTime, minuteOfHour: Int): DateTime =
    asDate(m).withTime(m.getHourOfDay, minuteOfHour, 0, 0)

  def trimToSecond(m: DateTime): DateTime = trimToSecond(m, 0)
  def trimToSecond(m: DateTime, secondOfMinute: Int): DateTime =
    asDate(m).withTime(m.getHourOfDay, m.getMinuteOfHour, secondOfMinute, 0)

  def trimToMillis(m: DateTime): DateTime = trimToMillis(m, 0)
  def trimToMillis(m: DateTime, millisOfSecond: Int): DateTime =
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

  def allItemsAreEqual(left: JIterable[_ <: ITimePeriod], right: JIterable[_ <: ITimePeriod]): Boolean = {
    require(left != null)
    require(right != null)

    if (left.asScala.size != right.asScala.size) false
    else left.asScala.sameElements(right.asScala)
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


  def foreachYears(period: ITimePeriod): JList[ITimePeriod] = {
    require(period != null)

    val years = new util.ArrayList[ITimePeriod]()
    if (period.isAnytime)
      return years

    if (isSameYear(period.start, period.end)) {
      years add TimeRange(period)
      return years
    }

    years add TimeRange(period.start, endTimeOfYear(period.start))

    var current = startTimeOfYear(period.start).plusYears(1)
    val endYear = period.end.getYear
    val calendar = DefaultTimeCalendar

    while (current.getYear < endYear) {
      years add yearRange(current, calendar)
      current = current.plusYears(1)
    }

    if (current < period.end) {
      years add TimeRange(startTimeOfYear(current), period.end)
    }

    years
  }


  def foreachHalfyears(period: ITimePeriod): JList[ITimePeriod] = {
    require(period != null)

    val halfyears = new util.ArrayList[ITimePeriod]()
    if (period.isAnytime)
      return halfyears

    assertHasPeriod(period)

    if (isSameHalfyear(period.start, period.end)) {
      halfyears add TimeRange(period)
      return halfyears
    }

    var current = endTimeOfHalfyear(period.start)
    halfyears add TimeRange(period.start, current)

    val endHashcode = period.end.getYear * 10 + halfyearOf(period.end).getValue
    current = current.plusDays(1)
    val calendar = DefaultTimeCalendar

    while (current.getYear * 10 + halfyearOf(current).getValue < endHashcode) {
      halfyears add halfyearRange(current, calendar)
      current = current.plusMonths(MonthsPerHalfyear)
    }

    if (current < period.end) {
      halfyears add TimeRange(startTimeOfHalfyear(current), period.end)
    }

    halfyears
  }


  def foreachQuarters(period: ITimePeriod): JList[ITimePeriod] = {
    require(period != null)

    val quarters = new util.ArrayList[ITimePeriod]()
    if (period.isAnytime)
      return quarters

    assertHasPeriod(period)

    if (isSameQuarter(period.start, period.end)) {
      quarters add TimeRange(period)
      return quarters
    }

    var current = endTimeOfQuarter(period.start)
    quarters add TimeRange(period.start, current)

    val endHashcode = period.end.getYear * 10 + quarterOf(period.end).getValue
    current = current + 1.days
    val calendar = DefaultTimeCalendar

    while (current.getYear * 10 + quarterOf(current).getValue < endHashcode) {
      quarters add quarterRange(current, calendar)
      current = current.plusMonths(MonthsPerQuarter)
    }

    if (current < period.end) {
      quarters add TimeRange(startTimeOfQuarter(current), period.end)
    }

    quarters
  }


  def foreachMonths(period: ITimePeriod): JList[ITimePeriod] = {
    require(period != null)

    val months = new util.ArrayList[ITimePeriod]()
    if (period.isAnytime)
      return months

    assertHasPeriod(period)

    if (isSameMonth(period.start, period.end)) {
      months add TimeRange(period)
      return months
    }

    var current = endTimeOfMonth(period.start)
    months add TimeRange(period.start, current)

    val monthEnd = startTimeOfMonth(period.end)
    val calendar = DefaultTimeCalendar

    current = current.plusDays(1)
    while (current < monthEnd) {
      months add monthRange(current, calendar)
      current = current.plusMonths(1)
    }

    current = startTimeOfMonth(current)
    if (current < period.end) {
      months add TimeRange(current, period.end)
    }
    months
  }


  def foreachWeeks(period: ITimePeriod): JList[ITimePeriod] = {
    require(period != null)

    val weeks = new util.ArrayList[ITimePeriod]()
    if (period.isAnytime)
      return weeks

    assertHasPeriod(period)

    if (isSameWeek(period.start, period.end)) {
      weeks add TimeRange(period)
      return weeks
    }

    var current = period.start
    val endWeek = endTimeOfWeek(current)
    if (endWeek >= period.end) {
      weeks add TimeRange(current, period.end)
      return weeks
    }

    weeks add TimeRange(current, endWeek)
    current = endWeek.plusWeeks(1)
    val calendar = DefaultTimeCalendar

    while (current < period.end) {
      weeks add weekRange(current, calendar)
      current = current.plusWeeks(1)
    }

    current = startTimeOfWeek(current)
    if (current < period.end) {
      weeks add TimeRange(current, period.end)
    }
    weeks
  }


  def foreachDays(period: ITimePeriod): JList[ITimePeriod] = {
    require(period != null)

    val days = new util.ArrayList[ITimePeriod]()
    if (period.isAnytime)
      return days

    assertHasPeriod(period)

    if (isSameDay(period.start, period.end)) {
      days add TimeRange(period)
      return days
    }

    days add TimeRange(period.start, endTimeOfDay(period.start))
    val endDay = period.end.withTimeAtStartOfDay()
    var current = period.start.withTimeAtStartOfDay().plusDays(1)

    while (current < endDay) {
      days add dayRange(current, DefaultTimeCalendar)
      current = current.plusDays(1)
    }

    if (period.end.getMillisOfDay > 0)
      days add TimeRange(endDay, period.end)

    days
  }


  def foreachHours(period: ITimePeriod): JList[ITimePeriod] = {
    require(period != null)

    val hours = new util.ArrayList[ITimePeriod]()
    if (period.isAnytime)
      return hours

    assertHasPeriod(period)

    if (isSameHour(period.start, period.end)) {
      hours add TimeRange(period)
      return hours
    }

    hours add TimeRange(period.start, endTimeOfHour(period.start))

    val endHour = period.end
    var current = trimToHour(period.start, period.start.getHourOfDay + 1)
    val maxHour = endHour.minusHours(1)

    while (current <= maxHour) {
      hours add hourRange(current, DefaultTimeCalendar)
      current = current.plusHours(1)
    }

    if (endHour.minusHours(endHour.getHourOfDay).getMillisOfDay > 0) {
      hours add TimeRange(startTimeOfHour(endHour), endHour)
    }
    hours
  }


  def foreachMinutes(period: ITimePeriod): JList[ITimePeriod] = {
    require(period != null)

    val minutes = new util.ArrayList[ITimePeriod]()
    if (period.isAnytime)
      return minutes

    assertHasPeriod(period)

    if (isSameMinute(period.start, period.end)) {
      minutes add TimeRange(period)
      return minutes
    }

    minutes add TimeRange(period.start, endTimeOfMinute(period.start))

    val endMin = period.end
    var current = trimToMinute(period.start, period.start.getMinuteOfHour + 1)
    val maxMin = endMin - 1.minutes

    while (current <= maxMin) {
      minutes add minuteRange(current, DefaultTimeCalendar)
      current = current.plusMinutes(1)
    }

    if (endMin.minusMinutes(endMin.getMinuteOfHour).getMillisOfDay > 0) {
      minutes add TimeRange(startTimeOfMinute(endMin), period.end)
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

    val endHashcode = period.end.getYear * 10 + halfyearOf(period.end).getValue
    val calendar = DefaultTimeCalendar
    current = current.plusDays(1)

    def nextHalfyears(current: DateTime): Stream[ITimePeriod] = {
      if (current.getYear * 10 + halfyearOf(current).getValue < endHashcode) {
        halfyearRange(current, calendar) #:: nextHalfyears(current.plusMonths(MonthsPerHalfyear))
      } else if (current < period.end) {
        TimeRange(startTimeOfHalfyear(current), period.end) #:: Stream.empty[ITimePeriod]
      } else {
        Stream.empty[ITimePeriod]
      }
    }

    head #:: nextHalfyears(current)
  }


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

    val endHashcode = period.end.getYear * 10 + quarterOf(period.end).getValue
    current = current + 1.days
    val calendar = DefaultTimeCalendar

    def nextQuarters(current: DateTime): Stream[ITimePeriod] = {
      if (current.getYear * 10 + quarterOf(current).getValue < endHashcode) {
        quarterRange(current, calendar) #:: nextQuarters(current.plusMonths(MonthsPerQuarter))
      } else if (current < period.end) {
        TimeRange(startTimeOfQuarter(current), period.end) #:: Stream.empty[ITimePeriod]
      } else {
        Stream.empty[ITimePeriod]
      }
    }

    head #:: nextQuarters(current)
  }


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

  def runPeriods[R](period: ITimePeriod, unit: PeriodUnit)
                   (func: ITimePeriod => R): JIterable[R] = {
    require(period != null)
    require(func != null)

    foreachPeriods(period, unit).asScala.map(func).asJava
  }

  def runPeriods[R](period: ITimePeriod,
                    unit: PeriodUnit,
                    func1: Func1[ITimePeriod, R]): JIterable[R] = {
    require(period != null)
    require(func1 != null)

    foreachPeriods(period, unit).asScala.map(func1.execute).asJava
  }

  def runPeriodsAsStream[R](period: ITimePeriod, unit: PeriodUnit)
                           (func: ITimePeriod => R): Stream[R] = {
    require(period != null)
    require(func != null)

    periodsStream(period, unit).map(func)
  }

  def runPeriodsAsStream[R](period: ITimePeriod,
                            unit: PeriodUnit,
                            func1: Func1[ITimePeriod, R]): Stream[R] = {
    require(period != null)
    require(func1 != null)

    periodsStream(period, unit).map(func1.execute)
  }

  def runPeriodsAsParallel[R](period: ITimePeriod, unit: PeriodUnit)
                             (func: ITimePeriod => R): JIterable[(ITimePeriod, R)] = {
    require(period != null)
    require(func != null)

    foreachPeriods(period, unit).asScala.par.map(p => (p, func(p))).seq.asJava
  }

  def runPeriodsAsParallel[R](period: ITimePeriod,
                              unit: PeriodUnit,
                              func1: Func1[ITimePeriod, R]): JIterable[(ITimePeriod, R)] = {
    require(period != null)
    require(func1 != null)

    foreachPeriods(period, unit).asScala.par.map(p => (p, func1.execute(p))).seq.asJava
  }

  def getOrDefaultTimeCalendar(calendar: ITimeCalendar) =
    if (calendar != null) calendar else DefaultTimeCalendar


  /**
   * 해당 일자의 월 주차 (week of month)
   */
  def monthWeek(moment: DateTime): MonthWeek = {
    val result = moment.getWeekOfWeekyear - Times.startTimeOfMonth(moment).getWeekOfWeekyear + 1

    if (result > 0) MonthWeek(moment.getMonthOfYear, result)
    else MonthWeek(moment.plusMonths(1).getMonthOfYear, result)
  }


  def minusDate(moment: DateTime, unit: PeriodUnit, dates: Int): DateTime = {
    unit match {
      case PeriodUnit.Year => moment.minusYears(dates)
      case PeriodUnit.Halfyear => moment.minusMonths(MonthsPerHalfyear)
      case PeriodUnit.Quarter => moment.minusMonths(MonthsPerQuarter)
      case PeriodUnit.Month => moment.minusMonths(dates)
      case PeriodUnit.Week => moment.minusWeeks(dates)
      case PeriodUnit.Day => moment.minusDays(dates)
      case PeriodUnit.Hour => moment.minusHours(dates)
      case PeriodUnit.Minute => moment.minusMinutes(dates)
      case PeriodUnit.Second => moment.minusSeconds(dates)
      case PeriodUnit.Millisecond => moment.minusMillis(dates)

      case _ => moment
    }
  }


  def plusDate(moment: DateTime, unit: PeriodUnit, dates: Int): DateTime = {
    unit match {
      case PeriodUnit.Year => moment.plusYears(dates)
      case PeriodUnit.Halfyear => moment.plusMonths(MonthsPerHalfyear)
      case PeriodUnit.Quarter => moment.plusMonths(MonthsPerQuarter)
      case PeriodUnit.Month => moment.plusMonths(dates)
      case PeriodUnit.Week => moment.plusWeeks(dates)
      case PeriodUnit.Day => moment.plusDays(dates)
      case PeriodUnit.Hour => moment.plusHours(dates)
      case PeriodUnit.Minute => moment.plusMinutes(dates)
      case PeriodUnit.Second => moment.plusSeconds(dates)
      case PeriodUnit.Millisecond => moment.plusMillis(dates)

      case _ => moment
    }
  }
}
