package debop4s.core.time

import org.joda.time.Duration

/**
 * com.github.time.StaticDuration
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 6. 오후 9:49
 */

object StaticDuration extends StaticDuration {
    def apply(v: Long) = new Duration(v)

    def empty = Duration.ZERO

    def zero = empty
}

trait StaticDuration {
    def standardDays(days: Long) = Duration.standardDays(days)

    def standardHours(hours: Long) = Duration.standardHours(hours)

    def standardMinutes(minutes: Long) = Duration.standardMinutes(minutes)

    def standardSeconds(seconds: Long) = Duration.standardSeconds(seconds)
}
