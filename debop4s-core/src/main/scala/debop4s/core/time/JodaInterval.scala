package debop4s.core.time

import debop4s.core._
import org.joda.time.Interval

object JodaInterval extends JodaInterval

trait JodaInterval {

  def thisSecond: Interval = JodaDateTime.now.secondOfMinute.interval

  def thisMinute = JodaDateTime.now.minuteOfHour.interval

  def thisHour = JodaDateTime.now.hourOfDay.interval
}
