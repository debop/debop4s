package debop4s.core.jodatime

import org.joda.time.Duration

object JodaDuration extends JodaDuration {

  def apply(v: Long) = new Duration(v)

  lazy val empty: Duration = Duration.ZERO
  lazy val zero: Duration = empty
}

trait JodaDuration {

  def standardDays(days: Long): Duration = Duration.standardDays(days)
  def standardHours(hours: Long): Duration = Duration.standardHours(hours)
  def standardMinutes(minutes: Long): Duration = Duration.standardMinutes(minutes)
  def standardSeconds(seconds: Long): Duration = Duration.standardSeconds(seconds)
}
