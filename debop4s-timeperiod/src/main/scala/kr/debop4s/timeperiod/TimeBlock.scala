package kr.debop4s.timeperiod

import kr.debop4s.core.logging.Logger
import kr.debop4s.time._
import kr.debop4s.timeperiod.utils.Times
import org.joda.time.{DateTime, Duration}

/**
 * kr.debop4s.timeperiod.TimeBlock
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 14. 오후 9:59
 */
trait ITimeBlock extends ITimePeriod {

    def setStart(v: DateTime)

    def setEnd(v: DateTime)

    def setDuration(d: Duration)

    def setup(ns: DateTime, ne: DateTime)

    def durationFromStart(nd: Duration)

    def durationFromEnd(nd: Duration)

    def nextBlock(offset: Duration): ITimeBlock

    def previousBlock(offset: Duration): ITimeBlock
}

class TimeBlock(_start: DateTime = MinPeriodTime,
                _end: DateTime = MaxPeriodTime,
                _readonly: Boolean)
    extends TimePeriod(_start, _end, _readonly) with ITimeBlock {

    def this(_start: DateTime, _end: DateTime) {
        this(_start, _end, false)
    }

    override lazy val log = Logger[TimeBlock]

    override def start_=(v: DateTime) {
        assertMutable()
        assert(v <= end, "시작시각이 완료시각보다 클 수 없습니다.")
        super.start_=(v)
    }
    override def setStart(v: DateTime) {
        start = v
    }

    override def end_=(v: DateTime) {
        assertMutable()
        assert(v >= start, "완료시각이 시작시각보다 작을 수 없습니다.")
        super.end_=(v)

    }
    override def setEnd(v: DateTime) {
        end = v
    }

    var _duration: Duration = new Duration(start, end)

    override def duration = _duration

    override def duration_=(v: Duration) {
        setDuration(v)
    }

    override def getDuration = _duration

    override def setDuration(v: Duration) {
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

    def setup(start: DateTime, duration: Duration) {
        assertMutable()
        assertValidDuration(duration)
        log.trace(s"TimeBlock 값을 새로 설정합니다. start=$start, duration=$duration")

        setStart(start)
        setDuration(duration)
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

    override def getIntersection(other: ITimePeriod): TimeBlock = {
        assert(other != null)
        Times.getIntersectionBlock(this, other)
    }

    override def getUnion(other: ITimePeriod): TimeBlock = {
        assert(other != null)
        Times.getUnionBlock(this, other)
    }

    protected def assertValidDuration(v: Duration) {
        assert(v != null && v.getMillis >= 0, "duration은 0 이상의 값을 가져야 합니다.")
    }

    override protected def buildStringHelper =
        super.buildStringHelper
            .add("duration", duration)
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
        if (source.isAnytime)
            Anytime
        else
            new TimeBlock(source.getStart, source.getEnd, readonly)
    }

    def toRange(block: TimeBlock): TimeRange = TimeRange(block.start, block.end, block.isReadonly)

    def toInterval(block: TimeBlock): TimeInterval = TimeInterval(block)

    private def assertValidDuration(v: Duration) {
        assert(v != null && v.getMillis >= 0, "duration은 0 이상의 값을 가져야 합니다.")
    }
}
