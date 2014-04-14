package debop4s.core.conversions

import debop4s.core.Time
import java.util.concurrent.TimeUnit
import scala.concurrent.duration._

/**
 * time
 * Created by debop on 2014. 4. 5.
 */
object time {

    class RichWholeNumber(wrapped: Long) {
        def toNanos = Duration(wrapped, TimeUnit.NANOSECONDS)
        def toMicros = Duration(wrapped, TimeUnit.MICROSECONDS)
        def toMillis = Duration(wrapped, TimeUnit.MILLISECONDS)
        def toSeconds = Duration(wrapped, TimeUnit.SECONDS)
        def toMinutes = Duration(wrapped, TimeUnit.MINUTES)
        def toHours = Duration(wrapped, TimeUnit.HOURS)
        def toDays = Duration(wrapped, TimeUnit.DAYS)
    }

    private val ZeroRichWholeNumber = new RichWholeNumber(0L) {
        override def toNanos = Duration.Zero
        override def toMicros = Duration.Zero
        override def toMillis = Duration.Zero
        override def toSeconds = Duration.Zero
        override def toMinutes = Duration.Zero
        override def toHours = Duration.Zero
        override def toDays = Duration.Zero
    }

    implicit def intToRichWholeNumber(i: Int): RichWholeNumber = {
        i match {
            case 0 => ZeroRichWholeNumber
            case _ => new RichWholeNumber(i.toLong)
        }
    }

    implicit def intToRichWholeNumber(l: Long): RichWholeNumber = {
        l match {
            case 0L => ZeroRichWholeNumber
            case _ => new RichWholeNumber(l)
        }
    }

    implicit class DurationExtensions(self: Duration) {

        /**
        * Duration 값의 절대값을 반환합니다.
        */
        def abs = self match {
            case Duration.Inf => Duration.Inf
            case Duration.MinusInf => Duration.Inf
            case Duration.Undefined => Duration.Undefined
            case _ => if (self < Duration.fromNanos(0)) -self else self
        }

        /**
        * 현재 시각 (`Time.now`) 이후의 값을 반환합니다.
        */
        def fromNow = Time.now + self
        /**
        * 현재 시각 (`Time.now`) 이전의 값을 반환합니다.
        */
        def ago = Time.now - self
        /**
        * Unix Epoch 이후의 값을 반환합니다.
        */
        def afterEpoch = Time.epoch + self

        /**
        * `self` 와 지정된 `that`의 차이를 반환합니다.
        */
        def diff(that: Duration): Duration = self - that

        /**
        * `Duration` 값을 nanosecond 단위로 표현합니다.
        * `Duration.Inf`, `Duration.MinusInf`, `Duration.Undefined` 은 표현을 못하므로 `None`을 반환합니다.
        */
        def inNanos: Option[Long] = {
            if (self.isFinite()) Some(self.toNanos)
            else None
        }
    }
}
