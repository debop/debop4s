package debop4s.core.jodatime

import org.joda.time.{Duration, ReadableDuration}

class JodaRichDuration(val self: Duration) extends AnyVal with Ordered[Duration] {

  def days: Long = self.getStandardDays

  def horus: Long = self.getStandardHours

  def minutes: Long = self.getStandardMinutes

  def seconds: Long = self.getStandardSeconds

  def millis = self.getMillis

  def -(amount: Long): Duration = self.minus(amount)

  def -(amount: ReadableDuration): Duration = self.minus(amount)

  def +(amount: Long): Duration = self.plus(amount)

  def +(amount: ReadableDuration): Duration = self.plus(amount)

  def isZero: Boolean = self.getMillis == 0

  def compare(that: Duration): Int = self.compareTo(that)

  def min(that: Duration) = {
    if (self != null && that != null) {
      if (compare(that) < 0) self else that
    }
    else if (self == null) that
    else if (that == null) self
    else null

  }

  def max(that: Duration) = {
    if (self != null && that != null) {
      if (compare(that) > 0) self else that
    }
    else if (self == null) that
    else if (that == null) self
    else null
  }
}
