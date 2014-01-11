package kr.debop4s.timeperiod.tests.base

import kr.debop4s.core.logging.Logger
import kr.debop4s.time._
import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.tests.AbstractTimePeriodTest
import kr.debop4s.timeperiod.tests.samples.TimeRangePeriodRelationTestData
import kr.debop4s.timeperiod.utils.{Times, Durations}
import org.joda.time.{DateTime, Duration}
import org.junit.Test

/**
 * kr.debop4s.timeperiod.tests.base.TimeRangeTest
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 8. 오후 11:29
 */
class TimeRangeTest extends AbstractTimePeriodTest {

    override implicit lazy val log = Logger(getClass)

    val duration = 1.hours.toDuration
    val offset = Durations.Second
    var start = DateTime.now
    var end = start.plus(duration)

    var testData = new TimeRangePeriodRelationTestData(start, end, offset)

    test("TimeRange anytime") {
        assert(TimeRange.Anytime.start == MinPeriodTime)
        assert(TimeRange.Anytime.end == MaxPeriodTime)
        assert(TimeRange.Anytime.isAnytime)
        assert(TimeRange.Anytime.isReadonly)
        assert(!TimeRange.Anytime.hasPeriod)
        assert(!TimeRange.Anytime.hasStart)
        assert(!TimeRange.Anytime.hasEnd)
        assert(!TimeRange.Anytime.isMoment)
    }

    test("default constructor") {
        val range = TimeRange()
        assert(range != TimeRange.Anytime)
        assert(Times.getRelation(range, TimeRange.Anytime) == PeriodRelation.ExactMatch)
        assert(range.isAnytime)
        assert(!range.isReadonly)
        assert(!range.hasPeriod)
        assert(!range.hasStart)
        assert(!range.hasEnd)
        assert(!range.isMoment)
    }

    test("create with moment") {
        val moment = Times.now
        val range = TimeRange(moment)
        assert(range.hasStart)
        assert(range.hasEnd)
        assert(range.duration == MinDuration)
        assert(!range.isAnytime)
        assert(range.isMoment)
        assert(range.hasPeriod)
    }
    test("create with moment is moment") {
        val range = TimeRange(Times.now, Duration.ZERO)
        assert(range.isMoment)
    }
    test("create with some duration is not moment") {
        val range = TimeRange(Times.now, MinPositiveDuration)
        assert(!range.isMoment)
        assert(range.getDuration == MinPositiveDuration)
    }

    test("create with empty end") {
        val range = TimeRange(Times.now, null.asInstanceOf[DateTime])
        assert(range.hasStart)
        assert(!range.hasEnd)
    }
    test("create with some end") {
        val range = TimeRange(null, Times.now)
        assert(!range.hasStart)
        assert(range.hasEnd)
    }

    test("create with start and end") {
        val range = TimeRange(start, end)
        assert(range.start == start)
        assert(range.end == end)
        assert(range.getDuration == duration)
        assert(range.hasPeriod)
        assert(!range.isAnytime)
        assert(!range.isMoment)
        assert(!range.isReadonly)
    }

    test("create with end and start") {
        val range = TimeRange(end, start)
        assert(range.start == start)
        assert(range.end == end)
        assert(range.getDuration == duration)
        assert(range.hasPeriod)
        assert(!range.isAnytime)
        assert(!range.isMoment)
        assert(!range.isReadonly)
    }

    test("create with start and duration") {
        val range = TimeRange(start, duration)
        assert(range.start == start)
        assert(range.end == end)
        assert(range.getDuration == duration)
        assert(range.hasPeriod)
        assert(!range.isAnytime)
        assert(!range.isMoment)
        assert(!range.isReadonly)
    }

    test("create with start and negate duration") {
        val range = TimeRange(start, Durations.negate(duration))
        assert(range.start == start.minus(duration))
        assert(range.end == end.minus(duration))
        assert(range.getDuration == duration)
        assert(range.hasPeriod)
        assert(!range.isAnytime)
        assert(!range.isMoment)
        assert(!range.isReadonly)
    }


