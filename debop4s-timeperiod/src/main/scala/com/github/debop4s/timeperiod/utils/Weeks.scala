package com.github.debop4s.timeperiod.utils

import com.github.debop4s.timeperiod.DayOfWeek.DayOfWeek
import com.github.debop4s.timeperiod._
import com.github.debop4s.timeperiod.timerange.WeekRange
import java.util.Locale
import org.joda.time.DateTime
import org.slf4j.LoggerFactory

/**
 * com.github.debop4s.timeperiod.utils.Weeks
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

    def getWeekRange(v: YearWeek) = new WeekRange(v.weekyear, v.weekOfWeekyear)

    def getStartWeekRangeOfYear(year: Int) = getWeekRange(new YearWeek(year, 1))

    def getEndWeekRangeOfYear(year: Int) = getWeekRange(getEndYearAndWeek(year))

    def addWeekOfYears(year: Int, weekOfYear: Int, weeks: Int): YearWeek = {
        addWeekOfYears(YearWeek(year, weekOfYear), weeks)
    }


    def addWeekOfYears(yw: YearWeek, weeks: Int): YearWeek = {
        if (weeks == 0)
            YearWeek(yw)
        else {
            val result = if (weeks > 0) plusWeeks(yw, weeks) else minusWeeks(yw, weeks)
            // log.trace(s"주차연산을 수행했습니다. yearWeek=[$yw], weeks=[$weeks], result=[$result]")
            result
        }
    }

    private def plusWeeks(yw: YearWeek, weeks: Int): YearWeek = {
        val result = YearWeek(yw)
        var newWeeks = weeks + result.weekOfWeekyear

        if (newWeeks < getEndYearAndWeek(result.weekyear).weekOfWeekyear) {
            result.weekOfWeekyear = newWeeks
            result
        } else {
            while (newWeeks >= 0) {
                val endWeek = getEndYearAndWeek(result.weekyear)
                if (newWeeks <= endWeek.weekOfWeekyear) {
                    result.weekOfWeekyear = Math.max(newWeeks, 1)
                    return result
                }
                newWeeks -= endWeek.weekOfWeekyear
                result.weekyear = result.weekyear + 1
            }
            result.weekOfWeekyear = Math.max(newWeeks, 1)
            result
        }
    }

    private def minusWeeks(yw: YearWeek, weeks: Int): YearWeek = {
        val result = YearWeek(yw)
        var week = weeks + result.weekOfWeekyear

        if (week == 0) {
            result.weekyear -= 1
            result.weekOfWeekyear = getEndYearAndWeek(result.weekyear).weekOfWeekyear
            result
        } else if (week > 0) {
            result.weekOfWeekyear = week
            result
        } else {
            while (week <= 0) {
                result.weekyear -= 1
                val endWeek = getEndYearAndWeek(result.weekyear)
                week += endWeek.weekOfWeekyear

                if (week > 0) {
                    result.weekOfWeekyear = Math.max(week, 1)
                    return result
                }
            }
            result.weekOfWeekyear = Math.max(week, 1)
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
