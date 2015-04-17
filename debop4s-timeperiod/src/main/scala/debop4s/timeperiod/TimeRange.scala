package debop4s.timeperiod

import debop4s.core.conversions.jodatime._
import debop4s.timeperiod.TimeSpec._
import org.joda.time.{DateTime, Duration}

trait ITimeRange extends ITimePeriod {

  /** 시작시각을 설정합니다 */
  def start_=(v: DateTime)

  def setStart(v: DateTime) = start_=(v)

  /** 완료시각을 설정합니다. */
  def end_=(v: DateTime)

  def setEnd(v: DateTime) = end_=(v)

  /** 시작시각을 기준으로 기간을 설정합니다. */
  def duration_=(v: Duration)

  /** 시작시각을 지정된 시각으로 설정합니다. 시작시각 이전이여야 합니다. */
  def expandStartTo(moment: DateTime)

  /** 완료시각을 지정된 시각으로 설정합니다. 완료시각 이후여야 합니다. */
  def expandEndTo(moment: DateTime)

  /** 시작시각, 완료시각을 지정된 시각으로 설정합니다. */
  def expandTo(moment: DateTime)

  /** 시작시각과 완료시각을 지정된 기간으로 설정합니다. */
  def expandTo(period: ITimePeriod)

  /** 시작시각을 지정된 시각으로 설정합니다. 시작시각 이후여야 합니다. */
  def shrinkStartTo(moment: DateTime)

  /** 완료시각을 지정된 시각으로 설정합니다. 완료시각 이전이어야 합니다. */
  def shrinkEndTo(moment: DateTime)

  /** 시작시각, 완료시각을 지정된 시각으로 설정합니다. */
  def shrinkTo(moment: DateTime)

  /** 시작시각과 완료시각을 지정된 기간으로 설정합니다. */
  def shrinkTo(period: ITimePeriod)
}

class TimeRange(private[this] val _start: DateTime = MinPeriodTime,
                private[this] val _end: DateTime = MaxPeriodTime,
                private[this] val _readonly: Boolean = false)
  extends TimePeriod(_start, _end, _readonly) with ITimeRange {

  def this() = this(MinPeriodTime, MaxPeriodTime, false)
  def this(readonly: Boolean) = this(MinPeriodTime, MaxPeriodTime, readonly)
  def this(moment: DateTime) = this(moment, moment, false)
  def this(start: DateTime, end: DateTime) = this(start, end, false)
  def this(start: DateTime, duration: Duration) = this(start, start + duration, false)
  def this(start: DateTime, duration: Duration, readonly: Boolean) =
    this(start, start + duration, readonly)

  def this(period: ITimePeriod) = this(period.start, period.end, period.isReadonly)

  def this(start: Option[DateTime], end: Option[DateTime]) =
    this(start.getOrElse(MinPeriodTime),
      end.getOrElse(MaxPeriodTime),
      false)

  def this(start: Option[DateTime], end: Option[DateTime], readonly: Option[Boolean]) =
    this(start.getOrElse(MinPeriodTime),
      end.getOrElse(MaxPeriodTime),
      readonly.getOrElse(false))

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

  override def duration_=(v: Duration) {
    assertMutable()
    super.duration_=(v)
  }

  override def copy(offset: Duration = Duration.ZERO): TimeRange = {
    if (offset == Duration.ZERO)
      TimeRange(this)
    else
      TimeRange(
                 if (hasStart) start + offset else start,
                 if (hasEnd) end + offset else end,
                 readonly
               )
  }

  def expandStartTo(moment: DateTime) {
    assertMutable()
    if (start > moment)
      start = moment
  }

  def expandEndTo(moment: DateTime) {
    assertMutable()
    if (end < moment)
      end = moment
  }

  def expandTo(moment: DateTime) {
    assertMutable()
    expandStartTo(moment)
    expandEndTo(moment)
  }

  def expandTo(period: ITimePeriod) {
    require(period != null)
    assertMutable()

    if (period.hasStart)
      expandStartTo(period.start)

    if (period.hasEnd)
      expandEndTo(period.end)
  }

  def shrinkStartTo(moment: DateTime) {
    assertMutable()
    if (hasInside(moment) && start < moment)
      start = moment
  }

  def shrinkEndTo(moment: DateTime) {
    assertMutable()
    if (hasInside(moment) && end > moment)
      end = moment
  }

  def shrinkTo(moment: DateTime) {
    assertMutable()
    shrinkStartTo(moment)
    shrinkEndTo(moment)
  }

  def shrinkTo(period: ITimePeriod) {
    assert(period != null)
    assertMutable()

    if (period.hasStart)
      shrinkStartTo(period.start)

    if (period.hasEnd)
      shrinkEndTo(period.end)
  }
}

object TimeRange {

  val Anytime = apply(readonly = true)

  def apply(): TimeRange =
    new TimeRange(MinPeriodTime, MaxPeriodTime, false)

  def apply(readonly: Boolean): TimeRange =
    new TimeRange(MinPeriodTime, MaxPeriodTime, readonly)

  def apply(moment: DateTime): TimeRange =
    apply(moment, moment, readonly = false)

  def apply(start: DateTime, end: DateTime): TimeRange =
    apply(start, end, readonly = false)

  def apply(start: DateTime, end: DateTime, readonly: Boolean): TimeRange =
    new TimeRange(start, end, readonly)

  def apply(start: DateTime, duration: Duration): TimeRange =
    apply(start, duration, readonly = false)

  def apply(start: DateTime, duration: Duration, readonly: Boolean): TimeRange =
    new TimeRange(start, start + duration, readonly)

  def apply(period: ITimePeriod): TimeRange = apply(period, period.isReadonly)

  def apply(period: ITimePeriod, readonly: Boolean): TimeRange = {
    require(period != null)

    if (period.isAnytime) Anytime
    else apply(period.start, period.end, readonly)
  }

  def apply(start: Option[DateTime], end: Option[DateTime]): TimeRange = {
    apply(start.getOrElse(MinPeriodTime),
           end.getOrElse(MaxPeriodTime),
           readonly = false)
  }

  def apply(start: Option[DateTime], end: Option[DateTime], readonly: Option[Boolean]): TimeRange = {
    apply(start.getOrElse(MinPeriodTime),
           end.getOrElse(MaxPeriodTime),
           readonly.getOrElse(false))
  }
}
