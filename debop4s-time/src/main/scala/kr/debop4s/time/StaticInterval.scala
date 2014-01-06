package kr.debop4s.time

import org.joda.time._

object StaticInterval extends StaticInterval

trait StaticInterval {
    def thisSecond: Interval = StaticDateTime.now.second.interval
    def thisMinute = StaticDateTime.now.minute.interval
    def thisHour = StaticDateTime.now.hour.interval
}
