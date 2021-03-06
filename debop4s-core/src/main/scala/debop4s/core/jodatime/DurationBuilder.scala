package debop4s.core.jodatime

import org.joda.time._

object DurationBuilder {
  def apply(underlying: Period): DurationBuilder = new DurationBuilder(underlying)
}


class DurationBuilder(val underlying: Period) {

  def -(that: DurationBuilder): DurationBuilder = new DurationBuilder(this.underlying.minus(that.underlying))
  def +(that: DurationBuilder): DurationBuilder = new DurationBuilder(this.underlying.plus(that.underlying))

  def ago: DateTime = JodaDateTime.now.minus(underlying)
  def later: DateTime = JodaDateTime.now.plus(underlying)
  def from(moment: DateTime): DateTime = moment.plus(underlying)
  def before(moment: DateTime): DateTime = moment.minus(underlying)

  def standardDuration: Duration = underlying.toStandardDuration
  def toDuration: Duration = underlying.toStandardDuration

  def toPeriod: Period = underlying
  def -(period: ReadablePeriod): Period = underlying.minus(period)
  def +(period: ReadablePeriod): Period = underlying.plus(period)

  def millis: Long = standardDuration.getMillis
  def seconds: Long = standardDuration.getStandardSeconds

  def -(amount: Long): Duration = standardDuration.minus(amount)
  def -(amount: ReadableDuration): Duration = standardDuration.minus(amount)

  def +(amount: Long): Duration = standardDuration.plus(amount)
  def +(amount: ReadableDuration): Duration = standardDuration.plus(amount)
}




