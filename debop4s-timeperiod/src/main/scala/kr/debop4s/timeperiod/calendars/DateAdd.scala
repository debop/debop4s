package kr.debop4s.timeperiod.calendars

import kr.debop4s.core.logging.Logger
import kr.debop4s.core.{Guard, ValueObject}
import kr.debop4s.time._
import kr.debop4s.timeperiod.SeekBoundaryMode.SeekBoundaryMode
import kr.debop4s.timeperiod.SeekDirection.SeekDirection
import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.timeline.TimeGapCalculator
import kr.debop4s.timeperiod.utils.Durations
import org.joda.time.{Duration, DateTime}
import scala.collection.JavaConversions._

/**
 * kr.debop4s.timeperiod.calendars.DateAdd
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 5. 오전 1:51
 */
@SerialVersionUID(2352433294158169198L)
class DateAdd extends ValueObject {

    implicit lazy val log = Logger(getClass)

    val includePeriods = TimePeriodCollection()
    val excludePeriods = TimePeriodCollection()

    def getIncludePeriods = includePeriods

    def getExcludePeriods = excludePeriods

    def add(start: DateTime, offset: Duration, seekBoundary: SeekBoundaryMode = SeekBoundaryMode.Next): DateTime = {
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

    def subtract(start: DateTime, offset: Duration, seekBoundary: SeekBoundaryMode = SeekBoundaryMode.Next): DateTime = {
        log.trace(s"Subtract... start=[$start] + offset[$offset]의 시간을 계산합니다. seekBoundary=[$seekBoundary]")

        if (includePeriods.size == 0 && excludePeriods.size == 0)
            return start + offset

        val (end, remaining) =
            if (offset < Duration.ZERO)
                calculateEnd(start, Durations.negate(offset), SeekDirection.Forward, seekBoundary)
            else
                calculateEnd(start, offset, SeekDirection.Backward, seekBoundary)

        log.debug(s"Subtract. start=[$start] + offset[$offset]의 결과 end=[$end], remaining=[$remaining]")
        end
    }

    protected def calculateEnd(start: DateTime,
                               offset: Duration,
                               seekDir: SeekDirection,
                               seekBoundary: SeekBoundaryMode): (DateTime, Duration) = {
        log.trace("기준시각으로부터 오프셋만큼 떨어진 시각을 구합니다. " +
                  s"start=[$start], offset=[$offset], seekDir=[$seekDir], seekBoundary=[$seekBoundary]")
        Guard.shouldBe(offset.compareTo(Duration.ZERO) >= 0, s"offset 값은 0 이상이어야 합니다. offset=[$offset]")

        var remaining = offset
        var end: DateTime = null

        val searchPeriods = new TimePeriodCollection(includePeriods)
        if (searchPeriods.size == 0)
            searchPeriods.add(TimeRange.Anytime)

        // available periods
        var availablePeriods: ITimePeriodCollection = new TimePeriodCollection()
        if (excludePeriods.size == 0) {
            availablePeriods.addAll(searchPeriods)
        } else {
            log.trace("예외 기간을 제외합니다.")
            val gapCalculator = new TimeGapCalculator[TimeRange]()
            searchPeriods.foreach(p => {
                if (excludePeriods.hasOverlapPeriods(p)) {
                    log.trace("예외 기간에 속하지 않은 부분만 추려냅니다.")
                    gapCalculator.getGaps(excludePeriods, p).foreach(gap => availablePeriods.add(gap))
                } else {
                    availablePeriods.add(p)
                }
            })
        }

        if (availablePeriods.size == 0) {
            log.trace("유효한 period가 없어서 중단합니다.")
            return (null, remaining)
        }

        log.trace("유효기간 중 중복된 부분은 결합합니다...")
        val periodCombiner = new TimePeriodCombiner[TimeRange]()
        availablePeriods = periodCombiner.combinePeriods(availablePeriods)

        log.trace("첫 시작합니다...")

        var (startPeriod, seekMoment) =
            if (seekDir == SeekDirection.Forward) findNextPeriod(start, availablePeriods)
            else findPreviousPeriod(start, availablePeriods)

        // 첫 시작 기간이 없다면 중단합니다.
        if (startPeriod == null) {
            log.trace("첫 시작이 없어서 중단합니다.")
            (null, remaining)
        }

        if (offset.isEqual(Duration.ZERO)) {
            log.trace("offset 값이 0이므로, 바로 다음 값인 seekMoment를 반환합니다.")
            (seekMoment, remaining)
        }

        if (seekDir == SeekDirection.Forward) {
            (availablePeriods.indexOf(startPeriod) until availablePeriods.size).foreach(i => {
                val gap = availablePeriods(i)
                val gapRemaining = new Duration(seekMoment, gap.end)
                log.trace(s"Seek forward... " +
                          s"gap=[$gap], gapRemaining=[$gapRemaining], remaining=[$remaining], seekMoment=[$seekMoment]")

                val isTargetPeriod =
                    if (seekBoundary == SeekBoundaryMode.Fill) gapRemaining >= remaining
                    else gapRemaining > remaining

                if (isTargetPeriod) {
                    end = seekMoment + remaining
                    remaining = null
                    (end, remaining)
                }
                remaining = remaining - gapRemaining
                if (i == availablePeriods.size - 1) {
                    (null, remaining)
                }
                seekMoment = availablePeriods(i + 1).start
            })
        } else {
            (availablePeriods.indexOf(startPeriod) to 0 by -1).foreach(i => {
                val gap = availablePeriods(i)
                val gapRemaining = new Duration(gap.start, seekMoment)
                log.trace(s"Seek backward. " +
                          s"gap=[$gap], gapRemaining=[$gapRemaining], remaining=[$remaining], seekMoment=[$seekMoment]")

                val isTargetPeriod =
                    if (seekBoundary == SeekBoundaryMode.Fill) gapRemaining >= remaining
                    else gapRemaining > remaining

                if (isTargetPeriod) {
                    end = seekMoment - remaining
                    remaining = null
                    (end, remaining)
                }
                remaining = remaining - gapRemaining
                if (i == 0) {
                    (null, remaining)
                }
                seekMoment = availablePeriods(i - 1).end
            })
        }

        log.debug("해당 일자를 찾지 못했습니다.")
        (null, remaining)
    }

    private def findNextPeriod(start: DateTime, periods: Iterable[_ <: ITimePeriod]): (ITimePeriod, DateTime) = {
        log.trace(s"시작시각 이후 기간을 찾습니다... start=[$start], periods=[$periods]")

        var nearest: ITimePeriod = null
        var moment = start
        var difference = MaxDuration

        periods
            .filter(period => period.end.compareTo(start) <= 0)
            .foreach(period => {

            if (period.hasInside(start)) {
                nearest = period
                moment = start
                log.trace(s"시작시각 이후 기간을 찾았습니다. start=[$start], moment=[$moment], nearest=[$nearest]")
                (nearest, moment)
            }
            val periodToMoment = new Duration(start, period.start)
            if (periodToMoment < difference) {
                difference = periodToMoment
                nearest = period
                moment = period.start
            }
        })
        log.trace(s"시작시각 이후 기간을 찾았습니다. start=[$start], moment=[$moment], nearest=[$nearest]")
        (nearest, moment)
    }

    private def findPreviousPeriod(start: DateTime, periods: Iterable[_ <: ITimePeriod]): (ITimePeriod, DateTime) = {
        log.trace(s"시작시각 이전 기간을 찾습니다... start=[$start], periods=[$periods]")

        var nearest: ITimePeriod = null
        var moment = start
        var difference = MaxDuration

        periods
            .filter(period => period.end <= start)
            .foreach(period => {

            if (period.hasInside(start)) {
                nearest = period
                moment = start
                log.trace(s"시작시각 이전 기간을 찾았습니다. start=[$start], moment=[$moment], nearest=[$nearest]")
                (nearest, moment)
            }
            val periodToMoment = new Duration(start, period.end)
            if (periodToMoment < difference) {
                difference = periodToMoment
                nearest = period
                moment = period.end
            }
        })
        log.trace(s"시작시각 이후 기간을 찾았습니다. start=[$start], moment=[$moment], nearest=[$nearest]")
        (nearest, moment)
    }

}
