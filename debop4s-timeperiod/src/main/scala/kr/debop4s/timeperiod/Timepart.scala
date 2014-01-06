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

    def hourOfDay = value.getHourOfDay

    def minuteOfHour = value.getMinuteOfHour

    def secondOfMinute = value.getSecondOfMinute

    def millisOfSecond = value.getMillisOfSecond

    def totalHours: Double = millis / MillisPerHour

    def totalMinutes: Double = millis / MillisPerMinute

    def totalSeconds: Double = millis / MillisPerSecond

    def totalMillis: Long = millis

    def millis: Long = value.getMillisOfDay

    def getDateTime(moment: DateTime): DateTime =
        moment.withTimeAtStartOfDay().plus(millis)

    def compare(that: Timepart) = value.compareTo(that.value)

    override def hashCode() = millis.toInt

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