    test("create with copy constructor") {
        val source = TimeRange(start, start.plusHours(1), readonly = true)
        val copy = TimeRange(source)

        assert(copy.start == source.start)
        assert(copy.end == source.end)
        assert(copy.getDuration == source.getDuration)
        assert(copy.isReadonly == source.isReadonly)
        assert(copy.hasPeriod)
        assert(!copy.isAnytime)
        assert(!copy.isMoment)
    }

    test("set start") {
        val range = TimeRange(start, start + 1.hour)
        assert(range.start == start)

        val chanedStart = start + 1.hours
        range.start = chanedStart
        assert(range.start == chanedStart)
    }

    test("readonly range with set start") {
        val range = TimeRange(Times.now, 1.hour, readonly = true)
        intercept[AssertionError] {
            range.start = range.start.minusHours(2)
        }
    }
    test("start has out of range") {
        val range = TimeRange(Times.now, 1.hour, readonly = false)
        intercept[AssertionError] {
            range.start = range.start.plusHours(2)
        }
    }

    test("set end") {
        val range = TimeRange(end.minusHours(1), end)
        assert(range.end == end)

        val changedEnd = end + 1.hour
        range.end = changedEnd
        range.end should be eq changedEnd
    }

    test("readonly instance set end") {
        val range = TimeRange(Times.now, 1.hour, readonly = true)
        intercept[AssertionError] {
            range.end = range.end.plusHours(1)
        }
    }
    test("end has out of range") {
        val range = TimeRange(Times.now, 1.hour, readonly = false)
        intercept[AssertionError] {
            range.end = range.end.minusHours(2)
        }
    }

    test("hasInside with DateTime") {
        val range = TimeRange(start, end)
        assert(range.end == end)
        assert(!range.hasInside(start.minus(duration)))
        assert(range.hasInside(start))
        assert(range.hasInside(start.plus(duration)))
        assert(range.hasInside(end.minus(duration)))
        assert(range.hasInside(end))
        assert(!range.hasInside(end.plus(duration)))
    }

    test("hasPeriod with TimeRange") {
        val range = TimeRange(start, end)
        assert(range.end == end)

        val before1 = TimeRange(start.minusHours(2), start.minusHours(1))
        val before2 = TimeRange(start.minusMillis(1), end)
        val before3 = TimeRange(start.minusMillis(1), start)
        assert(!range.hasInside(before1))
        assert(!range.hasInside(before2))
        assert(!range.hasInside(before3))

        val after1 = TimeRange(start.plusHours(1), end.plusHours(1))
        val after2 = TimeRange(start, end.plusMillis(1))
        val after3 = TimeRange(end, end.plusMillis(1))
        assert(!range.hasInside(after1))
        assert(!range.hasInside(after2))
        assert(!range.hasInside(after3))
        assert(range.hasInside(range))

        val inside1 = TimeRange(start.plusMillis(1), end)
        val inside2 = TimeRange(start.plusMillis(1), end.minusMillis(1))
        val inside3 = TimeRange(start, end.minusMillis(1))
        assert(range.hasInside(inside1))
        assert(range.hasInside(inside2))
        assert(range.hasInside(inside3))
    }

    test("copy TimeRange") {
        val readonlyTimeRange = TimeRange(start, end)
        assert(readonlyTimeRange.copy() == readonlyTimeRange)
        assert(readonlyTimeRange.copy(Duration.ZERO) == readonlyTimeRange)

        val range = TimeRange(start, end)
        assert(range.start == start)
        assert(range.end == end)

        val noMove = range.copy(Durations.Zero)
        assert(noMove.start == range.start)
        assert(noMove.end == range.end)
        assert(noMove.getDuration == range.getDuration)
        assert(noMove == noMove)

        val forwardOffset = Durations.hours(2, 30, 15)
        val forward = range.copy(forwardOffset)
        assert(forward.start == start.plus(forwardOffset))
        assert(forward.end == end.plus(forwardOffset))
        assert(forward.getDuration == duration)

        val backwardOffset = Durations.hours(-1, 10, 30)
        val backward = range.copy(backwardOffset)
        assert(backward.start == start.plus(backwardOffset))
        assert(backward.end == end.plus(backwardOffset))
        assert(backward.getDuration == duration)
    }

