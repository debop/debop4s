package kr.debop4s.time

import java.util.Locale
import org.joda.time.Partial

/**
 * kr.debop4s.time.RichPartialProperty
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 6. 오후 8:53
 */
class RichPartialProperty(val self: Partial.Property) extends AnyVal {

    def partial: Partial = self.getPartial

    def apply(value: Int): Partial = self.setCopy(value)
    def apply(text: String): Partial = self.setCopy(text)
    def apply(text: String, locale: Locale): Partial = self.setCopy(text, locale)

}
