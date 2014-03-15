package com.github.debop4s.timeperiod.calendars.seeker

import com.github.debop4s.timeperiod.SeekDirection.SeekDirection
import com.github.debop4s.timeperiod._
import com.github.debop4s.timeperiod.calendars.{CalendarVisitor, CalendarVisitorFilter}
import com.github.debop4s.timeperiod.timerange.{YearRangeCollection, YearRange, MonthRange, DayRange}
import org.slf4j.LoggerFactory


object DaySeeker {

    def apply(): DaySeeker =
        apply(SeekDirection.Forward)

    def apply(seekDir: SeekDirection): DaySeeker =
        apply(seekDir, DefaultTimeCalendar)

    def apply(seekDir: SeekDirection, calendar: ITimeCalendar): DaySeeker =
        apply(new CalendarVisitorFilter(), seekDir, calendar)

    def apply(filter: CalendarVisitorFilter, seekDir: SeekDirection, calendar: ITimeCalendar): DaySeeker =
        new DaySeeker(filter, seekDir, calendar)
}

/**
 * com.github.debop4s.timeperiod.calendars.seeker.DaySeeker
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 5. 오후 8:17
 */
class DaySeeker(private[this] val _filter: CalendarVisitorFilter,
                private[this] val _seekDir: SeekDirection = SeekDirection.Forward,
                private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
    extends CalendarVisitor[CalendarVisitorFilter, DaySeekerContext](_filter,
        TimeRange.Anytime,
        _seekDir,
        _calendar) {

    private lazy val log = LoggerFactory.getLogger(getClass)

    @inline
    def findDay(startDay: DayRange, dayCount: Int): DayRange = {
        log.trace(s"Day 찾기... startDay=[$startDay], dayCount=[$dayCount]")

        if (dayCount == 0)
            return startDay

        val context = new DaySeekerContext(startDay, dayCount)
        var visitDir = seekDirection

        if (dayCount < 0)
            visitDir = if (visitDir == SeekDirection.Forward) SeekDirection.Backward else SeekDirection.Forward

        startDayVisit(startDay, context, visitDir)
        val foundDay = context.foundDay
        log.trace(s"Day 찾기 완료. startDay=[$startDay], dayCount=[$dayCount], foundDay=[$foundDay]")

        foundDay
    }

    override protected def enterYears(years: YearRangeCollection, context: DaySeekerContext) =
        !context.isFinished

    override protected def enterMonths(year: YearRange, context: DaySeekerContext) =
        !context.isFinished

    override protected def enterDays(month: MonthRange, context: DaySeekerContext) =
        !context.isFinished

    override protected def enterHours(day: DayRange, context: DaySeekerContext) = false

    @inline
    override protected def onVisitDay(day: DayRange, context: DaySeekerContext): Boolean = {
        assert(day != null)

        if (context.isFinished) return false
        else if (day.isSamePeriod(context.startDay)) return true
        else if (!isMatchingDay(day, context)) return true
        else if (!checkLimits(day)) return true

        context.processDay(day)
        // context가 찾기를 완료하면, 탐색(Visit)를 중단하도록 합니다.
        !context.isFinished
    }
}
