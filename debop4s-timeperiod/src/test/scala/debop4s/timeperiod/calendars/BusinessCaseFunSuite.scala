package debop4s.timeperiod.calendars

import debop4s.core.conversions.jodatime._
import debop4s.timeperiod._
import debop4s.timeperiod.tests.AbstractTimeFunSuite
import debop4s.timeperiod.timerange._
import debop4s.timeperiod.utils.Times


/**
 * BusinessCaseTest
 * Created by debop on 2014. 2. 18.
 */
class BusinessCaseFunSuite extends AbstractTimeFunSuite {

  test("time range calendar") {

    val now = Times.now

    (0 until 500).par.foreach {
      i =>
        val current = now + i.day
        val currentFiveSeconds = TimeRange(Times.trimToSecond(current, 15), Times.trimToSecond(current, 20))

        YearRange(current).hasInside(currentFiveSeconds) shouldEqual true
        HalfyearRange(current).hasInside(currentFiveSeconds) shouldEqual true
        QuarterRange(current).hasInside(currentFiveSeconds) shouldEqual true
        MonthRange(current).hasInside(currentFiveSeconds) shouldEqual true
        WeekRange(current).hasInside(currentFiveSeconds) shouldEqual true
        DayRange(current).hasInside(currentFiveSeconds) shouldEqual true
        HourRange(current).hasInside(currentFiveSeconds) shouldEqual true
        MinuteRange(current).hasInside(currentFiveSeconds) shouldEqual true
    }

    val anytime = TimeRange()

    YearRange().hasInside(anytime) shouldEqual false
    HalfyearRange().hasInside(anytime) shouldEqual false
    QuarterRange().hasInside(anytime) shouldEqual false
    MonthRange().hasInside(anytime) shouldEqual false
    WeekRange().hasInside(anytime) shouldEqual false
    DayRange().hasInside(anytime) shouldEqual false
    HourRange().hasInside(anytime) shouldEqual false
    MinuteRange().hasInside(anytime) shouldEqual false
  }

}
