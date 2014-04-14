package debop4s.core.jodatime

import java.util.Locale
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

/**
 * com.github.time.StaticDateTimeFormat
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 6. 오후 9:37
 */
object JodaDateTimeFormat extends JodaDateTimeFormat

trait JodaDateTimeFormat {

    //    @deprecated(message = "use StaticISODateTimeFormat")
    //    val jsonFormat: DateTimeFormatter = forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")

    def forPattern(pattern: String) = DateTimeFormat.forPattern(pattern)

    def forStyle(style: String) = DateTimeFormat.forStyle(style)

    def fullDate(): DateTimeFormatter = DateTimeFormat.fullDate()

    def fullDateTime(): DateTimeFormatter = DateTimeFormat.fullDateTime()

    def fullTime(): DateTimeFormatter = DateTimeFormat.fullTime()

    def longDate(): DateTimeFormatter = DateTimeFormat.longDate()

    def longDateTime(): DateTimeFormatter = DateTimeFormat.longDateTime()

    def longTime(): DateTimeFormatter = DateTimeFormat.longTime()

    def mediumDate(): DateTimeFormatter = DateTimeFormat.mediumDate()

    def mediumDateTime(): DateTimeFormatter = DateTimeFormat.mediumDateTime()

    def mediumTime(): DateTimeFormatter = DateTimeFormat.mediumTime()

    def shortDate(): DateTimeFormatter = DateTimeFormat.shortDate()

    def shortDateTime(): DateTimeFormatter = DateTimeFormat.shortDateTime()

    def shortTime(): DateTimeFormatter = DateTimeFormat.shortTime()

    def patternForStyle(style: String, locale: Locale) = DateTimeFormat.patternForStyle(style, locale)
}

