package debop4s.core.utils

import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit
import java.util.{Date, Locale, TimeZone}

import debop4s.core.Logging
import debop4s.core.conversions.time._
import org.joda.time.DateTime

import scala.concurrent.duration.Duration


/**
 * TimeLike
 * @author Sunghyouk Bae
 */
trait TimeLike[This <: TimeLike[This]] extends Ordered[This] {
  self: This =>
  protected val ops: TimeLikeOps[This]

  import ops._

  /** The `TimeLike `'s value in nanoseconds */
  def inNanoseconds: Long

  def inMicroseconds: Long = Duration.fromNanos(inNanoseconds).toMicros

  def inMilliseconds: Long = Duration.fromNanos(inNanoseconds).toMillis

  def inLongSeconds: Long = Duration.fromNanos(inNanoseconds).toSeconds

  def inSeconds: Int = {
    val longSeconds = inLongSeconds
    if (longSeconds > Int.MaxValue) Int.MaxValue
    else if (longSeconds < Int.MinValue) Int.MinValue
    else longSeconds.toInt
  }

  def inMinutes: Int = Duration.fromNanos(inNanoseconds).toMinutes.toInt

  def inHours: Int = Duration.fromNanos(inNanoseconds).toHours.toInt

  def inDays: Int = Duration.fromNanos(inNanoseconds).toDays.toInt

  def inMillis: Long = inMilliseconds

  def inTimeUnit: (Long, TimeUnit) = Duration.fromNanos(inNanoseconds)

  def +(delta: Duration): This = delta match {
    case Duration.Inf => Inf
    case Duration.MinusInf => MinusInf
    case Duration.Undefined => Undefined
    case _ =>
      val ns = delta.toNanos
      try {
        fromNanoseconds(LongOverflowArith.add(inNanoseconds, ns))
      } catch {
        case _: LongOverflowException if ns < 0 => MinusInf
        case _: LongOverflowException => Inf
      }
  }

  def -(delta: Duration): This = this.+(-delta)

  def isFinite: Boolean

  def diff(that: This): Duration

  /**
   * `x` 값을 내림합니다.
   */
  def floor(x: Duration): This =
    (this, x.inNanos) match {
      case (Nanoseconds(0), Some(0)) => Undefined
      case (Nanoseconds(num), Some(0)) => if (num < 0) MinusInf else Inf
      case (Nanoseconds(num), Some(denom)) => fromNanoseconds((num / denom) * denom)
      case (`self`, _) => self
      case (_, _) => Undefined
    }

  def max(that: This): This = if ((this compare that) < 0) that else this

  def min(that: This): This = if ((this compare that) < 0) this else that

  override def compare(that: This): Int = {
    if ((that eq Inf) || (that eq Undefined)) -1
    else if (that eq MinusInf) 1
    else if (inNanoseconds < that.inNanoseconds) -1
    else if (inNanoseconds > that.inNanoseconds) 1
    else 0
  }

  def moreOrLessEquals(other: This, maxDelta: Duration): Boolean =
    (other ne Undefined) && ((this == other) || ((this diff other).abs lteq maxDelta))
}


/**
 * TimeBox
 */
private[utils] object TimeBox {

  case class Finite(nanos: Long) extends Serializable {
    private def readResolve: Object = Time.fromNanoseconds(nanos)
  }

  case class Inf() extends Serializable {
    private def readResolve: Object = Time.Inf
  }

  case class MinusInf() extends Serializable {
    private def readResolve: Object = Time.MinusInf
  }

  case class Undefined() extends Serializable {
    private def readResolve: Object = Time.Undefined
  }

}

trait TimeControl {

  def set(time: Time): Unit

  def advance(delta: Duration): Unit
}

/**
 * A thread-safe wrapper around a SimpleDateFormat object.
 * The timezone used will be UTC.
 */
class TimeFormat(pattern: String, locale: Option[Locale]) {

  // jdk6 and jdk7 pick up the default locale differently in SimpleDateFormat
  // so we can't rely on Locale.getDefault here.
  // instead we let SimpleDateFormat do the work for us above
  /** Create a new TimeFormat with the default locale. **/
  def this(pattern: String) = this(pattern, None)

  private[this] val format: SimpleDateFormat =
    locale map { new SimpleDateFormat(pattern, _) } getOrElse new SimpleDateFormat(pattern)

  format.setTimeZone(TimeZone.getTimeZone("UTC"))

