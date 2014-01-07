package kr.debop4s.time

import java.sql.Timestamp
import java.util.Date
import org.joda.time.DateTime.Property
import org.joda.time._

/**
 * kr.debop4s.time.RichDateTime
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 5. 오후 10:51
 */
class RichDateTime(val self: DateTime) extends AnyVal with Ordered[DateTime] {

    def -(duration: Long): DateTime = self.minus(duration)
    def -(duration: ReadableDuration): DateTime = self.minus(duration)
    def -(period: ReadablePeriod): DateTime = self.minus(period)
    def -(builder: DurationBuilder): DateTime = self.minus(builder.underlying)

    def +(duration: Long): DateTime = self.plus(duration)
    def +(duration: ReadableDuration): DateTime = self.plus(duration)
    def +(period: ReadablePeriod): DateTime = self.plus(period)
    def +(builder: DurationBuilder): DateTime = self.plus(builder.underlying)

    def millisOfSecond: Property = self.millisOfSecond()
    def millisOfDay: Property = self.millisOfDay()

    def secondOfMinute: Property = self.secondOfMinute()
    def secondOfDay: Property = self.secondOfDay()

    def minuteOfHour: Property = self.minuteOfHour()
    def minuteOfDay: Property = self.minuteOfDay()

    def hourOfDay: Property = self.hourOfDay()

    def dayOfMonth: Property = self.dayOfMonth()
    def dayOfWeek: Property = self.dayOfWeek()
    def dayOfYear: Property = self.dayOfYear()

    def weekyear: Property = self.weekyear()
    def weekOfWeekyear: Property = self.weekOfWeekyear()

    def month: Property = self.monthOfYear()
    def year: Property = self.year()
    def century: Property = self.centuryOfEra()
    def era: Property = self.era()

    def withMillis(newMillis: Long): DateTime = self.withMillis(newMillis)
    def withMillisOfSecond(millis: Int): DateTime = self.withMillisOfSecond(millis)
    def withMillisOfDay(millis: Int): DateTime = self.withMillisOfDay(millis)
    def withSecondOfMinute(second: Int): DateTime = self.withSecondOfMinute(second)
    def withMinuteOfHour(minute: Int): DateTime = self.withMinuteOfHour(minute)
    def withHourOfDay(hour: Int): DateTime = self.withHourOfDay(hour)
    def withDayOfMonth(day: Int): DateTime = self.withDayOfMonth(day)
    def withWeekyear(weekyear: Int): DateTime = self.withWeekyear(weekyear)
    def withWeekOfWeekyear(week: Int): DateTime = self.withWeekOfWeekyear(week)
    def withMonthOfYear(month: Int): DateTime = self.withMonthOfYear(month)
    def withYear(year: Int): DateTime = self.withYear(year)
    def withCentury(century: Int): DateTime = self.withCenturyOfEra(century)
    def withEra(era: Int): DateTime = self.withEra(era)

    def compare(that: DateTime): Int = self.compareTo(that)

    // def toJsonString:String = self.withZone(DateTimeZone.UTC).toString(StaticISODateTimeFormat.dateTime)

    def monthInterval: Interval = {
        val start = withDayOfMonth(1).withTimeAtStartOfDay()
        new Interval(start, start.plusMonths(1))
    }

    def dayInterval: Interval = {
        val start = new DateTime(self.getMillis, self.getChronology).withTimeAtStartOfDay()
        new Interval(start, start.plusDays(1))
    }

    def toDate: Date = self.toDate
    def toTimestamp: Timestamp = new Timestamp(self.getMillis)
}
