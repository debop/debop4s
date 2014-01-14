package kr.debop4s.timeperiod

import org.joda.time.{Duration, DateTime, ReadablePeriod, Period}

/**
 * kr.debop4s.time.RichPeriod
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 6. 오후 8:54
 */
class RichPeriod(val self: Period) extends AnyVal {

    def millis: Int = self.getMillis
    def seconds: Int = self.getSeconds
    def minutes: Int = self.getMinutes
    def hours: Int = self.getHours
    def days: Int = self.getDays
    def weeks: Int = self.getWeeks
    def months: Int = self.getMonths
    def years: Int = self.getYears

    def -(period: ReadablePeriod): Period = self.minus(period)
    def +(period: ReadablePeriod): Period = self.plus(period)

    def ago: DateTime = StaticDateTime.now.minus(self)
    def later: DateTime = StaticDateTime.now.plus(self)
    def from(moment: DateTime): DateTime = moment.plus(self)
    def before(moment: DateTime): DateTime = moment.minus(self)

    def standardDuration: Duration = self.toStandardDuration
}
