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
 * kr.hconnect.timeperiod.timerange.YearTimeRange
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

  def halfyears: SeqView[HalfyearRange, Seq[_]] = {
    for {
      y <- (0 until yearCount).view
      hy <- Halfyear.values.view
    } yield {
      HalfyearRange(startYear + y, hy, calendar)
    }
  }

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

  def quarters = {
    for {
      y <- (0 until yearCount).view
      q <- Quarter.values.view
    } yield {
      QuarterRange(startYear + y, q, calendar)
    }
  }

  def getQuarters: util.List[QuarterRange] = {
    val quarters = Lists.newArrayListWithCapacity[QuarterRange](yearCount * 4)
    (0 until yearCount) foreach { y =>
      quarters.add(QuarterRange(startYear + y, Quarter.First, calendar))
      quarters.add(QuarterRange(startYear + y, Quarter.Second, calendar))
      quarters.add(QuarterRange(startYear + y, Quarter.Third, calendar))
      quarters.add(QuarterRange(startYear + y, Quarter.Fourth, calendar))
    }
    quarters
  }

  def months = {
    for {
      y <- (0 until yearCount).view
      baseTime = start + y.years
      m <- (0 until MonthsPerYear).view
    } yield MonthRange(baseTime.plusMonths(m), calendar)
  }

  def getMonths: util.List[MonthRange] = {
    val months = new util.ArrayList[MonthRange](yearCount * MonthsPerYear)
    (0 until yearCount) foreach { y =>
      val baseTime = start.plusYears(y)
      (0 until MonthsPerYear) foreach { m =>
        months.add(MonthRange(baseTime.plusMonths(m), calendar))
      }
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