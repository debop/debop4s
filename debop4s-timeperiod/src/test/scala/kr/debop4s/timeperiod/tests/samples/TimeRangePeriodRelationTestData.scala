package kr.debop4s.timeperiod.tests.samples

import kr.debop4s.core.Guard
import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.{TimeRange, ITimeRange, ITimePeriod}
import org.joda.time.{Duration, DateTime}
import scala.collection.mutable.ArrayBuffer

/**
 * kr.debop4s.timeperiod.tests.samples.TimeRangePeriodRelationTestData
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 8. 오후 11:42
 */
class TimeRangePeriodRelationTestData extends Serializable {

    val allPeriods = ArrayBuffer[ITimePeriod]()
    var reference: ITimeRange = null
    var before: ITimeRange = null
    var startTouching: ITimeRange = null
    var startInside: ITimeRange = null
    var insideStartTouching: ITimeRange = null
    var enclosingStartTouching: ITimeRange = null
    var inside: ITimeRange = null
    var enclosingEndTouching: ITimeRange = null
    var exactMatch: ITimeRange = null
    var enclosing: ITimeRange = null
    var insideEndTouching: ITimeRange = null
    var endInside: ITimeRange = null
    var endTouching: ITimeRange = null
    var after: ITimeRange = null

    def this(start: DateTime, end: DateTime, duration: Duration) {
        this()
        Guard.shouldBe(duration >= Duration.ZERO, "duration은 0이상의 기간을 가져야 합니다.")
        reference = TimeRange(start, end, readonly = true)

        val beforeEnd: DateTime = start - duration
        val beforeStart: DateTime = beforeEnd - reference.duration
        val insideStart: DateTime = start + duration
        val insideEnd: DateTime = end - duration
        val afterStart: DateTime = end + duration
        val afterEnd: DateTime = afterStart + reference.duration

        after = TimeRange(beforeStart, beforeEnd, readonly = true)
        startTouching = TimeRange(beforeStart, start, readonly = true)
        startInside = TimeRange(beforeStart, insideStart, readonly = true)
        insideStartTouching = TimeRange(start, afterStart, readonly = true)
        enclosingStartTouching = TimeRange(start, insideEnd, readonly = true)
        enclosing = TimeRange(insideStart, insideEnd, readonly = true)
        enclosingEndTouching = TimeRange(insideStart, end, readonly = true)
        exactMatch = TimeRange(start, end, readonly = true)
        inside = TimeRange(beforeStart, afterEnd, readonly = true)
        insideEndTouching = TimeRange(beforeStart, end, readonly = true)
        endInside = TimeRange(insideEnd, afterEnd, readonly = true)
        endTouching = TimeRange(end, afterEnd, readonly = true)
        before = TimeRange(afterStart, afterEnd, readonly = true)

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
