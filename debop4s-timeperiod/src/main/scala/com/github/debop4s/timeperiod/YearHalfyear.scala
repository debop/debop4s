package com.github.debop4s.timeperiod

import com.github.debop4s.core.ValueObject
import com.github.debop4s.core.utils.Hashs
import com.github.debop4s.timeperiod.Halfyear._
import com.github.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

/**
 * com.github.debop4s.timeperiod.YearHalfyear
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 2. 오전 11:26
 */
class YearHalfyear(var year: Int, var halfyear: Halfyear) extends ValueObject {

    val start: DateTime = Times.startTimeOfHalfyear(year, halfyear)

    val end: DateTime = Times.endTimeOfHalfyear(year, halfyear)

    override def hashCode() = Hashs.compute(year, halfyear)

    override protected def buildStringHelper =
        super.buildStringHelper
        .add("year", year)
        .add("halfyear", halfyear)
}

object YearHalfyear {

    def apply(year: Int, halfyear: Halfyear): YearHalfyear = new YearHalfyear(year, halfyear)

    def apply(src: YearHalfyear): YearHalfyear = apply(src.year, src.halfyear)
}
