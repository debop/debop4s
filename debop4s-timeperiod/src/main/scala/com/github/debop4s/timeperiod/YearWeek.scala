package com.github.debop4s.timeperiod

import com.github.debop4s.core.ValueObject
import com.github.debop4s.core.utils.Hashs
import com.github.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

/**
 * 주차를 표현합니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 2. 오전 11:27
 */
class YearWeek(var weekyear: Int, var weekOfWeekyear: Int) extends ValueObject {

    def start: DateTime = Times.startTimeOfWeek(weekyear, weekOfWeekyear)

    def end: DateTime = Times.endTimeOfMonth(weekyear, weekOfWeekyear)

    override def hashCode() = Hashs.compute(weekyear, weekOfWeekyear)

    override protected def buildStringHelper =
        super.buildStringHelper
        .add("weekyear", weekyear)
        .add("weekOfWeekyear", weekOfWeekyear)
}

object YearWeek {

    def apply(weekyear: Int = 0, weekOfWeekyear: Int = 1): YearWeek = {
        new YearWeek(weekyear, weekOfWeekyear)
    }

    def apply(src: YearWeek): YearWeek = {
        require(src != null)
        new YearWeek(src.weekyear, src.weekOfWeekyear)
    }
}
