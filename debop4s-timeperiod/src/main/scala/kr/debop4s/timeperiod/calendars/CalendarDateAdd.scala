package kr.debop4s.timeperiod.calendars

import java.util
import kr.debop4s.core.{Guard, NotSupportedException}
import kr.debop4s.timeperiod.DayOfWeek.DayOfWeek
import kr.debop4s.timeperiod.SeekBoundaryMode.SeekBoundaryMode
import kr.debop4s.timeperiod.SeekDirection.SeekDirection
import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.timeline.TimeGapCalculator
import kr.debop4s.timeperiod.timerange.WeekRange
import kr.debop4s.timeperiod.utils.Durations
import org.joda.time.{Duration, DateTime}
import org.slf4j.LoggerFactory
import scala.annotation.varargs
import scala.collection.mutable.ArrayBuffer

/**
 * kr.debop4s.timeperiod.calendars.CalendarDayAdd
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 5. 오전 4:21
 */
class CalendarDateAdd extends DateAdd {

    private lazy val log = LoggerFactory.getLogger(getClass)

    val calendar = TimeCalendar.getEmptyOffset
    val weekDays = ArrayBuffer[DayOfWeek]()
    val workingHours = ArrayBuffer[HourRangeInDay]()
    val workingDayHours = ArrayBuffer[DayHourRange]()

    override val includePeriods: TimePeriodCollection = null

    override def getIncludePeriods = throw new NotSupportedException("IncludePeriods는 지원하지 않습니다.")

    def addWorkingWeekDays() {
        addWeekDays(Weekdays: _*)
    }

    def addWeekendWeekDays() {
        addWeekDays(Weekends: _*)
    }

    @varargs
    def addWeekDays(dayOfWeeks: DayOfWeek*) {
        weekDays ++= dayOfWeeks
    }

    override def add(start: DateTime, offset: Duration, seekBoundary: SeekBoundaryMode = SeekBoundaryMode.Next): DateTime = {
        log.trace(s"Add... start=[$start] + offset=[$offset] 시각을 계산합니다. seekBoundary=$seekBoundary")

        if (weekDays.size == 0 && excludePeriods.size == 0 && workingHours.size == 0)
            return start.plus(offset)

        val (end, remaining) =
            if (offset.compareTo(Duration.ZERO) < 0)
                calculateEnd(start, Durations.negate(offset), SeekDirection.Backward, seekBoundary)
            else
                calculateEnd(start, offset, SeekDirection.Forward, seekBoundary)

        log.trace(s"Add finished. start=[$start] + offset=[$offset] => end=[$end] seekBoundary=[$seekBoundary]")
        end
    }

    override def subtract(start: DateTime, offset: Duration, seekBoundary: SeekBoundaryMode = SeekBoundaryMode.Next): DateTime = {
        log.trace(s"subtract... start=[$start] - offset=[$offset] 시각을 계산합니다. seekBoundary=$seekBoundary")

        if (weekDays.size == 0 && excludePeriods.size == 0 && workingHours.size == 0)
            return start.minus(offset)

        val (end, remaining) =
            if (offset.compareTo(Duration.ZERO) < 0)
                calculateEnd(start, Durations.negate(offset), SeekDirection.Forward, seekBoundary)
            else
                calculateEnd(start, offset, SeekDirection.Backward, seekBoundary)

        log.trace(s"Subtract finished. start=[$start] - offset=[$offset] => end=[$end] seekBoundary=[$seekBoundary]")
        end
    }

    override def calculateEnd(start: DateTime,
                              offset: Duration,
                              seekDir: SeekDirection = SeekDirection.Forward,
                              seekBoundary: SeekBoundaryMode = SeekBoundaryMode.Next): (DateTime, Duration) = {
        log.trace("기준시각으로부터 오프셋만큼 떨어진 시각을 구합니다. " +
                  s"start=[$start], offset=[$offset], seekDir=[$seekDir], seekBoundary=[$seekBoundary]")
        Guard.shouldBe(offset.compareTo(Duration.ZERO) >= 0, s"offset 값은 0 이상이어야 합니다. offset=[$offset]")

        var end: DateTime = null
        var moment = start
        var remaining = offset

        var week = WeekRange(start, calendar)
        while (week != null) {
            super.getIncludePeriods.clear()
            super.getIncludePeriods.addAll(getAvailableWeekPeriods(week))

            log.trace(s"가능한 기간=[${super.getIncludePeriods}]")
            val result = super.calculateEnd(moment, remaining, seekDir, seekBoundary)
            end = result._1
            remaining = result._2
            log.trace(s"완료기간을 구했습니다. end=[$end], remaining=[$remaining]")

            if (end != null || remaining == null)
                return (end, remaining)

            if (seekDir == SeekDirection.Forward) {
                week = findNextWeek(week)
                if (week != null)
                    moment = week.start
            } else {
                week = findPreviousWeek(week)
                if (week != null)
                    moment = week.end
            }
        }
        log.trace("기준시각으로부터 offset 기간만큼 떨어진 시각을 구했습니다. " +
                  s"start=[$start], offset=[$offset], seekDir=[$seekDir], seekBoundary=[$seekBoundary]")
        log.debug(s"결과: end=[$end], remaining=[$remaining]")

        (end, remaining)
    }

    private def findNextWeek(current: WeekRange): WeekRange = {
        log.trace(s"current week=[$current] 이후 week 기간을 구합니다...")

        var next: WeekRange = null

        if (getExcludePeriods.size == 0) {
            next = current.nextWeek
        } else {
            val limits = TimeRange(current.end.plusMillis(1))
            val gapCalculator = new TimeGapCalculator[TimeRange](calendar)
            val remainingPeriods = gapCalculator.getGaps(getExcludePeriods, limits)

            if (remainingPeriods.size > 0)
                next = WeekRange(remainingPeriods.get(0).start, calendar)
        }

        log.trace(s"current week=[$current] 이후 week 기간=[$next]")
        next
    }

    private def findPreviousWeek(current: WeekRange): WeekRange = {
        log.trace(s"current week=[$current] 이전 week 기간을 구합니다...")

        var previous: WeekRange = null

        if (getExcludePeriods.size == 0) {
            previous = current.previousWeek
        } else {
            val limits = new TimeRange(MinPeriodTime, current.start.minusMillis(1))
            val gapCalculator = new TimeGapCalculator[TimeRange](calendar)
            val remainingPeriods = gapCalculator.getGaps(getExcludePeriods, limits)

            if (remainingPeriods.size > 0)
                previous = WeekRange(remainingPeriods.get(remainingPeriods.size - 1).end, calendar)
        }

        log.trace(s"current week=[$current] 이전 week 기간=[$previous]")
        previous
    }

    private def getAvailableWeekPeriods(limits: ITimePeriod): util.List[ITimePeriod] = {
        assert(limits != null)
        log.trace(s"가능한 기간을 추출합니다. limits=[$limits]")

        if (weekDays.size == 0 && workingHours.size == 0 && workingDayHours.size == 0) {
            val result = TimePeriodCollection()
            result.add(limits)
            return result
        }

        val filter = new CalendarPeriodCollectorFilter()
        filter.weekDays ++ weekDays
        filter.collectingHours ++= workingHours
        filter.collectingDayHours ++= workingDayHours

        val weekCollector = new CalendarPeriodCollector(filter, limits, SeekDirection.Forward, calendar)
        weekCollector.collectHours()

        weekCollector.periods
    }

}
