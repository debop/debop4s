package debop4s.core.jodatime

import org.joda.time.{Chronology, DateTimeZone}

class JodaRichChronology(val self: Chronology) extends AnyVal {

  def zone: Option[DateTimeZone] = nullCheck(self.getZone)

  private def nullCheck[@miniboxed T](v: T): Option[T] = if (v == null) None else Some(v)
}
