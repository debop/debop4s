package kr.debop4s.timeperiod.tests.base

import kr.debop4s.time._
import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.tests.AbstractTimePeriodTest
import kr.debop4s.timeperiod.tests.samples.TimeBlockPeriodRelationTestData
import kr.debop4s.timeperiod.utils.{Times, Durations}
import org.joda.time.{Duration, DateTime}
import org.junit.Test
import org.slf4j.LoggerFactory

/**
 * kr.debop4s.timeperiod.tests.base.TimeBlockTest
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 9. 오전 1:44
 */
class TimeBlockTest extends AbstractTimePeriodTest {

    override implicit lazy val log = LoggerFactory.getLogger(getClass)

    val duration = 1.hours.toDuration
    val offset = Durations.Second
    var start = DateTime.now
    var end = start.plus(duration)

    var testData = new TimeBlockPeriodRelationTestData(start, end, offset)

    @Test
    def anytimeTest() {
        assert(TimeBlock.Anytime.start == MinPeriodTime)
        assert(TimeBlock.Anytime.end == MaxPeriodTime)
        assert(TimeBlock.Anytime.isAnytime)
        assert(TimeBlock.Anytime.isReadonly)
        assert(!TimeBlock.Anytime.hasPeriod)
        assert(!TimeBlock.Anytime.hasStart)
        assert(!TimeBlock.Anytime.hasEnd)
        assert(!TimeBlock.Anytime.isMoment)
    }
    @Test def defaultContructorTest() {
        val block = TimeBlock()
        assert(block != TimeBlock.Anytime)
        assert(Times.getRelation(block, TimeBlock.Anytime) == PeriodRelation.ExactMatch)
        assert(block.isAnytime)
        assert(!block.isReadonly)
        assert(!block.hasPeriod)
        assert(!block.hasStart)
        assert(!block.hasEnd)
        assert(!block.isMoment)
    }
    @Test def momentTest() {
        val moment = Times.now
        val block = TimeBlock(moment)
        assert(block.hasStart)
        assert(block.hasEnd)
        assert(block.duration == MinDuration)
        assert(!block.isAnytime)
        assert(block.isMoment)
        assert(block.hasPeriod)
    }
    @Test def momentByPeriod() {
        val block = TimeBlock(Times.now, Duration.ZERO)
        assert(block.isMoment)
    }
    @Test def nonMomentTest() {
        val block = TimeBlock(Times.now, MinPositiveDuration)
        assert(!block.isMoment)
        assert(block.getDuration == MinPositiveDuration)
    }
    @Test def hasStartTest() {
        val block = TimeBlock(Times.now, null.asInstanceOf[DateTime])
        assert(block.hasStart)
        assert(!block.hasEnd)
    }
    @Test def hasEndTest() {
        val block = TimeBlock(null.asInstanceOf[DateTime], Times.now)
        assert(!block.hasStart)
        assert(block.hasEnd)
    }
    @Test def startEndTest() {
        val block = TimeBlock(start, end)
        assert(block.start == start)
        assert(block.end == end)
        assert(block.duration == duration)
        assert(block.hasPeriod)
        assert(!block.isAnytime)
        assert(!block.isMoment)
        assert(!block.isReadonly)
    }
    @Test def startEndSwapTest() {
        val block = TimeBlock(end, start)
        assert(block.start == start)
        assert(block.end == end)
        assert(block.duration == duration)
        assert(block.hasPeriod)
        assert(!block.isAnytime)
        assert(!block.isMoment)
        assert(!block.isReadonly)
    }
    @Test def startAndDurationTest() {
        val block = TimeBlock(start, duration)
        assert(block.start == start)
        assert(block.end == end)
        assert(block.duration == duration)
        assert(block.hasPeriod)
        assert(!block.isAnytime)
        assert(!block.isMoment)
        assert(!block.isReadonly)
    }

    @Test(expected = classOf[AssertionError])
    def startAndNegateDurationTest() {
        val block = TimeBlock(start, Durations.negate(duration))
    }

