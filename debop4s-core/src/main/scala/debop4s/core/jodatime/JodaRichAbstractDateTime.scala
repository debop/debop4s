package debop4s.core.jodatime

import java.util.{Calendar, Locale}

import org.joda.time.base.AbstractDateTime
import org.joda.time.{Chronology, DateTimeZone}

class JodaRichAbstractDateTime(val self: AbstractDateTime) extends Ordered[AbstractDateTime] {

  def chronology: Chronology = self.getChronology

  def calendar(locale: Locale): Calendar = self.toCalendar(locale)

  def gregorianCalendar: Calendar = self.toGregorianCalendar

  def zone: DateTimeZone = self.getZone

  def compare(that: AbstractDateTime): Int = self.compareTo(that)
}
