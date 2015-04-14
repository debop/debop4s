package debop4s.core.jodatime

import java.util.{Calendar, Date}
import debop4s.core.conversions.jodatime._
import org.joda.time.LocalDate

object JodaLocalDate extends JodaLocalDate

trait JodaLocalDate {

  type Property = LocalDate.Property

  def fromCalendarFields(calendar: Calendar) = LocalDate.fromCalendarFields(calendar)

  def fromDateFields(date: Date) = LocalDate.fromDateFields(date)

  def now = new LocalDate

  def today = new LocalDate

  def nextDay = now + 1.day

  def tomorrow = now + 1.day

  def nextWeek = now + 1.week

  def nextMonth = now + 1.month

  def nextYear = now + 1.year

  def lastDay = now - 1.day

  def yesterday = now - 1.day

  def lastWeek = now - 1.week

  def lastMonth = now - 1.month

  def lastYear = now - 1.year
}
