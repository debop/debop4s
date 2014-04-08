package debop4s.timeperiod

import debop4s.core.ValueObject
import debop4s.core.utils.Hashs

/**
 * debop4s.timeperiod.HourRangeInDay
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 4. 오후 11:14
 */
class HourRangeInDay(val start: Timepart,
                     val end: Timepart) extends ValueObject with Ordered[HourRangeInDay] {


    def compare(that: HourRangeInDay) = start.compare(that.start)

    override def hashCode() = Hashs.compute(start, end)

    override protected def buildStringHelper =
        super.buildStringHelper
        .add("start", start)
        .add("end", end)
}

object HourRangeInDay {

    def apply(start: Timepart, end: Timepart): HourRangeInDay =
        new HourRangeInDay(start, end)

    def apply(startHour: Int = 0, endHour: Int = 23): HourRangeInDay =
        new HourRangeInDay(Timepart(startHour), Timepart(endHour))

    def apply(hourOfDay: Int): HourRangeInDay =
        apply(hourOfDay, hourOfDay)


}
