package debop4s.timeperiod.calendars

import debop4s.core._
import debop4s.timeperiod.SeekDirection.SeekDirection
import debop4s.timeperiod._
import debop4s.timeperiod.timerange._
import org.slf4j.LoggerFactory

/**
 * 특정 기간에 대한 필터링 정보를 기반으로 기간들을 필터링 할 수 있도록 특정 기간을 탐색하는 Visitor입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 3. 오후 2:14
 */
abstract class CalendarVisitor[F <: ICalendarVisitorFilter, C <: ICalendarVisitorContext]
(val filter: F,
 val limits: ITimePeriod,
 val seekDirection: SeekDirection = SeekDirection.Forward,
 val calendar: ITimeCalendar = DefaultTimeCalendar) {

    private lazy val log = LoggerFactory.getLogger(getClass)

    protected def startPeriodVisit(context: C) {
        startPeriodVisit(limits, context)
    }

    @inline
    protected final def startPeriodVisit(period: ITimePeriod, context: C) {
        log.trace(s"기간을 탐색합니다. period=[$period], context=[$context], seekDirection=[$seekDirection]")
        Guard.shouldNotBeNull(period, "period")

        if (period.isMoment) return

        onVisitStart()

        val years = YearRangeCollection(period.start.getYear,
            period.end.getYear - period.start.getYear + 1,
            if (calendar != null) calendar else DefaultTimeCalendar)

        val isForward = seekDirection == SeekDirection.Forward

        if (onVisitYears(years, context) && enterYears(years, context)) {
            val yearsToVisit =
                if (isForward) years.years
                else years.years.sortWith(_.end > _.end)

            yearsToVisit.foreach {
                year =>
                    val canVisitMonth =
                        if (!year.overlapsWith(period)) false
                        else if (!onVisitYear(year, context)) false
                        else if (!enterMonths(year, context)) false
                        else true

                    if (canVisitMonth) {
                        val monthsToVisit =
                            if (isForward) years.months
                            else years.months.sortWith(_.end > _.end)

                        monthsToVisit.foreach {
                            month =>
                                val canVisitDay =
                                    if (!month.overlapsWith(period)) false
                                    else if (!onVisitMonth(month, context)) false
                                    else if (!enterDays(month, context)) false
                                    else true

                                if (canVisitDay) {
                                    val daysToVisit =
                                        if (isForward) month.days
                                        else month.days.sortWith(_.end > _.end)

                                    daysToVisit.foreach {
                                        day =>
                                            val canVisitHour =
                                                if (!day.overlapsWith(period)) false
                                                else if (!onVisitDay(day, context)) false
                                                else if (!enterHours(day, context)) false
                                                else true

                                            if (canVisitHour) {
                                                val hoursToVisit =
                                                    if (isForward) day.hours
                                                    else day.hours.sortWith(_.end > _.end)

                                                hoursToVisit.foreach {
                                                    hour =>
                                                        val canVisitMinute = hour.overlapsWith(period) && onVisitHour(hour, context)
                                                        if (canVisitMinute) {
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

    @inline
    protected def startYearVisit(year: YearRange, context: C, direction: SeekDirection): YearRange = {
        var lastVisited: YearRange = null

        onVisitStart()

        val minStart = MinPeriodTime
        val maxEnd = MaxPeriodTime - 1.year
        val offset = direction.id

        var current = year
        while (lastVisited == null && current.start > minStart && current.end < maxEnd) {
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

    @inline
    protected def startMonthVisit(month: MonthRange, context: C, direction: SeekDirection): MonthRange = {
        var lastVisited: MonthRange = null
        onVisitStart()

        val minStart = MinPeriodTime
        val maxEnd = MaxPeriodTime - 1.year
        val offset = direction.id

        var current = month
        while (lastVisited == null && current.start > minStart && current.end < maxEnd) {
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

    @inline
    protected def startDayVisit(day: DayRange, context: C, direction: SeekDirection): DayRange = {
        var lastVisited: DayRange = null
        onVisitStart()

        val minStart = MinPeriodTime
        val maxEnd = MaxPeriodTime - 1.year
        val offset = direction.id

        var current = day
        while (lastVisited == null && current.start > minStart && current.end < maxEnd) {
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

    @inline
    protected def startHourVisit(hour: HourRange, context: C, direction: SeekDirection): HourRange = {
        var lastVisited: HourRange = null
        onVisitStart()

        val minStart = MinPeriodTime
        val maxEnd = MaxPeriodTime - 1.year
        val offset = direction.id

        var current = hour
        while (lastVisited == null && current.start > minStart && current.end < maxEnd) {
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

    protected def onVisitStart() {}

    protected def onVisitEnd() {}

    protected def checkLimits(target: ITimePeriod): Boolean = limits.hasInside(target)

    protected def checkExcludePeriods(target: ITimePeriod): Boolean = {
        if (filter.excludePeriods.size == 0) true
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

    @inline
    protected def isMatchingYear(yr: YearRange, context: C): Boolean = {
        if (filter.years.size > 0 && !filter.years.contains(yr.year)) false
        else checkExcludePeriods(yr)
    }

    @inline
    protected def isMatchingMonth(mr: MonthRange, context: C): Boolean = {
        if (filter.years.size > 0 && !filter.years.contains(mr.year))
            false
        else if (filter.monthOfYears.size > 0 && !filter.monthOfYears.contains(mr.monthOfYear))
            false
        else
            checkExcludePeriods(mr)
    }

    @inline
    protected def isMatchingDay(dr: DayRange, context: C): Boolean = {
        if (filter.years.size > 0 && !filter.years.contains(dr.year))
            false
        else if (filter.monthOfYears.size > 0 && !filter.monthOfYears.contains(dr.monthOfYear))
            false
        else if (filter.dayOfMonths.size > 0 && !filter.dayOfMonths.contains(dr.dayOfMonth))
            false
        else if (filter.weekDays.size > 0 && !filter.weekDays.contains(dr.dayOfWeek))
            false
        else
            checkExcludePeriods(dr)
    }

    @inline
    protected def isMatchingHour(hr: HourRange, context: C): Boolean = {
        if (filter.years.size > 0 && !filter.years.contains(hr.year))
            false
        else if (filter.monthOfYears.size > 0 && !filter.monthOfYears.contains(hr.monthOfYear))
            false
        else if (filter.dayOfMonths.size > 0 && !filter.dayOfMonths.contains(hr.dayOfMonth))
            false
        else if (filter.weekDays.size > 0 && !filter.weekDays.contains(DayOfWeek(hr.start.getDayOfWeek)))
            false
        else if (filter.hourOfDays.size > 0 && !filter.hourOfDays.contains(hr.hourOfDay))
            false
        else
            checkExcludePeriods(hr)
    }

    @inline
    protected def isMatchingMinute(mr: MinuteRange, context: C): Boolean = {
        if (filter.years.size > 0 && !filter.years.contains(mr.year))
            false
        else if (filter.monthOfYears.size > 0 && !filter.monthOfYears.contains(mr.monthOfYear))
            false
        else if (filter.dayOfMonths.size > 0 && !filter.dayOfMonths.contains(mr.dayOfMonth))
            false
        else if (filter.weekDays.size > 0 && !filter.weekDays.contains(DayOfWeek(mr.start.getDayOfWeek)))
            false
        else if (filter.hourOfDays.size > 0 && !filter.hourOfDays.contains(mr.hourOfDay))
            false
        else if (filter.minuteOfHours.size > 0 && !filter.minuteOfHours.contains(mr.minuteOfHour))
            false
        else
            checkExcludePeriods(mr)
    }
}
