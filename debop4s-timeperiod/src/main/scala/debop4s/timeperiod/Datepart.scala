package debop4s.timeperiod

import debop4s.core.ValueObject
import debop4s.core.utils.Hashs
import debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

@SerialVersionUID(-2730296141281632596L)
class Datepart(val value: DateTime = Times.today) extends ValueObject with Ordered[Datepart] {

  // def this() = this(Times.today)

  def year = value.getYear
  def monthOfYear = value.getMonthOfYear
  def dayOfMonth = value.getDayOfMonth

  def toDateTime(time: Timepart): DateTime = {
    if (time != null) value.plus(time.value.getMillis)
    else value
  }

  def toDateTime(hourOfDay: Int, minuteOfHour: Int = 0, secondOfMinute: Int = 0, millisOfSecond: Int = 0): DateTime =
    toDateTime(Timepart(hourOfDay, minuteOfHour, secondOfMinute, millisOfSecond))

  def toDateTime(millis: Long): DateTime =
    toDateTime(Timepart(new DateTime(millis)))

  def compare(that: Datepart) = value.compareTo(that.value)

  override def hashCode = Hashs.compute(value)

  override protected def buildStringHelper =
    super.buildStringHelper
    .add("value", value)
}

object Datepart {

  def today(): Datepart = apply()

  def apply(): Datepart = new Datepart(Times.today)

  def apply(moment: DateTime): Datepart =
    new Datepart(moment.withTimeAtStartOfDay())

  def apply(year: Int, monthOfYear: Int = 1, dayOfMonth: Int = 1): Datepart =
    new Datepart(Times.asDate(year, monthOfYear, dayOfMonth))

}

