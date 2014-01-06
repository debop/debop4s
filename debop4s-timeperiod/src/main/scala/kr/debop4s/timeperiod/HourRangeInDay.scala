package kr.debop4s.timeperiod

import kr.debop4s.core.ValueObject
import kr.debop4s.core.utils.Hashs

/**
 * kr.debop4s.timeperiod.HourRangeInDay
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 4. 오후 11:14
 */
class HourRangeInDay(private val _startHourOfDay: Int, private val _endHourOfDay: Int) extends ValueObject with Ordered[HourRangeInDay] {

    val start = Timepart(Math.min(_startHourOfDay, _endHourOfDay))
    val end = Timepart(Math.max(_startHourOfDay, _endHourOfDay))

    def this(hourOfDay: Int) {
        this(hourOfDay, hourOfDay)
    }

    def compare(that: HourRangeInDay) = start.compare(that.start)

    override def hashCode() = Hashs.compute(start, end)

    override protected def buildStringHelper =
        super.buildStringHelper
            .add("start", start)
            .add("end", end)
}
