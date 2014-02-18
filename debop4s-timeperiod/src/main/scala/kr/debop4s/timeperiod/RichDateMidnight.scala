package kr.debop4s.timeperiod

import org.joda.time.{ReadablePeriod, ReadableDuration, DateMidnight}

/**
 * kr.debop4s.time.RichDateMidnight
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 6. 오후 8:21
 */
@Deprecated
class RichDateMidnight(val self: DateMidnight) extends AnyVal with Ordered[DateMidnight] {

    def -(duration: Long): DateMidnight = self.minus(duration)

    def -(duration: ReadableDuration): DateMidnight = self.minus(duration)

    def -(period: ReadablePeriod): DateMidnight = self.minus(period)

    def -(builder: DurationBuilder): DateMidnight = self.minus(builder.underlying)

    def +(duration: Long): DateMidnight = self.plus(duration)

    def +(duration: ReadableDuration): DateMidnight = self.plus(duration)

    def +(period: ReadablePeriod): DateMidnight = self.plus(period)

    def +(builder: DurationBuilder): DateMidnight = self.plus(builder.underlying)

    def compare(that: DateMidnight): Int = self.compare(that)
}
