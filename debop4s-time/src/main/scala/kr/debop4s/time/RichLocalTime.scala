package kr.debop4s.time

import org.joda.time.LocalTime.Property
import org.joda.time._

/**
 * kr.debop4s.time.RichLocalTime
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 6. 오전 12:35
 */
class RichLocalTime(val self: LocalTime) extends AnyVal with Ordered[LocalTime] {

    def -(period: ReadablePeriod): LocalTime = self.minus(period)
    def -(builder: DurationBuilder): LocalTime = self.minus(builder.underlying)

    def +(period: ReadablePeriod): LocalTime = self.plus(period)
    def +(builder: DurationBuilder): LocalTime = self.plus(builder.underlying)

    def millis: Property = self.millisOfSecond()
    def second: Property = self.secondOfMinute()
    def minute: Property = self.minuteOfHour()
    def hour: Property = self.hourOfDay()

    def withMillisOfSecond(millis: Int): LocalTime = self.withMillisOfSecond(millis)
    def withMillisOfDay(millis: Int): LocalTime = self.withMillisOfDay(millis)
    def withSecondOfMinute(second: Int): LocalTime = self.withSecondOfMinute(second)
    def withMinuteOfHour(minute: Int): LocalTime = self.withMinuteOfHour(minute)
    def withHourOfDay(hour: Int): LocalTime = self.withHourOfDay(hour)

    def compare(that: LocalTime) = self.compareTo(that)
}
