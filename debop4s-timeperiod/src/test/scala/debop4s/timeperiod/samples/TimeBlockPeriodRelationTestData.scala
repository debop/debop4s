package debop4s.timeperiod.samples

import debop4s.core.conversions.jodatime._
import debop4s.timeperiod._
import org.joda.time.{DateTime, Duration}

import scala.beans.BeanProperty
import scala.collection.mutable.ArrayBuffer

/**
 * debop4s.timeperiod.tests.samples.TimeBlockPeriodRelationTestData
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 8. 오후 11:47
 */
class TimeBlockPeriodRelationTestData {

  @BeanProperty val allPeriods = ArrayBuffer[ITimePeriod]()
  @BeanProperty var reference: ITimeBlock = null
  @BeanProperty var before: ITimeBlock = null
  @BeanProperty var startTouching: ITimeBlock = null
  @BeanProperty var startInside: ITimeBlock = null
  @BeanProperty var insideStartTouching: ITimeBlock = null
  @BeanProperty var enclosingStartTouching: ITimeBlock = null
  @BeanProperty var inside: ITimeBlock = null
  @BeanProperty var enclosingEndTouching: ITimeBlock = null
  @BeanProperty var exactMatch: ITimeBlock = null
  @BeanProperty var enclosing: ITimeBlock = null
  @BeanProperty var insideEndTouching: ITimeBlock = null
  @BeanProperty var endInside: ITimeBlock = null
  @BeanProperty var endTouching: ITimeBlock = null
  @BeanProperty var after: ITimeBlock = null

  def this(start: DateTime, end: DateTime, duration: Duration) {
    this()
    assert(duration >= Duration.ZERO, "duration은 0이상의 기간을 가져야 합니다.")
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
