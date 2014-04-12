package debop4s.core.conversions

import java.util.concurrent.TimeUnit
import scala.concurrent.duration._

/**
 * time
 * Created by debop on 2014. 4. 5.
 */
object time {

  class RichWholeNumber(wrapped: Long) {
    def nanoseconds = Duration(wrapped, TimeUnit.NANOSECONDS)
    def nanosecond = nanoseconds

    def microseconds = Duration(wrapped, TimeUnit.MICROSECONDS)
    def microsecond = microseconds

    def milliseconds = Duration(wrapped, TimeUnit.MILLISECONDS)
    def millisecond = milliseconds

    def seconds = Duration(wrapped, TimeUnit.SECONDS)
    def second = seconds

    def minutes = Duration(wrapped, TimeUnit.MINUTES)
    def minute = minutes

    def hours = Duration(wrapped, TimeUnit.HOURS)
    def hour = hours

    def days = Duration(wrapped, TimeUnit.DAYS)
    def day = days
  }

  private val ZeroRichWholeNumber = new RichWholeNumber(0L) {
    override def nanoseconds = Duration.Zero
    override def microseconds = Duration.Zero
    override def milliseconds = Duration.Zero
    override def seconds = Duration.Zero
    override def minutes = Duration.Zero
    override def hours = Duration.Zero
    override def days = Duration.Zero
  }

  implicit def intToTimeableNumber(i: Int): RichWholeNumber = {
    i match {
      case 0 => ZeroRichWholeNumber
      case _ => new RichWholeNumber(i.toLong)
    }
  }

  implicit def longToTimeableNumber(l: Long): RichWholeNumber = {
    l match {
      case 0L => ZeroRichWholeNumber
      case _ => new RichWholeNumber(l)
    }
  }
}
