package debop4s.core.jodatime

import org.joda.time._

class JodaRichReadableInstant(val self: ReadableInstant) extends AnyVal with Ordered[ReadableInstant] {

  def chronology: Chronology = self.getChronology

  def millis: Long = self.getMillis

  def zone: DateTimeZone = self.getZone

  def to(other: ReadableInstant): Interval = new Interval(self, other)

  def instant: Instant = self.toInstant

  def compare(that: ReadableInstant): Int = self.compareTo(that)
}
