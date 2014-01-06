package kr.debop4s.time

import java.sql.Timestamp
import org.joda.time.DateTime.Property
import org.joda.time._

/**
 * kr.debop4s.time.RichDateTime
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 5. 오후 10:51
 */
class RichDateTime(val self: DateTime) extends AnyVal with Ordered[RichDateTime] {

    def -(duration: Long): DateTime = self.minus(duration)
    def -(duration: ReadableDuration): DateTime = self.minus(duration)
    def -(period: ReadablePeriod): DateTime = self.minus(period)
    def -(builder: DurationBuilder): DateTime = self.minus(builder.underlying)

    def +(duration: Long): DateTime = self.plus(duration)
    def +(duration: ReadableDuration): DateTime = self.plus(duration)
    def +(period: ReadablePeriod): DateTime = self.plus(period)
    def +(builder: DurationBuilder): DateTime = self.plus(builder.underlying)

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

    def withMillis(millis: Int) = self.withMillisOfSecond(millis)
    def withSecond(second: Int) = self.withSecondOfMinute(second)
    def withMinute(minute: Int) = self.withMinuteOfHour(minute)
    def withHour(hour: Int) = self.withHourOfDay(hour)
    def withDay(day: Int) = self.withDayOfMonth(day)
    def withWeek(week: Int) = self.withWeekOfWeekyear(week)
    def withMonth(month: Int) = self.withMonthOfYear(month)
    def withYear(year: Int) = self.withYear(year)
    def withCentury(century: Int) = self.withCenturyOfEra(century)
    def withEra(era: Int) = self.withEra(era)

    def compare(that: RichDateTime): Int = self.compareTo(that.self)

    // def toJsonString:String = self.withZone(DateTimeZone.UTC).toString(StaticISODateTimeFormat.dateTime)

    def monthInterval: Interval = {
        val start = withDay(1).withTimeAtStartOfDay()
        new Interval(start, start.plusMonths(1))
    }

    def dayInterval: Interval = {
        val start = new DateTime(self.getMillis, self.getChronology).withTimeAtStartOfDay()
        new Interval(start, start.plusDays(1))
    }

    def toTimestamp: Timestamp = new Timestamp(self.getMillis)
}