    @Test def copyConstructorTest() {
        val source = TimeBlock(start, start.plusHours(1), readonly = true)
        val copy = TimeBlock(source)

        assert(copy.start == source.start)
        assert(copy.end == source.end)
        assert(copy.duration == source.duration)

        assert(copy.isReadonly == source.isReadonly)

        assert(copy.hasPeriod)
        assert(!copy.isAnytime)
        assert(!copy.isMoment)
    }
    @Test def startTest() {
        val block = TimeBlock(start, start + 1.hour)
        assert(block.start == start)

        val chanedStart = start + 1.hours
        block.start = chanedStart
        assert(block.start == chanedStart)
    }
    @Test(expected = classOf[AssertionError])
    def startReadonlyTest() {
        val block = TimeBlock(Times.now, 1.hour, readonly = true)
        block.start = block.start.minusHours(2)
    }
    @Test(expected = classOf[AssertionError])
    def startOutOfblockTest() {
        val block = TimeBlock(Times.now, 1.hour, readonly = false)
        block.start = block.start.plusHours(2)
    }

    @Test def endTest() {
        val block = TimeBlock(end.minusHours(1), end)
        assert(block.end == end)

        val changedEnd = end + 1.hour
        block.end = changedEnd
        assert(block.end == changedEnd)
    }
    @Test(expected = classOf[AssertionError])
    def endReadonlyTest() {
        val block = TimeBlock(Times.now, 1.hour, readonly = true)
        block.end = block.end.plusHours(1)
    }
    @Test(expected = classOf[AssertionError])
    def endOutOfblockTest() {
        val block = TimeBlock(Times.now, 1.hour, readonly = false)
        block.end = block.end.minusHours(2)
    }
    @Test def hasInsideDateTimeTest() {
        val block = TimeBlock(start, end)
        assert(block.end == end)
        assert(!block.hasInside(start.minus(duration)))
        assert(block.hasInside(start))
        assert(block.hasInside(start.plus(duration)))
        assert(block.hasInside(end.minus(duration)))
        assert(block.hasInside(end))
        assert(!block.hasInside(end.plus(duration)))
    }
    @Test def hasInsidePeriodTest {
        val block = TimeBlock(start, end)
        assert(block.end == end)

        val before1 = TimeBlock(start.minusHours(2), start.minusHours(1))
        val before2 = TimeBlock(start.minusMillis(1), end)
        val before3 = TimeBlock(start.minusMillis(1), start)
        assert(!block.hasInside(before1))
        assert(!block.hasInside(before2))
        assert(!block.hasInside(before3))

        val after1 = TimeBlock(start.plusHours(1), end.plusHours(1))
        val after2 = TimeBlock(start, end.plusMillis(1))
        val after3 = TimeBlock(end, end.plusMillis(1))
        assert(!block.hasInside(after1))
        assert(!block.hasInside(after2))
        assert(!block.hasInside(after3))
        assert(block.hasInside(block))

        val inside1 = TimeBlock(start.plusMillis(1), end)
        val inside2 = TimeBlock(start.plusMillis(1), end.minusMillis(1))
        val inside3 = TimeBlock(start, end.minusMillis(1))
        assert(block.hasInside(inside1))
        assert(block.hasInside(inside2))
        assert(block.hasInside(inside3))
    }
    @Test def copyTest {
        val readonlyTimeBlock = TimeBlock(start, end)
        assert(readonlyTimeBlock.copy() == readonlyTimeBlock)
        assert(readonlyTimeBlock.copy(Duration.ZERO) == readonlyTimeBlock)

        val block = TimeBlock(start, end)
        assert(block.start == start)
        assert(block.end == end)

        val noMove = block.copy(Durations.Zero)
        assert(noMove.start == block.start)
        assert(noMove.end == block.end)
        assert(noMove.duration == block.duration)
        assert(noMove == noMove)

        val forwardOffset = Durations.hours(2, 30, 15)
        val forward = block.copy(forwardOffset)
        assert(forward.start == start.plus(forwardOffset))
        assert(forward.end == end.plus(forwardOffset))
        assert(forward.duration == duration)

        val backwardOffset = Durations.hours(-1, 10, 30)
        val backward = block.copy(backwardOffset)
        assert(backward.start == start.plus(backwardOffset))
        assert(backward.end == end.plus(backwardOffset))
        assert(backward.duration == duration)
    }
    @Test def moveTest {
        val moveZero = TimeBlock(start, end)
        moveZero.move(Durations.Zero)
        assert(moveZero.start == start)
        assert(moveZero.end == end)
        assert(moveZero.duration == duration)

        val forward = TimeBlock(start, end)
        val forwardOffset = Durations.hours(2, 30, 15)
        forward.move(forwardOffset)

        assert(forward.start == start.plus(forwardOffset))
        assert(forward.end == end.plus(forwardOffset))
        assert(forward.duration == duration)

        val backward = TimeBlock(start, end)
        val backwardOffset = Durations.hours(-1, 10, 30)
        backward.move(backwardOffset)
        assert(backward.start == start.plus(backwardOffset))
        assert(backward.end == end.plus(backwardOffset))
        assert(backward.duration == duration)
    }

