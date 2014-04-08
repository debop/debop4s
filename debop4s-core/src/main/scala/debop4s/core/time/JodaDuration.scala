package debop4s.core.time

import org.joda.time.Duration

/**
 * com.github.time.StaticDuration
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 6. 오후 9:49
 */

object JodaDuration extends JodaDuration {

    def apply(v: Long) = new Duration(v)

    lazy val empty = Duration.ZERO

    lazy val zero = empty
}

trait JodaDuration {

    def standardDays(days: Long) = Duration.standardDays(days)

    def standardHours(hours: Long) = Duration.standardHours(hours)

    def standardMinutes(minutes: Long) = Duration.standardMinutes(minutes)

    def standardSeconds(seconds: Long) = Duration.standardSeconds(seconds)
}
