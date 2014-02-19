package kr.debop4s.timeperiod.tests.samples

import kr.debop4s.core.Guard
import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.{TimeBlock, ITimeBlock, ITimePeriod}
import org.joda.time.{Duration, DateTime}
import scala.collection.mutable.ArrayBuffer

/**
 * kr.debop4s.timeperiod.tests.samples.TimeBlockPeriodRelationTestData
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 8. 오후 11:47
 */
class TimeBlockPeriodRelationTestData {

    val allPeriods = ArrayBuffer[ITimePeriod]()
    var reference: ITimeBlock = null
    var before: ITimeBlock = null
    var startTouching: ITimeBlock = null
    var startInside: ITimeBlock = null
    var insideStartTouching: ITimeBlock = null
    var enclosingStartTouching: ITimeBlock = null
    var inside: ITimeBlock = null
    var enclosingEndTouching: ITimeBlock = null
    var exactMatch: ITimeBlock = null
    var enclosing: ITimeBlock = null
    var insideEndTouching: ITimeBlock = null
    var endInside: ITimeBlock = null
    var endTouching: ITimeBlock = null
    var after: ITimeBlock = null

    def this(start: DateTime, end: DateTime, duration: Duration) {
        this()
        Guard.shouldBe(duration >= Duration.ZERO, "duration은 0이상의 기간을 가져야 합니다.")
        reference = new TimeBlock(start, end, true)

        val beforeEnd: DateTime = start - duration
        val beforeStart: DateTime = beforeEnd - reference.duration
        val insideStart: DateTime = start + duration
        val insideEnd: DateTime = end - duration
        val afterStart: DateTime = end + duration
        val afterEnd: DateTime = afterStart + reference.duration

        after = new TimeBlock(beforeStart, beforeEnd, true)
        startTouching = new TimeBlock(beforeStart, start, true)
        startInside = new TimeBlock(beforeStart, insideStart, true)
        insideStartTouching = new TimeBlock(start, afterStart, true)
        enclosingStartTouching = new TimeBlock(start, insideEnd, true)
        enclosing = new TimeBlock(insideStart, insideEnd, true)
        enclosingEndTouching = new TimeBlock(insideStart, end, true)
        exactMatch = new TimeBlock(start, end, true)
        inside = new TimeBlock(beforeStart, afterEnd, true)
        insideEndTouching = new TimeBlock(beforeStart, end, true)
        endInside = new TimeBlock(insideEnd, afterEnd, true)
        endTouching = new TimeBlock(end, afterEnd, true)
        before = new TimeBlock(afterStart, afterEnd, true)

        allPeriods += reference
        allPeriods += after
        allPeriods += startTouching
        allPeriods += startInside
        allPeriods += insideStartTouching
        allPeriods += enclosingStartTouching
        allPeriods += enclosing
        allPeriods += enclosingEndTouching
        allPeriods += exactMatch
        allPeriods += inside
        allPeriods += insideEndTouching
        allPeriods += endInside
        allPeriods += endTouching
        allPeriods += before
    }
}