    test("move TimeRange") {
        val moveZero = TimeRange(start, end)
        moveZero.move(Durations.Zero)
        assert(moveZero.start == start)
        assert(moveZero.end == end)
        assert(moveZero.getDuration == duration)

        val forward = TimeRange(start, end)
        val forwardOffset = Durations.hours(2, 30, 15)
        forward.move(forwardOffset)

        assert(forward.start == start.plus(forwardOffset))
        assert(forward.end == end.plus(forwardOffset))
        assert(forward.getDuration == duration)

        val backward = TimeRange(start, end)
        val backwardOffset = Durations.hours(-1, 10, 30)
        backward.move(backwardOffset)
        assert(backward.start == start.plus(backwardOffset))
        assert(backward.end == end.plus(backwardOffset))
        assert(backward.getDuration == duration)
    }

    test("expandStartTo") {
        val range = TimeRange(start, end)
        range.expandStartTo(start + 1.millis)
        assert(range.start == start)

        range.expandStartTo(start - 1.millis)
        assert(range.start == start - 1.millis)
    }

    test("expandEndTo") {
        val range = TimeRange(start, end)
        range.expandEndTo(end - 1.millis)
        assert(range.end == end)

        range.expandEndTo(end + 1.millis)
        assert(range.end == end + 1.millis)
    }

    test("expand with DateTime") {
        val range = TimeRange(start, end)
        range.expandTo(start + 1.millis)
        assert(range.start == start)

        range.expandTo(start.minusMillis(1))
        assert(range.start == start.minusMillis(1))

        range.expandTo(end.minusMillis(1))
        assert(range.end == end)

        range.expandTo(end.plusMillis(1))
        assert(range.end == end.plusMillis(1))
    }

    test("expand with Period") {
        val range = TimeRange(start, end)
        range.expandTo(TimeRange(start + 1.millis, end - 1.millis))
        assert(range.start == start)
        assert(range.end == end)

        var changedStart = start - 1.minutes
        range.expandTo(TimeRange(changedStart, end))
        assert(range.start == changedStart)
        assert(range.end == end)

        var changedEnd = end + 1.minutes
        range.expandTo(TimeRange(changedStart, changedEnd))
        assert(range.start == changedStart)
        assert(range.end == changedEnd)

        changedStart = changedStart - 1.minutes
        changedEnd = changedEnd + 1.minutes
        range.expandTo(TimeRange(changedStart, changedEnd))
        assert(range.start == changedStart)
        assert(range.end == changedEnd)
    }

    test("shrinkStartTo") {
        val range = TimeRange(start, end)

        range.shrinkStartTo(start - 1.millis)
        assert(range.start == start)

        range.shrinkStartTo(start + 1.millis)
        assert(range.start == start + 1.millis)
    }

    test("shrinkEndTo") {
        val range = TimeRange(start, end)

        range.shrinkEndTo(end + 1.millis)
        assert(range.end == end)

        range.shrinkEndTo(end - 1.millis)
        assert(range.end == end - 1.millis)
    }

    @Test def shrinkToDateTimeTest() {
        var range = TimeRange(start, end)

        range.shrinkTo(start.minusMillis(1))
        assert(range.start == start)

        range.shrinkTo(start.plusMillis(1))
        assert(range.start == start.plusMillis(1))

        range = TimeRange(start, end)
        range.shrinkTo(end.plusMillis(1))
        assert(range.end == end)

        range.shrinkTo(end.minusMillis(1))
        assert(range.end == end.minusMillis(1))
    }

    test("shrinkToPeriod") {
        val range = TimeRange(start, end)
        range.shrinkTo(TimeRange(start.minusMillis(1), end.plusMillis(1)))
        assert(range.start == start)
        assert(range.end == end)

        var changedStart = start.plusMinutes(1)
        range.shrinkTo(TimeRange(changedStart, end))
        assert(range.start == changedStart)
        assert(range.end == end)

        var changedEnd = end.minusMinutes(1)
        range.shrinkTo(TimeRange(changedStart, changedEnd))
        assert(range.start == changedStart)
        assert(range.end == changedEnd)

        changedStart = changedStart.plusMinutes(1)
        changedEnd = changedEnd.minusMinutes(1)
        range.shrinkTo(TimeRange(changedStart, changedEnd))
        assert(range.start == changedStart)
        assert(range.end == changedEnd)
    }

