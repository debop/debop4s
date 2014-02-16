package kr.debop4s.timeperiod.utils

import java.util.Locale
import kr.debop4s.timeperiod.DayOfWeek.DayOfWeek
import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.timerange.WeekRange
import org.joda.time.DateTime
import org.slf4j.LoggerFactory

/**
 * kr.debop4s.timeperiod.utils.Weeks
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 2. 오후 9:23
 */
object Weeks {

  lazy val log = LoggerFactory.getLogger(getClass)

  def firstDayOfWeek: DayOfWeek = FirstDayOfWeek

  def firstDayOfWeek(locale: Locale): DayOfWeek = FirstDayOfWeek

  def getYearAndWeek(moment: DateTime, baseMonth: Int = 1): YearWeek =
    YearWeek(moment.getWeekyear, moment.getWeekOfWeekyear)

  def getEndYearAndWeek(year: Int) = {
    val m = Times.asDate(year, 12, 28)
    YearWeek(m.getWeekyear, m.getWeekOfWeekyear)
  }

  def getWeekRange(v: YearWeek) = new WeekRange(v.year, v.weekOfYear)

  def getStartWeekRangeOfYear(year: Int) = getWeekRange(new YearWeek(year, 1))

  def getEndWeekRangeOfYear(year: Int) = getWeekRange(getEndYearAndWeek(year))

  def addWeekOfYears(year: Int, weekOfYear: Int, weeks: Int): YearWeek = {
    addWeekOfYears(YearWeek(year, weekOfYear), weeks)
  }

  def addWeekOfYears(yw: YearWeek, weeks: Int): YearWeek = {
    log.trace(s"주차 연산을 수행합니다. yearWeek=[$yw], weeks=[$weeks]")

    var result = YearWeek(yw)

    if (weeks == 0)
      result

    result = if (weeks > 0) plusWeeks(yw, weeks) else minusWeeks(yw, weeks)

    log.debug(s"주차연산을 수행했습니다. yearWeek=[$yw], weeks=[$weeks], result=[$result]")
    result
  }

  private def plusWeeks(yw: YearWeek, weeks: Int): YearWeek = {
    val result = YearWeek(yw)
    var newWeeks = weeks + result.weekOfYear

    if (newWeeks < getEndYearAndWeek(result.year).weekOfYear) {
      result.weekOfYear = newWeeks
      result
    } else {
      while (newWeeks >= 0) {
        val endWeek = getEndYearAndWeek(result.year)
        if (newWeeks <= endWeek.weekOfYear) {
          result.weekOfYear = Math.max(newWeeks, 1)
          return result
        }
        newWeeks -= endWeek.weekOfYear
        result.year = result.year + 1
      }
      result.weekOfYear = Math.max(newWeeks, 1)
      result
    }
  }

  private def minusWeeks(yw: YearWeek, weeks: Int): YearWeek = {
    val result = YearWeek(yw)
    var week = weeks + result.weekOfYear

    if (week == 0) {
      result.year -= 1
      result.weekOfYear = getEndYearAndWeek(result.year).weekOfYear
      result
    } else if (week > 0) {
      result.weekOfYear = week
      result
    } else {
      while (week <= 0) {
        result.year -= 1
        val endWeek = getEndYearAndWeek(result.year)
        week += endWeek.weekOfYear

        if (week > 0) {
          result.weekOfYear = Math.max(week, 1)
          return result
        }
      }
      result.weekOfYear = Math.max(week, 1)
      result
    }
  }

  /**
   * 해당 일자의 월 주차 (week of month)
   */
  def getMonthAndWeekOfMonth(moment: DateTime): (Int, Int) = {
    val result = moment.getWeekOfWeekyear - Times.startTimeOfMonth(moment).getWeekOfWeekyear + 1
    if (result > 0) (moment.getMonthOfYear, result)
    else (moment.plusMonths(1).getMonthOfYear, result)
  }
}
