package debop4s.core.jodatime

import org.joda.time.{Instant, ReadableDuration}

class JodaRichInstant(val self: Instant) extends AnyVal with Ordered[Instant] {

  def -(duration: Long): Instant = self.minus(duration)

  def -(duration: ReadableDuration): Instant = self.minus(duration)

  def -(builder: DurationBuilder): Instant = self.minus(builder.underlying.toStandardDuration)

  def +(duration: Long): Instant = self.plus(duration)

  def +(duration: ReadableDuration): Instant = self.plus(duration)

  def +(builder: DurationBuilder): Instant = self.plus(builder.underlying.toStandardDuration)

  def compare(that: Instant): Int = self.compareTo(that)
}
