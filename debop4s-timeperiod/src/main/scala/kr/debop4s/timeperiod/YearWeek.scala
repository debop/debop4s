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
class YearWeek(var year: Int = 0, var weekOfYear: Int = 1) extends ValueObject {

  def this(yw: YearWeek) {
    this(yw.year, yw.weekOfYear)
  }

  def start: DateTime = Times.startTimeOfWeek(year, weekOfYear)

  def end: DateTime = Times.endTimeOfMonth(year, weekOfYear)

  override def hashCode() = Hashs.compute(year, weekOfYear)

  override protected def buildStringHelper =
    super.buildStringHelper
    .add("year", year)
    .add("weekOfYear", weekOfYear)
}

object YearWeek {

  def apply(year: Int, weekOfYear: Int): YearWeek = new YearWeek(year, weekOfYear)

  def apply(src: YearWeek): YearWeek = new YearWeek(src.year, src.weekOfYear)
}
