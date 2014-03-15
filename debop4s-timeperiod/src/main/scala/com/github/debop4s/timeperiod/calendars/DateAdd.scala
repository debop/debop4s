package com.github.debop4s.timeperiod.calendars

import com.github.debop4s.timeperiod.SeekBoundaryMode.SeekBoundaryMode
import com.github.debop4s.timeperiod.SeekDirection.SeekDirection
import com.github.debop4s.timeperiod._
import com.github.debop4s.timeperiod.timeline.TimeGapCalculator
import com.github.debop4s.timeperiod.utils.Durations
import org.joda.time.{Duration, DateTime}
import org.slf4j.LoggerFactory

/**
 * com.github.debop4s.timeperiod.calendars.DateAdd
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 5. 오전 1:51
 */
class DateAdd {

    private lazy val log = LoggerFactory.getLogger(getClass)

    val includePeriods = TimePeriodCollection()
    val excludePeriods = TimePeriodCollection()

    def getIncludePeriods = includePeriods

    def getExcludePeriods = excludePeriods

    /**
     * start 시각으로부터 offset 기간이 지난 시각을 계산합니다.
     */
    def add(start: DateTime, offset: Duration): DateTime = {
        add(start, offset, SeekBoundaryMode.Next)
    }

    /**
     * start 시각으로부터 offset 기간이 지난 시각을 계산합니다.
     */
    @inline
    def add(start: DateTime, offset: Duration, seekBoundary: SeekBoundaryMode): DateTime = {
        log.trace(s"Add... start=[$start] + offset[$offset]의 시간을 계산합니다. seekBoundary=[$seekBoundary]")

        if (includePeriods.size == 0 && excludePeriods.size == 0)
            return start + offset

        val (end, remaining) =
            if (offset < Duration.ZERO)
                calculateEnd(start, Durations.negate(offset), SeekDirection.Backward, seekBoundary)
            else
                calculateEnd(start, offset, SeekDirection.Forward, seekBoundary)

        log.debug(s"Add. start=[$start] + offset[$offset]의 결과 end=[$end], remaining=[$remaining]")
        end
    }

    /**
     * start 시각으로부터 offset 기간 전 시각을 계산합니다.
     */
    @inline
    def subtract(start: DateTime, offset: Duration): DateTime = {
        subtract(start, offset, SeekBoundaryMode.Next)
    }

    @inline
    def subtract(start: DateTime, offset: Duration, seekBoundary: SeekBoundaryMode): DateTime = {
        log.trace(s"Subtract... start=[$start] + offset[$offset]의 시간을 계산합니다. seekBoundary=[$seekBoundary]")

        if (includePeriods.size == 0 && excludePeriods.size == 0)
            return start - offset

        val (end, remaining) =
            if (offset < Duration.ZERO)
                calculateEnd(start, Durations.negate(offset), SeekDirection.Forward, seekBoundary)
            else
                calculateEnd(start, offset, SeekDirection.Backward, seekBoundary)

        log.debug(s"Subtract. start=[$start] + offset[$offset]의 결과 end=[$end], remaining=[$remaining]")
        end
    }

