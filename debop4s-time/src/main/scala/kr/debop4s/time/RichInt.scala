package kr.debop4s.time

import org.joda.time.Period

class RichInt(val self: Int) extends AnyVal {

    def millis = DurationBuilder(Period.millis(self))
    def seconds = DurationBuilder(Period.seconds(self))
    def minutes = DurationBuilder(Period.minutes(self))
    def hours = DurationBuilder(Period.hours(self))

    def days = Period.days(self)
    def weeks = Period.weeks(self)
    def months = Period.months(self)
    def years = Period.years(self)

    def milli = DurationBuilder(Period.millis(self))
    def second = DurationBuilder(Period.seconds(self))
    def minute = DurationBuilder(Period.minutes(self))
    def hour = DurationBuilder(Period.hours(self))

    def day = Period.days(self)
    def week = Period.weeks(self)
    def month = Period.months(self)
    def year = Period.years(self)
}
