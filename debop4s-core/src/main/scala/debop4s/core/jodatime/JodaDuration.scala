package debop4s.core.jodatime

import org.joda.time.Duration

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