  def parse(str: String): Time = {
    // SimpleDateFormat is not thread-safe
    val date = format.synchronized(format.parse(str))
    if (date == null)
      throw new Exception(s"Unable to parse date-time: $str")
    else
      Time.fromMilliseconds(date.getTime)
  }

  def format(time: Time): String =
    format.synchronized(format.format(time.toDate))
}

/**
 * TimeLikeOps
 * @author Sunghyouk Bae
 */
trait TimeLikeOps[This <: TimeLike[This]] {

  /** 최대 값 또는 무한대 값 */
  val Inf: This

  /** 최소 값 */
  val MinusInf: This

  /** 정의되지 않은 값. Double.NaN 과 유사 */
  val Undefined: This

  val Zero: This = fromNanoseconds(0)

  /**
   * An extractor for finite `This`, yielding its value in nanoseconds.
   *
   * {{{
   *   duration match {
   *     case Duration.Nanoseocnds(ns) => ...
   *     case Duration.Top =>
   *   }
   * }}}
   */
  object Nanoseconds {
    def unapply(x: This): Option[Long] =
      if (x.isFinite) Some(x.inNanoseconds) else None
  }

  /**
   * An extractor for finite TimeLikes
   *
   * {{{
   *   duration match {
   *     case Duration.Finite(d) => ...
   *     case Duration.Top =>
   *   }
   * }}}
   */
  object Finite {
    def unapply(x: This): Option[This] =
      if (x.isFinite) Some(x) else None
  }

  def fromNanoseconds(nanoseconds: Long): This

  def fromSeconds(seconds: Int): This = fromMilliseconds(1000L * seconds)

  def fromMilliseconds(millis: Long): This = {
    if (millis > 9223372036854L) Inf
    else if (millis < -9223372036854L) MinusInf
    else fromNanoseconds(TimeUnit.MILLISECONDS.toNanos(millis))
  }

  def fromMicroseconds(micros: Long): This = {
    if (micros > 9223372036854775L) Inf
    else if (micros < -9223372036854775L) MinusInf
    else fromNanoseconds(TimeUnit.MICROSECONDS.toNanos(micros))
  }
}


/**
 * Use `Time.now` in your program instead of
 * `System.currentTimeMillis`, and unit tests will be able to adjust
 * the current time to verify timeouts and other time-dependent
 * behavior, without calling `sleep`.
 *
 * If you import the [[debop4s.core.conversions.time]] implicits you
 * can write human-readable values such as `1.minute` or
 * `250.millis`.
 */
object Time extends TimeLikeOps[Time] with Logging {

  def fromNanoseconds(nanoseconds: Long): Time = new Time(nanoseconds)

  // This is needed for Java compatibility
  override def fromSeconds(seconds: Int): Time = super.fromSeconds(seconds)

  override def fromMilliseconds(millis: Long): Time = super.fromMilliseconds(millis)

  /**
   * Time `Top` is greater than any other definable time, and is
   * equal only to itself. It may be used as a sentinel value,
   * representing a time infinitely far into the future.
   */
  override val Inf: Time = new Time(Long.MaxValue) {
    override def toString: String = "Time.Inf"
    override def compare(that: Time): Int = {
      if (that eq Undefined) -1
      else if (that eq Inf) 0
      else 1
    }
    override def +(delta: Duration): Time =
      delta match {
        case Duration.MinusInf | Duration.Undefined => Undefined
        case _ => this
      }
    override def diff(that: Time) =
      that match {
        case Inf | Undefined => Duration.Undefined
        case other => Duration.Inf
      }

    override def isFinite: Boolean = false
    private def writeReplace: Object = TimeBox.Inf()
  }

  override val MinusInf: Time = new Time(Long.MinValue) {
    override def toString: String = "Time.MinusInf"
    override def compare(that: Time): Int = if (this eq that) 0 else -1
    override def +(delta: Duration) =
      delta match {
        case Duration.Inf | Duration.Undefined => Undefined
        case _ => this
      }
    override def diff(that: Time) =
      that match {
        case MinusInf | Undefined => Duration.Undefined
        case other => Duration.MinusInf
      }
    override def isFinite: Boolean = false
    private def writeReplace: Object = TimeBox.MinusInf()
  }

