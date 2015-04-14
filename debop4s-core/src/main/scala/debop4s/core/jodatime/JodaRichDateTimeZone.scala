package debop4s.core.jodatime

import java.util.Locale

import org.joda.time.{DateTimeZone, ReadableInstant}

class JodaRichDateTimeZone(val self: DateTimeZone) extends AnyVal {

  def id: String = self.getID

  def name(instant: Long): String = self.getName(instant)

  def name(instant: Long, locale: Locale): String = self.getName(instant, locale)

  def offset(instant: Long): Int = self.getOffset(instant)

  def offset(instant: ReadableInstant): Int = self.getOffset(instant)

  def offsetFromLocal(instantLocal: Long) = self.getOffsetFromLocal(instantLocal)

  def standardOffset(instant: Long): Int = self.getStandardOffset(instant)

  def isStandardOffset(instant: Long) = self.isStandardOffset(instant)

  def nameKey(instant: Long): String = self.getNameKey(instant)

  def shortName(instant: Long): String = self.getShortName(instant)

}
