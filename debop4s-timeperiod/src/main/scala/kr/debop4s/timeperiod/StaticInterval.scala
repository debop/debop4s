package kr.debop4s.timeperiod

import org.joda.time._

object StaticInterval extends StaticInterval

trait StaticInterval {
  def thisSecond: Interval = StaticDateTime.now.secondOfMinute.interval

  def thisMinute = StaticDateTime.now.minuteOfHour.interval

  def thisHour = StaticDateTime.now.hourOfDay.interval
}
