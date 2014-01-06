package kr.debop4s.timeperiod

import kr.debop4s.timeperiod.IntervalEdge.IntervalEdge
import kr.debop4s.timeperiod.utils.Times
import org.joda.time.{Duration, DateTime}

/**
 * kr.debop4s.timeperiod.TimeInterval
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 14. 오후 10:48
 */
trait ITimeInterval extends ITimePeriod {

    def isStartOpen: Boolean

    def isEndOpen: Boolean

    def isOpen: Boolean

    def isStartClosed: Boolean

    def isEndClosed: Boolean

    def isClosed: Boolean

    def isEmpty: Boolean

    def isDegenerate: Boolean

    def isIntervalEnabled: Boolean

    def getStartInterval: DateTime

    def setStartInterval(si: DateTime)

    def getEndInterval: DateTime

    def setEndInterval(ei: DateTime)

    def getStartEdge: IntervalEdge

    def setStartEdge(edge: IntervalEdge)

    def getEndEdge: IntervalEdge

    def setEndEdge(edge: IntervalEdge)

    def expandStartTo(moment: DateTime)

    def expandEndTo(moment: DateTime)

    def expandTo(moment: DateTime)

    def expandTo(period: ITimePeriod)

    def shrinkStartTo(moment: DateTime)

    def shrinkEndTo(moment: DateTime)

    def shrinkTo(moment: DateTime)

    def shrinkTo(period: ITimePeriod)

    def copy(offset: Duration): ITimeInterval

}

