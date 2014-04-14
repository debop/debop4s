package debop4s.core.jodatime

import org.joda.time.{ReadablePeriod, PeriodType}

/**
 * com.github.time.RichReadablePeriod
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 6. 오후 9:27
 */
class RichReadablePeriod(val self: ReadablePeriod) extends AnyVal {

    def periodType: PeriodType = self.getPeriodType

}
