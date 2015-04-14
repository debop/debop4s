package debop4s.core.jodatime

import java.util.Locale

import org.joda.time.Partial


class JodaRichPartialProperty(val self: Partial.Property) extends AnyVal {

  def partial: Partial = self.getPartial

  def apply(value: Int): Partial = self.setCopy(value)

  def apply(text: String): Partial = self.setCopy(text)

  def apply(text: String, locale: Locale): Partial = self.setCopy(text, locale)

}
