package debop4s.core.time

import org.joda.time._
import org.joda.time.format.DateTimeFormatter

object JodaDateTime extends JodaDateTime

trait JodaDateTime {

  type Property = DateTime.Property

  def now = DateTime.now

  def nextSecond = now.plusSeconds(1)
  def nextMinute = now.plusMinutes(1)
  def nextHour = now.plusHours(1)
  def nextDay = now.plusDays(1)
  def tomorrow = nextDay
  def nextWeek = now.plusWeeks(1)
  def nextMonth = now.plusMonths(1)
  def nextYear = now.plusYears(1)

  def lastSecond = now.minusSeconds(1)
  def lastMinute = now.minusMinutes(1)
  def lastHour = now.minusHours(1)
  def lastDay = now.minusDays(1)
  def yesterday = lastDay
  def lastweek = now.minusWeeks(1)
  def lastMonth = now.minusMonths(1)
  def lastYear = now.minusYears(1)

  def parse(s: String, fmt: DateTimeFormatter) = DateTime.parse(s, fmt)
  def fromJsonString(x: String) = new DateTime(x)
}
