package debop4s.core.jodatime

import org.joda.time.LocalDate.Property
import org.joda.time._

class JodaRichLocalDate(val self: LocalDate) extends AnyVal with Ordered[LocalDate] {

  def -(period: ReadablePeriod): LocalDate = self.minus(period)

  def -(builder: DurationBuilder): LocalDate = self.minus(builder.underlying)

  def +(period: ReadablePeriod): LocalDate = self.plus(period)

  def +(builder: DurationBuilder): LocalDate = self.plus(builder.underlying)

  def day: Property = self.dayOfMonth()

  def week: Property = self.weekOfWeekyear()

  def weekyear: Property = self.weekyear()

  def month: Property = self.monthOfYear()

  def year: Property = self.yearOfCentury()

  def century: Property = self.centuryOfEra()

  def era: Property = self.era

  def withDay(day: Int): LocalDate = self.withDayOfMonth(day)

  def withWeek(week: Int): LocalDate = self.withWeekOfWeekyear(week)

  def withWeekyear(weekyear: Int): LocalDate = self.withWeekyear(weekyear)

  def withMonth(month: Int): LocalDate = self.withMonthOfYear(month)

  def withYear(year: Int): LocalDate = self.withYear(year)

  def withCentury(century: Int): LocalDate = self.withCenturyOfEra(century)

  def withEra(era: Int): LocalDate = self.withEra(era)

  def compare(that: LocalDate): Int = self.compareTo(that)

  def interval: Interval = self.toInterval

  def interval(zone: DateTimeZone): Interval = self.toInterval(zone)
}
