package debop4s.timeperiod.timerange

import java.util

import com.google.common.collect.Lists
import debop4s.core.conversions.jodatime._
import debop4s.timeperiod.TimeSpec._
import debop4s.timeperiod._
import debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

import scala.beans.BeanProperty
import scala.collection.SeqView

/**
 * debop4s.timeperiod.timerange.YearTimeRange
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 27. 오후 6:48
 */
@SerialVersionUID(1604523513628691621L)
class YearTimeRange(private[this] val _year: Int,
                    @BeanProperty val yearCount: Int,
                    private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
  extends YearCalendarTimeRange(Times.relativeYearPeriod(Times.startTimeOfYear(_year), yearCount), _calendar) {

  def this(year: Int, yearCount: Int) = this(year, yearCount, DefaultTimeCalendar)
  def this(moment: DateTime, yearCount: Int) = this(moment.getYear, yearCount, DefaultTimeCalendar)
  def this(moment: DateTime, yearCount: Int, calendar: ITimeCalendar) = this(moment.getYear, yearCount, calendar)

  def halfyearsView: SeqView[HalfyearRange, Seq[_]] = {
    for {
      y <- (0 until yearCount).view
      hy <- Halfyear.values.view
    } yield {
      HalfyearRange(startYear + y, hy, calendar)
    }
  }

  @inline
  def getHalfyears: util.List[HalfyearRange] = {
    val halfyears = Lists.newArrayListWithCapacity[HalfyearRange](yearCount * 2)
    var y = 0
    while (y < yearCount) {
      halfyears.add(HalfyearRange(startYear + y, Halfyear.First, calendar))
      halfyears.add(HalfyearRange(startYear + y, Halfyear.Second, calendar))
      y += 1
    }
    halfyears
  }

  def quartersView: SeqView[QuarterRange, Seq[_]] = {
    for {
      y <- (0 until yearCount).view
      q <- Quarter.values.view
    } yield {
      QuarterRange(startYear + y, q, calendar)
    }
  }

  @inline
  def getQuarters: util.List[QuarterRange] = {
    val quarters = Lists.newArrayListWithCapacity[QuarterRange](yearCount * 4)
    var y = 0
    while (y < yearCount) {
      val syear = startYear + y
      quarters.add(QuarterRange(syear, Quarter.First, calendar))
      quarters.add(QuarterRange(syear, Quarter.Second, calendar))
      quarters.add(QuarterRange(syear, Quarter.Third, calendar))
      quarters.add(QuarterRange(syear, Quarter.Fourth, calendar))
      y += 1
    }
    quarters
  }

  def monthsView: SeqView[MonthRange, Seq[_]] = {
    for {
      y <- (0 until yearCount).view
      baseTime = start + y.years
      m <- (0 until MonthsPerYear).view
    } yield MonthRange(baseTime.plusMonths(m), calendar)
  }

  @inline
  def getMonths: util.List[MonthRange] = {
    val months = new util.ArrayList[MonthRange](yearCount * MonthsPerYear)
    var y = 0
    while (y < yearCount) {
      val baseTime = start.plusYears(y)
      var m = 0
      while (m < MonthsPerYear) {
        months.add(MonthRange(baseTime.plusMonths(m), calendar))
        m += 1
      }
      y += 1
    }
    months
  }
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