    test("is same period") {
        val range1 = TimeRange(start, end)
        val range2 = TimeRange(start, end)

        assert(range1.isSamePeriod(range1))
        assert(range2.isSamePeriod(range2))

        assert(range1.isSamePeriod(range2))
        assert(range2.isSamePeriod(range1))

        assert(!range1.isSamePeriod(TimeRange.Anytime))
        assert(!range2.isSamePeriod(TimeRange.Anytime))

        range1.move(Durations.Millisecond)
        assert(!range1.isSamePeriod(range2))
        assert(!range2.isSamePeriod(range1))

        range1.move(Durations.millis(-1))
        log.debug(s"range1=$range1, range2=$range2")
        assert(range1.isSamePeriod(range2))
        assert(range2.isSamePeriod(range1))
    }

    test("hasInside with Period") {
        assert(!testData.reference.hasInside(testData.before))
        assert(!testData.reference.hasInside(testData.startTouching))
        assert(!testData.reference.hasInside(testData.startInside))
        assert(!testData.reference.hasInside(testData.insideStartTouching))
        assert(testData.reference.hasInside(testData.enclosingStartTouching))
        assert(testData.reference.hasInside(testData.enclosing))
        assert(testData.reference.hasInside(testData.enclosingEndTouching))
        assert(testData.reference.hasInside(testData.exactMatch))
        assert(!testData.reference.hasInside(testData.inside))
        assert(!testData.reference.hasInside(testData.insideEndTouching))
        assert(!testData.reference.hasInside(testData.endTouching))
        assert(!testData.reference.hasInside(testData.after))
    }
    test("intersectsWith period") {
        assert(!testData.reference.intersectsWith(testData.before))
        assert(testData.reference.intersectsWith(testData.startTouching))
        assert(testData.reference.intersectsWith(testData.startInside))
        assert(testData.reference.intersectsWith(testData.insideStartTouching))
        assert(testData.reference.intersectsWith(testData.enclosingStartTouching))
        assert(testData.reference.intersectsWith(testData.enclosing))
        assert(testData.reference.intersectsWith(testData.enclosingEndTouching))
        assert(testData.reference.intersectsWith(testData.exactMatch))
        assert(testData.reference.intersectsWith(testData.inside))
        assert(testData.reference.intersectsWith(testData.insideEndTouching))
        assert(testData.reference.intersectsWith(testData.endTouching))
        assert(!testData.reference.intersectsWith(testData.after))
    }
    test("overlapsWith period") {
        assert(!testData.reference.overlapsWith(testData.before))
        assert(!testData.reference.overlapsWith(testData.startTouching))
        assert(testData.reference.overlapsWith(testData.startInside))
        assert(testData.reference.overlapsWith(testData.insideStartTouching))
        assert(testData.reference.overlapsWith(testData.enclosingStartTouching))
        assert(testData.reference.overlapsWith(testData.enclosing))
        assert(testData.reference.overlapsWith(testData.enclosingEndTouching))
        assert(testData.reference.overlapsWith(testData.exactMatch))
        assert(testData.reference.overlapsWith(testData.inside))
        assert(testData.reference.overlapsWith(testData.insideEndTouching))
        assert(!testData.reference.overlapsWith(testData.endTouching))
        assert(!testData.reference.overlapsWith(testData.after))
    }
    test("intersectsWith DateTime") {
        val range = TimeRange(start, end)
        assert(!range.intersectsWith(TimeRange(start.minusHours(2), start.minusHours(1))))
        assert(range.intersectsWith(TimeRange(start.minusHours(1), start)))
        assert(range.intersectsWith(TimeRange(start.minusHours(1), start.plusMillis(1))))
        assert(!range.intersectsWith(TimeRange(end.plusHours(1), end.plusHours(2))))
        assert(range.intersectsWith(TimeRange(end, end.plusMillis(1))))
        assert(range.intersectsWith(TimeRange(end.minusMillis(1), end.plusMillis(1))))
        assert(range.intersectsWith(range))
        assert(range.intersectsWith(TimeRange(start.minusMillis(1), end.plusHours(2))))
        assert(range.intersectsWith(TimeRange(start.minusMillis(1), start.plusMillis(1))))
        assert(range.intersectsWith(TimeRange(end.minusMillis(1), end.plusMillis(1))))
    }

