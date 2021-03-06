package debop4s.timeperiod.utils

import debop4s.timeperiod.TimeSpec._
import debop4s.timeperiod._
import debop4s.timeperiod.tests.AbstractTimeFunSuite
import debop4s.timeperiod.utils.Times._
import org.joda.time.Duration


/**
 * debop4s.timeperiod.tests.debop4s.redis.base.WeeksTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 17. 오전 11:45
 */
class WeeksFunSuite extends AbstractTimeFunSuite {

  val testTimes = Array(asDate(2003, 12, 28), asDate(2014, 12, 30))

  test("year and week") {
    testTimes.foreach { m =>
      val yw = Weeks.yearAndWeek(m)
      yw.weekyear should equal(m.getWeekyear)
      yw.weekOfWeekyear should equal(m.getWeekOfWeekyear)
    }
  }

  test("parallel year and week") {
    (2000 to 2100).par.foreach { year =>
      val startDay = Times.startTimeOfYear(year)
      val endDay = Times.endTimeOfYear(year - 1)

      val startYW = Weeks.yearAndWeek(startDay)
      val endYW = Weeks.yearAndWeek(endDay)

      if (startDay.getDayOfWeek == FirstDayOfWeek.getValue)
        endYW should not equal startYW
      else
        endYW should equal(startYW)
    }
  }

  test("start week range of year") {
    (2000 to 2100).par.foreach { year =>
      val startWR = Weeks.startWeekRangeOfYear(year)
      log.trace(s"year=$year, startWeek=${ startWR.startDayStart }")

      new Duration(asDate(year - 1, 12, 28), startWR.startDayStart).getStandardDays should be > 0L
      new Duration(asDate(year, 1, 3), startWR.endDayStart).getStandardDays should be > 0L
    }
  }

  test("endYearAndWeek") {
    (1980 to 2200).par.foreach { year =>
      val yw = Weeks.endYearAndWeek(year)
      yw.weekyear should equal(year)
      yw.weekOfWeekyear should be >= 52
    }
  }

  test("end week range of year") {
    (2000 to 2100).par.foreach { year =>
      val startWR = Weeks.startWeekRangeOfYear(year)
      val endWR = Weeks.endWeekRangeOfYear(year - 1)
      log.trace(s"year=$year, startWR=${ startWR.startDayStart }, endWR=${ endWR.startDayStart }")

      new Duration(asDate(year - 1, 12, 28), startWR.startDayStart).getStandardDays should be > 0L
      new Duration(asDate(year, 1, 3), startWR.endDayStart).getStandardDays should be > 0L

      endWR.startDayStart.plusWeeks(1) should equal(startWR.startDayStart)
      endWR.endDayStart.plusDays(1) should equal(startWR.startDayStart)
    }
  }

  test("get week range") {
    (2000 to 2100).par.foreach {
      year =>
        val endDay = Times.endTimeOfYear(year - 1)
        val startDay = Times.startTimeOfYear(year)

        val endDayYearWeek = Weeks.yearAndWeek(endDay)
        endDayYearWeek.weekyear should be >= year - 1

        val startDayYearWeek = Weeks.yearAndWeek(startDay)
        startDayYearWeek.weekyear should be <= year

        // 해당일자가 속한 주차의 날짜들을 구한다. 년말/년초 구간은 꼭 7일이 아닐 수 있다.
        val endDayWeekRange = Weeks.weekRange(endDayYearWeek)
        val startDayWeekRange = Weeks.weekRange(startDayYearWeek)

        log.trace(s"start day weeksView=$startDayWeekRange")

        if (endDayYearWeek == startDayYearWeek)
          startDayWeekRange should equal(endDayWeekRange)
        else
          startDayWeekRange should not equal endDayWeekRange
    }
  }

  test("add WeekOfYears") {
    (2000 to 2100).par.foreach {
      weekyear =>
        val step = 2
        val maxAddWeeks = 40

        var prevResult: YearWeek = null
        val maxWeek = Weeks.endYearAndWeek(weekyear)

        for (week <- 1 until maxWeek.weekOfWeekyear by step) {
          for (addWeeks <- -maxAddWeeks until maxAddWeeks by step) {
            val current = YearWeek(weekyear, week)
            val result = Weeks.addWeekOfYears(current, addWeeks)

            if (addWeeks != 0 && prevResult != null) {
              if (result.weekyear == prevResult.weekyear)
                result.weekOfWeekyear should equal(prevResult.weekOfWeekyear + step)
            }

            result.weekOfWeekyear should be > 0
            result.weekOfWeekyear should be <= MaxWeeksPerYear

            prevResult = result
          }
        }
    }
  }

}
