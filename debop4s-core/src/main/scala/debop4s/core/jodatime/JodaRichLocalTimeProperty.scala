package debop4s.core.jodatime

import java.util.Locale

import org.joda.time.LocalTime


class JodaRichLocalTimeProperty(val self: LocalTime.Property) extends AnyVal {

  def localTime: LocalTime = self.getLocalTime

  def roundCeiling: LocalTime = self.roundCeilingCopy()

  def roundFloor: LocalTime = self.roundFloorCopy()

  def roundDown: LocalTime = self.roundFloorCopy()

  def roundUp: LocalTime = self.roundCeilingCopy()

  def round: LocalTime = self.roundHalfEvenCopy()

  def apply(value: Int): LocalTime = self.setCopy(value)

  def apply(text: String): LocalTime = self.setCopy(text)

  def apply(text: String, locale: Locale): LocalTime = self.setCopy(text, locale)
}
