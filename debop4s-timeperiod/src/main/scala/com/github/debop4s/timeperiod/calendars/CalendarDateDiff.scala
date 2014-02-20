package com.github.debop4s.timeperiod.calendars

import com.github.debop4s.timeperiod.DayOfWeek.DayOfWeek
import com.github.debop4s.timeperiod._
import com.github.debop4s.timeperiod.timeline.TimeGapCalculator
import com.github.debop4s.timeperiod.utils.{Durations, Times}
import org.joda.time.{DateTime, Duration}
import org.slf4j.LoggerFactory
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
 * com.github.debop4s.timeperiod.calendars.CalendarDateDiff
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 5. 오후 7:55
 */
class CalendarDateDiff(val calendar: ITimeCalendar = TimeCalendar.getEmptyOffset) {

    lazy val log = LoggerFactory.getLogger(getClass)

    assert(calendar.getStartOffset.isEqual(Duration.ZERO), "startOffset은 0 이여야 합니다.")
    assert(calendar.getEndOffset.isEqual(Duration.ZERO), "startOffset은 0 이여야 합니다.")

    private val collectorFilter = new CalendarPeriodCollectorFilter()

    def weekDays: mutable.Set[DayOfWeek.DayOfWeek] = collectorFilter.weekDays

    def workingHours: ArrayBuffer[HourRangeInDay] = collectorFilter.collectingHours

    def workingDayHours: ArrayBuffer[DayHourRange] = collectorFilter.collectingDayHours

    def addWokringDays() {
        addWeekDays(Weekdays: _*)
    }

    def addWeekendDays() {
        addWeekDays(Weekends: _*)
    }

    def addWeekDays(dayOfWeeks: DayOfWeek*) {
        if (weekDays != null) {
            weekDays ++= dayOfWeeks
        }
    }

    def difference(moment: DateTime): Duration = difference(moment, Times.now)

    def difference(fromTime: DateTime, toTime: DateTime): Duration = {
        log.trace(s"fromTime=[$fromTime] ~ toTime=[$toTime]의 Working Time을 구합니다.")

        if (fromTime.equals(toTime))
            return Duration.ZERO

        val filterIsEmpty = weekDays.size == 0 &&
                            workingHours.size == 0 &&
                            workingDayHours.size == 0
        if (filterIsEmpty) {
            return new DateDiff(fromTime, toTime, calendar).difference
        }

        val differenceRange = new TimeRange(fromTime, toTime)
        val limits = new TimeRange(Times.startTimeOfDay(differenceRange.start),
                                      Times.startTimeOfDay(differenceRange.end.plusDays(1)))
        val collector = new CalendarPeriodCollector(collectorFilter,
                                                       limits,
                                                       SeekDirection.Forward,
                                                       calendar)

        // Gap을 계산합니다.
        val gapCalculator = new TimeGapCalculator[TimeRange](calendar)
        val gaps: ITimePeriodCollection = gapCalculator.getGaps(collector.periods, differenceRange)
        var difference = Duration.ZERO
        gaps.foreach(gap => difference = difference.plus(gap.duration))

        log.trace(s"fromTime=[$fromTime] ~ toTime=[$toTime]의 Working Time을 구했습니다. difference=[$difference]")

        if (fromTime.compareTo(toTime) <= 0) difference
        else Durations.negate(difference)
    }
}
