package kr.debop4s.timeperiod.tests.base

import kr.debop4s.time._
import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.tests.AbstractTimePeriodTest
import kr.debop4s.timeperiod.tests.samples.TimeRangePeriodRelationTestData
import kr.debop4s.timeperiod.utils.{Times, Durations}
import org.fest.assertions.Assertions
import org.joda.time.{DateTime, Duration}
import org.junit.Test

/**
 * kr.debop4s.timeperiod.tests.base.TimeRangeTest
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 8. 오후 11:29
 */
class TimeRangeTest extends AbstractTimePeriodTest {

    val duration = 1.hours.toDuration
    val offset = Durations.Second
    var start = DateTime.now
    var end = start.plus(duration)

    var testData = new TimeRangePeriodRelationTestData(start, end, offset)

    @Test
    def anytimeTest() {
        assert(TimeRange.Anytime.start == MinPeriodTime)
        assert(TimeRange.Anytime.end == MaxPeriodTime)
        assert(TimeRange.Anytime.isAnytime)
        assert(TimeRange.Anytime.isReadonly)
        assert(!TimeRange.Anytime.hasPeriod)
        assert(!TimeRange.Anytime.hasStart)
        assert(!TimeRange.Anytime.hasEnd)
        assert(!TimeRange.Anytime.isMoment)
    }
    @Test def defaultContructorTest() {
        val range = new TimeRange
        assert(range != TimeRange.Anytime)
        assert(Times.getRelation(range, TimeRange.Anytime) == PeriodRelation.ExactMatch)
        Assertions.assertThat(range.isAnytime).isTrue()
        Assertions.assertThat(range.isReadonly).isFalse()
        Assertions.assertThat(range.hasPeriod).isFalse()
        Assertions.assertThat(range.hasStart).isFalse()
        Assertions.assertThat(range.hasEnd).isFalse()
        Assertions.assertThat(range.isMoment).isFalse()
    }
    @Test def momentTest() {
        val moment = Times.now
        val range = TimeRange(moment)
        assert(range.hasStart)
        assert(range.hasEnd)
        assert(range.duration == MinDuration)
        assert(!range.isAnytime)
        assert(range.isMoment)
        assert(range.hasPeriod)
    }
    @Test def momentByPeriod() {
        val range = TimeRange(Times.now, Duration.ZERO)
        assert(range.isMoment)
    }
    @Test def nonMomentTest() {
        val range = TimeRange(Times.now, MinPositiveDuration)
        assert(!range.isMoment)
        assert(range.getDuration == MinPositiveDuration)
    }
    @Test def hasStartTest() {
        val range = TimeRange(Times.now, null.asInstanceOf[DateTime])
        Assertions.assertThat(range.hasStart).isTrue()
        Assertions.assertThat(range.hasEnd).isFalse()
    }
    @Test def hasEndTest() {
        val range = TimeRange(null, Times.now)
        assert(!range.hasStart)
        assert(range.hasEnd)
    }
    @Test def startEndTest {
        val range = TimeRange(start, end)
        assert(range.start == start)
        assert(range.end == end)
        assert(range.getDuration == duration)
        Assertions.assertThat(range.hasPeriod).isTrue
        Assertions.assertThat(range.isAnytime).isFalse
        Assertions.assertThat(range.isMoment).isFalse
        Assertions.assertThat(range.isReadonly).isFalse
    }
    @Test def startEndSwapTest {
        val range = TimeRange(end, start)
        assert(range.start == start)
        assert(range.end == end)
        assert(range.getDuration == duration)
        Assertions.assertThat(range.hasPeriod).isTrue
        Assertions.assertThat(range.isAnytime).isFalse
        Assertions.assertThat(range.isMoment).isFalse
        Assertions.assertThat(range.isReadonly).isFalse
    }
    @Test def startAndDurationTest {
        val range = TimeRange(start, duration)
        Assertions.assertThat(range.start == start)
        Assertions.assertThat(range.end == end)
        Assertions.assertThat(range.getDuration == duration)
        Assertions.assertThat(range.hasPeriod).isTrue
        Assertions.assertThat(range.isAnytime).isFalse
        Assertions.assertThat(range.isMoment).isFalse
        Assertions.assertThat(range.isReadonly).isFalse
    }
    @Test def startAndNegateDurationTest {
        val range = TimeRange(start, Durations.negate(duration))
        Assertions.assertThat(range.start == start.minus(duration))
        Assertions.assertThat(range.end == end.minus(duration))
        Assertions.assertThat(range.getDuration == duration)
        Assertions.assertThat(range.hasPeriod).isTrue
        Assertions.assertThat(range.isAnytime).isFalse
        Assertions.assertThat(range.isMoment).isFalse
        Assertions.assertThat(range.isReadonly).isFalse
    }
    @Test def copyConstructorTest {
        val source = TimeRange(start, start.plusHours(1), true)
        val copy = TimeRange(source)

        assert(copy.start == source.start)
        assert(copy.end == source.getEnd)
        assert(copy.getDuration == source.getDuration)
        assert(copy.isReadonly == source.isReadonly)
        Assertions.assertThat(copy.hasPeriod).isTrue
        Assertions.assertThat(copy.isAnytime).isFalse
        Assertions.assertThat(copy.isMoment).isFalse
    }
    @Test def startTest {
        val range = TimeRange(start, start + 1.hour)
        assert(range.start == start)
        val chanedStart = start.plusHours(1)
        range.setStart(chanedStart)
        assert(range.start == chanedStart)
    }
    @Test(expected = classOf[AssertionError]) def startReadonlyTest {
        val range = TimeRange(Times.now, 1.hour, true)
        range.setStart(range.start.minusHours(2))
    }
    @Test(expected = classOf[AssertionError]) def startOutOfRangeTest {
        val range = TimeRange(Times.now, 1.hour, false)
        range.setStart(range.start.plusHours(2))
    }

