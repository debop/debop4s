package debop4s.core.jodatime

import org.joda.time.{Chronology, ReadablePartial}


class JodaRichReadablePartial(val self: ReadablePartial) extends AnyVal {

  def chronology: Chronology = self.getChronology

  def value(index: Int): Int = self.getValue(index)
}
