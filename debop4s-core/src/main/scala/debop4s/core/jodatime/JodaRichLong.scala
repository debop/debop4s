package debop4s.core.jodatime

import java.sql.Timestamp

import org.joda.time.{DateTime, Duration, Period}

class JodaRichLong(val self: Long) extends AnyVal {

  def toDateTime = new DateTime(self.toInt)

  def toDuration = new Duration(self.toInt)

  def toTimestamp = new Timestamp(self.toInt)

  def milli = millis
  def millis = new DurationBuilder(Period.millis(self.toInt))

  def second = seconds
  def seconds = new DurationBuilder(Period.seconds(self.toInt))

  def minute = minutes
  def minutes = new DurationBuilder(Period.minutes(self.toInt))

  def hour = hours
  def hours = new DurationBuilder(Period.hours(self.toInt))

  def day = days
  def days = Period.days(self.toInt)

  def week = weeks
  def weeks = Period.weeks(self.toInt)

  def month = months
  def months = Period.months(self.toInt)

  def year = years
  def years = Period.years(self.toInt)
}
