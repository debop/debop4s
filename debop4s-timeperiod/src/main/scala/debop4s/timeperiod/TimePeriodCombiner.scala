package debop4s.timeperiod

import java.lang.{Iterable => JIterable}

import debop4s.timeperiod.timeline.TimeLine

class TimePeriodCombiner[@miniboxed T <: ITimePeriod](val mapper: Option[ITimePeriodMapper] = None) {

  def this() = this(None)
  def this(mapper: ITimePeriodMapper) = this(Some(mapper))

  def combinePeriods(periods: JIterable[ITimePeriod]): ITimePeriodCollection =
    TimeLine(TimePeriodCollection(periods), null, mapper).combinePeriods

  def combinePeriods(periods: ITimePeriodContainer): ITimePeriodCollection =
    TimeLine(periods, null, mapper).combinePeriods
}
