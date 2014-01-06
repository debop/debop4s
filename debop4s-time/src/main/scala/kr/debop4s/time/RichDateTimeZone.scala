package kr.debop4s.time

import java.util.Locale
import org.joda.time.{ReadableInstant, DateTimeZone}

/**
 * kr.debop4s.time.RichDateTimeZone
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 6. 오후 8:30
 */
class RichDateTimeZone(val self: DateTimeZone) extends AnyVal {

    def id: String = self.getID

    def name(instant: Long): String = self.getName(instant)
    def name(instant: Long, locale: Locale): String = self.getName(instant, locale)
    
    def offset(instant:Long): Int = self.getOffset(instant)
    def offset(instant: ReadableInstant): Int = self.getOffset(instant)

    def standardOffset(instant:Long): Int = self.getStandardOffset(instant)
}
