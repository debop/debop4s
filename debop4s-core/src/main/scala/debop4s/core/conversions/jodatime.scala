package debop4s.core.conversions

import java.sql.Timestamp

import debop4s.core.TimestampZoneText
import debop4s.core.jodatime._
import org.joda.time.base.{AbstractDateTime, AbstractInstant, AbstractPartial, BaseSingleFieldPeriod}
import org.joda.time.field.AbstractReadableInstantFieldProperty
import org.joda.time.format.DateTimeFormatter
import org.joda.time.{Duration => JDuration, _}

/**
  * Joda Time 에 대한 변환 작업을 수행하는 implicit methods
  *
  * @author Sunghyouk Bae
  */
object jodatime {

  implicit def forcePeriod(builder: DurationBuilder): Period = builder.underlying

  implicit def forceDuration(builder: DurationBuilder): JDuration = builder.underlying.toStandardDuration

  implicit def richInt(n: Int): JodaRichInt = new JodaRichInt(n)

  implicit def richLong(n: Long): JodaRichLong = new JodaRichLong(n)

  implicit def richTimestamp(self: Timestamp): JodaRichTimestamp = new JodaRichTimestamp(self)

  implicit def richAbstractDateTime(v: AbstractDateTime): JodaRichAbstractDateTime = new JodaRichAbstractDateTime(v)

  implicit def richAbstractInstant(v: AbstractInstant): JodaRichAbstractInstant = new JodaRichAbstractInstant(v)

  implicit def richAbstractPartial(v: AbstractPartial): JodaRichAbstractPartial = new JodaRichAbstractPartial(v)

  implicit def richAbstractReadableInstantFieldProperty(v: AbstractReadableInstantFieldProperty): JodaRichAbstractReadableInstantFieldProperty =
    new JodaRichAbstractReadableInstantFieldProperty(v)

  implicit def richChronology(v: Chronology): JodaRichChronology = new JodaRichChronology(v)

  implicit def richDateTime(v: DateTime): JodaRichDateTime = new JodaRichDateTime(v)

  implicit def richDateTimeFormatter(v: DateTimeFormatter): JodaRichDateTimeFormatter = new JodaRichDateTimeFormatter(v)

  implicit def richDateTimeProperty(v: DateTime.Property): JodaRichDateTimeProperty = new JodaRichDateTimeProperty(v)

  implicit def richDateTimeZone(v: DateTimeZone): JodaRichDateTimeZone = new JodaRichDateTimeZone(v)

  implicit def richRichDuration(v: JDuration): JodaRichDuration = new JodaRichDuration(v)

  implicit def richRichInstant(v: Instant): JodaRichInstant = new JodaRichInstant(v)

  implicit def richLocalDate(v: LocalDate): JodaRichLocalDate = new JodaRichLocalDate(v)

  implicit def richLocalDateProperty(v: LocalDate.Property): JodaRichLocalDateProperty = new JodaRichLocalDateProperty(v)

  implicit def richLocalDateTime(v: LocalDateTime): JodaRichLocalDateTime = new JodaRichLocalDateTime(v)

  implicit def richLocalDateTimeProperty(v: LocalDateTime.Property): JodaRichLocalDateTimeProperty = new JodaRichLocalDateTimeProperty(v)

  implicit def richLocalTime(v: LocalTime): JodaRichLocalTime = new JodaRichLocalTime(v)

  implicit def richLocalTimeProperty(v: LocalTime.Property): JodaRichLocalTimeProperty = new JodaRichLocalTimeProperty(v)

  implicit def richPartial(v: Partial): JodaRichPartial = new JodaRichPartial(v)

  implicit def richPartialProperty(v: Partial.Property): JodaRichPartialProperty = new JodaRichPartialProperty(v)

  implicit def richPeriod(v: Period): JodaRichPeriod = new JodaRichPeriod(v)

  implicit def richReadableDateTime(v: ReadableDateTime): JodaRichReadableDateTime = new JodaRichReadableDateTime(v)

  implicit def richReadableDuration(v: ReadableDuration): JodaRichReadableDuration = new JodaRichReadableDuration(v)

  implicit def richReadableInstant(v: ReadableInstant): JodaRichReadableInstant = new JodaRichReadableInstant(v)

