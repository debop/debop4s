package debop4s

import debop4s.core.time._
import debop4s.core.utils.Strings
import java.sql.Timestamp
import java.util.concurrent.TimeUnit
import org.joda.time._
import org.joda.time.base.{BaseSingleFieldPeriod, AbstractPartial, AbstractInstant, AbstractDateTime}
import org.joda.time.field.AbstractReadableInstantFieldProperty
import org.joda.time.format.DateTimeFormatter
import org.joda.time.{Duration => JDuration}
import scala.concurrent._
import scala.concurrent.duration.Duration


/**
 * debop4s.core.package
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 10:03
 */
package object core {

  val TimeConversions = debop4s.core.conversions.time
  val StorageConversions = debop4s.core.conversions.storage

  // implicit val executor = ExecutionContext.fromExecutor(scala.concurrent.ExecutionContext.Implicits.global)

  val ShouldNotBeNull = "[%s] should not be null."
  val ShouldBeNull = "[%s] should be null."

  val ShouldBeEquals = "%s=[%s] should be equals expected=[%s]"
  val ShouldNotBeEquals = "%s=[%s] should not be equals expected=[%s]"
  val ShouldBeEmptyString = "[%s] should be empty string."
  val ShouldNotBeEmptyString = "[%s] should not be empty string."

  val ShouldBeWhiteSpace = "[%s] should be white space."
  val ShouldNotBeWhiteSpace = "[%s] should not be white space."

  val ShouldBeNumber = "[%s] should be number."

  val ShouldBePositiveNumber = "[%s] should be positive number"
  val ShouldNotBePositiveNumber = "[%s] should not be positive number"

  val ShouldBeNegativeNumber = "[%s] should be negative number"
  val ShouldNotBeNegativeNumber = "[%s] should not be negative number"

  val ShouldBeInRangeInt = "%s[%d]이 범위 [%d, %d) 를 벗어났습니다."
  val ShouldBeInRangeDouble = "%s[%f]이 범위 [%f, %f) 를 벗어났습니다."

  val ElipsisLength = 80: Int

  implicit class StringExtensions(s: String) {
    def words: Array[String] = s split " "

    def isWhitespace: Boolean = Strings.isWhitespace(s)

    def ellipseChar(maxLength: Int = ElipsisLength) = Strings.ellipsisChar(s, maxLength)

    def ellipseFirst(maxLength: Int = ElipsisLength) = Strings.ellipsisFirst(s, maxLength)

    def ellipsePath(maxLength: Int = ElipsisLength) = Strings.ellipsisPath(s, maxLength)

    def toUtf8Bytes = Strings.getUtf8Bytes(s)
  }

  implicit class ByteExtensions(bytes: Array[Byte]) {
    def toUtf8String = Strings.getUtf8String(bytes)
  }

  implicit val defaultDuration: Duration = Duration(60, TimeUnit.MINUTES)

  implicit class AwaitableExtensions[T](task: Awaitable[T]) {

    def ready()(implicit atMost: Duration = defaultDuration) {
      Await.ready(task, atMost)
    }

    def result()(implicit atMost: Duration = defaultDuration): T = {
      Await.result[T](task, atMost)
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

  implicit def forcePeriod(builder: DurationBuilder) = builder.underlying

  implicit def forceDuration(builder: DurationBuilder) = builder.underlying.toStandardDuration

  implicit def richInt(n: Int) = new debop4s.core.time.RichInt(n)

  implicit def richLong(n: Long) = new debop4s.core.time.RichLong(n)

  implicit def richTimestamp(self: Timestamp) = new debop4s.core.time.RichTimestamp(self)

  implicit def richAbstractDateTime(v: AbstractDateTime) = new RichAbstractDateTime(v)

  implicit def richAbstractInstant(v: AbstractInstant) = new RichAbstractInstant(v)

  implicit def richAbstractPartial(v: AbstractPartial) = new RichAbstractPartial(v)

  implicit def richAbstractReadableInstantFieldProperty(v: AbstractReadableInstantFieldProperty) =
    new RichAbstractReadableInstantFieldProperty(v)

  implicit def richChronology(v: Chronology) = new RichChronology(v)

  implicit def richDateTime(v: DateTime) = new RichDateTime(v)

  implicit def richDateTimeFormatter(v: DateTimeFormatter) = new RichDateTimeFormatter(v)

  implicit def richDateTimeProperty(v: DateTime.Property) = new RichDateTimeProperty(v)

  implicit def richDateTimeZone(v: DateTimeZone) = new RichDateTimeZone(v)

  implicit def richRichDuration(v: JDuration) = new RichDuration(v)

  implicit def richRichInstant(v: Instant) = new RichInstant(v)

  implicit def richLocalDate(v: LocalDate) = new RichLocalDate(v)

  implicit def richLocalDateProperty(v: LocalDate.Property) = new RichLocalDateProperty(v)

  implicit def richLocalDateTime(v: LocalDateTime) = new RichLocalDateTime(v)

  implicit def richLocalDateTimeProperty(v: LocalDateTime.Property) = new RichLocalDateTimeProperty(v)

  implicit def richLocalTime(v: LocalTime) = new RichLocalTime(v)

  implicit def richLocalTimeProperty(v: LocalTime.Property) = new RichLocalTimeProperty(v)

  implicit def richPartial(v: Partial) = new RichPartial(v)

  implicit def richPartialProperty(v: Partial.Property) = new RichPartialProperty(v)

  implicit def richPeriod(v: Period) = new RichPeriod(v)

  implicit def richReadableDateTime(v: ReadableDateTime) = new RichReadableDateTime(v)

  implicit def richReadableDuration(v: ReadableDuration) = new RichReadableDuration(v)

  implicit def richReadableInstant(v: ReadableInstant) = new RichReadableInstant(v)

  implicit def richReadableInterval(v: ReadableInterval) = new RichReadableInterval(v)

  implicit def richReadablePartial(v: ReadablePartial) = new RichReadablePartial(v)

  implicit def richReadablePeriod(v: ReadablePeriod) = new RichReadablePeriod(v)

  implicit val DateTimeOrdering: Ordering[DateTime] = ReadableInstantOrdering[DateTime]
  implicit val LocalDateOrdering: Ordering[LocalDate] = ReadablePartialOrdering[LocalDate]
  implicit val LocalTimeOrdering: Ordering[LocalTime] = ReadablePartialOrdering[LocalTime]
  implicit val LocalDateTimeOrdering: Ordering[LocalDateTime] = ReadablePartialOrdering[LocalDateTime]
  implicit val DurationOrdering: Ordering[JDuration] = ReadableDurationOrdering[JDuration]

  implicit def ReadableInstantOrdering[A <: ReadableInstant] = order[A, ReadableInstant]

  implicit def ReadablePartialOrdering[A <: ReadablePartial] = order[A, ReadablePartial]

  implicit def BaseSingleFieldPeriodOrdering[A <: BaseSingleFieldPeriod] = order[A, BaseSingleFieldPeriod]

  implicit def ReadableDurationOrdering[A <: ReadableDuration] = order[A, ReadableDuration]

  private def order[A, B <: Comparable[B]](implicit ev: A <:< B): Ordering[A] =
    Ordering.by[A, B](ev)

  // import javax.xml.datatype.{XMLGregorianCalendar, DatatypeFactory}

  // lazy val factory: DatatypeFactory = DatatypeFactory.newInstance

  //    implicit def dateTime2XmlGregCalendar(dt: DateTime): XMLGregorianCalendar =
  //        factory.newXMLGregorianCalendar(dt.toGregorianCalendar)
  //
  //    implicit def xmlGregCalendar2DateTime(calendar: XMLGregorianCalendar): DateTime =
  //        new DateTime(calendar.toGregorianCalendar.getTimeInMillis)

  implicit def tuple2Time(t: (Int, Int, Int)): DateTime = {
    val (year, month, day) = t
    new DateTime(year, month, day, 0, 0, 0, 0)
  }

  implicit def tuple2Time(t: (Int, Int, Int, Int)): DateTime = {
    val (year, month, day, hour) = t
    new DateTime(year, month, day, hour, 0, 0, 0)
  }

  implicit def tuple2Time(t: (Int, Int, Int, Int, Int)): DateTime = {
    val (year, month, day, hour, minute) = t
    new DateTime(year, month, day, hour, minute, 0, 0)
  }

  implicit def tuple2Time(t: (Int, Int, Int, Int, Int, Int)): DateTime = {
    val (year, month, day, hour, minute, second) = t
    new DateTime(year, month, day, hour, minute, second, 0)
  }

  implicit def tuple2Time(t: (Int, Int, Int, Int, Int, Int, Int)): DateTime = {
    val (year, month, day, hour, minute, second, millis) = t
    new DateTime(year, month, day, hour, minute, second, millis)
  }

  val JodaDateTime = debop4s.core.time.JodaDateTime
  val JodaDateTimeFormat = debop4s.core.time.JodaDateTimeFormat
  val JodaISODateTimeFormat = debop4s.core.time.JodaISODateTimeFormat
  val JodaDateTimeZone = debop4s.core.time.JodaDateTimeZone
  val JodaDuration = debop4s.core.time.JodaDuration
  val JodaInterval = debop4s.core.time.JodaInterval
  val JodaLocalDate = debop4s.core.time.JodaLocalDate
  val JodaLocalDateTime = debop4s.core.time.JodaLocalDateTime
  val JodaLocalTime = debop4s.core.time.JodaLocalTime
  val JodaPeriod = debop4s.core.time.JodaPeriod
  val JodaPartial = debop4s.core.time.JodaPartial
}