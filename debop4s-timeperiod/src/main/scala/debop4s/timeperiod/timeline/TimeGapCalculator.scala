package debop4s.timeperiod.timeline

import debop4s.timeperiod._
import org.slf4j.LoggerFactory

/**
 * TimeGapCalculator
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 5. 오전 2:20
 */
class TimeGapCalculator[@miniboxed T <: ITimePeriod](val mapper: Option[ITimeCalendar] = None) {

  def this() = this(None)
  def this(mapper: ITimeCalendar) = this(Some(mapper))

  private lazy val log = LoggerFactory.getLogger(getClass)

  def gaps(excludePeriods: ITimePeriodContainer): ITimePeriodCollection =
    gaps(excludePeriods, null)

  def gaps(excludePeriods: ITimePeriodContainer, limits: ITimePeriod): ITimePeriodCollection = {
    require(excludePeriods != null)

    val timeLine = new TimeLine[T](excludePeriods, limits, mapper)
    timeLine.calculateGaps
  }

  def getGaps(excludePeriods: ITimePeriodContainer): ITimePeriodCollection =
    gaps(excludePeriods, null)

  def getGaps(excludePeriods: ITimePeriodContainer, limits: ITimePeriod): ITimePeriodCollection =
    gaps(excludePeriods, limits)
}

object TimeGapCalculator {

  def apply[T <: ITimePeriod](): TimeGapCalculator[T] = apply(None)

  def apply[T <: ITimePeriod](mapper: Option[ITimeCalendar] = None): TimeGapCalculator[T] =
    new TimeGapCalculator[T](mapper)
}

