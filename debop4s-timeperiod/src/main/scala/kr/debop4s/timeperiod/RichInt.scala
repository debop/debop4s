package kr.debop4s.timeperiod

import org.joda.time.Period

class RichInt(val self: Int) extends AnyVal {

  def millis: DurationBuilder = DurationBuilder(Period.millis(self))

  def seconds: DurationBuilder = DurationBuilder(Period.seconds(self))

  def minutes: DurationBuilder = DurationBuilder(Period.minutes(self))

  def hours: DurationBuilder = DurationBuilder(Period.hours(self))

  def days: Period = Period.days(self)

  def weeks: Period = Period.weeks(self)

  def months: Period = Period.months(self)

  def years: Period = Period.years(self)

  def milli: DurationBuilder = DurationBuilder(Period.millis(self))

  def second: DurationBuilder = DurationBuilder(Period.seconds(self))

  def minute: DurationBuilder = DurationBuilder(Period.minutes(self))

  def hour: DurationBuilder = DurationBuilder(Period.hours(self))

  def day: Period = Period.days(self)

  def week: Period = Period.weeks(self)

  def month: Period = Period.months(self)

  def year: Period = Period.years(self)
}
