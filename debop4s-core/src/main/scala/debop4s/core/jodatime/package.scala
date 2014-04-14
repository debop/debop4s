package debop4s.core

import java.sql.Timestamp
import org.joda.time._
import org.joda.time.base.{BaseSingleFieldPeriod, AbstractPartial, AbstractInstant, AbstractDateTime}
import org.joda.time.field.AbstractReadableInstantFieldProperty
import org.joda.time.format.DateTimeFormatter
import org.joda.time.{Duration => JDuration}

/**
 * package
 * Created by debop on 2014. 4. 14.
 */
package object jodatime {

    implicit def forcePeriod(builder: DurationBuilder) = builder.underlying

    implicit def forceDuration(builder: DurationBuilder) = builder.underlying.toStandardDuration

    implicit def richInt(n: Int) = new debop4s.core.jodatime.RichInt(n)

    implicit def richLong(n: Long) = new debop4s.core.jodatime.RichLong(n)

    implicit def richTimestamp(self: Timestamp) = new debop4s.core.jodatime.RichTimestamp(self)

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

    val JDateTime = debop4s.core.jodatime.JodaDateTime
    val JDateTimeFormat = debop4s.core.jodatime.JodaDateTimeFormat
    val JISODateTimeFormat = debop4s.core.jodatime.JodaISODateTimeFormat
    val JDateTimeZone = debop4s.core.jodatime.JodaDateTimeZone
    val JDuration = debop4s.core.jodatime.JodaDuration
    val JInterval = debop4s.core.jodatime.JodaInterval
    val JLocalDate = debop4s.core.jodatime.JodaLocalDate
    val JLocalDateTime = debop4s.core.jodatime.JodaLocalDateTime
    val JLocalTime = debop4s.core.jodatime.JodaLocalTime
    val JPeriod = debop4s.core.jodatime.JodaPeriod
    val JPartial = debop4s.core.jodatime.JodaPartial
}
