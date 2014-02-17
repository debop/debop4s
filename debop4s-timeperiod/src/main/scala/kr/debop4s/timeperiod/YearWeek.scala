package kr.debop4s.timeperiod

import kr.debop4s.core.ValueObject
import kr.debop4s.core.utils.Hashs
import kr.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

/**
 * kr.debop4s.timeperiod.YearWeek
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 2. 오전 11:27
 */
class YearWeek(var weekyear: Int = 0, var weekOfWeekyear: Int = 1) extends ValueObject {

  def start: DateTime = Times.startTimeOfWeek(weekyear, weekOfWeekyear)

  def end: DateTime = Times.endTimeOfMonth(weekyear, weekOfWeekyear)

  override def hashCode() = Hashs.compute(weekyear, weekOfWeekyear)

  override protected def buildStringHelper =
    super.buildStringHelper
      .add("weekyear", weekyear)
      .add("weekOfWeekyear", weekOfWeekyear)
}

object YearWeek {

  def apply(weekyear: Int, weekOfWeekyear: Int): YearWeek = new YearWeek(weekyear, weekOfWeekyear)

  def apply(src: YearWeek): YearWeek = new YearWeek(src.weekyear, src.weekOfWeekyear)
}
