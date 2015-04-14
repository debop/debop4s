package debop4s.core.jodatime

import java.util.Locale

import org.joda.time.DateTime

class JodaRichDateTimeProperty(val self: DateTime.Property) extends AnyVal {

  def dateTime: DateTime = self.getDateTime

  def roundCeiling: DateTime = self.roundCeilingCopy()

  def roundFloor: DateTime = self.roundFloorCopy()

  def roundDown: DateTime = self.roundFloorCopy()

  def roundUp: DateTime = self.roundCeilingCopy()

  def round: DateTime = self.roundHalfEvenCopy()

  def apply(value: Int): DateTime = self.setCopy(value)

  def apply(text: String): DateTime = self.setCopy(text)

  def apply(text: String, locale: Locale): DateTime = self.setCopy(text, locale)
}
