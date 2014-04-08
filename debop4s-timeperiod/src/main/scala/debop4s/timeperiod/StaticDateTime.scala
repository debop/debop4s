package debop4s.timeperiod

import org.joda.time._
import org.joda.time.format.DateTimeFormatter

object StaticDateTime extends StaticDateTime

trait StaticDateTime {

  type Property = DateTime.Property

  def now = DateTime.now

  def nextSecond: DateTime = now + 1.second

  def nextMinute: DateTime = now + 1.minute

  def nextHour: DateTime = now + 1.hour

  def nextDay: DateTime = now + 1.day

  def tomorrow: DateTime = now + 1.day

  def nextWeek: DateTime = now + 1.week

  def nextMonth: DateTime = now + 1.month

  def nextYear: DateTime = now + 1.year

  def lastSecond: DateTime = now - 1.second

  def lastMinute: DateTime = now - 1.minute

  def lastHour: DateTime = now - 1.hour

  def lastDay: DateTime = now - 1.day

  def yesterday: DateTime = now - 1.day

  def lastweek: DateTime = now - 1.week

  def lastMonth: DateTime = now - 1.month

  def lastYear: DateTime = now - 1.year

  def parse(s: String, fmt: DateTimeFormatter): DateTime = DateTime.parse(s, fmt)

  def fromJsonString(x: String): DateTime = new DateTime(x)
}
