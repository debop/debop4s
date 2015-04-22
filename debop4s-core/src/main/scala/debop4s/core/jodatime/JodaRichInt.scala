package debop4s.core.jodatime

import org.joda.time.Period

class JodaRichInt(val self: Int) extends AnyVal {

  def milli: DurationBuilder = millis
  def millis: DurationBuilder = new DurationBuilder(Period.millis(self))

  def second: DurationBuilder = seconds
  def seconds: DurationBuilder = new DurationBuilder(Period.seconds(self))

  def minute: DurationBuilder = minutes
  def minutes: DurationBuilder = new DurationBuilder(Period.minutes(self))

  def hour: DurationBuilder = hours
  def hours: DurationBuilder = new DurationBuilder(Period.hours(self))

  def day: Period = days
  def days: Period = Period.days(self)

  def week: Period = weeks
  def weeks: Period = Period.weeks(self)

  def month: Period = months
  def months: Period = Period.months(self)

  def year: Period = years
  def years: Period = Period.years(self)

}
