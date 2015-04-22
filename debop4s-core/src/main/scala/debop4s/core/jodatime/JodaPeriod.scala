package debop4s.core.jodatime

import org.joda.time.{Period, ReadablePartial}


object JodaPeriod extends JodaPeriod {
  def empty = new Period()

  def ∅ = empty
}

trait JodaPeriod {

  def days(days: Int): Period = Period.days(days)

  def fieldDifference(start: ReadablePartial, end: ReadablePartial): Period = Period.fieldDifference(start, end)

  def hours(hours: Int): Period = Period.hours(hours)

  def millis(millis: Int): Period = Period.millis(millis)

  def minutes(minutes: Int): Period = Period.minutes(minutes)

  def months(months: Int): Period = Period.months(months)

  def seconds(seconds: Int): Period = Period.seconds(seconds)

  def weeks(weeks: Int): Period = Period.weeks(weeks)

  def years(years: Int): Period = Period.years(years)
}
