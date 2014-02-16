package kr.debop4s.timeperiod

import org.joda.time._


/**
 * kr.debop4s.time.RichReadableInstant
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 6. 오후 9:02
 */
class RichReadableInstant(val self: ReadableInstant) extends AnyVal with Ordered[ReadableInstant] {

  def chronology: Chronology = self.getChronology

  def millis: Long = self.getMillis

  def zone: DateTimeZone = self.getZone

  def to(other: ReadableInstant): Interval = new Interval(self, other)

  def instant: Instant = self.toInstant

  def compare(that: ReadableInstant) = self.compareTo(that)
}
