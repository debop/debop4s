package kr.debop4s.timeperiod.timerange

import kr.debop4s.core.utils.{ToStringHelper, Hashs}
import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime
import scala.collection.mutable.ArrayBuffer

/**
 * kr.debop4s.timeperiod.timerange.YearTimeRange
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 27. 오후 6:48
 */
@SerialVersionUID(1604523513628691621L)
class YearTimeRange(private val _year: Int,
                    val yearCount: Int,
                    private val _calendar: ITimeCalendar = DefaultTimeCalendar)
  extends YearCalendarTimeRange(Times.relativeYearPeriod(Times.startTimeOfYear(_year), yearCount), _calendar) {

  def getHalfyears: Seq[HalfyearRange] = {
    val halfyears = new ArrayBuffer[HalfyearRange](yearCount)
    for (y <- 0 until yearCount) {
      Halfyear.values.foreach(hy => halfyears += new HalfyearRange(startYear + y, hy, calendar))
    }
    halfyears
  }

  def getQuarters: Seq[QuarterRange] = {
    val quarters = new ArrayBuffer[QuarterRange](yearCount)
    for (y <- 0 until yearCount) {
      Quarter.values.foreach(q => quarters += new QuarterRange(startYear + y, q, calendar))
    }
    quarters
  }

  def getMonths: Seq[MonthRange] = {
    val months = new ArrayBuffer[MonthRange](yearCount * MonthsPerYear)
    for (y <- 0 until yearCount) {
      val baseTime = start.plusYears(y)
      for (m <- 0 until MonthsPerYear)
        months += MonthRange(baseTime.plusMonths(m), calendar)
    }
    months
  }

  override def hashCode(): Int = Hashs.compute(startYear, yearCount)

  override protected def buildStringHelper: ToStringHelper =
    ToStringHelper(this)
    .add("year", startYear)
    .add("yearCount", yearCount)
    .add("calendar", calendar)
}

object YearTimeRange {

  def apply(year: Int, yearCount: Int): YearTimeRange =
    apply(year, yearCount, DefaultTimeCalendar)

  def apply(year: Int, yearCount: Int, calendar: ITimeCalendar): YearTimeRange =
    new YearTimeRange(year, yearCount, calendar)

  def apply(moment: DateTime, yearCount: Int): YearTimeRange =
    apply(moment, yearCount, DefaultTimeCalendar)


  def apply(moment: DateTime, yearCount: Int, calendar: ITimeCalendar): YearTimeRange =
    new YearTimeRange(moment.getYear, yearCount, calendar)
}