    @Test def isSamePeriodTest() {
        val block1 = TimeBlock(start, end)
        val block2 = TimeBlock(start, end)

        assert(block1.isSamePeriod(block1))
        assert(block2.isSamePeriod(block2))

        assert(block1.isSamePeriod(block2))
        assert(block2.isSamePeriod(block1))

        assert(!block1.isSamePeriod(TimeBlock.Anytime))
        assert(!block2.isSamePeriod(TimeBlock.Anytime))

        block1.move(Durations.Millisecond)
        assert(!block1.isSamePeriod(block2))
        assert(!block2.isSamePeriod(block1))

        block1.move(Durations.millis(-1))
        log.debug(s"block1=$block1, block2=$block2")
        assert(block1.isSamePeriod(block2))
        assert(block2.isSamePeriod(block1))
    }
    @Test def hasInsideTest() {
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
    @Test def intersectsWithTest() {
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
    @Test def overlapsWithTest() {
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
    @Test def intersectsWithDateTimeTest() {
        val block = TimeBlock(start, end)
        assert(!block.intersectsWith(TimeBlock(start.minusHours(2), start.minusHours(1))))
        assert(block.intersectsWith(TimeBlock(start.minusHours(1), start)))
        assert(block.intersectsWith(TimeBlock(start.minusHours(1), start.plusMillis(1))))
        assert(!block.intersectsWith(TimeBlock(end.plusHours(1), end.plusHours(2))))
        assert(block.intersectsWith(TimeBlock(end, end.plusMillis(1))))
        assert(block.intersectsWith(TimeBlock(end.minusMillis(1), end.plusMillis(1))))
        assert(block.intersectsWith(block))
        assert(block.intersectsWith(TimeBlock(start.minusMillis(1), end.plusHours(2))))
        assert(block.intersectsWith(TimeBlock(start.minusMillis(1), start.plusMillis(1))))
        assert(block.intersectsWith(TimeBlock(end.minusMillis(1), end.plusMillis(1))))
    }
    @Test def intersectionTest() {
        val block = TimeBlock(start, end)

        // before
        assert(block.getIntersection(TimeBlock(start.minusHours(2), start.minusHours(1))) == null)
        assert(block.getIntersection(TimeBlock(start.minusMillis(1), start)) == TimeBlock(start))
        assert(block.getIntersection(TimeBlock(start.minusHours(1), start.plusMillis(1))) == TimeBlock(start, start.plusMillis(1)))

        // after
        assert(block.getIntersection(TimeBlock(end.plusHours(1), end.plusHours(2))) == null)
        assert(block.getIntersection(TimeBlock(end, end.plusMillis(1))) == TimeBlock(end))
        assert(block.getIntersection(TimeBlock(end.minusMillis(1), end.plusMillis(1))) == TimeBlock(end.minusMillis(1), end))

        // intersect
        assert(block.getIntersection(block) == block)
        assert(block.getIntersection(TimeBlock(start.minusMillis(1), end.plusMillis(1))) == block)
        assert(block.getIntersection(TimeBlock(start.plusMillis(1), end.minusMillis(1))) == TimeBlock(start.plusMillis(1), end
            .minusMillis(1)))
    }
    @Test def relationTest() {
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

    @Test def resetTest() {
        val block = TimeBlock(start, end)
        assert(block.start == start)
        assert(block.hasStart)
        assert(block.end == end)
        assert(block.hasEnd)
        block.reset()
        assert(block.start == MinPeriodTime)
        assert(!block.hasStart)
        assert(block.end == MaxPeriodTime)
        assert(!block.hasEnd)
    }
    @Test def equalsTest() {
        val block1 = TimeBlock(start, end)
        val block2 = TimeBlock(start, end)
        val block3 = TimeBlock(start - 1.millis, end + 1.millis)
        val block4 = TimeBlock(start, end, readonly = true)

        assert(block1 == block2)
        assert(block1 != block3)
        assert(block2 == block1)
        assert(block2 != block3)
        assert(block1 != block4)
    }


}
