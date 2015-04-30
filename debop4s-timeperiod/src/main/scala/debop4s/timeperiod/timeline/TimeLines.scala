package debop4s.timeperiod.timeline

import debop4s.core.conversions.jodatime._
import debop4s.timeperiod._
import org.slf4j.LoggerFactory

/**
 * TimeLine 관련 Helper class 입니다.
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 31. 오후 9:17
 */
object TimeLines {

  private[this] lazy val log = LoggerFactory.getLogger(getClass)

  @inline
  def combinePeriods(moments: ITimeLineMomentCollection): ITimePeriodCollection = {
    val periods = new TimePeriodCollection()
    if (moments.isEmpty)
      return periods


    val momentsSize = moments.size
    var itemIndex = 0


    while (itemIndex < momentsSize) {
      val periodStart = moments(itemIndex)
      var balance = periodStart.startCount
      assert(balance > 0, s"balance > 0 이여아합니다. balance=[$balance]")

      var periodEnd: ITimeLineMoment = null

      while (itemIndex < momentsSize - 1 && balance > 0) {
        itemIndex += 1
        periodEnd = moments(itemIndex)
        balance += periodEnd.startCount
        balance -= periodEnd.endCount
      }

      assert(periodEnd != null, s"periodEnd should not null.")

      if (periodEnd.startCount <= 0 && itemIndex < momentsSize) {
        val period = TimeRange(periodStart.moment, periodEnd.moment)
        periods.add(period)
      }
      itemIndex += 1
    }

    periods
  }

  @inline
  def intersectPeriods(moments: ITimeLineMomentCollection): ITimePeriodCollection = {
    val periods: ITimePeriodCollection = new TimePeriodCollection()
    if (moments.isEmpty) return periods

    var intersectionStart = -1
    var balance = 0
    var i = 0

    while (i < moments.size) {
      val moment = moments(i)
      val startCount = moment.startCount
      val endCount = moment.endCount
      balance += startCount
      balance -= endCount

      if (startCount > 0 && balance > 1 && intersectionStart < 0) {
        intersectionStart = i
      } else if (endCount > 0 && balance <= 1 && intersectionStart >= 0) {
        val period = TimeRange(moments(intersectionStart).moment, moment.moment)
        periods.add(period)
        intersectionStart = -1
      }
      i += 1
    }

    periods
  }

  @inline
  def calculateGap(moments: ITimeLineMomentCollection, range: ITimePeriod): ITimePeriodCollection = {
    val gaps = new TimePeriodCollection
    if (moments.isEmpty) return gaps

    // find leading gap
    val periodStart = moments.min

    if (periodStart != null && range.start < periodStart.moment) {
      val startingGap = TimeRange(range.start, periodStart.moment)
      gaps.add(startingGap)
    }

    // find intermediated gap
    var itemIndex = 0

    while (itemIndex < moments.size) {
      val moment = moments(itemIndex)
      assert(moment != null)
      assert(moment.startCount > 0, s"moment.getStartCount() 값이 0보다 커야합니다. balance=[${ moment.startCount }]")

      var balance = moment.startCount
      var gapStart: ITimeLineMoment = null

      while (itemIndex < moments.size - 1 && balance > 0) {
        itemIndex += 1
        gapStart = moments(itemIndex)
        balance += gapStart.startCount
        balance -= gapStart.endCount
      }
      assert(gapStart != null)

      if (gapStart.startCount <= 0) {

        // found a gap
        if (itemIndex < moments.size - 1) {
          val gap = TimeRange(gapStart.moment, moments(itemIndex + 1).moment)
          gaps.add(gap)
        }
      }
      itemIndex += 1
    }

    // find ending gap
    val periodEnd = moments.max

    if (periodEnd != null && range.end > periodEnd.moment) {
      val endingGap = TimeRange(periodEnd.moment, range.end)
      gaps.add(endingGap)
    }
    gaps
  }
}
