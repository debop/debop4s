package debop4s.core.jodatime

import org.joda.time.ReadableDuration

class JodaRichReadableDuration(val self: ReadableDuration) extends AnyVal with Ordered[ReadableDuration] {

  def millis: Long = self.getMillis

  def compare(that: ReadableDuration): Int = self.compareTo(that)
}
