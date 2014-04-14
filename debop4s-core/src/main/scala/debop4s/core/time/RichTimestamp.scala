package debop4s.core.time

import debop4s.core._
import java.sql.Timestamp
import org.joda.time.DateTime

class RichTimestamp(val self: Timestamp) extends AnyVal with Ordered[Timestamp] {

    def toDateTime: DateTime = new DateTime(self)

    def compare(that: Timestamp): Int = self.compare(that)
}
