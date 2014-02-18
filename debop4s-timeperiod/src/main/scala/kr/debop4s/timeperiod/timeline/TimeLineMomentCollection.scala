package kr.debop4s.timeperiod.timeline

import kr.debop4s.timeperiod.{DateTimeOrdering, ITimePeriod}
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import scala.collection.mutable

/**
 * kr.debop4s.timeperiod.timeline.TimeLineMomentCollection
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 31. 오후 8:39
 */
object TimeLineMomentCollection {

    def apply(): TimeLineMomentCollection = new TimeLineMomentCollection()
}

@SerialVersionUID(-5739605965754152358L)
class TimeLineMomentCollection extends ITimeLineMomentCollection {

    lazy val log = LoggerFactory.getLogger(getClass)

    implicit val dateTimeOrdering = new DateTimeOrdering()

    var moments = mutable.ArrayBuffer[ITimeLineMoment]()

    override def size: Int = moments.size

    override def isEmpty: Boolean = moments.isEmpty

    def getMin: ITimeLineMoment = if (isEmpty) null else moments.minBy(_.getMoment)

    def getMax: ITimeLineMoment = if (isEmpty) null else moments.maxBy(_.getMoment)

    def get(index: Int): ITimeLineMoment = moments(index)

    def apply(index: Int): ITimeLineMoment = moments(index)

    def add(period: ITimePeriod) {
        if (period != null) {
            addPeriod(period.getStart, period)
            addPeriod(period.getEnd, period)
        }
    }

    def addAll(periods: Iterable[_ <: ITimePeriod]) {
        periods.filter(x => x != null).toSeq.sortBy(x => x.start).foreach(x => add(x))
    }

    def remove(period: ITimePeriod) {
        if (period != null) {
            removePeriod(period.getStart, period)
            removePeriod(period.getEnd, period)
        }
    }

    def find(moment: DateTime): ITimeLineMoment =
        moments.find(x => x.getMoment.equals(moment)).getOrElse(null)


    def contains(moment: DateTime): Boolean = find(moment) != null

    def iterator: Iterator[ITimeLineMoment] = moments.iterator

    protected def addPeriod(moment: DateTime, period: ITimePeriod) {
        var item = find(moment)
        if (item == null) {
            item = new TimeLineMoment(moment)
            moments += item
            moments = moments.sortBy(_.getMoment)
            log.trace(s"TimeLineMoment를 추가했습니다. item=[$item]")
        }
        item.getPeriods.add(period)
    }

    protected def removePeriod(moment: DateTime, period: ITimePeriod) {
        val item = find(moment)

        if (item != null && item.getPeriods.contains(period)) {
            item.getPeriods.remove(period)
            if (item.getPeriods.size == 0)
                moments -= item
            log.trace(s"TimeLineMoment를 제거했습니다. item=[$item]")
        }
    }

    override def toString(): String = "TimeLineMomentCollection# moments=" + moments.toString()
}
