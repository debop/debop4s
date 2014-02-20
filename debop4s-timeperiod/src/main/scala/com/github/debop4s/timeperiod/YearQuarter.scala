package com.github.debop4s.timeperiod

import com.github.debop4s.core.ValueObject
import com.github.debop4s.core.utils.Hashs
import com.github.debop4s.timeperiod.Quarter._
import com.github.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

/**
 * com.github.debop4s.timeperiod.YearQuarter
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 2. 오전 11:27
 */
class YearQuarter(var year: Int, var quarter: Quarter) extends ValueObject {

    def start: DateTime = Times.startTimeOfQuarter(year, quarter)

    def end: DateTime = Times.endTimeOfQuarter(year, quarter)

    override def hashCode() = Hashs.compute(year, quarter)

    override protected def buildStringHelper =
        super.buildStringHelper
        .add("year", year)
        .add("quarter", quarter)
}

object YearQuarter {

    def apply(year: Int, quarter: Quarter): YearQuarter = new YearQuarter(year, quarter)

    def apply(src: YearQuarter): YearQuarter = new YearQuarter(src.year, src.quarter)
}
