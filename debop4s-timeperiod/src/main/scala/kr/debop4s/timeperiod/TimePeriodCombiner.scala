package kr.debop4s.timeperiod

import kr.debop4s.timeperiod.timeline.TimeLine

/**
 * kr.debop4s.timeperiod.TimePeriodCombiner
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 31. 오후 7:52
 */
class TimePeriodCombiner[T <: ITimePeriod](val mapper: ITimePeriodMapper) {

    def this() {
        this(null)
    }

    def combinePeriods(periods: ITimePeriod*): ITimePeriodCollection =
        new TimeLine(new TimePeriodCollection(periods: _*), mapper = mapper).combinePeriods

    def combinePeriods(periods: ITimePeriodContainer): ITimePeriodCollection =
        new TimeLine(periods, mapper = mapper).combinePeriods
}
