package kr.debop4s.time

import java.util.Locale
import org.joda.time.LocalDateTime

/**
 * kr.debop4s.time.RichLocalDateTimeProperty
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 6. 오후 8:50
 */
class RichLocalDateTimeProperty(val self: LocalDateTime.Property) extends AnyVal {

    def localDateTime: LocalDateTime = self.getLocalDateTime
    def roundFloor: LocalDateTime = self.roundFloorCopy
    def roundCeiling: LocalDateTime = self.roundCeilingCopy
    def roundDown: LocalDateTime = self.roundFloorCopy
    def roundUp: LocalDateTime = self.roundCeilingCopy
    def round: LocalDateTime = self.roundHalfEvenCopy

    def apply(value: Int): LocalDateTime = self.setCopy(value)
    def apply(text: String): LocalDateTime = self.setCopy(text)
    def apply(text: String, locale: Locale): LocalDateTime = self.setCopy(text, locale)
}
