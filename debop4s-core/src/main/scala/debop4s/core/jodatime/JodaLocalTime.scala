package debop4s.core.jodatime

import java.util.{Calendar, Date}

import debop4s.core.conversions.jodatime._
import org.joda.time.{Chronology, LocalTime}

object JodaLocalTime extends JodaLocalTime

trait JodaLocalTime {
  type Property = LocalTime.Property

  final val MIDNIGHT = LocalTime.MIDNIGHT
  final val Midnight = LocalTime.MIDNIGHT

  def fromCalendarFields(calendar: Calendar): LocalTime = LocalTime.fromCalendarFields(calendar)
  def fromDateFields(date: Date): LocalTime = LocalTime.fromDateFields(date)
  def fromMillisOfDay(millis: Long): LocalTime = LocalTime.fromMillisOfDay(millis)
  def fromMillisOfDay(millis: Long, chrono: Chronology): LocalTime = LocalTime.fromMillisOfDay(millis, chrono)

  def now: LocalTime = new LocalTime
  def nextSecond: LocalTime = now + 1.second
  def nextMinute: LocalTime = now + 1.minute
  def nextHour: LocalTime = now + 1.hour
  def lastSecond: LocalTime = now - 1.second
  def lastMinute: LocalTime = now - 1.minute
  def lastHour: LocalTime = now - 1.hour
}
