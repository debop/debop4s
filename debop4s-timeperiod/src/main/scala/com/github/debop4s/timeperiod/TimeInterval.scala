package com.github.debop4s.timeperiod

import com.github.debop4s.timeperiod.IntervalEdge.IntervalEdge
import com.github.debop4s.timeperiod.utils.Times
import org.joda.time.{Duration, DateTime}

/**
 * com.github.debop4s.timeperiod.TimeInterval
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

class TimeInterval(private[this] val _start: DateTime = MinPeriodTime,
                   private[this] val _end: DateTime = MaxPeriodTime,
                   private[this] var _startEdge: IntervalEdge = IntervalEdge.Closed,
                   private[this] var _endEdge: IntervalEdge = IntervalEdge.Closed,
                   private[this] var _intervalEnabled: Boolean = true,
                   private[this] val _readonly: Boolean = false)
    extends TimePeriod(_start, _end, _readonly) with ITimeInterval {

    def startEdge = _startEdge

    def startEdge_=(edge: IntervalEdge) { _startEdge = edge }

    def endEdge = _endEdge

    def endEdge_=(edge: IntervalEdge) { _endEdge = edge }

    def intervalEnabled = _intervalEnabled

    def intervalEnabled_=(v: Boolean) { _intervalEnabled = v }

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

    def getStartInterval: DateTime = super.start

    def setStartInterval(v: DateTime) {
        assertMutable()
        assert(v <= end)
        start = v
    }

    override def start: DateTime =
        if (isIntervalEnabled && isStartOpen) super.start + 1
        else super.start

    def getStartEdge: IntervalEdge = startEdge

    def setStartEdge(edge: IntervalEdge) {
        assertMutable()
        startEdge = edge
    }

    override def hasEnd: Boolean = !(super.end == MaxPeriodTime) || !isEndClosed

    def getEndInterval: DateTime = super.end

    def setEndInterval(v: DateTime) {
        assertMutable()
        assert(v >= super.start)
        end = v
    }

    override def end: DateTime =
        if (isIntervalEnabled && isEndOpen) super.end - 1
        else super.end


    def getEndEdge: IntervalEdge = this.endEdge

    def setEndEdge(edge: IntervalEdge) {
        assertMutable()
        this.endEdge = edge
    }

    def expandStartTo(moment: DateTime) {
        assertMutable()
        if (start.compareTo(moment) > 0)
            start = moment
    }

    def expandEndTo(moment: DateTime) {
        assertMutable()
        if (end.compareTo(moment) < 0)
            end = moment
    }

    def expandTo(moment: DateTime) {
        expandStartTo(moment)
        expandEndTo(moment)
    }

    def expandTo(period: ITimePeriod) {
        assert(period != null)
        if (period.hasStart) expandStartTo(period.start)
        if (period.hasEnd) expandEndTo(period.end)
    }

    def shrinkStartTo(moment: DateTime) {
        assertMutable()
        if (start.compareTo(moment) < 0)
            start = moment
    }

    def shrinkEndTo(moment: DateTime) {
        assertMutable()
        if (end.compareTo(moment) > 0)
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
        if (period.hasStart) shrinkStartTo(period.start)
        if (period.hasEnd) shrinkEndTo(period.end)
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
        new TimeInterval(range.start, range.end)
    }

    override def getUnion(other: ITimePeriod): ITimeInterval = {
        assert(other != null)
        val union: ITimePeriod = Times.getUnionRange(this, other)
        new TimeInterval(union.start, union.end)
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
        apply(period, startEdge, endEdge, intervalEnabled, period.isReadonly)

    def apply(period: ITimePeriod,
              startEdge: IntervalEdge,
              endEdge: IntervalEdge,
              intervalEnabled: Boolean,
              readonly: Boolean): TimeInterval = {
        assert(period != null)
        if (period.isAnytime) Anytime
        else new TimeInterval(period.start,
                                 period.end,
                                 startEdge,
                                 endEdge,
                                 intervalEnabled,
                                 readonly)
    }
}




