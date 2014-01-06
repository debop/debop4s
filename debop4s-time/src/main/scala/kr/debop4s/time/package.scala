package kr.debop4s

import org.joda.time._
import java.sql.Timestamp
import org.joda.time.base.{BaseSingleFieldPeriod, AbstractPartial, AbstractInstant, AbstractDateTime}
import org.joda.time.field.AbstractReadableInstantFieldProperty
import org.joda.time.format.DateTimeFormatter

/**
 * kr.debop4s.time.package
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 6. 오후 11:07
 */
package object time {

    implicit def forcePeriod(builder: DurationBuilder): Period = builder.underlying
    implicit def forceDuration(builder: DurationBuilder): Duration = builder.underlying.toStandardDuration

    implicit def richInt(n: Int): RichInt = new kr.debop4s.time.RichInt(n)
    implicit def richLong(n: Long): RichLong = new kr.debop4s.time.RichLong(n)

    implicit def richTimestamp(self: Timestamp): RichTimestamp = new RichTimestamp(self)

    implicit def richAbstractDateTime(v: AbstractDateTime): RichAbstractDateTime = new RichAbstractDateTime(v)
    implicit def richAbstractInstant(v: AbstractInstant): RichAbstractInstant = new RichAbstractInstant(v)
    implicit def richAbstractPartial(v: AbstractPartial): RichAbstractPartial = new RichAbstractPartial(v)
    implicit def richAbstractReadableInstantFieldProperty(v: AbstractReadableInstantFieldProperty)
    : RichAbstractReadableInstantFieldProperty = new RichAbstractReadableInstantFieldProperty(v)

    implicit def richChronology(v: Chronology): RichChronology = new RichChronology(v)
    implicit def richDateMidnight(v: DateMidnight): RichDateMidnight = new RichDateMidnight(v)
    implicit def richDateTime(v: DateTime): RichDateTime = new RichDateTime(v)
    implicit def richDateTimeFormatter(v: DateTimeFormatter): RichDateTimeFormatter = new RichDateTimeFormatter(v)
    implicit def richDateTimeProperty(v: DateTime.Property): RichDateTimeProperty = new RichDateTimeProperty(v)
    implicit def richDateTimeZone(v: DateTimeZone): RichDateTimeZone = new RichDateTimeZone(v)
    implicit def richRichDuration(v: Duration): RichDuration = new RichDuration(v)
    implicit def richRichInstant(v: Instant): RichInstant = new RichInstant(v)
    implicit def richLocalDate(v: LocalDate): RichLocalDate = new RichLocalDate(v)
    implicit def richLocalDateProperty(v: LocalDate.Property): RichLocalDateProperty = new RichLocalDateProperty(v)
    implicit def richLocalDateTime(v: LocalDateTime): RichLocalDateTime = new RichLocalDateTime(v)
    implicit def richLocalDateTimeProperty(v: LocalDateTime.Property): RichLocalDateTimeProperty = new RichLocalDateTimeProperty(v)
    implicit def richLocalTime(v: LocalTime): RichLocalTime = new RichLocalTime(v)
    implicit def richLocalTimeProperty(v: LocalTime.Property): RichLocalTimeProperty = new RichLocalTimeProperty(v)
    implicit def richPartial(v: Partial): RichPartial = new RichPartial(v)
    implicit def richPartialProperty(v: Partial.Property): RichPartialProperty = new RichPartialProperty(v)
    implicit def richPeriod(v: Period): RichPeriod = new RichPeriod(v)
    implicit def richReadableDateTime(v: ReadableDateTime): RichReadableDateTime = new RichReadableDateTime(v)
    implicit def richReadableDuration(v: ReadableDuration): RichReadableDuration = new RichReadableDuration(v)
    implicit def richReadableInstant(v: ReadableInstant): RichReadableInstant = new RichReadableInstant(v)
    implicit def richReadableInterval(v: ReadableInterval): RichReadableInterval = new RichReadableInterval(v)
    implicit def richReadablePartial(v: ReadablePartial): RichReadablePartial = new RichReadablePartial(v)
    implicit def richReadablePeriod(v: ReadablePeriod): RichReadablePeriod = new RichReadablePeriod(v)

    implicit val DateTimeOrdering: Ordering[DateTime] = ReadableInstantOrdering[DateTime]
    implicit val DateMidnightOrdering: Ordering[DateMidnight] = ReadableInstantOrdering[DateMidnight]
    implicit val LocalDateOrdering: Ordering[LocalDate] = ReadablePartialOrdering[LocalDate]
    implicit val LocalTimeOrdering: Ordering[LocalTime] = ReadablePartialOrdering[LocalTime]
    implicit val LocalDateTimeOrdering: Ordering[LocalDateTime] = ReadablePartialOrdering[LocalDateTime]
    implicit val DurationOrdering: Ordering[Duration] = ReadableDurationOrdering[Duration]

    implicit def ReadableInstantOrdering[A <: ReadableInstant]: Ordering[A] = order[A, ReadableInstant]
    implicit def ReadablePartialOrdering[A <: ReadablePartial]: Ordering[A] = order[A, ReadablePartial]
    implicit def BaseSingleFieldPeriodOrdering[A <: BaseSingleFieldPeriod]: Ordering[A] = order[A, BaseSingleFieldPeriod]
    implicit def ReadableDurationOrdering[A <: ReadableDuration]: Ordering[A] = order[A, ReadableDuration]
    private def order[A, B <: Comparable[B]](implicit ev: A <:< B): Ordering[A] = Ordering.by[A, B](ev)

    import javax.xml.datatype.{XMLGregorianCalendar, DatatypeFactory}

    lazy val factory: DatatypeFactory = DatatypeFactory.newInstance

    implicit def dateTime2XmlGregCalendar(dt: DateTime): XMLGregorianCalendar =
        factory.newXMLGregorianCalendar(dt.toGregorianCalendar)

    implicit def xmlGregCalendar2DateTime(calendar: XMLGregorianCalendar): DateTime =
        new DateTime(calendar.toGregorianCalendar.getTimeInMillis)

    type Year = Int
    type Month = Int
    type Day = Int
    type Hour = Int
    type Minute = Int
    type Second = Int
    type Millis = Int

    implicit def tuple2Time(t: (Year, Month, Day)): DateTime = {
        val (year, month, day) = t
        new DateTime(year, month, day, 0, 0, 0, 0)
    }
    implicit def tuple2Time(t: (Year, Month, Day, Hour)): DateTime = {
        val (year, month, day, hour) = t
        new DateTime(year, month, day, hour, 0, 0, 0)
    }
    implicit def tuple2Time(t: (Year, Month, Day, Hour, Minute)): DateTime = {
        val (year, month, day, hour, minute) = t
        new DateTime(year, month, day, hour, minute, 0, 0)
    }
    implicit def tuple2Time(t: (Year, Month, Day, Hour, Minute, Second)): DateTime = {
        val (year, month, day, hour, minute, second) = t
        new DateTime(year, month, day, hour, minute, second, 0)
    }
    implicit def tuple2Time(t: (Year, Month, Day, Hour, Minute, Second, Millis)): DateTime = {
        val (year, month, day, hour, minute, second, millis) = t
        new DateTime(year, month, day, hour, minute, second, millis)
    }

}
