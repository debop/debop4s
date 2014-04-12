package debop4s.core.time

import org.joda.time.{ReadableDuration, Duration}

/**
 * com.github.time.RichDuration
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 6. 오후 8:35
 */
class RichDuration(val self: Duration) extends AnyVal with Ordered[Duration] {

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
}
