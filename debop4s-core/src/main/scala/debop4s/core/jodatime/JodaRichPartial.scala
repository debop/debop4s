package debop4s.core.jodatime

import org.joda.time.{Partial, ReadablePeriod}


class JodaRichPartial(val self: Partial) extends AnyVal {

  def formatter = self.getFormatter

  def -(period: ReadablePeriod) = self.minus(period)

  def -(builder: DurationBuilder) = self.minus(builder.underlying)

  def +(period: ReadablePeriod) = self.plus(period)

  def +(builder: DurationBuilder) = self.plus(builder.underlying)

}
