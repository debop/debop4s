package debop4s.timeperiod.calendars

import debop4s.core._
import debop4s.timeperiod.DayOfWeek._
import debop4s.timeperiod.SeekBoundaryMode._
import debop4s.timeperiod.SeekDirection._
import debop4s.timeperiod._
import debop4s.timeperiod.timeline.TimeGapCalculator
import debop4s.timeperiod.timerange.WeekRange
import debop4s.timeperiod.utils.Durations
import org.joda.time.{Duration, DateTime}
import org.slf4j.LoggerFactory
import scala.annotation.varargs
import scala.collection.mutable.ArrayBuffer

object CalendarDateAdd {

  def apply(): CalendarDateAdd = new CalendarDateAdd()
}

class CalendarDateAdd extends DateAdd {

  private lazy val log = LoggerFactory.getLogger(getClass)

  val calendar = TimeCalendar.getEmptyOffset
  val weekDays = ArrayBuffer[DayOfWeek]()
  val workingHours = ArrayBuffer[HourRangeInDay]()
  val workingDayHours = ArrayBuffer[DayHourRange]()

  override def includePeriods = throw new NotSupportedException("IncludePeriods는 지원하지 않습니다.")

  /**
   * 주중 (월-금)을 working day로 추가합니다.
   */

  def addWorkingWeekDays() {
    addWeekDays(Weekdays: _*)
  }

  /**
   * 주말 (토-일)을 working day로 추가합니다.
   */
  def addWeekendWeekDays() {
    addWeekDays(Weekends: _*)
  }

  @varargs
  def addWeekDays(dayOfWeeks: DayOfWeek*) {
    weekDays ++= dayOfWeeks
  }

  /**
   * start 시각으로부터 offset 기간이 지난 시각을 계산합니다.
   */
  @inline
  override def add(start: DateTime, offset: Duration, seekBoundary: SeekBoundaryMode): DateTime = {
    log.trace(s"Add... start=$start, offset=$offset 시각을 계산합니다. seekBoundary=$seekBoundary")

    if (weekDays.size == 0 && excludePeriods.size == 0 && workingHours.size == 0)
      return start.plus(offset)

    val (end, remaining) =
      if (offset < Duration.ZERO)
        calculateEnd(start, Durations.negate(offset), SeekDirection.Backward, seekBoundary)
      else
        calculateEnd(start, offset, SeekDirection.Forward, seekBoundary)

    log.trace(s"Add finished. start=[$start] + offset=[$offset] => end=[$end] seekBoundary=[$seekBoundary]")
    end
  }

  /**
   * start 시각으로부터 offset 기간 전 시각을 계산합니다.
   */
  @inline
  override def subtract(start: DateTime, offset: Duration, seekBoundary: SeekBoundaryMode): DateTime = {
    log.trace(s"subtract... start=[$start] - offset=[$offset] 시각을 계산합니다. seekBoundary=$seekBoundary")

    if (weekDays.size == 0 && excludePeriods.size == 0 && workingHours.size == 0)
      return start.minus(offset)

    val (end, remaining) =
      if (offset < Duration.ZERO)
        calculateEnd(start, Durations.negate(offset), SeekDirection.Forward, seekBoundary)
      else
        calculateEnd(start, offset, SeekDirection.Backward, seekBoundary)

    log.trace(s"Subtract finished. start=[$start] - offset=[$offset] => end=[$end] seekBoundary=[$seekBoundary]")
    end
  }

  @inline
  override def calculateEnd(start: DateTime,
                            offset: Duration,
                            seekDir: SeekDirection = SeekDirection.Forward,
                            seekBoundary: SeekBoundaryMode = SeekBoundaryMode.Next): (DateTime, Duration) = {
    log.trace("기준시각으로부터 오프셋만큼 떨어진 시각을 구합니다. " +
              s"start=[$start], offset=[$offset], seekDir=[$seekDir], seekBoundary=[$seekBoundary]")

    Guard.shouldBe(offset >= Duration.ZERO, s"offset 값은 0 이상이어야 합니다. offset=[$offset]")

    var moment = start
    var end: DateTime = null
    var remaining = offset

    var week = WeekRange(start, calendar)

    while (week != null) {
      _includePeriods.clear()
      _includePeriods.addAll(getAvailableWeekPeriods(week))

      log.trace(s"가능한 기간=[${ _includePeriods }]")

      val results = super.calculateEnd(moment, remaining, seekDir, seekBoundary)
      end = results._1
      remaining = results._2

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

  @inline
  private def findNextWeek(current: WeekRange): WeekRange = {
    log.trace(s"current week=[$current] 이후 week 기간을 구합니다...")

    var next: WeekRange = null

    if (excludePeriods.size == 0) {
      next = current.nextWeek
    } else {
      val limits = TimeRange(current.end + 1.millis, null.asInstanceOf[DateTime])
      val gapCalculator = new TimeGapCalculator[TimeRange](calendar)
      val remainingPeriods = gapCalculator.gaps(excludePeriods, limits)

      if (remainingPeriods.size > 0)
        next = WeekRange(remainingPeriods(0).start, calendar)
      else
        next = null
    }

    log.trace(s"current week=[$current] 이후 week 기간=[$next]")
    next
  }

  @inline
  private def findPreviousWeek(current: WeekRange): WeekRange = {
    log.trace(s"current week=[$current] 이전 week 기간을 구합니다...")

    var previous: WeekRange = null

    if (excludePeriods.size == 0) {
      previous = current.previousWeek
    } else {
      val limits = new TimeRange(MinPeriodTime, current.start - 1.millis)
      val gapCalculator = new TimeGapCalculator[TimeRange](calendar)
      val remainingPeriods = gapCalculator.gaps(excludePeriods, limits)

      if (remainingPeriods.size > 0)
        previous = WeekRange(remainingPeriods.get(remainingPeriods.size - 1).end, calendar)
      else
        previous = null
    }

    log.trace(s"current week=[$current] 이전 week 기간=[$previous]")
    previous
  }

  @inline
  private def getAvailableWeekPeriods(limits: ITimePeriod): Seq[ITimePeriod] = {
    assert(limits != null)
    log.trace(s"가능한 주간 기간을 추출합니다... limits=[$limits]")

    if (weekDays.size == 0 && workingHours.size == 0 && workingDayHours.size == 0) {
      val result = TimePeriodCollection()
      result.addAll(limits)
      return result
    }

    val filter = new CalendarPeriodCollectorFilter()
    filter.weekDays ++= weekDays
    filter.collectingHours ++= workingHours
    filter.collectingDayHours ++= workingDayHours

    val weekCollector = CalendarPeriodCollector(filter, limits, SeekDirection.Forward, calendar)
    weekCollector.collectHours()

    log.trace(s"가능한 주간 기간=${ weekCollector.periods }")
    weekCollector.periods
  }

}
