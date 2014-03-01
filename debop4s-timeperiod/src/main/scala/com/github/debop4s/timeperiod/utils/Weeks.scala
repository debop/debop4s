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

    private lazy val log = LoggerFactory.getLogger(getClass)

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

    def addWeekOfYears(weekyear: Int, weekOfYear: Int, weeks: Int): YearWeek = {
        addWeekOfYears(YearWeek(weekyear, weekOfYear), weeks)
    }

    def addWeekOfYears(yw: YearWeek, weeks: Int): YearWeek = {
        if (weeks == 0)
            return YearWeek(yw)

        if (weeks > 0) plusWeeks(yw, weeks)
        else minusWeeks(yw, weeks)
    }

    private def plusWeeks(yw: YearWeek, weeks: Int): YearWeek = {
        var newWeeks = weeks + yw.weekOfWeekyear

        if (newWeeks < getEndYearAndWeek(yw.weekyear).weekOfWeekyear) {
            return YearWeek(yw.weekyear, newWeeks)
        }

        var weekyear = yw.weekyear
        while (newWeeks >= 0) {
            val endWeek = getEndYearAndWeek(weekyear)
            if (newWeeks <= endWeek.weekOfWeekyear) {
                return YearWeek(weekyear, math.max(newWeeks, 1))
            }
            newWeeks -= endWeek.weekOfWeekyear
            weekyear += 1
        }
        YearWeek(weekyear, math.max(newWeeks, 1))
    }

    private def minusWeeks(yw: YearWeek, weeks: Int): YearWeek = {
        var week = weeks + yw.weekOfWeekyear

        if (week == 0) {
            return YearWeek(yw.weekyear - 1, getEndYearAndWeek(yw.weekyear - 1).weekOfWeekyear)
        } else if (week > 0) {
            return YearWeek(yw.weekyear, week)
        }

        var weekyear = yw.weekyear
        while (week <= 0) {
            weekyear -= 1
            val endWeek = getEndYearAndWeek(weekyear)
            week += endWeek.weekOfWeekyear

            if (week > 0) {
                return YearWeek(weekyear, math.max(week, 1))
            }
        }
        YearWeek(weekyear, math.max(week, 1))
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
