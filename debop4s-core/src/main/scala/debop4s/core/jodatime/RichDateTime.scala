package debop4s.core.jodatime

import java.sql.Timestamp
import java.util.Date
import org.joda.time.DateTime.Property
import org.joda.time._

/**
 * com.github.time.RichDateTime
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

    def withMillis(newMillis: Long): DateTime = self.withMillis(newMillis)

    def withSecond(second: Int): DateTime = self.withSecondOfMinute(second)

    def withMinute(minute: Int): DateTime = self.withMinuteOfHour(minute)

    def withHour(hour: Int): DateTime = self.withHourOfDay(hour)

    def withDay(day: Int): DateTime = self.withDayOfMonth(day)

    def withWeekyear(weekyear: Int): DateTime = self.withWeekyear(weekyear)

    def withWeek(week: Int): DateTime = self.withWeekOfWeekyear(week)

    def withMonth(month: Int): DateTime = self.withMonthOfYear(month)

    def withYear(year: Int): DateTime = self.withYear(year)

    def withCentury(century: Int): DateTime = self.withCenturyOfEra(century)

    def withEra(era: Int): DateTime = self.withEra(era)

    def compare(that: DateTime): Int = self.compareTo(that)

    // def toJsonString:String = self.withZone(DateTimeZone.UTC).toString(StaticISODateTimeFormat.dateTime)

    def monthInterval: Interval = {
        val start = withDay(1).withTimeAtStartOfDay()
        new Interval(start, start.plusMonths(1))
    }

    def dayInterval: Interval = {
        val start = new DateTime(self.getMillis, self.getChronology).withTimeAtStartOfDay()
        new Interval(start, start.plusDays(1))
    }

    def toDate: Date = self.toDate

    def toTimestamp: Timestamp = new Timestamp(self.getMillis)
}
