package kr.debop4s.timeperiod.timerange

import kr.debop4s.timeperiod.Quarter.Quarter
import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

/**
 * kr.debop4s.timeperiod.timerange.QuarterRange
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 29. 오후 5:41
 */
@SerialVersionUID(-5373404703149628573L)
class QuarterRange(private val _year: Int,
                   private val _quarter: Quarter,
                   private val _calendar: ITimeCalendar = DefaultTimeCalendar)
  extends QuarterTimeRange(_year, _quarter, 1, _calendar) {

  def year: Int = startYear

  def quarter: Quarter = startQuarter

  def addQuarters(quarters: Int): QuarterRange = {
    val yq = Times.addQuarter(startYear, startQuarter, quarters)
    new QuarterRange(yq.year, yq.quarter, calendar)
  }

  def nextQuarter: QuarterRange = addQuarters(1)

  def previousQuarter: QuarterRange = addQuarters(-1)
}

object QuarterRange {

  def apply(year: Int, quarter: Quarter): QuarterRange = apply(year, quarter, DefaultTimeCalendar)

  def apply(year: Int, quarter: Quarter, calendar: ITimeCalendar): QuarterRange =
    new QuarterRange(year, quarter, calendar)

  def apply(moment: DateTime): QuarterRange = apply(moment, DefaultTimeCalendar)

  def apply(moment: DateTime, calendar: ITimeCalendar): QuarterRange =
    new QuarterRange(moment.getYear, Times.getQuarterOfMonth(moment.getMonthOfYear), calendar)
}