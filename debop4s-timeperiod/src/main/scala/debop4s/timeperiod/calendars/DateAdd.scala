package debop4s.timeperiod.calendars

import debop4s.core.conversions.jodatime._
import debop4s.timeperiod.TimeSpec._
import debop4s.timeperiod._
import debop4s.timeperiod.timeline.TimeGapCalculator
import debop4s.timeperiod.utils.Durations
import org.joda.time.{DateTime, Duration}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

/**
 * debop4s.timeperiod.calendars.DateAdd
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 5. 오전 1:51
 */
class DateAdd {

  private[this] lazy val log = LoggerFactory.getLogger(getClass)

  protected val _includePeriods = TimePeriodCollection()
  protected val _excludePeriods = TimePeriodCollection()

  def includePeriods = _includePeriods
  def excludePeriods = _excludePeriods

  def getIncludePeriods = _includePeriods
  def getExcludePeriods = _excludePeriods

  /**
   * start 시각으로부터 offset 기간이 지난 시각을 계산합니다.
   */
  def add(start: DateTime, offset: Duration): DateTime = {
    add(start, offset, SeekBoundaryMode.Next)
  }

  /**
   * start 시각으로부터 offset 기간이 지난 시각을 계산합니다.
   */
  def add(start: DateTime, offset: Duration, seekBoundary: SeekBoundaryMode): DateTime = {
    log.trace(s"Add... start=[$start] + offset[$offset]의 시간을 계산합니다. seekBoundary=[$seekBoundary]")

    if (_includePeriods.size == 0 && _excludePeriods.size == 0)
      return start + offset

    val (end, remaining) =
      if (offset < Duration.ZERO)
        calculateEnd(start, Durations.negate(offset), SeekDirection.Backward, seekBoundary)
      else
        calculateEnd(start, offset, SeekDirection.Forward, seekBoundary)

    log.trace(s"Add. start=[$start] + offset[$offset]의 결과 end=[$end], remaining=[$remaining]")

    end
  }

  /**
   * start 시각으로부터 offset 기간 전 시각을 계산합니다.
   */
  def subtract(start: DateTime, offset: Duration): DateTime = {
    subtract(start, offset, SeekBoundaryMode.Next)
  }

  def subtract(start: DateTime, offset: Duration, seekBoundary: SeekBoundaryMode): DateTime = {
    log.trace(s"Subtract... start=[$start] + offset[$offset]의 시간을 계산합니다. seekBoundary=[$seekBoundary]")

    if (_includePeriods.size == 0 && _excludePeriods.size == 0)
      return start - offset

    val (end, remaining) =
      if (offset < Duration.ZERO)
        calculateEnd(start, Durations.negate(offset), SeekDirection.Forward, seekBoundary)
      else
        calculateEnd(start, offset, SeekDirection.Backward, seekBoundary)

    log.trace(s"Subtract. start=[$start] + offset[$offset]의 결과 end=[$end], remaining=[$remaining]")

    end
  }

  protected def calculateEnd(start: DateTime,
                             offset: Duration,
                             seekDir: SeekDirection,
                             seekBoundary: SeekBoundaryMode): (DateTime, Duration) = {
    assert(offset >= Duration.ZERO, s"offset 값은 0 이상이어야 합니다. offset=[$offset]")

    var remaining = offset
    var end: DateTime = null

    val searchPeriods = TimePeriodCollection(_includePeriods.periods.asJava)
    if (searchPeriods.size == 0)
      searchPeriods.add(TimeRange.Anytime)

    // available periods
    var availablePeriods: ITimePeriodCollection = new TimePeriodCollection()
    if (excludePeriods.size == 0) {
      availablePeriods.addAll(searchPeriods)
    } else {
      val gapCalculator = new TimeGapCalculator[TimeRange]()

      var i = 0
      while (i < searchPeriods.length) {
        val p = searchPeriods(i)
        if (excludePeriods.hasOverlapPeriods(p)) {
          gapCalculator.gaps(excludePeriods, p).foreach(gap => availablePeriods.add(gap))
        } else {
          availablePeriods.add(p)
        }
        i += 1
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
      var i = start
      while (i < availablePeriods.length) {
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
        i += 1
      }
    } else {
      val start = availablePeriods.indexOf(startPeriod)
      // for (i <- start to 0 by -1) {
      var i = start
      while (i >= 0) {
        val gap = availablePeriods(i)
        val gapRemaining = new Duration(gap.start, seekMoment)

        log.debug(s"gap=$gap, gapRemaining=$gapRemaining")

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
        i -= 1
      }
    }

    log.debug("해당 일자를 찾지 못했습니다.")

    (null, remaining)
  }
}

object DateAdd {

  def apply(): DateAdd = new DateAdd()

  private[this] lazy val log = LoggerFactory.getLogger(getClass)

  private def findNextPeriod(start: DateTime, periods: Iterable[_ <: ITimePeriod]): (ITimePeriod, DateTime) = {
    var nearest: ITimePeriod = null
    var moment = start
    var difference = MaxDuration

    log.trace(s"find next period. start=$start")

    periods
    .filter(period => period.end >= moment)
    .foreach { period =>
      if (period.hasInside(moment)) {
        return (period, moment)
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

  private def findPreviousPeriod(start: DateTime, periods: Iterable[_ <: ITimePeriod]): (ITimePeriod, DateTime) = {
    var nearest: ITimePeriod = null
    var moment = start
    var difference = MaxDuration

    log.trace(s"find previous period. start=$start, periods=$periods")

    periods
    .filter(p => p.start <= moment)
    .foreach { period =>
      if (period.hasInside(moment)) {
        // start가 기간에 속한다면...
        return (period, moment)
      }

      // 근처 값이 아니라면 포기
      val periodToMoment = new Duration(moment, period.end)
      if (periodToMoment < difference) {
        difference = periodToMoment
        nearest = period
        moment = period.end
      }
    }

    (nearest, moment)
  }
}
