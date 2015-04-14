package debop4s.core.jodatime

import debop4s.core.conversions.jodatime._
import org.joda.time.{DateTime, Interval}

object JodaInterval extends JodaInterval

trait JodaInterval {

  def thisSecond: Interval = DateTime.now.secondOfMinute.interval

  def thisMinute = DateTime.now.minuteOfHour.interval

  def thisHour = DateTime.now.hourOfDay.interval
}
