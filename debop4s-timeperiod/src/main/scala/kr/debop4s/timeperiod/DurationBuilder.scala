package kr.debop4s.timeperiod

import org.joda.time._

private[timeperiod] object DurationBuilder {
  def apply(underlying: Period): DurationBuilder = new DurationBuilder(underlying)
}

sealed private[timeperiod] class DurationBuilder(val underlying: Period) {

  def -(that: DurationBuilder): DurationBuilder = DurationBuilder(this.underlying.minus(that.underlying))

  def +(that: DurationBuilder): DurationBuilder = DurationBuilder(this.underlying.plus(that.underlying))

  def ago: DateTime = StaticDateTime.now.minus(underlying)

  def later: DateTime = StaticDateTime.now.plus(underlying)

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
