package debop4s.core.jodatime

import java.util.{ Calendar, Date }
import org.joda.time.LocalDateTime

object JodaLocalDateTime extends JodaLocalDateTime

trait JodaLocalDateTime {
  type Property = LocalDateTime.Property

  def fromCalendarFields(calendar: Calendar) = LocalDateTime.fromCalendarFields(calendar)

  def fromDateFields(date: Date) = LocalDateTime.fromDateFields(date)

  def now = new LocalDateTime

  def nextSecond = now + 1.second

  def nextMinute = now + 1.minute

  def nextHour = now + 1.hour

  def nextDay = now + 1.day

  def tomorrow = now + 1.day

  def nextWeek = now + 1.week

  def nextMonth = now + 1.month

  def nextYear = now + 1.year

  def lastSecond = now - 1.second

  def lastMinute = now - 1.minute

  def lastHour = now - 1.hour

  def lastDay = now - 1.day

  def yesterday = now - 1.day

  def lastWeek = now - 1.week

  def lastMonth = now - 1.month

  def lastYear = now - 1.year
}
