package debop4s.core.jodatime

import java.util.Locale
import org.joda.time.format.{DateTimePrinter, DateTimeParser, DateTimeFormatter}
import org.joda.time.{DateTimeZone, DateTime, Chronology}

/**
 * com.github.time.RichDateTimeFormatter
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 6. 오후 8:23
 */
class RichDateTimeFormatter(val self: DateTimeFormatter) extends AnyVal {

    def chronology: Chronology = self.getChronology

    def locale: Locale = self.getLocale

    def parser: DateTimeParser = self.getParser

    def pivotYear: Int = self.getPivotYear

    def printer: DateTimePrinter = self.getPrinter

    def zone: DateTimeZone = self.getZone

    def parseOption(text: String): Option[DateTime] = try {
        Some(self.parseDateTime(text))
    } catch {
        case _: UnsupportedOperationException => None
        case _: IllegalArgumentException => None
    }

    def defaultYear: Int = self.getDefaultYear
}
