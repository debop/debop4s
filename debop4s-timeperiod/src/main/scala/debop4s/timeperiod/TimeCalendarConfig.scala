package debop4s.timeperiod

import debop4s.core.ValueObject
import debop4s.core.utils.{ToStringHelper, Hashs}
import debop4s.timeperiod.DayOfWeek.DayOfWeek
import java.util.Locale
import org.joda.time.Duration

/**
 * debop4s.timeperiod.TimeCalendarConfig
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 26. 오후 1:54
 */
class TimeCalendarConfig(val locale: Locale = Locale.getDefault,
                         val startOffset: Duration = DefaultStartOffset,
                         val endOffset: Duration = DefaultEndOffset) extends ValueObject {

    val firstDayOfWeek: DayOfWeek = DayOfWeek.Monday

    override def hashCode(): Int =
        Hashs.compute(locale, startOffset, endOffset)

    override protected def buildStringHelper: ToStringHelper =
        super.buildStringHelper
        .add("locale", locale)
        .add("startOffset", startOffset)
        .add("endOffset", endOffset)
}

object TimeCalendarConfig {

    def apply(): TimeCalendarConfig = {
        new TimeCalendarConfig()
    }

    def apply(locale: Locale = Locale.getDefault,
              startOffset: Duration = DefaultStartOffset,
              endOffset: Duration = DefaultEndOffset): TimeCalendarConfig = {
        new TimeCalendarConfig(locale, startOffset, endOffset)
    }
}
