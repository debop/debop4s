package kr.debop4s.timeperiod

import java.sql.Timestamp
import org.joda.time.DateTime

class RichTimestamp(val self: Timestamp) extends AnyVal with Ordered[Timestamp] {

  def toDateTime: DateTime = new DateTime(self)

  def compare(that: Timestamp): Int = self.compare(that)
}
