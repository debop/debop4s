package debop4s.core.jodatime

import java.util.{Calendar, Date}

import debop4s.core.conversions.jodatime._
import org.joda.time.LocalDate

object JodaLocalDate extends JodaLocalDate

trait JodaLocalDate {

  type Property = LocalDate.Property

  def fromCalendarFields(calendar: Calendar): LocalDate = LocalDate.fromCalendarFields(calendar)
  def fromDateFields(date: Date): LocalDate = LocalDate.fromDateFields(date)

  def now: LocalDate = new LocalDate
  def today: LocalDate = new LocalDate
  def nextDay: LocalDate = now + 1.day
  def tomorrow: LocalDate = now + 1.day
  def nextWeek: LocalDate = now + 1.week
  def nextMonth: LocalDate = now + 1.month
  def nextYear: LocalDate = now + 1.year
  def lastDay: LocalDate = now - 1.day
  def yesterday: LocalDate = now - 1.day
  def lastWeek: LocalDate = now - 1.week
  def lastMonth: LocalDate = now - 1.month
  def lastYear: LocalDate = now - 1.year
}
