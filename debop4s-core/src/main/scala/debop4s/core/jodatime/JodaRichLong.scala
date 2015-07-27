package debop4s.core.jodatime

import java.sql.Timestamp

import org.joda.time.{DateTime, Duration, Period}

class JodaRichLong(val self: Long) extends AnyVal {

  def toDateTime: DateTime = new DateTime(self)

  def toDuration: Duration = new Duration(self)

  def toTimestamp: Timestamp = new Timestamp(self)

  def milli: DurationBuilder = millis
  def millis: DurationBuilder = new DurationBuilder(Period.millis(self.toInt))

  def second: DurationBuilder = seconds
  def seconds: DurationBuilder = new DurationBuilder(Period.seconds(self.toInt))

  def minute: DurationBuilder = minutes
  def minutes: DurationBuilder = new DurationBuilder(Period.minutes(self.toInt))

  def hour: DurationBuilder = hours
  def hours: DurationBuilder = new DurationBuilder(Period.hours(self.toInt))

  def day: Period = days
  def days: Period = Period.days(self.toInt)

  def week: Period = weeks
  def weeks: Period = Period.weeks(self.toInt)

  def month: Period = months
  def months: Period = Period.months(self.toInt)

  def year: Period = years
  def years: Period = Period.years(self.toInt)
}
