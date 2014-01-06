package kr.debop4s.timeperiod

import java.util.Locale
import kr.debop4s.core.ValueObject
import kr.debop4s.core.utils.{ToStringHelper, Hashs}
import kr.debop4s.timeperiod.DayOfWeek.DayOfWeek
import org.joda.time.Duration

/**
 * kr.debop4s.timeperiod.TimeCalendarConfig
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 26. 오후 1:54
 */
class TimeCalendarConfig(var locale: Locale = Locale.getDefault,
                         var startOffset: Duration = DefaultStartOffset,
                         var endOffset: Duration = DefaultEndOffset) extends ValueObject {

    var firstDayOfWeek: DayOfWeek = DayOfWeek.Monday

    override def hashCode(): Int =
        Hashs.compute(locale, startOffset, endOffset)

    override protected def buildStringHelper: ToStringHelper =
        super.buildStringHelper
            .add("locale", locale)
            .add("startOffset", startOffset)
            .add("endOffset", endOffset)
}
