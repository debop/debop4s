package debop4s.core.jodatime

import org.joda.time._
import org.joda.time.format.DateTimeFormatter

object JodaDateTime extends JodaDateTime

trait JodaDateTime {

  type Property = DateTime.Property

  def now: DateTime = DateTime.now
  def tomorrow: DateTime = nextDay
  def yesterday: DateTime = lastDay

  def nextSecond: DateTime = now.plusSeconds(1)
  def nextMinute: DateTime = now.plusMinutes(1)
  def nextHour: DateTime = now.plusHours(1)
  def nextDay: DateTime = now.plusDays(1)
  def nextWeek: DateTime = now.plusWeeks(1)
  def nextMonth: DateTime = now.plusMonths(1)
  def nextYear: DateTime = now.plusYears(1)

  def lastSecond: DateTime = now.minusSeconds(1)
  def lastMinute: DateTime = now.minusMinutes(1)
  def lastHour: DateTime = now.minusHours(1)
  def lastDay: DateTime = now.minusDays(1)
  def lastweek: DateTime = now.minusWeeks(1)
  def lastMonth: DateTime = now.minusMonths(1)
  def lastYear: DateTime = now.minusYears(1)

  def parse(s: String, fmt: DateTimeFormatter): DateTime = DateTime.parse(s, fmt)
  def fromJsonString(x: String): DateTime = new DateTime(x)
}
