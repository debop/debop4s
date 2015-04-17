package debop4s.timeperiod.samples

import debop4s.core.conversions.jodatime._
import debop4s.timeperiod._
import org.joda.time.{DateTime, Duration}

import scala.beans.BeanProperty
import scala.collection.mutable.ArrayBuffer

/**
 * kr.hconnect.timeperiod.tests.samples.TimeRangePeriodRelationTestData
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 8. 오후 11:42
 */
class TimeRangePeriodRelationTestData extends Serializable {

  @BeanProperty val allPeriods = ArrayBuffer[ITimePeriod]()
  @BeanProperty var reference: ITimeRange = null
  @BeanProperty var before: ITimeRange = null
  @BeanProperty var startTouching: ITimeRange = null
  @BeanProperty var startInside: ITimeRange = null
  @BeanProperty var insideStartTouching: ITimeRange = null
  @BeanProperty var enclosingStartTouching: ITimeRange = null
  @BeanProperty var inside: ITimeRange = null
  @BeanProperty var enclosingEndTouching: ITimeRange = null
  @BeanProperty var exactMatch: ITimeRange = null
  @BeanProperty var enclosing: ITimeRange = null
  @BeanProperty var insideEndTouching: ITimeRange = null
  @BeanProperty var endInside: ITimeRange = null
  @BeanProperty var endTouching: ITimeRange = null
  @BeanProperty var after: ITimeRange = null

  def this(start: DateTime, end: DateTime, duration: Duration) {
    this()
    assert(duration >= Duration.ZERO, "duration은 0이상의 기간을 가져야 합니다.")
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
