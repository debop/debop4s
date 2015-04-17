package debop4s.timeperiod.timerange

import java.util

import com.google.common.collect.Lists
import debop4s.timeperiod.TimeSpec._
import debop4s.timeperiod._
import debop4s.timeperiod.utils.Times

import scala.beans.BeanProperty
import scala.collection.SeqView


@SerialVersionUID(-1642725884160403253L)
abstract class QuarterTimeRange(private[this] val _year: Int,
                                private[this] val _quarter: Quarter,
                                @BeanProperty val quarterCount: Int,
                                private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
  extends CalendarTimeRange(QuarterTimeRange.getPeriodOf(_year, _quarter, quarterCount, _calendar),
                             _calendar) {

  val startQuarter: Quarter = _quarter
  val endQuarter: Quarter = Times.quarterOfMonth(end.getMonthOfYear)

  def getStartQuarter = startQuarter
  def getEndQuarter = endQuarter

  override def startMonthOfYear: Int = Times.startMonthOfQuarter(startQuarter)

  override def endMonthOfYear: Int = Times.endMonthOfQuarter(endQuarter)

  def isMultipleCalendarYears: Boolean = startYear != endYear

  def months: SeqView[MonthRange, Seq[_]] = {
    val monthCount = quarterCount * MonthsPerQuarter
    (0 until monthCount).view.map { m =>
      MonthRange(start.plusMonths(m), calendar)
    }
  }

  def getMonths: util.List[MonthRange] = {
    val monthCount = quarterCount * MonthsPerQuarter
    val mrs = Lists.newArrayListWithCapacity[MonthRange](monthCount)
    (0 until monthCount) foreach { m =>
      mrs add MonthRange(start.plusMonths(m), calendar)
    }
    mrs
  }
}

object QuarterTimeRange {

  private[timerange] def getPeriodOf(year: Int,
                                     quarter: Quarter,
                                     quarterCount: Int,
                                     calendar: ITimeCalendar): ITimePeriod = {
    require(quarterCount > 0)

    val yearStart = Times.asDate(year, 1, 1)
    val start = yearStart.plusMonths((quarter.getValue - 1) * MonthsPerQuarter)
    val end = start.plusMonths(quarterCount * MonthsPerQuarter)

    TimeRange(start, end)
  }
}