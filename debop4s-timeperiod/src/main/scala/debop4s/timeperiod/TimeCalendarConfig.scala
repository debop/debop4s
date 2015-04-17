package debop4s.timeperiod

import java.util.Locale

import debop4s.core.utils.Hashs
import debop4s.core.{ToStringHelper, ValueObject}
import debop4s.timeperiod.TimeSpec._
import org.joda.time.Duration

case class TimeCalendarConfig(locale: Locale = Locale.getDefault,
                              startOffset: Duration = DefaultStartOffset,
                              endOffset: Duration = DefaultEndOffset) extends ValueObject {

  val firstDayOfWeek: DayOfWeek = DayOfWeek.Monday

  override def hashCode: Int =
    Hashs.compute(locale, startOffset, endOffset)

  override protected def buildStringHelper: ToStringHelper =
    super.buildStringHelper
    .add("locale", locale)
    .add("startOffset", startOffset)
    .add("endOffset", endOffset)
}
