package debop4s.timeperiod.calendars

import debop4s.core.conversions.jodatime._
import debop4s.timeperiod.TimeSpec._
import debop4s.timeperiod._
import debop4s.timeperiod.timeline.TimeGapCalculator
import debop4s.timeperiod.utils.{Durations, Times}
import org.joda.time.{DateTime, Duration}
import org.slf4j.LoggerFactory

import scala.annotation.varargs
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer


object CalendarDateDiff {

  def apply(): CalendarDateDiff =
    new CalendarDateDiff()

  def apply(calendar: ITimeCalendar): CalendarDateDiff =
    new CalendarDateDiff(calendar)
}

/**
 * debop4s.timeperiod.calendars.CalendarDateDiff
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 5. 오후 7:55
 */
class CalendarDateDiff(val calendar: ITimeCalendar = TimeCalendar.getEmptyOffset) {

  private[this] lazy val log = LoggerFactory.getLogger(getClass)

  // def this() = this(TimeCalendar.getEmptyOffset)

  require(calendar != null)
  assert(calendar.startOffset.isEqual(Duration.ZERO), "startOffset은 0 이여야 합니다.")
  assert(calendar.endOffset.isEqual(Duration.ZERO), "startOffset은 0 이여야 합니다.")

  private val collectorFilter = new CalendarPeriodCollectorFilter()

  def weekDays: mutable.Set[DayOfWeek] = collectorFilter.weekDays

  def workingHours: ArrayBuffer[HourRangeInDay] = collectorFilter.collectingHours

  def workingDayHours: ArrayBuffer[DayHourRange] = collectorFilter.collectingDayHours

  def addWokringDays(): Unit = {
    addWeekDays(Weekdays: _*)
  }

  def addWeekendDays(): Unit = {
    addWeekDays(Weekends: _*)
  }

  @varargs
  def addWeekDays(dayOfWeeks: DayOfWeek*): Unit = {
    if (weekDays != null) {
      weekDays ++= dayOfWeeks
    }
  }

  def difference(moment: DateTime): Duration = difference(moment, Times.now)

  @inline
  def difference(fromTime: DateTime, toTime: DateTime): Duration = {
    if (fromTime.equals(toTime))
      return Duration.ZERO

    val isEmpty = weekDays.size == 0 &&
                  workingHours.size == 0 &&
                  workingDayHours.size == 0

    if (isEmpty) {
      return new DateDiff(fromTime, toTime, calendar).difference
    }

    val diffRange = new TimeRange(fromTime, toTime)
    val limits = new TimeRange(Times.startTimeOfDay(diffRange.start), Times.startTimeOfDay(diffRange.end.plusDays(1)))
    val collector = new CalendarPeriodCollector(collectorFilter, limits, SeekDirection.Forward, calendar)

    // Gap을 계산합니다.
    val gapCalc = new TimeGapCalculator[TimeRange](calendar)
    val gaps: ITimePeriodCollection = gapCalc.gaps(collector.periods, diffRange)
    var difference = Duration.ZERO

    gaps.foreach(gap => difference = difference + gap.duration)

    log.trace(s"fromTime=[$fromTime] ~ toTime=[$toTime]의 Working Time을 구했습니다. difference=[$difference]")

    if (fromTime <= toTime) difference
    else Durations.negate(difference)
  }
}