  override val Undefined: Time = new Time(0) {
    override def toString: String = "Time.Undefined"
    override def compare(that: Time): Int = if (this eq that) 0 else 1
    override def +(delta: Duration) = this
    override def diff(that: Time) = Duration.Undefined
    override def isFinite: Boolean = false
    private def writeReplace: Object = TimeBox.Undefined()
  }

  def now: Time = {
    localGetTime() match {
      case None => Time.fromMilliseconds(System.currentTimeMillis())
      case Some(f) => f()
    }
  }

  lazy val epoch: Time = fromNanoseconds(0L)

  lazy val defaultFormat = new TimeFormat("yyyy-MM-dd HH:mm:ss Z")
  lazy val rssFormat = new TimeFormat("E, dd MMM yyyy HH:mm:ss Z")

  /**
   * Note, this should only ever be updated by methods used for testing.
   */
  private[utils] val localGetTime = new Local[() => Time]

  def apply(date: Date): Time = fromMilliseconds(date.getTime)

  def apply(dt: DateTime): Time = fromMilliseconds(dt.getMillis)

  def at(datetime: String): Time = defaultFormat.parse(datetime)

  /**
   * 기존의 `Time.now`를 사용하지 않고, 임시로 `timeFunction`이 반환한 `Time`값을 사용하여 `body`를 실행합니다.
   */
  def withTimeFunction[A](timeFunction: => Time)(body: TimeControl => A): A = {
    require(timeFunction != null)
    require(body != null)

    @volatile var tf = () => timeFunction
    val save = Local.save()
    try {
      val timeControl = new TimeControl {
        def set(time: Time) {
          tf = () => time
        }

        def advance(delta: Duration) {
          val newTime = tf() + delta
          tf = () => newTime
        }
      }
      Time.localGetTime() = () => tf()
      body(timeControl)
    } finally {
      Local.restore(save)
    }
  }

  /** 기존의 `Time.now`를 사용하지 않고, 임시로 `time` 값을 사용하여 `body`를 실행합니다. */
  def withTimeAt[A](time: Time)(body: TimeControl => A): A = withTimeFunction(time)(body)

  /** `Time.now` 값을 사용하여 `body`를 실행합니다. */
  def withCurrentTimeFrozen[A](body: TimeControl => A): A = withTimeAt(Time.now)(body)

  def fromRss(rss: String) = rssFormat.parse(rss)
}

sealed class Time(protected val nanos: Long)
  extends {protected val ops = Time } with TimeLike[Time] with Serializable {

  import ops._

  def inNanoseconds: Long = nanos

  override def toString: String = defaultFormat.format(this)

  override def equals(other: Any): Boolean =
    other match {
      case t: Time => (this compare t) == 0
      case _ => false
    }

  override def hashCode: Int = nanos.hashCode()

  def format(pattern: String): String = new TimeFormat(pattern).format(this)

  def format(pattern: String, locale: Locale): String =
    new TimeFormat(pattern, Some(locale)).format(this)

  override def isFinite: Boolean = true

  def -(that: Time): Duration = diff(that)

  /** that ~ 현 Time 의 기간 (this - that) */
  def diff(that: Time): Duration =
    that match {
      case Undefined => Duration.Undefined
      case Inf => Duration.MinusInf
      case MinusInf => Duration.Inf
      case other =>
        try {
          Duration.fromNanos(LongOverflowArith.sub(this.inNanoseconds, other.inNanoseconds))
        } catch {
          case _: LongOverflowException if other.inNanoseconds < 0 => Duration.Inf
          case _: LongOverflowException => Duration.MinusInf
        }
    }

  /** that 부터 this 까지의 기간 */
  def since(that: Time): Duration = this - that

  /** Unit `epoch` 부터 this 까지의 기간 */
  def sinceEpoch: Duration = since(epoch)

  /** 현재 시각으로부터 this 까지의 기간 */
  def sinceNow: Duration = since(now)

  /** this 부터 that 까지의 기간 */
  def until(that: Time): Duration = that - this

  /** this 부터 unix epoch 까지의 기간 */
  def untilEpoch: Duration = until(epoch)

  /** this 부터 현재 시각까지의 기간 */
  def untilNow: Duration = until(now)

  /** this 를 Date로 변환 */
  def toDate: Date = new Date(inMillis)

  /** this 를 DateTime로 변환 */
  def toDateTime: DateTime = new DateTime(inMillis)

  private def writeReplace: Object = TimeBox.Finite(inNanoseconds)

}