    @inline
    protected def calculateEnd(start: DateTime,
                               offset: Duration,
                               seekDir: SeekDirection,
                               seekBoundary: SeekBoundaryMode): (DateTime, Duration) = {
        assert(offset >= Duration.ZERO, s"offset 값은 0 이상이어야 합니다. offset=[$offset]")

        var remaining = offset
        var end: DateTime = null

        val searchPeriods = TimePeriodCollection(includePeriods.periods)
        if (searchPeriods.size == 0)
            searchPeriods.add(TimeRange.Anytime)

        // available periods
        var availablePeriods: ITimePeriodCollection = new TimePeriodCollection()
        if (excludePeriods.size == 0) {
            availablePeriods.addAll(searchPeriods)
        } else {
            val gapCalculator = new TimeGapCalculator[TimeRange]()
            searchPeriods.foreach {
                p =>
                    if (excludePeriods.hasOverlapPeriods(p)) {
                        gapCalculator.getGaps(excludePeriods, p).foreach(gap => availablePeriods.add(gap))
                    } else {
                        availablePeriods.add(p)
                    }
            }
        }

        if (availablePeriods.size == 0) {
            return (null, remaining)
        }

        val periodCombiner = new TimePeriodCombiner[TimeRange]()
        availablePeriods = periodCombiner.combinePeriods(availablePeriods)

        var (startPeriod, seekMoment) =
            if (seekDir == SeekDirection.Forward)
                DateAdd.findNextPeriod(start, availablePeriods)
            else
                DateAdd.findPreviousPeriod(start, availablePeriods)

        // 첫 시작 기간이 없다면 중단합니다.
        if (startPeriod == null) {
            return (null, remaining)
        }

        if (offset.isEqual(Duration.ZERO)) {
            return (seekMoment, remaining)
        }

        if (seekDir == SeekDirection.Forward) {
            val start = availablePeriods.indexOf(startPeriod)
            for (i <- start until availablePeriods.size) {
                val gap = availablePeriods(i)
                val gapRemaining = new Duration(seekMoment, gap.end)

                val isTargetPeriod =
                    if (seekBoundary == SeekBoundaryMode.Fill) gapRemaining >= remaining
                    else gapRemaining > remaining

                if (isTargetPeriod) {
                    end = seekMoment + remaining
                    remaining = null
                    return (end, remaining)
                }
                remaining = remaining - gapRemaining
                if (i == availablePeriods.size - 1) {
                    return (null, remaining)
                }
                seekMoment = availablePeriods(i + 1).start
            }
        } else {
            val start = availablePeriods.indexOf(startPeriod)
            for (i <- start to 0 by -1) {
                val gap = availablePeriods(i)
                val gapRemaining = new Duration(gap.start, seekMoment)

                val isTargetPeriod =
                    if (seekBoundary == SeekBoundaryMode.Fill) gapRemaining >= remaining
                    else gapRemaining > remaining

                if (isTargetPeriod) {
                    end = seekMoment - remaining
                    remaining = null
                    return (end, remaining)
                }
                remaining = remaining - gapRemaining
                if (i == 0) {
                    return (null, remaining)
                }
                seekMoment = availablePeriods(i - 1).end
            }
        }

        log.debug("해당 일자를 찾지 못했습니다.")
        (null, remaining)
    }
}

object DateAdd {

    def apply(): DateAdd = new DateAdd()

    private lazy val log = LoggerFactory.getLogger(getClass)

    @inline
    private[timeperiod] def findNextPeriod(start: DateTime, periods: Iterable[_ <: ITimePeriod]): (ITimePeriod, DateTime) = {
        var nearest: ITimePeriod = null
        var moment = start
        var difference = MaxDuration

        periods
        .filter(period => period.end >= start)
        .foreach { period =>
            if (period.hasInside(start)) {
                nearest = period
                moment = start
                return (nearest, moment)
            }
            val periodToMoment = new Duration(start, period.start)
            if (periodToMoment < difference) {
                difference = periodToMoment
                nearest = period
                moment = period.start
            }
        }
        (nearest, moment)
    }

    private[timeperiod] def findPreviousPeriod(start: DateTime, periods: Iterable[_ <: ITimePeriod]): (ITimePeriod, DateTime) = {
        var nearest: ITimePeriod = null
        var moment = start
        var difference = MaxDuration

        periods
        .filter(p => p.start <= start)
        .foreach { period =>
            if (period.hasInside(start)) {
                // start가 기간에 속한다면...
                nearest = period
                moment = start
                return (nearest, moment)
            }

            // 근처 값이 아니라면 포기
            val periodToMoment = new Duration(start, period.end)
            if (periodToMoment < difference) {
                difference = periodToMoment
                nearest = period
                moment = period.end
            }
        }
        (nearest, moment)
    }
}
