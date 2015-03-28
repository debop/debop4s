package debop4s.timeperiod.timeline

import debop4s.timeperiod.{ ITimePeriodCollection, ITimePeriodContainer, ITimeCalendar, ITimePeriod }
import org.slf4j.LoggerFactory

/**
 * TimeGapCalculator
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 5. 오전 2:20
 */
class TimeGapCalculator[T <: ITimePeriod](val mapper: ITimeCalendar = null) {

  private lazy val log = LoggerFactory.getLogger(getClass)

  def gaps(excludePeriods: ITimePeriodContainer, limits: ITimePeriod = null): ITimePeriodCollection = {
    require(excludePeriods != null)

    val timeLine = new TimeLine[T](excludePeriods, limits, mapper)
    timeLine.calculateGaps
  }
}

object TimeGapCalculator {

  def apply[T <: ITimePeriod](): TimeGapCalculator[T] = new TimeGapCalculator[T]()

  def apply[T <: ITimePeriod](mapper: ITimeCalendar): TimeGapCalculator[T] = {
    new TimeGapCalculator[T](mapper)
  }
}

