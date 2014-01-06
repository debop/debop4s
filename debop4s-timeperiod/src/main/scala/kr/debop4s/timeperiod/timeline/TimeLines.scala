package kr.debop4s.timeperiod.timeline

import kr.debop4s.core.Guard
import kr.debop4s.core.logging.Logger
import kr.debop4s.timeperiod.{ITimePeriod, TimeRange, TimePeriodCollection, ITimePeriodCollection}

/**
 * kr.debop4s.timeperiod.timeline.TimeLines
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 31. 오후 9:17
 */
object TimeLines {

    lazy val log = Logger(getClass)

    def combinePeriods(moments: ITimeLineMomentCollection): ITimePeriodCollection = {

        log.trace("ITimeLineMomentCollection에서 모든 기간을 결합합니다...")

        val periods = new TimePeriodCollection()
        if (moments.isEmpty)
            return periods

        val momentsSize = moments.size
        var itemIndex = 0
        while (itemIndex < momentsSize) {
            val periodStart = moments(itemIndex)
            var balance = periodStart.getStartCount
            Guard.shouldBe(balance > 0, s"balance > 0 이여아합니다. balance=[$balance]")

            var periodEnd: ITimeLineMoment = null
            while (itemIndex < momentsSize - 1 && balance > 0) {
                itemIndex += 1
                periodEnd = moments(itemIndex)
                balance += periodEnd.getStartCount
                balance -= periodEnd.getEndCount
            }
            Guard.shouldNotBeNull(periodEnd, "periodEnd")

            if (periodEnd.getStartCount <= 0 && itemIndex < momentsSize) {
                val period = TimeRange(periodStart.getMoment, periodEnd.getMoment)
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
            val startCount = moment.getStartCount
            val endCount = moment.getEndCount
            balance += startCount
            balance -= endCount

            if (startCount > 0 && balance > 1 && intersectionStart < 0) {
                intersectionStart = i
            } else if (endCount > 0 && balance <= 1 && intersectionStart >= 0) {
                val period = TimeRange(moments(intersectionStart).getMoment, moment.getMoment)
                log.trace(s"intersect period에 추가합니다. period=[$period]")
                periods.add(period)
                intersectionStart = -1
            }
        }

        log.debug(s"ITimeLineMomentCollection으로부터 교집합에 해당하는 기간을 구했습니다. periods=[periods]", periods)
        periods
    }

    def calculateGap(moments: ITimeLineMomentCollection, range: ITimePeriod): ITimePeriodCollection = {
        log.trace(s"ITimeLineMomentCollection의 모든 ITimePeriod에 속하지 않는 Gap을 구합니다(여집합). range=[$range]")

        val gaps: ITimePeriodCollection = new TimePeriodCollection
        if (moments.isEmpty) return gaps

        // find leading gap
        val periodStart: ITimeLineMoment = moments.getMin
        if (periodStart != null && range.getStart.compareTo(periodStart.getMoment) < 0) {
            val startingGap = TimeRange(range.getStart, periodStart.getMoment)
            log.trace(s"starting gap을 추가합니다... startingGap=[$startingGap]")
            gaps.add(startingGap)
        }
        // find intermediated gap
        var itemIndex: Int = 0

        while (itemIndex < moments.size) {
            val moment: ITimeLineMoment = moments(itemIndex)
            var balance: Int = moment.getStartCount
            Guard.shouldBe(balance > 0, s"moment.getStartCount() 값이 0보다 커야합니다. balance=[$balance]")
            var gapStart: ITimeLineMoment = null

            while (itemIndex < moments.size - 1 && balance > 0) {
                itemIndex += 1
                gapStart = moments(itemIndex)
                balance += gapStart.getStartCount
                balance -= gapStart.getEndCount
            }
            Guard.shouldNotBeNull(gapStart, "gapStart")

            if (gapStart.getStartCount <= 0 && itemIndex < moments.size - 1) {
                val gap = TimeRange(gapStart.getMoment, moments(itemIndex + 1).getMoment)
                log.trace(s"intermediated gap을 추가합니다. gap=[$gap]")
                gaps.add(gap)
            }
            itemIndex += 1
        }
        // find ending gap
        val periodEnd: ITimeLineMoment = moments.getMax

        if (periodEnd != null && range.getEnd.compareTo(periodEnd.getMoment) > 0) {
            val endingGap = TimeRange(periodEnd.getMoment, range.getEnd)
            log.trace(s"ending gap을 추가합니다. endingGap=[$endingGap]")
            gaps.add(endingGap)
        }
        log.debug(s"ITimeLineMomentCollection에서 gap을 계산했습니다. gaps=[$gaps]", gaps)
        gaps
    }
}
