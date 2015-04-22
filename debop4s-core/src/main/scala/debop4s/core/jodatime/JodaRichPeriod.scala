package debop4s.core.jodatime

import org.joda.time._


class JodaRichPeriod(val self: Period) extends AnyVal {

  def millis: Int = self.getMillis

  def seconds: Int = self.getSeconds

  def minutes: Int = self.getMinutes

  def hours: Int = self.getHours

  def days: Int = self.getDays

  def weeks: Int = self.getWeeks

  def months: Int = self.getMonths

  def years: Int = self.getYears

  def -(period: ReadablePeriod): Period = self.minus(period)

  def +(period: ReadablePeriod): Period = self.plus(period)

  def ago: DateTime = JodaDateTime.now.minus(self)

  def later: DateTime = JodaDateTime.now.plus(self)

  def from(moment: DateTime): DateTime = moment.plus(self)

  def before(moment: DateTime): DateTime = moment.minus(self)

  def standardDuration: Duration = self.toStandardDuration
}
