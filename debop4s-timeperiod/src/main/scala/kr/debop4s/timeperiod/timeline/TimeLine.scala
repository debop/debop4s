package kr.debop4s.timeperiod.timeline

import java.util
import kr.debop4s.timeperiod._
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import scala.collection.JavaConversions._

/**
 * kr.debop4s.timeperiod.timeline.TimeLine
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 31. 오후 8:10
 */
@SerialVersionUID(8784228432548497611L)
class TimeLine[T <: ITimePeriod](val periods: ITimePeriodContainer,
                                 private val _limits: ITimePeriod = null,
                                 private val mapper: ITimePeriodMapper = null) extends ITimeLine {

    require(periods != null)
    lazy val log = LoggerFactory.getLogger(getClass)

    val limits = if (_limits != null) TimeRange(_limits) else TimeRange(periods)

    def getPeriod = periods

    def getLimits = limits

    def getPeriodMapper = mapper

    def combinePeriods: ITimePeriodCollection = {
        if (periods.size == 0)
            return new TimePeriodCollection()

        val moments = getTimeLineMoments
        if (moments == null || moments.size == 0)
            return new TimePeriodCollection(TimeRange(periods))

        TimeLines.combinePeriods(moments)
    }

    def intersectPeriods: ITimePeriodCollection = {
        if (periods.size == 0)
            return new TimePeriodCollection()

        val moments = getTimeLineMoments
        if (moments == null || moments.size == 0)
            return new TimePeriodCollection()

        TimeLines.intersectPeriods(moments)
    }

    def calculateGaps: ITimePeriodCollection = {
        log.trace("calculate gaps...")
        val gapPeriods = TimePeriodCollection()

        periods
            .filter(x => limits.intersectsWith(x))
            .foreach(x => gapPeriods.add(TimeRange(x)))

        val moments = getTimeLineMoments(gapPeriods)
        if (moments == null || moments.size == 0)
            return new TimePeriodCollection(limits)

        val range = TimeRange(mapPeriodStart(limits.getStart), mapPeriodEnd(limits.getEnd))
        TimeLines.calculateGap(moments, range)
    }

    private def getTimeLineMoments: ITimeLineMomentCollection = getTimeLineMoments(periods)

    private def getTimeLineMoments(periods: util.Collection[_ <: ITimePeriod]): ITimeLineMomentCollection = {
        log.trace(s"기간 컬렉션으로부터 ITimeLineMoment 컬렉션을 빌드합니다... periods=[$periods]")

        val moments = TimeLineMomentCollection()
        if (periods == null || periods.size == 0)
            return moments

        // setup gap set with all start/end points
        //
        val intersections = new TimePeriodCollection()
        periods
            .filter(mp => !mp.isMoment)
            .foreach(mp => {
            val intersection = limits.getIntersection(mp)
            if (intersection != null && !intersection.isMoment) {
                if (mapper != null) {
                    intersection.setup(mapPeriodStart(intersection.getStart),
                                          mapPeriodEnd(intersection.getEnd))
                }
                log.trace(s"add intersection. intersection=[$intersection]")
                intersections.add(intersection)
            }
        })
        moments.addAll(intersections)
        log.trace(s"기간 컬렉션으로부터 ITimeLineMoment 컬렉션을 빌드했습니다. moments=[$moments]")
        moments
    }

    private def mapPeriodStart(moment: DateTime) =
        if (mapper != null) mapper.unmapStart(moment) else moment

    private def mapPeriodEnd(moment: DateTime) =
        if (mapper != null) mapper.unmapEnd(moment) else moment

}
