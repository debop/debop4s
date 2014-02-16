package kr.debop4s.timeperiod

import kr.debop4s.core.ValueObject
import kr.debop4s.core.utils.{Options, Hashs}
import kr.debop4s.timeperiod.PeriodRelation.PeriodRelation
import kr.debop4s.timeperiod.utils.Times
import org.joda.time.{Duration, DateTime}
import org.slf4j.LoggerFactory

/**
 * kr.debop4s.timeperiod.TimePeriod
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 14. 오후 8:15
 */
trait ITimePeriod extends ValueObject with Ordered[ITimePeriod] with Serializable {

  lazy val log = LoggerFactory.getLogger(getClass)

  def compare(that: ITimePeriod): Int = if (that != null) start.compareTo(that.start) else 1

  /** 시작 시각 */
  def start: DateTime

  /** 시작 시각 */
  def getStart: DateTime

  /** 종료 시각 */
  def end: DateTime

  /** 종료 시각 */
  def getEnd: DateTime

  /** 기간 */
  def duration: Duration

  /** 기간 */
  def getDuration: Duration

  /** 시작 시각이 있는지 여부 */
  def hasStart: Boolean

  /** 종료 시각이 있는지 여부 */
  def hasEnd: Boolean

  /** 기간이 있는지 여부 */
  def hasPeriod: Boolean

  /** 시작시각과 완료시각의 값이 있고, 같은 값인가? */
  def isMoment: Boolean

  /** 시작시각과 완료시각 모두 정해지지 않은 경우 */
  def isAnytime: Boolean

  /** 읽기 전용 여부 */
  def isReadonly: Boolean

  /** 기간을 새로운 값으로 설정합니다. */
  def setup(ns: DateTime, ne: DateTime)

  /** 기간을 offset만큼 이동한 새로운 인스턴스를 반환합니다. */
  def copy(offset: Duration = Duration.ZERO): ITimePeriod

  /** 기간을 offset만큼 이동시킵니다. */
  def move(offset: Duration = Duration.ZERO)

  /** 시작과 완료 시각이 같은지 여부 */
  def isSamePeriod(other: ITimePeriod): Boolean

  /** 기간안에 moment 가 속하는지 여부 */
  def hasInside(moment: DateTime): Boolean

  /** 기간안에 other 기간이 속하는지 여부 */
  def hasInside(other: ITimePeriod): Boolean

  /** 두 기간이 교차하거나, 기간 target의 내부 구간이면 true를 반환합니다. */
  def intersectsWith(other: ITimePeriod): Boolean

  /** 기간과 other 기간이 교집합에 해당하는 부분이 있는지 여부 */
  def overlapsWith(other: ITimePeriod): Boolean

  /** 기간을 미정으로 초기화합니다. */
  def reset()

  /** 다른 TimePeriod 와의 관계를 나타냅니다. */
  def getRelation(other: ITimePeriod): PeriodRelation

  /** 두 기간이 겹치는 (교집합) 기간을 반환합니다. */
  def getIntersection(other: ITimePeriod): ITimePeriod

  /** 두 기간의 합집합 기간을 반환합니다. */
  def getUnion(other: ITimePeriod): ITimePeriod

}

abstract class TimePeriod(private var _start: DateTime = MinPeriodTime,
                          private var _end: DateTime = MaxPeriodTime,
                          var readonly: Boolean = false) extends ITimePeriod {

  override lazy val log = LoggerFactory.getLogger(getClass)

  private var (startTime, endTime) = Times.adjustPeriod(Options.get(_start).getOrElse(MinPeriodTime),
                                                         Options.get(_end).getOrElse(MaxPeriodTime))

  def this(start: DateTime, duration: Duration, readonly: Boolean) {
    this(start, start.plus(duration), readonly)
  }

  def this(start: DateTime, duration: Duration) {
    this(start, duration, false)
  }

  def start = startTime

  def getStart = startTime

  protected def start_=(v: DateTime) {
    assertMutable()
    startTime = v
  }

  protected def setStart(v: DateTime) {
    start = v
  }

  def end = endTime

  def getEnd = endTime

  protected def end_=(v: DateTime) {
    assertMutable()
    endTime = v
  }

  protected def setEnd(v: DateTime) {
    end = v
  }

  def isReadonly = readonly

  protected def setReadonly(v: Boolean) { readonly = v }

  def duration = new Duration(start, end)

  def getDuration = new Duration(startTime, endTime)

  def duration_=(v: Duration) {
    assert(duration >= 0.millis, "Duration 은 0 이상이어야 합니다.")
    if (hasStart)
      end = start + duration
  }

  def setDuration(duration: Duration) {
    assert(duration >= 0.millis, "Duration 은 0 이상이어야 합니다.")
    if (hasStart)
      end = start + duration
  }

  def hasStart = start != MinPeriodTime

  def hasEnd = end != MaxPeriodTime

  def hasPeriod = hasStart && hasEnd

  def isMoment = start == end

  def isAnytime = !hasStart && !hasEnd

  def setup(start: DateTime, end: DateTime) {
    log.trace(s"기간을 새로 설정합니다. newStart=[$start], newEnd=[$end]")
    assertMutable()
    val ns: DateTime = Options.get(start).getOrElse(MinPeriodTime)
    val ne: DateTime = Options.get(end).getOrElse(MaxPeriodTime)

    if (ns < ne) {
      this.startTime = ns
      this.endTime = ne
    } else {
      this.startTime = ne
      this.endTime = ns
    }
  }

  override def copy(offset: Duration = Duration.ZERO): ITimePeriod = {
    log.trace(s"기간[$this]에 offset[$offset]을 준 기간을 반환합니다...")

    if (offset.isZero)
      return TimeRange(this)

    val s = if (hasStart) start + offset else start
    val e = if (hasEnd) end + offset else end
    TimeRange(s, e, readonly)
  }

  def move(offset: Duration = Duration.ZERO) {
    if (offset.isZero) return

    assertMutable()

    if (hasStart)
      startTime = startTime + offset

    if (hasEnd)
      endTime = endTime + offset
  }

  def isSamePeriod(other: ITimePeriod): Boolean =
    (other != null) && (start == other.start) && (end == other.end)

  def hasInside(moment: DateTime) = Times.hasInside(this, moment)

  def hasInside(other: ITimePeriod) = Times.hasInside(this, other)

  def intersectsWith(other: ITimePeriod) = Times.intersectWith(this, other)

  def overlapsWith(other: ITimePeriod) = Times.overlapsWith(this, other)

  def reset() {
    assertMutable()
    startTime = MinPeriodTime
    endTime = MaxPeriodTime
    log.trace(s"기간을 리셋했습니다. start=[$start], end=[$end]")
  }

  def getRelation(other: ITimePeriod): PeriodRelation = Times.getRelation(this, other)

  def getIntersection(other: ITimePeriod): ITimePeriod = Times.getIntersectionRange(this, other)

  def getUnion(other: ITimePeriod): ITimePeriod = Times.getUnionBlock(this, other)

  protected def assertMutable() {
    assert(!isReadonly, "일기전용 인스턴스입니다.")
  }


  override def equals(obj: Any): Boolean =
    obj != null && getClass().equals(obj.getClass) && hashCode() == obj.hashCode()

  override def hashCode() = Hashs.compute(start, end, readonly)

  override protected def buildStringHelper =
    super.buildStringHelper
    .add("start", start)
    .add("end", end)
    .add("readonly", readonly)
}