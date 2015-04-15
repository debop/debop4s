package debop4s.timeperiod

import debop4s.core.conversions.jodatime._
import debop4s.timeperiod.utils.Times
import org.joda.time.{DateTime, Duration}
import org.slf4j.LoggerFactory

/**
 * debop4s.timeperiod.TimeBlock
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 14. 오후 9:59
 */
trait ITimeBlock extends ITimePeriod {

  def start_=(v: DateTime)

  def end_=(v: DateTime)

  def duration: Duration

  def duration_=(d: Duration)

  def setup(ns: DateTime, ne: DateTime)

  def durationFromStart(nd: Duration)

  def durationFromEnd(nd: Duration)

  def nextBlock(offset: Duration): ITimeBlock

  def previousBlock(offset: Duration): ITimeBlock
}

class TimeBlock(private[this] val _start: DateTime = MinPeriodTime,
                private[this] val _end: DateTime = MaxPeriodTime,
                private[this] val _readonly: Boolean = false)
  extends TimePeriod(_start, _end, _readonly) with ITimeBlock {

  private lazy val log = LoggerFactory.getLogger(getClass)

  override def start_=(v: DateTime) {
    assertMutable()
    assert(v <= end, "시작시각이 완료시각보다 클 수 없습니다.")
    super.start_=(v)
  }
  override def end_=(v: DateTime) {
    assertMutable()
    assert(v >= start, "완료시각이 시작시각보다 작을 수 없습니다.")
    super.end_=(v)

  }

  var _duration: Duration = new Duration(start, end)
  override def duration = _duration
  override def duration_=(v: Duration) {
    assertMutable()
    assertValidDuration(v)
    durationFromStart(v)
  }

  override def copy(offset: Duration = Duration.ZERO) = {
    if (offset == Duration.ZERO)
      TimeBlock(this)
    else
      TimeBlock(if (hasStart) start.plus(offset) else start,
        if (hasEnd) end.plus(offset) else end,
        readonly)
  }

  def setup(ns: DateTime, nd: Duration) {
    assertMutable()
    assertValidDuration(nd)
    log.trace(s"TimeBlock 값을 새로 설정합니다. start=$ns, duration=$nd")

    start = ns
    duration = nd
  }

  def durationFromStart(nd: Duration) {
    assertMutable()
    assertValidDuration(nd)

    if (nd.isEqual(MaxDuration)) {
      _duration = nd
      this.end = MaxPeriodTime
    } else {
      _duration = nd
      this.end = start.plus(nd)
    }
  }

  def durationFromEnd(nd: Duration) {
    assertMutable()
    assertValidDuration(nd)
    _duration = nd
    this.start = this.end.minus(nd)
  }

  def previousBlock(offset: Duration = Duration.ZERO): ITimeBlock = {
    val endOffset = if (offset > Duration.ZERO) new Duration(-offset.getMillis) else offset
    TimeBlock(duration, start + endOffset, readonly)
  }

  def nextBlock(offset: Duration = Duration.ZERO): ITimeBlock = {
    val startOffset = if (offset > Duration.ZERO) offset else new Duration(-offset.getMillis)
    TimeBlock(end + startOffset, duration, readonly)
  }

  override def intersection(other: ITimePeriod): TimeBlock = {
    require(other != null)
    Times.intersectBlock(this, other)
  }

  override def union(other: ITimePeriod): TimeBlock = {
    require(other != null)
    Times.unionBlock(this, other)
  }

  protected def assertValidDuration(v: Duration) {
    assert(v != null && v.getMillis >= 0, "duration은 0 이상의 값을 가져야 합니다.")
  }
}


object TimeBlock {

  val Anytime: TimeBlock = TimeBlock(readonly = true)

  def apply(): TimeBlock = new TimeBlock(MinPeriodTime, MaxPeriodTime, false)
  def apply(readonly: Boolean) = new TimeBlock(MinPeriodTime, MaxPeriodTime, readonly)
  def apply(start: DateTime, end: DateTime): TimeBlock = apply(start, end, readonly = false)
  def apply(start: DateTime, end: DateTime, readonly: Boolean): TimeBlock =
    new TimeBlock(start, end, readonly)

  def apply(moment: DateTime): TimeBlock = apply(moment, moment)
  def apply(moment: DateTime, readonly: Boolean): TimeBlock = apply(moment, moment, readonly)

  def apply(start: DateTime, duration: Duration): TimeBlock = {
    assertValidDuration(duration)
    apply(start, duration, readonly = false)
  }

  def apply(start: DateTime, duration: Duration, readonly: Boolean): TimeBlock = {
    assertValidDuration(duration)
    new TimeBlock(start, start + duration, readonly)
  }

  def apply(duration: Duration, end: DateTime): TimeBlock = {
    assertValidDuration(duration)
    apply(duration, end, readonly = false)
  }

  def apply(duration: Duration, end: DateTime, readonly: Boolean): TimeBlock = {
    assertValidDuration(duration)
    new TimeBlock(end - duration, end, readonly)
  }

  def apply(source: ITimePeriod): TimeBlock = {
    assert(source != null)
    apply(source, source.isReadonly)
  }

  def apply(source: ITimePeriod, readonly: Boolean): TimeBlock = {
    assert(source != null)

    if (source.isAnytime) Anytime
    else new TimeBlock(source.start, source.end, readonly)
  }

  def toRange(block: TimeBlock): TimeRange = TimeRange(block.start, block.end, block.isReadonly)

  def toInterval(block: TimeBlock): TimeInterval = TimeInterval(block)

  private def assertValidDuration(v: Duration) {
    assert(v != null && v.getMillis >= 0, "duration은 0 이상의 값을 가져야 합니다.")
  }
}
