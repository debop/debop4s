package debop4s.timeperiod

import debop4s.timeperiod.timeline.TimeLine

/**
 * debop4s.timeperiod.TimePeriodCombiner
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 31. 오후 7:52
 */
class TimePeriodCombiner[T <: ITimePeriod](val mapper: ITimePeriodMapper = null) {

  def combinePeriods(periods: Iterable[ITimePeriod]): ITimePeriodCollection =
    TimeLine(TimePeriodCollection(periods), null, mapper).combinePeriods

  def combinePeriods(periods: ITimePeriodContainer): ITimePeriodCollection =
    TimeLine(periods, null, mapper).combinePeriods
}
