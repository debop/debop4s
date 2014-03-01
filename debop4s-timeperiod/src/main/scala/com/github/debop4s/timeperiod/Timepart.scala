package com.github.debop4s.timeperiod

import com.github.debop4s.core.ValueObject
import com.github.debop4s.timeperiod.utils.Times
import org.joda.time.{Duration, DateTime}

/**
 * com.github.debop4s.timeperiod.Timepart
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 14. 오후 7:37
 */
@SerialVersionUID(-4029003873537088627L)
class Timepart(val value: DateTime) extends ValueObject with Ordered[Timepart] {

    assert(value != null)

    def hour: Int = value.getHourOfDay

    def minute: Int = value.getMinuteOfHour

    def second: Int = value.getSecondOfMinute

    def millis: Int = value.getMillisOfSecond

    def totalHours: Double = totalMillis / MillisPerHour

    def totalMinutes: Double = totalMillis / MillisPerMinute

    def totalSeconds: Double = totalMillis / MillisPerSecond

    def totalMillis: Long = value.getMillisOfDay

    def getDateTime(moment: DateTime): DateTime = moment.withTimeAtStartOfDay().plus(totalMillis)

    def compare(that: Timepart) = value.compareTo(that.value)

    override def hashCode() = totalMillis.toInt

    override protected def buildStringHelper = {
        super.buildStringHelper
        .add("value", value)
    }
}

object Timepart {

    def now(): Timepart = new Timepart(Times.now)

    def apply(): Timepart = new Timepart(Times.zero)

    def apply(moment: DateTime): Timepart = new Timepart(Times.zero.withMillisOfDay(moment.getMillisOfDay))

    def apply(hourOfDay: Int, minuteOfHour: Int = 0, secondOfMinute: Int = 0, millisOfSecond: Int = 0): Timepart = {
        new Timepart(Times.zero.withTime(hourOfDay, minuteOfHour, secondOfMinute, millisOfSecond))
    }

    def apply(duration: Option[Duration]): Timepart = {
        new Timepart(Times.zero + duration.getOrElse(Duration.ZERO))
    }
}
