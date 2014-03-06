package com.github.debop4s.timeperiod.timeline

import com.github.debop4s.timeperiod._
import org.slf4j.LoggerFactory

/**
 * com.github.debop4s.timeperiod.timeline.TimeLines
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 31. 오후 9:17
 */
object TimeLines {

    private lazy val log = LoggerFactory.getLogger(getClass)

    def combinePeriods(moments: ITimeLineMomentCollection): ITimePeriodCollection = {

        log.trace("ITimeLineMomentCollection에서 모든 기간을 결합합니다...")

        val periods = new TimePeriodCollection()
        if (moments.isEmpty)
            return periods

        val momentsSize = moments.size
        var itemIndex = 0
        while (itemIndex < momentsSize) {
            val periodStart = moments(itemIndex)
            var balance = periodStart.startCount
            assert(balance > 0, s"balance > 0 이여아합니다. balance=[$balance]")

            var periodEnd: ITimeLineMoment = null
            while (itemIndex < momentsSize - 1 && balance > 0) {
                itemIndex += 1
                periodEnd = moments(itemIndex)
                balance += periodEnd.startCount
                balance -= periodEnd.endCount
            }
            assert(periodEnd != null, s"periodEnd should not null.")

            if (periodEnd.startCount <= 0 && itemIndex < momentsSize) {
                val period = TimeRange(periodStart.moment, periodEnd.moment)
                log.trace(s"period를 추가합니다. period=[$period]")
                periods.add(period)
            }
            itemIndex += 1
        }

        log.debug(s"기간을 결합했습니다. periods=[$periods]")
        periods
    }

    def intersectPeriods(moments: ITimeLineMomentCollection): ITimePeriodCollection = {

        log.trace("ITimeLineMomentCollection의 요소들의 모든 Period로부터 교집합에 해당하는 구간을 구합니다...")

        val periods: ITimePeriodCollection = new TimePeriodCollection()
        if (moments.isEmpty) return periods

        var intersectionStart = -1
        var balance = 0

        for (i <- 0 until moments.size) {
            val moment = moments(i)
            val startCount = moment.startCount
            val endCount = moment.endCount
            balance += startCount
            balance -= endCount

            if (startCount > 0 && balance > 1 && intersectionStart < 0) {
                intersectionStart = i
            } else if (endCount > 0 && balance <= 1 && intersectionStart >= 0) {
                val period = TimeRange(moments(intersectionStart).moment, moment.moment)
                log.trace(s"intersect period에 추가합니다. period=[$period]")
                periods.add(period)
                intersectionStart = -1
            }
        }

        log.debug(s"ITimeLineMomentCollection으로부터 교집합에 해당하는 기간을 구했습니다. periods=[$periods]")
        periods
    }

    def calculateGap(moments: ITimeLineMomentCollection, range: ITimePeriod): ITimePeriodCollection = {
        log.trace(s"ITimeLineMomentCollection의 모든 ITimePeriod에 속하지 않는 Gap을 구합니다(여집합)." +
                  s"moments=[$moments], range=[$range]")

        val gaps = new TimePeriodCollection
        if (moments.isEmpty) return gaps

        // find leading gap
        val periodStart = moments.min

        if (periodStart != null && range.start < periodStart.moment) {
            val startingGap = TimeRange(range.start, periodStart.moment)
            log.trace(s"starting gap을 추가합니다... startingGap=[$startingGap]")
            gaps.add(startingGap)
        }

        // find intermediated gap
        var itemIndex = 0

        while (itemIndex < moments.size) {
            val moment = moments(itemIndex)
            assert(moment != null)
            assert(moment.startCount > 0, s"moment.getStartCount() 값이 0보다 커야합니다. balance=[${ moment.startCount }]")

            var balance = moment.startCount
            var gapStart: ITimeLineMoment = null

            while (itemIndex < moments.size - 1 && balance > 0) {
                itemIndex += 1
                gapStart = moments(itemIndex)
                balance += gapStart.startCount
                balance -= gapStart.endCount
            }
            assert(gapStart != null)

            if (gapStart.startCount <= 0) {

                // found a gap
                if (itemIndex < moments.size - 1) {
                    val gap = TimeRange(gapStart.moment, moments(itemIndex + 1).moment)
                    log.trace(s"intermediated gap을 추가합니다. gap=[$gap]")
                    gaps.add(gap)
                }
            }
            itemIndex += 1
        }
        // find ending gap
        val periodEnd = moments.max
        log.trace(s"periodEnd=[$periodEnd]")

        if (periodEnd != null && range.end > periodEnd.moment) {
            val endingGap = TimeRange(periodEnd.moment, range.end)
            log.trace(s"ending gap을 추가합니다. endingGap=[$endingGap]")
            gaps.add(endingGap)
        }
        log.debug(s"ITimeLineMomentCollection에서 gap을 계산했습니다. gaps=[$gaps]")
        gaps
    }
}
