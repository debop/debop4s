package debop4s.core.jodatime

import org.joda.time.LocalDate.Property
import org.joda.time._

/**
 * com.github.time.RichLocalDate
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 6. 오전 12:30
 */
class RichLocalDate(val self: LocalDate) extends AnyVal with Ordered[LocalDate] {

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

  def withDay(day: Int) = self.withDayOfMonth(day)

  def withWeek(week: Int) = self.withWeekOfWeekyear(week)

  def withWeekyear(weekyear: Int) = self.withWeekyear(weekyear)

  def withMonth(month: Int) = self.withMonthOfYear(month)

  def withYear(year: Int) = self.withYear(year)

  def withCentury(century: Int) = self.withCenturyOfEra(century)

  def withEra(era: Int) = self.withEra(era)

  def compare(that: LocalDate) = self.compareTo(that)

  def interval = self.toInterval

  def interval(zone: DateTimeZone) = self.toInterval(zone)
}
