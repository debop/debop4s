package kr.debop4s.timeperiod

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

  def millis: Property = self.millisOfSecond()

  def second: Property = self.secondOfMinute()

  def minute: Property = self.minuteOfHour()

  def hour: Property = self.hourOfDay()

  def day: Property = self.dayOfMonth()

  def week: Property = self.weekOfWeekyear()

  def month: Property = self.monthOfYear()

  def year: Property = self.year()

  def century: Property = self.centuryOfEra()

  def era: Property = self.era()

  def withMillis(millis: Int): LocalDateTime = self.withMillisOfSecond(millis)

  def withSecond(second: Int): LocalDateTime = self.withSecondOfMinute(second)

  def withMinute(minute: Int): LocalDateTime = self.withMinuteOfHour(minute)

  def withHour(hour: Int): LocalDateTime = self.withHourOfDay(hour)

  def withDay(day: Int): LocalDateTime = self.withDayOfMonth(day)

  def withWeek(week: Int): LocalDateTime = self.withWeekOfWeekyear(week)

  def withMonth(month: Int): LocalDateTime = self.withMonthOfYear(month)

  def withYear(year: Int): LocalDateTime = self.withYear(year)

  def withCentury(century: Int): LocalDateTime = self.withCenturyOfEra(century)

  def withEra(era: Int): LocalDateTime = self.withEra(era)

  def compare(that: LocalDateTime): Int = self.compareTo(that)
}
