package debop4s.timeperiod.timerange

import debop4s.timeperiod.TimeSpec._
import debop4s.timeperiod._
import debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

/**
 * kr.hconnect.timeperiod.timerange.QuarterRange
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 29. 오후 5:41
 */
@SerialVersionUID(-5373404703149628573L)
class QuarterRange(private[this] val _year: Int,
                   private[this] val _quarter: Quarter,
                   private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
  extends QuarterTimeRange(_year, _quarter, 1, _calendar) {

  def this() = this(Times.today.getYear, Times.quarterOf(Times.today), DefaultTimeCalendar)
  def this(year: Int, quarter: Quarter) = this(year, quarter, DefaultTimeCalendar)
  def this(moment: DateTime) = this(moment.getYear, Times.quarterOf(moment), DefaultTimeCalendar)
  def this(moment: DateTime, calendar: ITimeCalendar) = this(moment.getYear, Times.quarterOf(moment), calendar)

  def year: Int = startYear
  def getYear = year

  def quarter: Quarter = startQuarter
  def getQuarter = quarter

  def addQuarters(quarters: Int): QuarterRange = {
    val yq = Times.addQuarter(startYear, startQuarter, quarters)
    new QuarterRange(yq.year, yq.quarter, calendar)
  }

  def nextQuarter: QuarterRange = addQuarters(1)

  def previousQuarter: QuarterRange = addQuarters(-1)
}

object QuarterRange {

  def apply(): QuarterRange = apply(Times.now)

  def apply(year: Int, quarter: Quarter): QuarterRange = apply(year, quarter, DefaultTimeCalendar)

  def apply(year: Int, quarter: Quarter, calendar: ITimeCalendar): QuarterRange =
    new QuarterRange(year, quarter, calendar)

  def apply(moment: DateTime): QuarterRange = apply(moment, DefaultTimeCalendar)

  def apply(moment: DateTime, calendar: ITimeCalendar): QuarterRange =
    new QuarterRange(moment.getYear, Times.quarterOfMonth(moment.getMonthOfYear), calendar)
}