package debop4s.core.jodatime

import java.util.Locale

import org.joda.time.LocalDate

class JodaRichLocalDateProperty(val self: LocalDate.Property) extends AnyVal {

  def localDate: LocalDate = self.getLocalDate

  def roundCeiling: LocalDate = self.roundCeilingCopy()

  def roundFloor: LocalDate = self.roundFloorCopy()

  def roundDown: LocalDate = self.roundFloorCopy()

  def roundUp: LocalDate = self.roundCeilingCopy()

  def round: LocalDate = self.roundHalfEvenCopy()

  def apply(value: Int): LocalDate = self.setCopy(value)

  def apply(text: String): LocalDate = self.setCopy(text)

  def apply(text: String, locale: Locale): LocalDate = self.setCopy(text, locale)

}
