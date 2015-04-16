package debop4s.core.conversions

import java.util.concurrent.TimeUnit

import debop4s.core.utils.Time

import scala.concurrent.duration.Duration
import scala.language.implicitConversions

/**
 * time
 * Created by debop on 2014. 4. 5.
 */
object time {

  class RichWholeNumber(wrapped: Long) {

    def nanoseconds = Duration(wrapped, TimeUnit.NANOSECONDS)
    def nanosecond = nanoseconds
    def nanos = nanoseconds

    def microseconds = Duration(wrapped, TimeUnit.MICROSECONDS)
    def microsecond = microseconds
    def micros = microseconds

    def milliseconds = Duration(wrapped, TimeUnit.MILLISECONDS)
    def millisecond = milliseconds
    def millis = milliseconds

    def seconds = Duration(wrapped, TimeUnit.SECONDS)
    def second = seconds

    def minutes = Duration(wrapped, TimeUnit.MINUTES)
    def minute = minutes

    def hours = Duration(wrapped, TimeUnit.HOURS)
    def hour = hours

    def days = Duration(wrapped, TimeUnit.DAYS)
    def day = days
  }

  private val ZeroRichWholeNumber = new RichWholeNumber(0L) {
    override def nanoseconds = Duration.Zero
    override def microseconds = Duration.Zero
    override def milliseconds = Duration.Zero
    override def seconds = Duration.Zero
    override def minutes = Duration.Zero
    override def hours = Duration.Zero
    override def days = Duration.Zero
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