    test("getIntersection with Period") {

        val range = TimeRange(start, end)

        // before
        assert(range.getIntersection(TimeRange(start.minusHours(2), start.minusHours(1))) == null)
        assert(range.getIntersection(TimeRange(start.minusMillis(1), start)) == TimeRange(start))
        assert(range.getIntersection(TimeRange(start.minusHours(1), start.plusMillis(1))) == TimeRange(start, start.plusMillis(1)))

        // after
        assert(range.getIntersection(TimeRange(end.plusHours(1), end.plusHours(2))) == null)
        assert(range.getIntersection(TimeRange(end, end.plusMillis(1))) == TimeRange(end))
        assert(range.getIntersection(TimeRange(end.minusMillis(1), end.plusMillis(1))) == TimeRange(end.minusMillis(1), end))

        // intersect
        assert(range.getIntersection(range) == range)
        assert(range.getIntersection(TimeRange(start.minusMillis(1), end.plusMillis(1))) == range)
        assert(range.getIntersection(TimeRange(start.plusMillis(1), end.minusMillis(1))) == TimeRange(start.plusMillis(1), end
            .minusMillis(1)))
    }

    test("getRelation with Period") {
        assert(testData.reference.getRelation(testData.before) == PeriodRelation.Before)
        assert(testData.reference.getRelation(testData.startTouching) == PeriodRelation.StartTouching)
        assert(testData.reference.getRelation(testData.startInside) == PeriodRelation.StartInside)
        assert(testData.reference.getRelation(testData.insideStartTouching) == PeriodRelation.InsideStartTouching)
        assert(testData.reference.getRelation(testData.enclosing) == PeriodRelation.Enclosing)
        assert(testData.reference.getRelation(testData.exactMatch) == PeriodRelation.ExactMatch)
        assert(testData.reference.getRelation(testData.inside) == PeriodRelation.Inside)
        assert(testData.reference.getRelation(testData.insideEndTouching) == PeriodRelation.InsideEndTouching)
        assert(testData.reference.getRelation(testData.endInside) == PeriodRelation.EndInside)
        assert(testData.reference.getRelation(testData.endTouching) == PeriodRelation.EndTouching)
        assert(testData.reference.getRelation(testData.after) == PeriodRelation.After)

        // reference
        assert(testData.reference.start == start)
        assert(testData.reference.end == end)
        assert(testData.reference.isReadonly)

        // after
        assert(testData.after.isReadonly)
        assert(testData.after.start.compareTo(start) < 0)
        assert(testData.after.end.compareTo(start) < 0)

        assert(!testData.reference.hasInside(testData.after.start))
        assert(!testData.reference.hasInside(testData.after.end))
        assert(testData.reference.getRelation(testData.after) == PeriodRelation.After)

        // start touching
        assert(testData.startTouching.isReadonly)
        assert(testData.startTouching.start < start)
        assert(testData.startTouching.end == start)

        assert(!testData.reference.hasInside(testData.startTouching.start))
        assert(testData.reference.hasInside(testData.startTouching.end))
        assert(testData.reference.getRelation(testData.startTouching) == PeriodRelation.StartTouching)

        // start inside
        assert(testData.startInside.isReadonly)
        assert(testData.startInside.start < start)
        assert(testData.startInside.end < end)

        assert(!testData.reference.hasInside(testData.startInside.start))
        assert(testData.reference.hasInside(testData.startInside.end))
        assert(testData.reference.getRelation(testData.startInside) == PeriodRelation.StartInside)

        // inside start touching
        assert(testData.insideStartTouching.isReadonly)
        assert(testData.insideStartTouching.start == start)
        assert(testData.insideStartTouching.end > end)

        assert(testData.reference.hasInside(testData.insideStartTouching.start))
        assert(!testData.reference.hasInside(testData.insideStartTouching.end))
        assert(testData.reference.getRelation(testData.insideStartTouching) == PeriodRelation.InsideStartTouching)

        // enclosing start touch
        assert(testData.enclosingStartTouching.isReadonly)
        assert(testData.enclosingStartTouching.start == start)
        assert(testData.enclosingStartTouching.end < end)

        assert(testData.reference.hasInside(testData.enclosingStartTouching.start))
        assert(testData.reference.hasInside(testData.enclosingStartTouching.end))
        assert(testData.reference.getRelation(testData.enclosingStartTouching) == PeriodRelation.EnclosingStartTouching)

        // enclosing
        assert(testData.enclosing.isReadonly)
        assert(testData.enclosing.start > start)
        assert(testData.enclosing.end < end)

        assert(testData.reference.hasInside(testData.enclosing.start))
        assert(testData.reference.hasInside(testData.enclosing.end))
        assert(testData.reference.getRelation(testData.enclosing) == PeriodRelation.Enclosing)

        // enclosing end touching
        assert(testData.enclosingEndTouching.isReadonly)
        assert(testData.enclosingEndTouching.start > start)
        assert(testData.enclosingEndTouching.end == end)

        assert(testData.reference.hasInside(testData.enclosingEndTouching.start))
        assert(testData.reference.hasInside(testData.enclosingEndTouching.end))
        assert(testData.reference.getRelation(testData.enclosingEndTouching) == PeriodRelation.EnclosingEndTouching)

        // exact match
        assert(testData.exactMatch.isReadonly)
        assert(testData.exactMatch.start == start)
        assert(testData.exactMatch.end == end)

        assert(testData.reference.hasInside(testData.exactMatch.start))
        assert(testData.reference.hasInside(testData.exactMatch.end))
        assert(testData.reference.getRelation(testData.exactMatch) == PeriodRelation.ExactMatch)

        // inside
        assert(testData.inside.isReadonly)
        assert(testData.inside.start < start)
        assert(testData.inside.end > end)

        assert(!testData.reference.hasInside(testData.inside.start))
        assert(!testData.reference.hasInside(testData.inside.end))
        assert(testData.reference.getRelation(testData.inside) == PeriodRelation.Inside)

        // inside end touching
        assert(testData.insideEndTouching.isReadonly)
        assert(testData.insideEndTouching.start < start)
        assert(testData.insideEndTouching.end == end)

        assert(!testData.reference.hasInside(testData.insideEndTouching.start))
        assert(testData.reference.hasInside(testData.insideEndTouching.end))
        assert(testData.reference.getRelation(testData.insideEndTouching) == PeriodRelation.InsideEndTouching)

        // end inside
        assert(testData.endInside.isReadonly)
        assert(testData.endInside.start > start)
        assert(testData.endInside.start < end)
        assert(testData.endInside.end > end)
        assert(testData.reference.hasInside(testData.endInside.start))
        assert(!testData.reference.hasInside(testData.endInside.end))
        assert(testData.reference.getRelation(testData.endInside) == PeriodRelation.EndInside)

        // end touching
        assert(testData.endTouching.isReadonly)
        assert(testData.endTouching.start == end)
        assert(testData.endTouching.end > end)

        assert(testData.reference.hasInside(testData.endTouching.start))
        assert(!testData.reference.hasInside(testData.endTouching.end))
        assert(testData.reference.getRelation(testData.endTouching) == PeriodRelation.EndTouching)

        // before
        assert(testData.before.isReadonly)
        assert(testData.before.start > end)
        assert(testData.before.end > end)

        assert(!testData.reference.hasInside(testData.before.start))
        assert(!testData.reference.hasInside(testData.before.end))
        assert(testData.reference.getRelation(testData.before) == PeriodRelation.Before)
    }

    test("reset Period") {
        val range = TimeRange(start, end)
        assert(range.start == start)
        assert(range.hasStart)
        assert(range.end == end)
        assert(range.hasEnd)
        range reset()
        assert(range.start == MinPeriodTime)
        assert(!range.hasStart)
        assert(range.end == MaxPeriodTime)
        assert(!range.hasEnd)
    }

    test("equals TimeRange") {
        val range1 = TimeRange(start, end)
        val range2 = TimeRange(start, end)
        val range3 = TimeRange(start - 1.millis, end + 1.millis)
        val range4 = TimeRange(start, end, readonly = true)

        assert(range1 === range2)
        assert(range1 !== range3)
        assert(range2 === range1)
        assert(range2 !== range3)
        assert(range1 !== range4)
    }
}
