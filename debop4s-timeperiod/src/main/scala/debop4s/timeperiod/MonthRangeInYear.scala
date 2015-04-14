package debop4s.timeperiod

import debop4s.core.ValueObjectBase
import debop4s.core.utils.ToStringHelper

/**
 * debop4s.timeperiod.MonthRangeInYear
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 3. 오전 10:42
 */
@SerialVersionUID(-1797303419172720812L)
class MonthRangeInYear(val startMonthOfYear: Int, val endMonthOfYear: Int)
  extends ValueObjectBase with Ordered[MonthRangeInYear] {

  require(startMonthOfYear <= endMonthOfYear,
           s"startMonthOfYear[$startMonthOfYear] <= endMonthOfYear[$endMonthOfYear] 여야 합니다.")

  def isSingleMonth = startMonthOfYear == endMonthOfYear

  def hasInside(monthOfYear: Int) =
    startMonthOfYear <= monthOfYear && monthOfYear <= endMonthOfYear

  def compare(that: MonthRangeInYear) =
    hashCode() - that.hashCode()

  override def hashCode(): Int =
    startMonthOfYear * 100 + endMonthOfYear

  override protected def buildStringHelper: ToStringHelper =
    super.buildStringHelper
    .add("startMonthOfYear", startMonthOfYear)
    .add("endMonthOfYear", endMonthOfYear)
}

object MonthRangeInYear {

  def apply(startMonthOfYear: Int = 1, endMonthOfYear: Int = 12): MonthRangeInYear =
    new MonthRangeInYear(startMonthOfYear, endMonthOfYear)

  def apply(period: ITimePeriod): MonthRangeInYear =
    apply(period.start.getMonthOfYear, period.end.getMonthOfYear)
}

