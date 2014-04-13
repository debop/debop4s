package debop4s.core.conversions

import java.util.concurrent.TimeUnit
import scala.concurrent.duration._

/**
 * time
 * Created by debop on 2014. 4. 5.
 */
object time {

  class RichWholeNumber(wrapped: Long) {
    def toNanos = Duration(wrapped, TimeUnit.NANOSECONDS)
    def toMicros = Duration(wrapped, TimeUnit.MICROSECONDS)
    def toMillis = Duration(wrapped, TimeUnit.MILLISECONDS)
    def toSeconds = Duration(wrapped, TimeUnit.SECONDS)
    def toMinutes = Duration(wrapped, TimeUnit.MINUTES)
    def toHours = Duration(wrapped, TimeUnit.HOURS)
    def toDays = Duration(wrapped, TimeUnit.DAYS)
  }

  private val ZeroRichWholeNumber = new RichWholeNumber(0L) {
    override def toNanos = Duration.Zero
    override def toMicros = Duration.Zero
    override def toMillis = Duration.Zero
    override def toSeconds = Duration.Zero
    override def toMinutes = Duration.Zero
    override def toHours = Duration.Zero
    override def toDays = Duration.Zero
  }

  implicit def intToRichWholeNumber(i: Int): RichWholeNumber = {
    i match {
      case 0 => ZeroRichWholeNumber
      case _ => new RichWholeNumber(i.toLong)
    }
  }

  implicit def intToRichWholeNumber(l: Long): RichWholeNumber = {
    l match {
      case 0L => ZeroRichWholeNumber
      case _ => new RichWholeNumber(l)
    }
  }
}
