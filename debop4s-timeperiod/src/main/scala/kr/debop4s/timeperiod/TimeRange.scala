package kr.debop4s.timeperiod

import org.joda.time.{Duration, DateTime}

/**
 * kr.debop4s.timeperiod.TimeRange
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 14. 오후 9:40
 */
trait ITimeRange extends ITimePeriod {

    /** 시작시각을 설정합니다. */
    def setStart(start: DateTime)

    /** 완료시각을 설정합니다. */
    def setEnd(end: DateTime)

    /** 시작시각을 기준으로 기간을 설정합니다. */
    def setDuration(duration: Duration)

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

class TimeRange(_start: DateTime = MinPeriodTime,
                _end: DateTime = MaxPeriodTime,
                _readonly: Boolean = false) extends TimePeriod(_start, _end, _readonly) with ITimeRange {

    override def setStart(v: DateTime) {
        super.setStart(v)
    }

    override def setEnd(v: DateTime) {
        super.setEnd(v)
    }

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
        assertMutable()
        expandStartTo(moment)
        expandEndTo(moment)
    }

    def expandTo(period: ITimePeriod) {
        assert(period != null)
        assertMutable()
        expandStartTo(period.getStart)
        expandEndTo(period.getEnd)
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
        shrinkStartTo(period.getStart)
        shrinkEndTo(period.getEnd)
    }
}

object TimeRange {

    val Anytime = apply(readonly = true)

    def apply(): TimeRange =
        new TimeRange(MinPeriodTime, MaxPeriodTime, false)

    def apply(readonly: Boolean): TimeRange =
        new TimeRange(MinPeriodTime, MaxPeriodTime, readonly)

    def apply(moment: DateTime): TimeRange = apply(moment, moment, readonly = false)

    def apply(start: DateTime, end: DateTime): TimeRange = apply(start, end, readonly = false)

    def apply(start: DateTime, end: DateTime, readonly: Boolean): TimeRange =
        new TimeRange(start, end, readonly)

    def apply(start: DateTime, duration: Duration): TimeRange = apply(start, duration, readonly = false)

    def apply(start: DateTime, duration: Duration, readonly: Boolean): TimeRange =
        new TimeRange(start, start.plus(duration), readonly)

    def apply(period: ITimePeriod): TimeRange = apply(period, readonly = false)

    def apply(period: ITimePeriod, readonly: Boolean): TimeRange = {
        assert(period != null)
        if (period.isAnytime)
            Anytime
        else
            apply(period.getStart, period.getEnd, readonly)
    }
}
