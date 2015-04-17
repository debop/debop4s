package debop4s.timeperiod.timerange

import java.util

import debop4s.timeperiod.TimeSpec._
import debop4s.timeperiod._
import debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

import scala.beans.BeanProperty
import scala.collection.SeqView


/**
 * kr.hconnect.timeperiod.timerange.HalfyearTimeRange
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 29. 오후 5:49
 */
class HalfyearTimeRange(@BeanProperty val year: Int,
                        @BeanProperty val halfyear: Halfyear,
                        @BeanProperty val halfyearCount: Int,
                        private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
  extends CalendarTimeRange(HalfyearTimeRange.getPeriodOf(year, halfyear, halfyearCount), _calendar) {

  val startHalfyear: Halfyear = halfyear
  val endHalfyear: Halfyear = Times.halfyearOfMonth(endMonthOfYear)

  def getStartHalfyear = startHalfyear
  def getEndHalfyear = endHalfyear

  def isMultipleCalendarYears: Boolean = startYear != endYear

  def quarters: SeqView[QuarterRange, Seq[_]] = {
    val quarterCount = halfyearCount * QuartersPerHalfyear
    val startQuarter = Times.quarterOf(startMonthOfYear)

    (0 until quarterCount).view.map { q =>
      val yq = Times.addQuarter(startYear, startQuarter, q)
      QuarterRange(yq.year, yq.quarter, calendar)
    }
  }

  def getQuarters: util.List[QuarterRange] = {
    val quarterCount = halfyearCount * QuartersPerHalfyear
    val startQuarter = Times.quarterOf(startMonthOfYear)

    val results = new util.ArrayList[QuarterRange](quarterCount)
    var q = 0
    while (q < quarterCount) {
      val yq = Times.addQuarter(startYear, startQuarter, q)
      results add QuarterRange(yq.year, yq.quarter, calendar)
      q += 1
    }
    results
  }

  def months = {
    val monthCount = halfyearCount * MonthsPerHalfyear

    (0 until monthCount).view.map { m =>
      val ym = Times.addMonth(startYear, startMonthOfYear, m)
      MonthRange(ym.year, ym.monthOfYear, calendar)
    }
  }

  def getMonths: util.List[MonthRange] = {
    val monthCount = halfyearCount * MonthsPerHalfyear
    val results = new util.ArrayList[MonthRange](monthCount)
    var m = 0

    while (m < monthCount) {
      val ym = Times.addMonth(startYear, startMonthOfYear, m)
      results add MonthRange(ym.year, ym.monthOfYear, calendar)
      m += 1
    }
    results
  }
}

object HalfyearTimeRange {

  def apply(moment: DateTime, halfyearCount: Int, calendar: ITimeCalendar): HalfyearTimeRange = {
    new HalfyearTimeRange(moment.getYear,
      Times.halfyearOfMonth(moment.getMonthOfYear),
      halfyearCount,
      calendar)
  }

  def apply(moment: DateTime, halfyearCount: Int): HalfyearTimeRange = {
    new HalfyearTimeRange(moment.getYear,
      Times.halfyearOfMonth(moment.getMonthOfYear),
      halfyearCount,
      DefaultTimeCalendar)
  }

  def getPeriodOf(year: Int, halfyear: Halfyear, halfyearCount: Int): TimeRange = {
    val yearStart = Times.startTimeOfYear(year)
    val start = yearStart.plusMonths((halfyear.getValue - 1) * MonthsPerHalfyear)
    val end = start.plusMonths(halfyearCount * MonthsPerHalfyear)
    TimeRange(start, end)
  }
}