  implicit def richReadableInterval(v: ReadableInterval): JodaRichReadableInterval = new JodaRichReadableInterval(v)

  implicit def richReadablePartial(v: ReadablePartial): JodaRichReadablePartial = new JodaRichReadablePartial(v)

  implicit def richReadablePeriod(v: ReadablePeriod): JodaRichReadablePeriod = new JodaRichReadablePeriod(v)

  implicit val DateTimeOrdering: Ordering[DateTime] = ReadableInstantOrdering[DateTime]
  implicit val LocalDateOrdering: Ordering[LocalDate] = ReadablePartialOrdering[LocalDate]
  implicit val LocalTimeOrdering: Ordering[LocalTime] = ReadablePartialOrdering[LocalTime]
  implicit val LocalDateTimeOrdering: Ordering[LocalDateTime] = ReadablePartialOrdering[LocalDateTime]
  implicit val DurationOrdering: Ordering[JDuration] = ReadableDurationOrdering[JDuration]

  implicit def ReadableInstantOrdering[A <: ReadableInstant]: Ordering[A] = order[A, ReadableInstant]

  implicit def ReadablePartialOrdering[A <: ReadablePartial]: Ordering[A] = order[A, ReadablePartial]

  implicit def BaseSingleFieldPeriodOrdering[A <: BaseSingleFieldPeriod]: Ordering[A] = order[A, BaseSingleFieldPeriod]

  implicit def ReadableDurationOrdering[A <: ReadableDuration]: Ordering[A] = order[A, ReadableDuration]

  private def order[A, B <: Comparable[B]](implicit ev: A <:< B): Ordering[A] =
    Ordering.by[A, B](ev)


  /**
    * convert tuple to Joda DateTime
    * val date = (2016, 10, 14).tuple2Time()
    *
    * @param t tuple (year, month, day)
    * @return DateTime instance
    */
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

  /** joda-time Duration 객체에 대한 extensions class 입니다. */
  implicit class JDurationExtensions(self: JDuration) {
    /**
      * Duration 값의 절대값을 반환합니다.
      */
    def abs: JDuration = if (self < JDuration.ZERO) new JDuration(-self.getMillis) else self

    /**
      * 현재 시각 (`Time.now`) 이후의 값을 반환합니다.
      */
    def fromNow: DateTime = DateTime.now() + self

    /**
      * 현재 시각 (`Time.now`) 이전의 값을 반환합니다.
      */
    def ago: DateTime = DateTime.now() - self

    /**
      * Unix Epoch 이후의 값을 반환합니다.
      */
    def afterEpoch: DateTime = new DateTime(0) + self

    /**
      * `self` 와 지정된 `that`의 차이를 반환합니다.
      */
    def diff(that: JDuration): JDuration = self - that
  }

  /**
    * Joda DateTime 에 대한 Extension Class 입니다.
    *
    * @param self `DateTime` 인스턴스
    */
  implicit class JodaDateTimeExtensions(self: DateTime) {

    /** `DateTime` 정보를 UTC TimeZone 기준으로 변환합니다. */
    def asUtc(): DateTime = self.toDateTime(DateTimeZone.UTC)

    /** 지정한 TimeZone으로 변경한다 */
    def asLocal(tz: DateTimeZone = DateTimeZone.getDefault): DateTime = self.toDateTime(tz)

    /** Timestamp 수형으로 변환한다 */
    def asTimestamp: Timestamp = new Timestamp(self.getMillis)

    /** DateTime 값을 "YYYY-MM-DD'T'HH:mm:SS.SSS'Z'" 형식의 문자열로 반환 */
    def asIsoFormatDateTime: String = JodaISODateTimeFormat.dateTime.print(self)

    /** DateTime 값을 "YYYY-MM-DD'T'HH:mm:SS" 형식의 문자열로 반환 */
    def asIsoFormatDateHMS: String = JodaISODateTimeFormat.dateHourMinuteSecond.print(self)

    /** `DateTime` 을 이용하여 `TimestampZoneText` 객체를 빌드합니다 */
    def asTimestampZoneText: TimestampZoneText = new TimestampZoneText(self)
  }

}
