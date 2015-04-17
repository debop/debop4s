package debop4s.timeperiod.utils

import java.util.Locale

import debop4s.core._
import debop4s.timeperiod.TimeSpec._
import debop4s.timeperiod._
import debop4s.timeperiod.timerange.WeekRange
import org.joda.time.DateTime

/**
 * Weeks
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 2. 오후 9:23
 */
object Weeks extends Logging {

  def firstDayOfWeek: DayOfWeek = FirstDayOfWeek

  def firstDayOfWeek(locale: Locale): DayOfWeek = FirstDayOfWeek

  def yearAndWeek(moment: DateTime): YearWeek =
    YearWeek(moment.getWeekyear, moment.getWeekOfWeekyear)

  def endYearAndWeek(year: Int): YearWeek = {
    val m = Times.asDate(year, 12, 28)
    YearWeek(m.getWeekyear, m.getWeekOfWeekyear)
  }

  def weekRange(v: YearWeek) = new WeekRange(v.weekyear, v.weekOfWeekyear)

  def startWeekRangeOfYear(year: Int) = weekRange(new YearWeek(year, 1))

  def endWeekRangeOfYear(year: Int) = weekRange(endYearAndWeek(year))

  def addWeekOfYears(weekyear: Int, weekOfYear: Int, weeks: Int): YearWeek = {
    addWeekOfYears(YearWeek(weekyear, weekOfYear), weeks)
  }

  def addWeekOfYears(yw: YearWeek, weeks: Int): YearWeek = {
    if (weeks == 0)
      return YearWeek(yw.weekyear, yw.weekOfWeekyear)

    if (weeks > 0) plusWeeks(yw, weeks)
    else minusWeeks(yw, weeks)
  }

  @inline
  private def plusWeeks(yw: YearWeek, weeks: Int): YearWeek = {
    var newWeeks = weeks + yw.weekOfWeekyear

    if (newWeeks < endYearAndWeek(yw.weekyear).weekOfWeekyear) {
      return YearWeek(yw.weekyear, newWeeks)
    }

    var weekyear = yw.weekyear
    while (newWeeks >= 0) {
      val endWeek = endYearAndWeek(weekyear)
      if (newWeeks <= endWeek.weekOfWeekyear) {
        return YearWeek(weekyear, newWeeks max 1)
      }
      newWeeks -= endWeek.weekOfWeekyear
      weekyear += 1
    }
    YearWeek(weekyear, newWeeks max 1)
  }

  @inline
  private def minusWeeks(yw: YearWeek, weeks: Int): YearWeek = {
    var week = weeks + yw.weekOfWeekyear

    if (week == 0) {
      return YearWeek(yw.weekyear - 1, endYearAndWeek(yw.weekyear - 1).weekOfWeekyear)
    } else if (week > 0) {
      return YearWeek(yw.weekyear, week)
    }

    var weekyear = yw.weekyear
    while (week <= 0) {
      weekyear -= 1
      val endWeek = endYearAndWeek(weekyear)
      week += endWeek.weekOfWeekyear

      if (week > 0) {
        return YearWeek(weekyear, week max 1)
      }
    }
    YearWeek(weekyear, week max 1)
  }

  /**
   * 해당 일자의 월 주차 (week of month)
   */
  def monthWeek(moment: DateTime): MonthWeek = {
    val result = moment.getWeekOfWeekyear - Times.startTimeOfMonth(moment).getWeekOfWeekyear + 1

    if (result > 0) MonthWeek(moment.getMonthOfYear, result)
    else MonthWeek(moment.plusMonths(1).getMonthOfYear, result)
  }
}
