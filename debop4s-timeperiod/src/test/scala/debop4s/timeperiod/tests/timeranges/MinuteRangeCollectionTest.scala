package debop4s.timeperiod.tests.timeranges


import debop4s.timeperiod._
import debop4s.timeperiod.tests.AbstractTimePeriodTest
import debop4s.timeperiod.timerange.MinuteRangeCollection
import debop4s.timeperiod.utils.Times


/**
 * MinuteRangeCollectionTest
 * Created by debop on 2014. 2. 16.
 */
class MinuteRangeCollectionTest extends AbstractTimePeriodTest {

  test("single minute") {
    val now = Times.now
    val minutes = new MinuteRangeCollection(now, 1, EmptyOffsetTimeCalendar)

    val startTime = Times.trimToSecond(now)
    val endTime = Times.trimToSecond(now) + 1.minute

    minutes.minuteCount should equal(1)

    minutes.start should equal(startTime)
    minutes.end should equal(endTime)

    val mins = minutes.minutes
    mins.size should equal(1)
    mins(0).start should equal(startTime)
    mins(0).end should equal(endTime)
  }

  test("calendar minutes") {
    val now = Times.now

    for (m <- 1 until 100 by 5) {
      val minutes = MinuteRangeCollection(now, m)

      val startTime = Times.trimToSecond(now)
      val endTime = Times.trimToSecond(now).plusMinutes(m).plus(minutes.calendar.endOffset)

      minutes.minuteCount should equal(m)
      minutes.start should equal(startTime)
      minutes.end should equal(endTime)

      val items = minutes.minutes

      for (i <- 0 until m) {
        items(i).start should equal(startTime + i.minute)
        items(i).unmappedStart should equal(startTime + i.minute)

        items(i).end should equal(minutes.calendar.mapEnd(startTime + (i + 1).minute))
        items(i).unmappedEnd should equal(startTime + (i + 1).minute)
      }
    }
  }

  test("minutes") {
    val minuteCounts = Array(1, 24, 48, 64, 128)
    val now = Times.now

    minuteCounts.foreach {
      minuteCount =>
        val minuteRanges = MinuteRangeCollection(now, minuteCount)
        val startTime = Times.trimToSecond(now).plus(minuteRanges.calendar.startOffset)
        val endTime = startTime.plusMinutes(minuteCount).plus(minuteRanges.calendar.endOffset)

        minuteRanges.start should equal(startTime)
        minuteRanges.end should equal(endTime)
        minuteRanges.minuteCount should equal(minuteCount)

        val items = minuteRanges.minutes
        items.size should equal(minuteCount)

        (0 until minuteCount).par.foreach {
          m =>
            items(m).start should equal(startTime + m.minute)
            items(m).end should equal(minuteRanges.calendar.mapEnd(startTime + (m + 1).minute))
            items(m).unmappedEnd should equal(startTime + (m + 1).minute)
        }
    }
  }

}
