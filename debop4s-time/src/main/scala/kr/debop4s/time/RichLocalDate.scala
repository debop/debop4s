package kr.debop4s.time

import org.joda.time.LocalDate.Property
import org.joda.time._

/**
 * kr.debop4s.time.RichLocalDate
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 6. 오전 12:30
 */
class RichLocalDate(val self: LocalDate) extends AnyVal with Ordered[RichLocalDate] {

    def -(period: ReadablePeriod): LocalDate = self.minus(period)
    def -(builder: DurationBuilder): LocalDate = self.minus(builder.underlying)
    def +(period: ReadablePeriod): LocalDate = self.plus(period)
    def +(builder: DurationBuilder): LocalDate = self.plus(builder.underlying)

    def day: Property = self.dayOfMonth()
    def week: Property = self.weekOfWeekyear()
    def month: Property = self.monthOfYear()
    def year: Property = self.yearOfCentury()
    def century: Property = self.centuryOfEra()
    def era: Property = self.era

    def withDay(day: Int) = self.withDayOfMonth(day)
    def withWeek(week: Int) = self.withWeekOfWeekyear(week)
    def withMonth(month: Int) = self.withMonthOfYear(month)
    def withYear(year: Int) = self.withYear(year)
    def withCentury(century: Int) = self.withCenturyOfEra(century)
    def withEra(era: Int) = self.withEra(era)

    def compare(that: RichLocalDate) = self.compareTo(that.self)

    def interval = self.toInterval
    def interval(zone: DateTimeZone) = self.toInterval(zone)
}
