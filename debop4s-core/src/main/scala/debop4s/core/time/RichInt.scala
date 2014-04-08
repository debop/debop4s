package debop4s.core.time

import org.joda.time.Period

class RichInt(val self: Int) extends AnyVal {

    def milli = millis
    def millis = DurationBuilder(Period.millis(self))

    def second = seconds
    def seconds = DurationBuilder(Period.seconds(self))

    def minute = minutes
    def minutes = DurationBuilder(Period.minutes(self))

    def hour = hours
    def hours = DurationBuilder(Period.hours(self))

    def day = days
    def days = Period.days(self)

    def week = weeks
    def weeks = Period.weeks(self)

    def month = months
    def months = Period.months(self)

    def year = years
    def years = Period.years(self)

}
