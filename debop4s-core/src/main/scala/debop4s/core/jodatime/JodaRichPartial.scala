package debop4s.core.jodatime

import org.joda.time.format.DateTimeFormatter
import org.joda.time.{Partial, ReadablePeriod}


class JodaRichPartial(val self: Partial) extends AnyVal {

  def formatter: DateTimeFormatter = self.getFormatter

  def -(period: ReadablePeriod): Partial = self.minus(period)

  def -(builder: DurationBuilder): Partial = self.minus(builder.underlying)

  def +(period: ReadablePeriod): Partial = self.plus(period)

  def +(builder: DurationBuilder): Partial = self.plus(builder.underlying)

}
