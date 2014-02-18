package kr.debop4s.timeperiod

import org.joda.time.{PeriodType, ReadablePeriod}

/**
 * kr.debop4s.time.RichReadablePeriod
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 6. 오후 9:27
 */
class RichReadablePeriod(val self: ReadablePeriod) extends AnyVal {

    def periodType: PeriodType = self.getPeriodType

}
