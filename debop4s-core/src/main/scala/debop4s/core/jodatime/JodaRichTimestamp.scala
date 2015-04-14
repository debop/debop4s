package debop4s.core.jodatime

import java.sql.Timestamp

import org.joda.time.DateTime

class JodaRichTimestamp(val self: Timestamp) extends AnyVal with Ordered[Timestamp] {

  def toDateTime: DateTime = new DateTime(self)

  def compare(that: Timestamp): Int =
    self.getNanos - that.getNanos
}
