package debop4s.core.time

import org.joda.time.{DateTimeZone, Chronology}

/**
 * com.github.time.RichChronology
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 5. 오후 11:48
 */
class RichChronology(val self: Chronology) extends AnyVal {

    def zone: Option[DateTimeZone] = nullCheck(self.getZone)

    private def nullCheck[T <: AnyRef](v: T): Option[T] = if (v == null) None else Some(v)
}
