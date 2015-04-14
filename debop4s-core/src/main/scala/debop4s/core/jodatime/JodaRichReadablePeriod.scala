package debop4s.core.jodatime

import org.joda.time.{PeriodType, ReadablePeriod}

class JodaRichReadablePeriod(val self: ReadablePeriod) extends AnyVal {

  def periodType: PeriodType = self.getPeriodType

}
