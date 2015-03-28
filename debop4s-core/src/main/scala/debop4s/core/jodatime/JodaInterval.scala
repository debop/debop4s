package debop4s.core.jodatime

import org.joda.time.Interval

object JodaInterval extends JodaInterval

trait JodaInterval {

  def thisSecond: Interval = JDateTime.now.secondOfMinute.interval

  def thisMinute = JDateTime.now.minuteOfHour.interval

  def thisHour = JDateTime.now.hourOfDay.interval
}
