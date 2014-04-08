package debop4s.core.time

import debop4s.core._
import org.joda.time.Interval

object StaticInterval extends StaticInterval

trait StaticInterval {

    def thisSecond: Interval = StaticDateTime.now.secondOfMinute.interval

    def thisMinute = StaticDateTime.now.minuteOfHour.interval

    def thisHour = StaticDateTime.now.hourOfDay.interval
}
