package kr.debop4s.timeperiod.timeline

import kr.debop4s.timeperiod.{ITimePeriodCollection, ITimePeriodContainer, ITimeCalendar, ITimePeriod}
import org.slf4j.LoggerFactory

/**
 * kr.debop4s.timeperiod.timeline.TimeGapCalculator
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 5. 오전 2:20
 */
class TimeGapCalculator[T <: ITimePeriod](val mapper: ITimeCalendar) {

    lazy val log = LoggerFactory.getLogger(getClass)

    def this() {
        this(null)
    }

    def getGaps(excludePeriods: ITimePeriodContainer, limits: ITimePeriod = null): ITimePeriodCollection = {
        assert(excludePeriods != null)
        log.debug(s"Period들의 Gap을 계산합니다. excpudePeriods=[$excludePeriods], limits=[$limits]")

        val timeLine = new TimeLine[T](excludePeriods, limits, mapper)
        timeLine.calculateGaps
    }
}

object TimeGapCalculator {

    def apply[T <: ITimePeriod](): TimeGapCalculator[T] = new TimeGapCalculator[T]()

    def apply[T <: ITimePeriod](mapper: ITimeCalendar): TimeGapCalculator[T] = new TimeGapCalculator[T](mapper)
}

