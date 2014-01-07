package kr.debop4s.timeperiod

import kr.debop4s.core.ValueObject
import org.joda.time.{Duration, DateTime}

/**
 * kr.debop4s.timeperiod.Timepart
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

    def totalHours: Double = millisOfDay / MillisPerHour

    def totalMinutes: Double = millisOfDay / MillisPerMinute

    def totalSeconds: Double = millisOfDay / MillisPerSecond

    def totalMillis: Long = millisOfDay

    def millisOfDay: Long = value.getMillisOfDay

    def getDateTime(moment: DateTime): DateTime =
        moment.withTimeAtStartOfDay().plus(millisOfDay)

    def compare(that: Timepart) = value.compareTo(that.value)

    override def hashCode() = millisOfDay.toInt

    override protected def buildStringHelper =
        super.buildStringHelper.add("value", value)
}

object Timepart {

    def now(): Timepart = apply(DateTime.now())

    def apply() = new Timepart(new DateTime(0))

    def apply(moment: DateTime) = new Timepart(new DateTime(0).withMillisOfDay(moment.getMillisOfDay))

    def apply(hourOfDay: Int, minuteOfHour: Int = 0, secondOfMinute: Int = 0, millisOfSecond: Int = 0) =
        new Timepart(new DateTime(0).withTime(hourOfDay, minuteOfHour, secondOfMinute, millisOfSecond))

    def apply(duration: Duration) = new Timepart(new DateTime(0).withMillisOfDay(duration.getMillis.toInt))
}
