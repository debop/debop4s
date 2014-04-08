package debop4s.core.time

import org.joda.time.{ReadablePartial, Period}


object StaticPeriod extends StaticPeriod {
    def empty = new Period()

    def ∅ = empty
}

trait StaticPeriod {
    def days(days: Int) = Period.days(days)

    def fieldDifference(start: ReadablePartial, end: ReadablePartial) = Period.fieldDifference(start, end)

    def hours(hours: Int) = Period.hours(hours)

    def millis(millis: Int) = Period.millis(millis)

    def minutes(minutes: Int) = Period.minutes(minutes)

    def months(months: Int) = Period.months(months)

    def seconds(seconds: Int) = Period.seconds(seconds)

    def weeks(weeks: Int) = Period.weeks(weeks)

    def years(years: Int) = Period.years(years)
}