class TimeInterval(_start: DateTime = MinPeriodTime,
                   _end: DateTime = MaxPeriodTime,
                   var _startEdge: IntervalEdge = IntervalEdge.Closed,
                   var _endEdge: IntervalEdge = IntervalEdge.Closed,
                   var _intervalEnabled: Boolean = true,
                   _readonly: Boolean = false)
    extends TimePeriod(_start, _end, _readonly) with ITimeInterval {

    def this(moment: DateTime,
             startEdge: IntervalEdge,
             endEdge: IntervalEdge,
             intervalEnabled: Boolean,
             readonly: Boolean) {
        this(moment, moment, startEdge, endEdge, intervalEnabled, readonly)
    }

    def this() {
        this(MinPeriodTime, MaxPeriodTime)
    }

    def startEdge = _startEdge

    def startEdge_=(edge: IntervalEdge) { _startEdge = edge }

    def endEdge = _endEdge

    def endEdge_=(edge: IntervalEdge) { _endEdge = edge }

    def intervalEnabled = _intervalEnabled

    def intervalEnabled_=(v: Boolean) { _intervalEnabled = v }

    override def setStart(v: DateTime) { super.setStart(v) }

    override def setEnd(v: DateTime) { super.setEnd(v) }

    def isStartOpen: Boolean = _startEdge eq IntervalEdge.Opened

    def isEndOpen: Boolean = _endEdge eq IntervalEdge.Opened

    def isOpen: Boolean = isStartOpen && isEndOpen

    def isStartClosed: Boolean = _startEdge eq IntervalEdge.Closed

    def isEndClosed: Boolean = _endEdge eq IntervalEdge.Closed

    def isClosed: Boolean = isStartClosed && isEndClosed

    def isEmpty: Boolean = isMoment && !isClosed

    def isDegenerate: Boolean = isMoment && isClosed

    def isIntervalEnabled: Boolean = this.intervalEnabled

    override def hasStart: Boolean = !(start == MinPeriodTime) || !isStartClosed

    def setIntervalEnabled(intervalEnabled: Boolean) {
        this.intervalEnabled = intervalEnabled
    }

    def getStartInterval: DateTime = super.getStart

    def setStartInterval(v: DateTime) {
        assertMutable()
        assert(v.compareTo(getEnd) <= 0)
        setStart(v)
    }

    override def getStart: DateTime =
        if (isIntervalEnabled && isStartOpen) super.getStart.plus(1)
        else super.getStart

    def getStartEdge: IntervalEdge = startEdge

    def setStartEdge(edge: IntervalEdge) {
        assertMutable()
        startEdge = edge
    }

    override def hasEnd: Boolean = !(super.getEnd == MaxPeriodTime) || !isEndClosed

    def getEndInterval: DateTime = super.getEnd

    def setEndInterval(v: DateTime) {
        assertMutable()
        assert(v.compareTo(super.getStart) >= 0)
        setEnd(v)
    }

    override def getEnd: DateTime =
        if (isIntervalEnabled && isEndOpen) super.getEnd.minus(1)
        else super.getEnd


    def getEndEdge: IntervalEdge = this.endEdge

    def setEndEdge(edge: IntervalEdge) {
        assertMutable()
        this.endEdge = edge
    }

    override def getDuration: Duration = new Duration(super.getStart, super.getEnd)

    def expandStartTo(moment: DateTime) {
        assertMutable()
        if (start.compareTo(moment) > 0)
            setStart(moment)
    }

    def expandEndTo(moment: DateTime) {
        assertMutable()
        if (end.compareTo(moment) < 0)
            setEnd(moment)
    }

    def expandTo(moment: DateTime) {
        expandStartTo(moment)
        expandEndTo(moment)
    }

    def expandTo(period: ITimePeriod) {
        assert(period != null)
        if (period.hasStart) expandStartTo(period.getStart)
        if (period.hasEnd) expandEndTo(period.getEnd)
    }

    def shrinkStartTo(moment: DateTime) {
        assertMutable()
        if (start.compareTo(moment) < 0)
            setStart(moment)
    }

    def shrinkEndTo(moment: DateTime) {
        assertMutable()
        if (end.compareTo(moment) > 0)
            setEnd(moment)
    }

    def shrinkTo(moment: DateTime) {
        assertMutable()
        shrinkStartTo(moment)
        shrinkEndTo(moment)
    }

    def shrinkTo(period: ITimePeriod) {
        assert(period != null)
        assertMutable()
        if (period.hasStart) shrinkStartTo(period.getStart)
        if (period.hasEnd) shrinkEndTo(period.getEnd)
    }

    /** 현재 IInterval에서 오프셋만큼 이동한 {@link ITimeInterval}을 반환합니다. */
    override def copy(offset: Duration = Duration.ZERO): ITimeInterval = {
        new TimeInterval(getStartInterval.plus(offset),
                            getEndInterval.plus(offset),
                            getStartEdge,
                            getEndEdge,
                            isIntervalEnabled,
                            isReadonly)
    }

    override def reset() {
        super.reset()
        intervalEnabled = true
        startEdge = IntervalEdge.Closed
        endEdge = IntervalEdge.Closed
    }

    override def getIntersection(other: ITimePeriod): ITimeInterval = {
        assert(other != null)
        val range: ITimePeriod = super.getIntersection(other)
        new TimeInterval(range.getStart, range.getEnd)
    }

    override def getUnion(other: ITimePeriod): ITimeInterval = {
        assert(other != null)
        val union: ITimePeriod = Times.getUnionRange(this, other)
        new TimeInterval(union.getStart, union.getEnd)
    }
}

object TimeInterval {

    val Anytime: TimeInterval = apply(readonly = true)

    def apply(): TimeInterval = new TimeInterval()

    def apply(readonly: Boolean): TimeInterval = new TimeInterval(_readonly = readonly)

    def apply(start: DateTime, end: DateTime): TimeInterval =
        new TimeInterval(start, end, IntervalEdge.Closed, IntervalEdge.Closed, true, false)

    def apply(period: ITimePeriod): TimeInterval =
        apply(period, IntervalEdge.Closed, IntervalEdge.Closed, intervalEnabled = true)

    def apply(period: ITimePeriod,
              startEdge: IntervalEdge,
              endEdge: IntervalEdge,
              intervalEnabled: Boolean): TimeInterval =
        apply(period, startEdge, endEdge, intervalEnabled, readonly = false)

    def apply(period: ITimePeriod,
              startEdge: IntervalEdge,
              endEdge: IntervalEdge,
              intervalEnabled: Boolean,
              readonly: Boolean): TimeInterval = {
        assert(period != null)
        if (period.isAnytime)
            Anytime
        else
            new TimeInterval(period.getStart, period.getEnd, startEdge, endEdge, intervalEnabled, readonly)
    }
}




