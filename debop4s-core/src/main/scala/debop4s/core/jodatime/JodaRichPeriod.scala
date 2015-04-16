package debop4s.core.jodatime

import org.joda.time._


class JodaRichPeriod(val self: Period) extends AnyVal {

  def millis = self.getMillis

  def seconds = self.getSeconds

  def minutes = self.getMinutes

  def hours = self.getHours

  def days = self.getDays

  def weeks = self.getWeeks

  def months = self.getMonths

  def years = self.getYears

  def -(period: ReadablePeriod): Period = self.minus(period)

  def +(period: ReadablePeriod): Period = self.plus(period)

  def ago: DateTime = JodaDateTime.now.minus(self)

  def later: DateTime = JodaDateTime.now.plus(self)

  def from(moment: DateTime): DateTime = moment.plus(self)

  def before(moment: DateTime): DateTime = moment.minus(self)

  def standardDuration: Duration = self.toStandardDuration
}