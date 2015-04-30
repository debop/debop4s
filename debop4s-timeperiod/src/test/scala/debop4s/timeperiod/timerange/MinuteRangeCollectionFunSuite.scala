package debop4s.timeperiod.timerange

import debop4s.core.conversions.jodatime._
import debop4s.timeperiod.TimeSpec._
import debop4s.timeperiod.tests.AbstractTimeFunSuite
import debop4s.timeperiod.utils.Times

class MinuteRangeCollectionFunSuite extends AbstractTimeFunSuite {

  test("single minute") {
    val now = Times.now
    val minutes = new MinuteRangeCollection(now, 1, EmptyOffsetTimeCalendar)

    val startTime = Times.trimToSecond(now)
    val endTime = Times.trimToSecond(now) + 1.minute

    minutes.minuteCount shouldEqual 1

    minutes.start shouldEqual startTime
    minutes.end shouldEqual endTime

    val mins = minutes.minutesView
    mins.size shouldEqual 1
    mins(0).start shouldEqual startTime
    mins(0).end shouldEqual endTime
  }

  test("calendar minutesView") {
    val now = Times.now

    (1 until 100).par.foreach { m =>
      val minutes = MinuteRangeCollection(now, m)

      val startTime = Times.trimToSecond(now)
      val endTime = Times.trimToSecond(now).plusMinutes(m).plus(minutes.calendar.endOffset)

      minutes.minuteCount shouldEqual m
      minutes.start shouldEqual startTime
      minutes.end shouldEqual endTime

      val items = minutes.minutesView

      for (i <- 0 until m) {
        items(i).start shouldEqual (startTime + i.minute)
        items(i).unmappedStart shouldEqual (startTime + i.minute)

        items(i).end shouldEqual minutes.calendar.mapEnd(startTime + (i + 1).minute)
        items(i).unmappedEnd shouldEqual (startTime + (i + 1).minute)
      }
    }
  }

  test("minutes") {
    val minuteCounts = Array(1, 24, 48, 64, 128)
    val now = Times.now

    for (minuteCount <- minuteCounts) {
      val minuteRanges = MinuteRangeCollection(now, minuteCount)
      val startTime = Times.trimToSecond(now).plus(minuteRanges.calendar.startOffset)
      val endTime = startTime.plusMinutes(minuteCount).plus(minuteRanges.calendar.endOffset)

      minuteRanges.start shouldEqual startTime
      minuteRanges.end shouldEqual endTime
      minuteRanges.minuteCount shouldEqual minuteCount

      val items = minuteRanges.minutesView
      items.size shouldEqual minuteCount

      (0 until minuteCount).par.foreach { m =>
        items(m).start shouldEqual (startTime + m.minute)
        items(m).end shouldEqual minuteRanges.calendar.mapEnd(startTime + (m + 1).minute)
        items(m).unmappedEnd shouldEqual (startTime + (m + 1).minute)
      }
    }
  }

}
