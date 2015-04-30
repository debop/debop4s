package debop4s.timeperiod

import debop4s.core.ValueObject
import debop4s.timeperiod.TimeSpec._
import debop4s.timeperiod.utils.Times
import org.joda.time.{DateTime, Duration}

@SerialVersionUID(-4029003873537088627L)
class Timepart(val value: DateTime = Times.zero) extends ValueObject with Ordered[Timepart] {

  require(value != null)

  def this() = this(Times.zero)

  def this(hours: Long) =
    this(Times.zero.withTime(hours.toInt, 0, 0, 0))

  def this(hours: Long, minutes: Long) =
    this(Times.zero.withTime(hours.toInt, minutes.toInt, 0, 0))

  def this(hours: Long, minutes: Long, seconds: Long) =
    this(Times.zero.withTime(hours.toInt, minutes.toInt, seconds.toInt, 0))

  def this(hours: Long, minutes: Long, seconds: Long, millis: Long) =
    this(Times.zero.withTime(hours.toInt, minutes.toInt, seconds.toInt, millis.toInt))

  def this(duration: Duration) = this(Times.zero.plus(duration))


  def hour: Long = value.getHourOfDay

  def minute: Long = value.getMinuteOfHour

  def second: Long = value.getSecondOfMinute

  def millis: Long = value.getMillisOfSecond

  def totalHours: Double = totalMillis / MillisPerHour

  def totalMinutes: Double = totalMillis / MillisPerMinute

  def totalSeconds: Double = totalMillis / MillisPerSecond

  def totalMillis: Long = value.getMillisOfDay

  def getDateTime(moment: DateTime): DateTime = moment.withTimeAtStartOfDay().plus(totalMillis)

  def compare(that: Timepart) = value.compareTo(that.value)

  override def hashCode = totalMillis.toInt

  override protected def buildStringHelper = {
    super.buildStringHelper
    .add("value", value)
  }
}

object Timepart {

  def now(): Timepart = new Timepart(Times.now)

  def apply(): Timepart = new Timepart(Times.zero)

  def apply(moment: DateTime): Timepart = new Timepart(Times.zero.withMillisOfDay(moment.getMillisOfDay))

  def apply(hourOfDay: Long, minuteOfHour: Long = 0, secondOfMinute: Long = 0, millisOfSecond: Long = 0): Timepart = {
    new Timepart(Times.zero.withTime(hourOfDay.toInt, minuteOfHour.toInt, secondOfMinute.toInt, millisOfSecond.toInt))
  }

  def apply(duration: Option[Duration]): Timepart = {
    new Timepart(Times.zero.plus(duration.getOrElse(Duration.ZERO)))
  }
}