    @Test def endTest {
        val range = TimeRange(end.minusHours(1), end)
        assert(range.end == end)

        val changedEnd = end + 1.hour
        range.setEnd(changedEnd)
        assert(range.end == changedEnd)
    }
    @Test(expected = classOf[AssertionError])
    def endReadonlyTest {
        val range = TimeRange(Times.now, 1.hour, true)
        range.setEnd(range.getEnd.plusHours(1))
    }
    @Test(expected = classOf[AssertionError])
    def endOutOfRangeTest {
        val range = TimeRange(Times.now, 1.hour, false)
        range.setEnd(range.getEnd.minusHours(2))
    }
    @Test def hasInsideDateTimeTest() {
        val range = TimeRange(start, end)
        assert(range.end == end)
        Assertions.assertThat(range.hasInside(start.minus(duration))).isFalse
        Assertions.assertThat(range.hasInside(start)).isTrue
        Assertions.assertThat(range.hasInside(start.plus(duration))).isTrue
        Assertions.assertThat(range.hasInside(end.minus(duration))).isTrue
        Assertions.assertThat(range.hasInside(end)).isTrue
        Assertions.assertThat(range.hasInside(end.plus(duration))).isFalse
    }
    @Test def hasInsidePeriodTest {
        val range = TimeRange(start, end)
        assert(range.end == end)

        val before1 = TimeRange(start.minusHours(2), start.minusHours(1))
        val before2 = TimeRange(start.minusMillis(1), end)
        val before3 = TimeRange(start.minusMillis(1), start)
        Assertions.assertThat(range.hasInside(before1)).isFalse
        Assertions.assertThat(range.hasInside(before2)).isFalse
        Assertions.assertThat(range.hasInside(before3)).isFalse

        val after1 = TimeRange(start.plusHours(1), end.plusHours(1))
        val after2 = TimeRange(start, end.plusMillis(1))
        val after3 = TimeRange(end, end.plusMillis(1))
        Assertions.assertThat(range.hasInside(after1)).isFalse
        Assertions.assertThat(range.hasInside(after2)).isFalse
        Assertions.assertThat(range.hasInside(after3)).isFalse
        Assertions.assertThat(range.hasInside(range)).isTrue

        val inside1 = TimeRange(start.plusMillis(1), end)
        val inside2 = TimeRange(start.plusMillis(1), end.minusMillis(1))
        val inside3 = TimeRange(start, end.minusMillis(1))
        Assertions.assertThat(range.hasInside(inside1)).isTrue
        Assertions.assertThat(range.hasInside(inside2)).isTrue
        Assertions.assertThat(range.hasInside(inside3)).isTrue
    }
    @Test def copyTest {
        val readonlyTimeRange = TimeRange(start, end)
        assert(readonlyTimeRange.copy() == readonlyTimeRange)
        assert(readonlyTimeRange.copy(Duration.ZERO) == readonlyTimeRange)

        val range = TimeRange(start, end)
        assert(range.start == start)
        assert(range.end == end)

        val noMove = range.copy(Durations.Zero)
        assert(noMove.start == range.start)
        assert(noMove.end == range.getEnd)
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
    @Test def moveTest {
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
    @Test def expandStartToTest() {
        val range = TimeRange(start, end)
        range.expandStartTo(start + 1.millis)
        assert(range.start == start)

        range.expandStartTo(start - 1.millis)
        Assertions.assertThat(range.start == start - 1.millis)
    }
    @Test def expandEndToTest() {
        val range = TimeRange(start, end)
        range.expandEndTo(end - 1.millis)
        assert(range.end == end)

        range.expandEndTo(end + 1.millis)
        assert(range.end == end + 1.millis)
    }
    @Test def expandToDateTimeTest() {
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

    @Test def expandToPeriodTest() {
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
    @Test def shrinkStartToTest() {
        val range = TimeRange(start, end)

        range.shrinkStartTo(start - 1.millis)
        assert(range.start == start)

        range.shrinkStartTo(start + 1.millis)
        assert(range.start == start + 1.millis)
    }
    @Test def shrinkEndToTest() {
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
    @Test def shrinkToPeriodTest() {
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
    @Test def isSamePeriodTest() {
        val range1 = TimeRange(start, end)
        val range2 = TimeRange(start, end)
        Assertions.assertThat(range1.isSamePeriod(range1)).isTrue
        Assertions.assertThat(range2.isSamePeriod(range2)).isTrue
        Assertions.assertThat(range1.isSamePeriod(range2)).isTrue
        Assertions.assertThat(range2.isSamePeriod(range1)).isTrue
        Assertions.assertThat(range1.isSamePeriod(TimeRange.Anytime)).isFalse
        Assertions.assertThat(range2.isSamePeriod(TimeRange.Anytime)).isFalse

        range1.move(Durations.Millisecond)
        Assertions.assertThat(range1.isSamePeriod(range2)).isFalse
        Assertions.assertThat(range2.isSamePeriod(range1)).isFalse

        range1.move(Durations.millis(-1))
        Assertions.assertThat(range1.isSamePeriod(range2)).isTrue
        Assertions.assertThat(range2.isSamePeriod(range1)).isTrue
    }
    @Test def hasInsideTest() {
        Assertions.assertThat(testData.reference.hasInside(testData.before)).isFalse
        Assertions.assertThat(testData.reference.hasInside(testData.startTouching)).isFalse
        Assertions.assertThat(testData.reference.hasInside(testData.startInside)).isFalse
        Assertions.assertThat(testData.reference.hasInside(testData.insideStartTouching)).isFalse
        Assertions.assertThat(testData.reference.hasInside(testData.enclosingStartTouching)).isTrue
        Assertions.assertThat(testData.reference.hasInside(testData.enclosing)).isTrue
        Assertions.assertThat(testData.reference.hasInside(testData.enclosingEndTouching)).isTrue
        Assertions.assertThat(testData.reference.hasInside(testData.exactMatch)).isTrue
        Assertions.assertThat(testData.reference.hasInside(testData.inside)).isFalse
        Assertions.assertThat(testData.reference.hasInside(testData.insideEndTouching)).isFalse
        Assertions.assertThat(testData.reference.hasInside(testData.endTouching)).isFalse
        Assertions.assertThat(testData.reference.hasInside(testData.after)).isFalse
    }
    @Test def intersectsWithTest {
        Assertions.assertThat(testData.reference.intersectsWith(testData.before)).isFalse
        Assertions.assertThat(testData.reference.intersectsWith(testData.startTouching)).isTrue
        Assertions.assertThat(testData.reference.intersectsWith(testData.startInside)).isTrue
        Assertions.assertThat(testData.reference.intersectsWith(testData.insideStartTouching)).isTrue
        Assertions.assertThat(testData.reference.intersectsWith(testData.enclosingStartTouching)).isTrue
        Assertions.assertThat(testData.reference.intersectsWith(testData.enclosing)).isTrue
        Assertions.assertThat(testData.reference.intersectsWith(testData.enclosingEndTouching)).isTrue
        Assertions.assertThat(testData.reference.intersectsWith(testData.exactMatch)).isTrue
        Assertions.assertThat(testData.reference.intersectsWith(testData.inside)).isTrue
        Assertions.assertThat(testData.reference.intersectsWith(testData.insideEndTouching)).isTrue
        Assertions.assertThat(testData.reference.intersectsWith(testData.endTouching)).isTrue
        Assertions.assertThat(testData.reference.intersectsWith(testData.after)).isFalse
    }
    @Test def overlapsWithTest {
        Assertions.assertThat(testData.reference.overlapsWith(testData.before)).isFalse
        Assertions.assertThat(testData.reference.overlapsWith(testData.startTouching)).isFalse
        Assertions.assertThat(testData.reference.overlapsWith(testData.startInside)).isTrue
        Assertions.assertThat(testData.reference.overlapsWith(testData.insideStartTouching)).isTrue
        Assertions.assertThat(testData.reference.overlapsWith(testData.enclosingStartTouching)).isTrue
        Assertions.assertThat(testData.reference.overlapsWith(testData.enclosing)).isTrue
        Assertions.assertThat(testData.reference.overlapsWith(testData.enclosingEndTouching)).isTrue
        Assertions.assertThat(testData.reference.overlapsWith(testData.exactMatch)).isTrue
        Assertions.assertThat(testData.reference.overlapsWith(testData.inside)).isTrue
        Assertions.assertThat(testData.reference.overlapsWith(testData.insideEndTouching)).isTrue
        Assertions.assertThat(testData.reference.overlapsWith(testData.endTouching)).isFalse
        Assertions.assertThat(testData.reference.overlapsWith(testData.after)).isFalse
    }
    @Test def intersectsWithDateTimeTest {
        val range = TimeRange(start, end)
        Assertions.assertThat(range.intersectsWith(TimeRange(start.minusHours(2), start.minusHours(1)))).isFalse
        Assertions.assertThat(range.intersectsWith(TimeRange(start.minusHours(1), start))).isTrue
        Assertions.assertThat(range.intersectsWith(TimeRange(start.minusHours(1), start.plusMillis(1)))).isTrue
        Assertions.assertThat(range.intersectsWith(TimeRange(end.plusHours(1), end.plusHours(2)))).isFalse
        Assertions.assertThat(range.intersectsWith(TimeRange(end, end.plusMillis(1)))).isTrue
        Assertions.assertThat(range.intersectsWith(TimeRange(end.minusMillis(1), end.plusMillis(1)))).isTrue
        Assertions.assertThat(range.intersectsWith(range)).isTrue
        Assertions.assertThat(range.intersectsWith(TimeRange(start.minusMillis(1), end.plusHours(2)))).isTrue
        Assertions.assertThat(range.intersectsWith(TimeRange(start.minusMillis(1), start.plusMillis(1)))).isTrue
        Assertions.assertThat(range.intersectsWith(TimeRange(end.minusMillis(1), end.plusMillis(1)))).isTrue
    }
    @Test def getIntersectionTest {
        val range = TimeRange(start, end)
        assert(range.getIntersection(TimeRange(start.minusHours(2), start.minusHours(1))) == null)
        assert(range.getIntersection(TimeRange(start.minusMillis(1), start)) == TimeRange(start))
        assert(range.getIntersection(TimeRange(start.minusHours(1), start.plusMillis(1))) == TimeRange(start, start.plusMillis(1)))
        assert(range.getIntersection(TimeRange(end.plusHours(1), end.plusHours(2))) == null)
        assert(range.getIntersection(TimeRange(end, end.plusMillis(1))) == TimeRange(end))
        assert(range.getIntersection(TimeRange(end.minusMillis(1), end.plusMillis(1))) == TimeRange(end.minusMillis(1), end))
        assert(range.getIntersection(range) == range)
        assert(range.getIntersection(TimeRange(start.minusMillis(1), end.plusMillis(1))) == range)
        assert(range.getIntersection(TimeRange(start.plusMillis(1), end.minusMillis(1))) == TimeRange(start.plusMillis(1), end
            .minusMillis(1)))
    }
    @Test def getRelationTest {
        Assertions.assertThat(testData.reference.getRelation(testData.before) == PeriodRelation.Before)
        Assertions.assertThat(testData.reference.getRelation(testData.startTouching) == PeriodRelation.StartTouching)
        Assertions.assertThat(testData.reference.getRelation(testData.startInside) == PeriodRelation.StartInside)
        Assertions.assertThat(testData.reference.getRelation(testData.insideStartTouching) == PeriodRelation.InsideStartTouching)
        Assertions.assertThat(testData.reference.getRelation(testData.enclosing) == PeriodRelation.Enclosing)
        Assertions.assertThat(testData.reference.getRelation(testData.exactMatch) == PeriodRelation.ExactMatch)
        Assertions.assertThat(testData.reference.getRelation(testData.inside) == PeriodRelation.Inside)
        Assertions.assertThat(testData.reference.getRelation(testData.insideEndTouching) == PeriodRelation.InsideEndTouching)
        Assertions.assertThat(testData.reference.getRelation(testData.endInside) == PeriodRelation.EndInside)
        Assertions.assertThat(testData.reference.getRelation(testData.endTouching) == PeriodRelation.EndTouching)
        Assertions.assertThat(testData.reference.getRelation(testData.after) == PeriodRelation.After)
        Assertions.assertThat(testData.reference.start == start)
        Assertions.assertThat(testData.reference.end == end)
        Assertions.assertThat(testData.reference.isReadonly).isTrue
        Assertions.assertThat(testData.after.isReadonly).isTrue
        Assertions.assertThat(testData.after.start.compareTo(start)).isLessThan(0)
        Assertions.assertThat(testData.after.getEnd.compareTo(start)).isLessThan(0)
        Assertions.assertThat(testData.reference.hasInside(testData.after.start)).isFalse
        Assertions.assertThat(testData.reference.hasInside(testData.after.getEnd)).isFalse
        Assertions.assertThat(testData.reference.getRelation(testData.after) == PeriodRelation.After)
        Assertions.assertThat(testData.startTouching.isReadonly).isTrue
        Assertions.assertThat(testData.startTouching.start.getMillis).isLessThan(start.getMillis)
        Assertions.assertThat(testData.startTouching.end == start)
        Assertions.assertThat(testData.reference.hasInside(testData.startTouching.start)).isFalse
        Assertions.assertThat(testData.reference.hasInside(testData.startTouching.getEnd)).isTrue
        Assertions.assertThat(testData.reference.getRelation(testData.startTouching) == PeriodRelation.StartTouching)
        Assertions.assertThat(testData.startInside.isReadonly).isTrue
        Assertions.assertThat(testData.startInside.start.getMillis).isLessThan(start.getMillis)
        Assertions.assertThat(testData.startInside.getEnd.getMillis).isLessThan(end.getMillis)
        Assertions.assertThat(testData.reference.hasInside(testData.startInside.start)).isFalse
        Assertions.assertThat(testData.reference.hasInside(testData.startInside.getEnd)).isTrue
        Assertions.assertThat(testData.reference.getRelation(testData.startInside) == PeriodRelation.StartInside)
        Assertions.assertThat(testData.insideStartTouching.isReadonly).isTrue
        Assertions.assertThat(testData.insideStartTouching.start.getMillis == start.getMillis)
        Assertions.assertThat(testData.insideStartTouching.getEnd.getMillis).isGreaterThan(end.getMillis)
        Assertions.assertThat(testData.reference.hasInside(testData.insideStartTouching.start)).isTrue
        Assertions.assertThat(testData.reference.hasInside(testData.insideStartTouching.getEnd)).isFalse
        Assertions.assertThat(testData.reference.getRelation(testData.insideStartTouching) == PeriodRelation.InsideStartTouching)
        Assertions.assertThat(testData.insideStartTouching.isReadonly).isTrue
        Assertions.assertThat(testData.insideStartTouching.start.getMillis == start.getMillis)
        Assertions.assertThat(testData.insideStartTouching.getEnd.getMillis).isGreaterThan(end.getMillis)
        Assertions.assertThat(testData.reference.hasInside(testData.insideStartTouching.start)).isTrue
        Assertions.assertThat(testData.reference.hasInside(testData.insideStartTouching.getEnd)).isFalse
        Assertions.assertThat(testData.reference.getRelation(testData.insideStartTouching) == PeriodRelation.InsideStartTouching)
        Assertions.assertThat(testData.enclosing.isReadonly).isTrue
        Assertions.assertThat(testData.enclosing.start.getMillis).isGreaterThan(start.getMillis)
        Assertions.assertThat(testData.enclosing.getEnd.getMillis).isLessThan(end.getMillis)
        Assertions.assertThat(testData.reference.hasInside(testData.enclosing.start)).isTrue
        Assertions.assertThat(testData.reference.hasInside(testData.enclosing.getEnd)).isTrue
        Assertions.assertThat(testData.reference.getRelation(testData.enclosing) == PeriodRelation.Enclosing)
        Assertions.assertThat(testData.enclosingEndTouching.isReadonly).isTrue
        Assertions.assertThat(testData.enclosingEndTouching.start.getMillis).isGreaterThan(start.getMillis)
        Assertions.assertThat(testData.enclosingEndTouching.getEnd.getMillis == end.getMillis)
        Assertions.assertThat(testData.reference.hasInside(testData.enclosingEndTouching.start)).isTrue
        Assertions.assertThat(testData.reference.hasInside(testData.enclosingEndTouching.getEnd)).isTrue
        assert(testData.reference.getRelation(testData.enclosingEndTouching) == PeriodRelation.EnclosingEndTouching)
        Assertions.assertThat(testData.exactMatch.isReadonly).isTrue
        Assertions.assertThat(testData.exactMatch.start.getMillis == start.getMillis)
        Assertions.assertThat(testData.exactMatch.getEnd.getMillis == end.getMillis)
        Assertions.assertThat(testData.reference.hasInside(testData.exactMatch.start)).isTrue
        Assertions.assertThat(testData.reference.hasInside(testData.exactMatch.getEnd)).isTrue
        Assertions.assertThat(testData.reference.getRelation(testData.exactMatch) == PeriodRelation.ExactMatch)
        Assertions.assertThat(testData.inside.isReadonly).isTrue
        Assertions.assertThat(testData.inside.start.getMillis).isLessThan(start.getMillis)
        Assertions.assertThat(testData.inside.getEnd.getMillis).isGreaterThan(end.getMillis)
        Assertions.assertThat(testData.reference.hasInside(testData.inside.start)).isFalse
        Assertions.assertThat(testData.reference.hasInside(testData.inside.getEnd)).isFalse
        Assertions.assertThat(testData.reference.getRelation(testData.inside) == PeriodRelation.Inside)
        Assertions.assertThat(testData.insideEndTouching.isReadonly).isTrue
        Assertions.assertThat(testData.insideEndTouching.start.getMillis).isLessThan(start.getMillis)
        Assertions.assertThat(testData.insideEndTouching.getEnd.getMillis == end.getMillis)
        Assertions.assertThat(testData.reference.hasInside(testData.insideEndTouching.start)).isFalse
        Assertions.assertThat(testData.reference.hasInside(testData.insideEndTouching.getEnd)).isTrue
        Assertions.assertThat(testData.reference.getRelation(testData.insideEndTouching) == PeriodRelation.InsideEndTouching)
        Assertions.assertThat(testData.endInside.isReadonly).isTrue
        Assertions.assertThat(testData.endInside.start.getMillis).isGreaterThan(start.getMillis)
        Assertions.assertThat(testData.endInside.start.getMillis).isLessThan(end.getMillis)
        Assertions.assertThat(testData.endInside.getEnd.getMillis).isGreaterThan(end.getMillis)
        Assertions.assertThat(testData.reference.hasInside(testData.endInside.start)).isTrue
        Assertions.assertThat(testData.reference.hasInside(testData.endInside.getEnd)).isFalse
        Assertions.assertThat(testData.reference.getRelation(testData.endInside) == PeriodRelation.EndInside)
        Assertions.assertThat(testData.endTouching.isReadonly).isTrue
        Assertions.assertThat(testData.endTouching.start.getMillis == end.getMillis)
        Assertions.assertThat(testData.endTouching.getEnd.getMillis).isGreaterThan(end.getMillis)
        Assertions.assertThat(testData.reference.hasInside(testData.endTouching.start)).isTrue
        Assertions.assertThat(testData.reference.hasInside(testData.endTouching.getEnd)).isFalse
        Assertions.assertThat(testData.reference.getRelation(testData.endTouching) == PeriodRelation.EndTouching)
        Assertions.assertThat(testData.before.isReadonly).isTrue
        Assertions.assertThat(testData.before.start.getMillis).isGreaterThan(end.getMillis)
        Assertions.assertThat(testData.before.getEnd.getMillis).isGreaterThan(end.getMillis)
        Assertions.assertThat(testData.reference.hasInside(testData.before.start)).isFalse
        Assertions.assertThat(testData.reference.hasInside(testData.before.getEnd)).isFalse
        Assertions.assertThat(testData.reference.getRelation(testData.before) == PeriodRelation.Before)
    }

    @Test def resetTest() {
        val range = TimeRange(start, end)
        Assertions.assertThat(range.start == start)
        Assertions.assertThat(range.hasStart).isTrue
        Assertions.assertThat(range.end == end)
        Assertions.assertThat(range.hasEnd).isTrue
        range.reset
        Assertions.assertThat(range.start == MinPeriodTime)
        Assertions.assertThat(range.hasStart).isFalse
        Assertions.assertThat(range.end == MaxPeriodTime)
        Assertions.assertThat(range.hasEnd).isFalse
    }
    @Test def equalsTest() {
        val range1 = TimeRange(start, end)
        val range2 = TimeRange(start, end)
        val range3 = TimeRange(start - 1.millis, end + 1.millis)
        val range4 = TimeRange(start, end, readonly = true)

        assert(range1 == range2)
        assert(range1 != range3)
        assert(range2 == range1)
        assert(range2 != range3)
        assert(range1 != range4)
    }

}
