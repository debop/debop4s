package kr.debop4s.time

import java.sql.Timestamp
import org.joda.time._
import org.joda.time.base._
import org.joda.time.field._
import org.joda.time.format._
import scala.language.implicitConversions

object Implicits extends Implicits

object BuilderImplicits extends Implicits

object IntImplicits extends IntImplicits

object JodaImplicits extends JodaImplicits

trait Implicits extends BuilderImplicits
                        with IntImplicits
                        with JavaImplicits
                        with JodaImplicits
                        with OrderingImplicits
                        with XmlImplicits
                        with TupleImplicits

trait BuilderImplicits {
    implicit def forcePeriod(builder: DurationBuilder): Period = builder.underlying
    implicit def forceDuration(builder: DurationBuilder): Duration = builder.underlying.toStandardDuration
}

trait IntImplicits {
    implicit def richInt(n: Int): RichInt = new kr.debop4s.time.RichInt(n)
    implicit def richLong(n: Long): RichLong = new kr.debop4s.time.RichLong(n)
}

trait JavaImplicits {
    implicit def richTimestamp(self: Timestamp): RichTimestamp = new RichTimestamp(self)
}

trait JodaImplicits {
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
}

trait OrderingImplicits extends LowPriorityOrderingImplicits {
    implicit val DateTimeOrdering = ReadableInstantOrdering[DateTime]
    implicit val DateMidnightOrdering = ReadableInstantOrdering[DateMidnight]
    implicit val LocalDateOrdering = ReadablePartialOrdering[LocalDate]
    implicit val LocalTimeOrdering = ReadablePartialOrdering[LocalTime]
    implicit val LocalDateTimeOrdering = ReadablePartialOrdering[LocalDateTime]
    implicit val DurationOrdering = ReadableDurationOrdering[Duration]
}

trait LowPriorityOrderingImplicits {
    implicit def ReadableInstantOrdering[A <: ReadableInstant] = order[A, ReadableInstant]
    implicit def ReadablePartialOrdering[A <: ReadablePartial] = order[A, ReadablePartial]
    implicit def BaseSingleFieldPeriodOrdering[A <: BaseSingleFieldPeriod] = order[A, BaseSingleFieldPeriod]
    implicit def ReadableDurationOrdering[A <: ReadableDuration] = order[A, ReadableDuration]
    private def order[A, B <: Comparable[B]](implicit ev: A <:< B): Ordering[A] = Ordering.by[A, B](ev)
}

trait XmlImplicits {

    import javax.xml.datatype.{XMLGregorianCalendar, DatatypeFactory}

    lazy val factory = DatatypeFactory.newInstance

    implicit def dateTime2XmlGregCalendar(dt: DateTime) =
        factory.newXMLGregorianCalendar(dt.toGregorianCalendar)

    implicit def xmlGregCalendar2DateTime(calendar: XMLGregorianCalendar) =
        new DateTime(calendar.toGregorianCalendar.getTimeInMillis)
}

trait TupleImplicits {
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