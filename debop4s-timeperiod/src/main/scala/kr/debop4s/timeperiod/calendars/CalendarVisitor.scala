package kr.debop4s.timeperiod.calendars

import kr.debop4s.core.Guard
import kr.debop4s.timeperiod.SeekDirection.SeekDirection
import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.timerange._
import org.slf4j.LoggerFactory

/**
 * kr.debop4s.timeperiod.calendars.CalendarVisitor
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 3. 오후 2:14
 */
abstract class CalendarVisitor[F <: ICalendarVisitorFilter, C <: ICalendarVisitorContext]
(val filter: F,
 val limits: ITimePeriod,
 val seekDirection: SeekDirection = SeekDirection.Forward,
 val calendar: ITimeCalendar = DefaultTimeCalendar) {

    lazy val log = LoggerFactory.getLogger(getClass)

    protected def startPeriodVisit(context: C) {
        startPeriodVisit(limits, context)
    }

    protected def startPeriodVisit(period: ITimePeriod, context: C) {
        log.trace(s"기간을 탐색합니다. period=[$period], context=[$context], seekDirection=[$seekDirection]")
        Guard.shouldNotBeNull(period, "period")

        if (period.isMoment) return

        onVisitStart()

        val years = new YearRangeCollection(period.start.getYear,
                                               period.end.getYear - period.start.getYear + 1,
                                               calendar)

        if (onVisitYears(years, context) && enterYears(years, context)) {
            val yearsToVisit =
                if (seekDirection == SeekDirection.Forward)
                    years.getYears
                else
                    years.getYears.sortWith((x, y) => x.end.compareTo(y.end) > 0)

            for (year <- yearsToVisit) {
                log.trace(s"year=[${year.year}]를 탐색합니다.")

                if (year.overlapsWith(period) && onVisitYear(year, context) && enterMonths(year, context)) {
                    val monthsToVisit =
                        if (seekDirection == SeekDirection.Forward)
                            year.getMonths
                        else
                            years.getMonths.sortWith((x, y) => x.end.compareTo(y.end) > 0)

                    for (month <- monthsToVisit) {
                        log.trace(s"year=[${month.year}], month=[${month.monthOfYear}}]를 탐색합니다.")

                        if (month.overlapsWith(period) && onVisitMonth(month, context) && enterDays(month, context)) {

                            val daysToVisit =
                                if (seekDirection == SeekDirection.Forward) month.getDays
                                else month.getDays.sortWith((x, y) => x.end.compareTo(y.end) > 0)

                            for (day <- daysToVisit) {
                                log.trace(s"day를 탐색합니다. day=[${day.start}]")

                                if (day.overlapsWith(period) && onVisitDay(day, context) && enterHours(day, context)) {

                                    val hoursToVisit =
                                        if (seekDirection == SeekDirection.Forward) day.getHours
                                        else day.getHours.sortWith((x, y) => x.end.compareTo(y.end) > 0)

                                    for (hour <- hoursToVisit) {
                                        log.trace(s"Hour를 탐색합니다. hour=[${hour.hourOfDay}]")

                                        if (hour.overlapsWith(period) && onVisitHour(hour, context)) {
                                            enterMinutes(hour, context)
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }

        onVisitEnd()

        log.trace(s"기간에 대한 탐색을 완료했습니다. period=[$period], context=[$context], seekDirection=[$seekDirection]")
    }

    protected def startYearVisit(year: YearRange, context: C, direction: SeekDirection) {
        log.trace(s"Year 단위로 탐색을 수행합니다. year=[${year.year}], context=[$context], seekDirection=[$seekDirection]")

        var lastVisited: YearRange = null

        onVisitStart()

        val minStart = MinPeriodTime
        val maxEnd = MaxPeriodTime.minusYears(1)
        val offset = direction.id

        var current = year
        while (lastVisited == null && current.start.compareTo(minStart) > 0 && current.end.compareTo(maxEnd) < 0) {
            if (!onVisitYear(current, context)) {
                lastVisited = year
            } else {
                current = current.addYears(offset)
            }
        }

        onVisitEnd()
        log.trace(s"마지막 탐색 Year. lastVisited=[$lastVisited]")
        lastVisited
    }

    protected def startMonthVisit(month: MonthRange, context: C, direction: SeekDirection) {
        log.trace(s"Month 단위 탐색을 시작합니다. month=[$month], context=[$context], direction=[$direction]")

        var lastVisited: MonthRange = null
        onVisitStart()

        val minStart = MinPeriodTime
        val maxEnd = MaxPeriodTime.minusYears(1)
        val offset = direction.id

        var current = month
        while (lastVisited == null && current.start.compareTo(minStart) > 0 && current.end.compareTo(maxEnd) < 0) {
            if (!onVisitMonth(current, context)) {
                lastVisited = current
            } else {
                current = current.addMonths(offset)
            }
        }

        onVisitEnd()

        log.trace(s"Month 단위 탐색을 완료했습니다. lastVisited=[$lastVisited]")
        lastVisited
    }

    protected def startDayVisit(day: DayRange, context: C, direction: SeekDirection) {
        log.trace(s"Day 단위 탐색을 시작합니다. day=[$day], context=[$context], direction=[$direction]")

        var lastVisited: DayRange = null
        onVisitStart()

        val minStart = MinPeriodTime
        val maxEnd = MaxPeriodTime.minusYears(1)
        val offset = direction.id

        var current = day
        while (lastVisited == null && current.start.compareTo(minStart) > 0 && current.end.compareTo(maxEnd) < 0) {
            if (!onVisitDay(current, context)) {
                lastVisited = current
            } else {
                current = current.addDays(offset)
            }
        }

        onVisitEnd()

        log.trace(s"Day 단위 탐색을 완료했습니다. lastVisited=[$lastVisited]")
        lastVisited
    }

    protected def startHourVisit(hour: HourRange, context: C, direction: SeekDirection) {
        log.trace(s"Hour 단위 탐색을 시작합니다. hour=[$hour], context=[$context], direction=[$direction]")

        var lastVisited: HourRange = null
        onVisitStart()

        val minStart = MinPeriodTime
        val maxEnd = MaxPeriodTime.minusYears(1)
        val offset = direction.id

        var current = hour
        while (lastVisited == null && current.start.compareTo(minStart) > 0 && current.end.compareTo(maxEnd) < 0) {
            if (!onVisitHour(current, context)) {
                lastVisited = current
            } else {
                current = current.addHours(offset)
            }
        }

        onVisitEnd()

        log.trace(s"Hour 단위 탐색을 완료했습니다. lastVisited=[$lastVisited]")
        lastVisited
    }

    protected def onVisitStart() {
        log.trace("Calendar 탐색을 시작합니다...")
    }

    protected def onVisitEnd() {
        log.trace("Calendar 탐색을 종료합니다.")
    }

    protected def checkLimits(target: ITimePeriod): Boolean = limits.hasInside(target)

    protected def checkExcludePeriods(target: ITimePeriod): Boolean = {
        if (filter.excludePeriods.size() == 0) true
        else filter.excludePeriods.overlapPeriods(target).size == 0
    }

    protected def enterYears(years: YearRangeCollection, context: C) = true

    protected def enterMonths(year: YearRange, context: C) = true

    protected def enterDays(month: MonthRange, context: C) = true

    protected def enterHours(day: DayRange, context: C) = true

    protected def enterMinutes(hour: HourRange, context: C) = true

    protected def onVisitYears(years: YearRangeCollection, context: C) = true

    protected def onVisitYear(year: YearRange, context: C) = true

    protected def onVisitMonth(month: MonthRange, context: C) = true

    protected def onVisitDay(day: DayRange, context: C) = true

    protected def onVisitHour(hour: HourRange, context: C) = true

    protected def onVisitMinute(minute: MinuteRange, context: C) = true

    protected def isMatchingYear(year: YearRange, context: C): Boolean = {
        if (filter.years.size > 0 && !filter.years.contains(year.year))
            false
        else checkExcludePeriods(year)
    }

    protected def isMatchingMonth(month: MonthRange, context: C): Boolean = {
        if (filter.years.size > 0 && !filter.years.contains(month.year))
            false
        if (filter.monthOfYears.size > 0 && !filter.monthOfYears.contains(month.monthOfYear))
            false
        checkExcludePeriods(month)
    }

    protected def isMatchingDay(day: DayRange, context: C): Boolean = {
        if (filter.years.size > 0 && !filter.years.contains(day.year))
            false
        if (filter.monthOfYears.size > 0 && !filter.monthOfYears.contains(day.monthOfYear))
            false
        if (filter.dayOfMonths.size > 0 && !filter.dayOfMonths.contains(day.dayOfMonth))
            false
        if (filter.weekDays.size > 0 && !filter.weekDays.contains(day.dayOfWeek))
            false

        checkExcludePeriods(day)
    }

    protected def isMatchingHour(hour: HourRange, context: C): Boolean = {
        if (filter.years.size > 0 && !filter.years.contains(hour.year))
            false
        if (filter.monthOfYears.size > 0 && !filter.monthOfYears.contains(hour.monthOfYear))
            false
        if (filter.dayOfMonths.size > 0 && !filter.dayOfMonths.contains(hour.dayOfMonth))
            false
        if (filter.weekDays.size > 0 && !filter.weekDays.contains(DayOfWeek(hour.start.getDayOfWeek)))
            false
        if (filter.hourOfDays.size > 0 && !filter.hourOfDays.contains(hour.hourOfDay))
            false

        checkExcludePeriods(hour)
    }

    protected def isMatchingMinute(min: MinuteRange, context: C): Boolean = {
        if (filter.years.size > 0 && !filter.years.contains(min.year))
            false
        if (filter.monthOfYears.size > 0 && !filter.monthOfYears.contains(min.monthOfYear))
            false
        if (filter.dayOfMonths.size > 0 && !filter.dayOfMonths.contains(min.dayOfMonth))
            false
        if (filter.weekDays.size > 0 && !filter.weekDays.contains(DayOfWeek(min.start.getDayOfWeek)))
            false
        if (filter.hourOfDays.size > 0 && !filter.hourOfDays.contains(min.hourOfDay))
            false
        if (filter.minuteOfHours.size > 0 && !filter.minuteOfHours.contains(min.minuteOfHour))
            false

        checkExcludePeriods(min)
    }
}
