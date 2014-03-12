package com.github.debop4s.timeperiod.timeline

import com.github.debop4s.timeperiod.{ITimePeriodCollection, ITimePeriodContainer, ITimeCalendar, ITimePeriod}
import org.slf4j.LoggerFactory

/**
 * com.github.debop4s.timeperiod.timeline.TimeGapCalculator
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 5. 오전 2:20
 */
class TimeGapCalculator[T <: ITimePeriod](val mapper: ITimeCalendar = null) {

    private lazy val log = LoggerFactory.getLogger(getClass)

    def getGaps(excludePeriods: ITimePeriodContainer, limits: ITimePeriod = null): ITimePeriodCollection = {
        require(excludePeriods != null)
        log.debug(s"Period들의 Gap을 계산합니다. excpudePeriods=[$excludePeriods], limits=[$limits]")

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

