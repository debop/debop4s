package debop4s.core.jodatime

import java.util
import org.joda.time.DateTimeZone
import org.joda.time.tz.{ Provider, NameProvider }

/**
 * com.github.time.StaticDateTimeZone
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 6. 오후 9:44
 */

object JodaDateTimeZone extends JodaDateTimeZone

trait JodaDateTimeZone {

  lazy val UTC = DateTimeZone.UTC

  def forID(id: String): DateTimeZone = DateTimeZone.forID(id)

  def forOffsetHours(hours: Int): DateTimeZone = DateTimeZone.forOffsetHours(hours)

  def forOffsetHoursMinutes(hours: Int, minutes: Int): DateTimeZone = DateTimeZone.forOffsetHoursMinutes(hours, minutes)

  def forOffsetMillis(millis: Int): DateTimeZone = DateTimeZone.forOffsetMillis(millis)

  def forTimeZone(zone: java.util.TimeZone): DateTimeZone = DateTimeZone.forTimeZone(zone)

  def getAvailableIDs: util.Set[String] = DateTimeZone.getAvailableIDs

  def getDefault: DateTimeZone = DateTimeZone.getDefault

  def getNameProvider: NameProvider = DateTimeZone.getNameProvider

  def getProvider: Provider = DateTimeZone.getProvider

  def setDefault(zone: DateTimeZone): Unit = DateTimeZone.setDefault(zone)

  def setNameProvider(nameProvider: NameProvider) = DateTimeZone.setNameProvider(nameProvider)

  def setProvider(provider: Provider) = DateTimeZone.setProvider(provider)
}
