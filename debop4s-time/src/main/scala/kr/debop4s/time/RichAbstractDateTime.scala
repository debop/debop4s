package kr.debop4s.time

import java.util.{Locale, Calendar}
import org.joda.time.base.AbstractDateTime

/**
 * kr.debop4s.time.RichAbstractDateTime
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 5. 오후 11:24
 */
class RichAbstractDateTime(val self: AbstractDateTime) extends AnyVal {

    def calendar(locale: Locale): Calendar = self.toCalendar(locale)
    def gregorianCalendar: Calendar = self.toGregorianCalendar
}
