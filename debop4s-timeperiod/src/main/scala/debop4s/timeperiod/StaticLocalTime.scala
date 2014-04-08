package debop4s.timeperiod

import java.util.{Calendar, Date}
import org.joda.time._

object StaticLocalTime extends StaticLocalTime

trait StaticLocalTime {
  type Property = LocalTime.Property

  final val MIDNIGHT = LocalTime.MIDNIGHT
  final val Midnight = LocalTime.MIDNIGHT

  def fromCalendarFields(calendar: Calendar): LocalTime = org.joda.time.LocalTime.fromCalendarFields(calendar)

  def fromDateFields(date: Date) = org.joda.time.LocalTime.fromDateFields(date)

  def fromMillisOfDay(millis: Long) = org.joda.time.LocalTime.fromMillisOfDay(millis)

  def fromMillisOfDay(millis: Long, chrono: Chronology) = org.joda.time.LocalTime.fromMillisOfDay(millis, chrono)

  def now = new LocalTime

  def nextSecond = now + 1.second

  def nextMinute = now + 1.minute

  def nextHour = now + 1.hour

  def lastSecond = now - 1.second

  def lastMinute = now - 1.minute

  def lastHour = now - 1.hour
}
