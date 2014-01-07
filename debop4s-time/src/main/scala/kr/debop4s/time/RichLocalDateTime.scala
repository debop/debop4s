package kr.debop4s.time

import org.joda.time.LocalDateTime.Property
import org.joda.time.{ReadablePeriod, ReadableDuration, LocalDateTime}

/**
 * kr.debop4s.time.RichLocalDateTime
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 6. 오후 8:48
 */
class RichLocalDateTime(val self: LocalDateTime) extends AnyVal with Ordered[LocalDateTime] {

    def -(duration: ReadableDuration): LocalDateTime = self.minus(duration)
    def -(period: ReadablePeriod): LocalDateTime = self.minus(period)
    def -(builder: DurationBuilder): LocalDateTime = self.minus(builder.underlying)

    def +(duration: ReadableDuration): LocalDateTime = self.plus(duration)
    def +(period: ReadablePeriod): LocalDateTime = self.plus(period)
    def +(builder: DurationBuilder): LocalDateTime = self.plus(builder.underlying)

    def millisOfSecond: Property = self.millisOfSecond()
    def millisOfDay: Property = self.millisOfDay()
    def secondOfMinute: Property = self.secondOfMinute()
    def minuteOfHour: Property = self.minuteOfHour()
    def hourOfDay: Property = self.hourOfDay()
    def dayOfMonth: Property = self.dayOfMonth()
    def dayOfWeek: Property = self.dayOfWeek()
    def dayOfYear: Property = self.dayOfYear()

    def weekyear: Property = self.weekyear()
    def week: Property = self.weekOfWeekyear()


    def monthOfYear: Property = self.monthOfYear()
    def year: Property = self.year()
    def century: Property = self.centuryOfEra()
    def era: Property = self.era()

    def withMillisOfSecond(millis: Int): LocalDateTime = self.withMillisOfSecond(millis)
    def withMillisOfDay(millis: Int): LocalDateTime = self.withMillisOfDay(millis)
    def withSecondOfMinute(second: Int): LocalDateTime = self.withSecondOfMinute(second)
    def withMinuteOfHour(minute: Int): LocalDateTime = self.withMinuteOfHour(minute)
    def withHourOfDay(hour: Int): LocalDateTime = self.withHourOfDay(hour)
    def withDayOfMonth(day: Int): LocalDateTime = self.withDayOfMonth(day)
    def withWeekyear(weekyear: Int): LocalDateTime = self.withWeekyear(weekyear)
    def withWeekOfWeekyear(week: Int): LocalDateTime = self.withWeekOfWeekyear(week)
    def withMonthOfYear(month: Int): LocalDateTime = self.withMonthOfYear(month)
    def withYear(year: Int): LocalDateTime = self.withYear(year)
    def withCentury(century: Int): LocalDateTime = self.withCenturyOfEra(century)
    def withEra(era: Int): LocalDateTime = self.withEra(era)

    def compare(that: LocalDateTime): Int = self.compareTo(that)
}
