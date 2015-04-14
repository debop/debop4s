package debop4s.core.jodatime

import org.joda.time._

class JodaRichLocalTime(val self: LocalTime) extends AnyVal with Ordered[LocalTime] {

  def -(period: ReadablePeriod): LocalTime = self.minus(period)

  def -(builder: DurationBuilder): LocalTime = self.minus(builder.underlying)

  def +(period: ReadablePeriod): LocalTime = self.plus(period)

  def +(builder: DurationBuilder): LocalTime = self.plus(builder.underlying)

  def millis: LocalTime.Property = self.millisOfSecond()

  def second: LocalTime.Property = self.secondOfMinute()

  def minute: LocalTime.Property = self.minuteOfHour()

  def hour: LocalTime.Property = self.hourOfDay()

  def withMillis(millis: Int): LocalTime = self.withMillisOfSecond(millis)

  def withSecond(second: Int): LocalTime = self.withSecondOfMinute(second)

  def withMinute(minute: Int): LocalTime = self.withMinuteOfHour(minute)

  def withHour(hour: Int): LocalTime = self.withHourOfDay(hour)

  def compare(that: LocalTime) = self.compareTo(that)
}
