package debop4s.core.time

import java.util.Locale
import org.joda.time.LocalDate

/**
 * com.github.time.RichLocalDateProperty
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 6. 오후 8:47
 */
class RichLocalDateProperty(val self: LocalDate.Property) extends AnyVal {

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
