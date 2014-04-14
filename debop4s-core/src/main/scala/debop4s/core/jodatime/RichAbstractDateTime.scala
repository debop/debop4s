package debop4s.core.jodatime

import java.util.{Locale, Calendar}
import org.joda.time.base.AbstractDateTime
import org.joda.time.{Chronology, DateTimeZone}

/**
 * com.github.time.RichAbstractDateTime
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 5. 오후 11:24
 */
class RichAbstractDateTime(val self: AbstractDateTime) extends AnyVal with Ordered[AbstractDateTime] {

    def chronology: Chronology = self.getChronology

    def calendar(locale: Locale): Calendar = self.toCalendar(locale)

    def gregorianCalendar: Calendar = self.toGregorianCalendar

    def zone: DateTimeZone = self.getZone

    def compare(that: AbstractDateTime): Int = self.compareTo(that)
}
