package debop4s.core.jodatime

import java.util.{Calendar, Date}

import debop4s.core.conversions.jodatime._
import org.joda.time.LocalDateTime

object JodaLocalDateTime extends JodaLocalDateTime

trait JodaLocalDateTime {

  type Property = LocalDateTime.Property

  def fromCalendarFields(calendar: Calendar): LocalDateTime = LocalDateTime.fromCalendarFields(calendar)
  def fromDateFields(date: Date): LocalDateTime = LocalDateTime.fromDateFields(date)
  def now: LocalDateTime = new LocalDateTime
  def nextSecond: LocalDateTime = now + 1.second
  def nextMinute: LocalDateTime = now + 1.minute
  def nextHour: LocalDateTime = now + 1.hour
  def nextDay: LocalDateTime = now + 1.day
  def tomorrow: LocalDateTime = now + 1.day
  def nextWeek: LocalDateTime = now + 1.week
  def nextMonth: LocalDateTime = now + 1.month
  def nextYear: LocalDateTime = now + 1.year
  def lastSecond: LocalDateTime = now - 1.second
  def lastMinute: LocalDateTime = now - 1.minute
  def lastHour: LocalDateTime = now - 1.hour
  def lastDay: LocalDateTime = now - 1.day
  def yesterday: LocalDateTime = now - 1.day
  def lastWeek: LocalDateTime = now - 1.week
  def lastMonth: LocalDateTime = now - 1.month
  def lastYear: LocalDateTime = now - 1.year
}
