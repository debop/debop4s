package debop4s.core.jodatime

import org.joda.time.Period

class JodaRichInt(val self: Int) extends AnyVal {

  def milli = millis
  def millis = new DurationBuilder(Period.millis(self))

  def second = seconds
  def seconds = new DurationBuilder(Period.seconds(self))

  def minute = minutes
  def minutes = new DurationBuilder(Period.minutes(self))

  def hour = hours
  def hours = new DurationBuilder(Period.hours(self))

  def day = days
  def days = Period.days(self)

  def week = weeks
  def weeks = Period.weeks(self)

  def month = months
  def months = Period.months(self)

  def year = years
  def years = Period.years(self)

}
