package com.github.debop4s.timeperiod

import com.github.debop4s.core.ValueObject
import com.github.debop4s.core.utils.Hashs
import com.github.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

/**
 * com.github.debop4s.timeperiod.YearMonth
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 2. 오전 11:27
 */
class YearMonth(val year: Int, val monthOfYear: Int) extends ValueObject {

    val start: DateTime = Times.startTimeOfMonth(year, monthOfYear)

    val end: DateTime = Times.endTimeOfMonth(year, monthOfYear)

    override def hashCode() = Hashs.compute(year, monthOfYear)

    override protected def buildStringHelper =
        super.buildStringHelper
        .add("year", year)
        .add("monthOfYear", monthOfYear)
}

object YearMonth {

    def apply(year: Int = 0, monthOfYear: Int = 1): YearMonth = new YearMonth(year, monthOfYear)

    def apply(src: YearMonth): YearMonth = {
        require(src != null)
        new YearMonth(src.year, src.monthOfYear)
    }
}
