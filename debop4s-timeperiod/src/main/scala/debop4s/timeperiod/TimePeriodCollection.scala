package debop4s.timeperiod

import debop4s.timeperiod.PeriodRelation.PeriodRelation
import debop4s.timeperiod.utils.Times
import org.joda.time.DateTime
import scala.collection.mutable.ArrayBuffer

/**
 * debop4s.timeperiod.TimePeriodCollection
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 14. 오후 11:45
 */
trait ITimePeriodCollection extends ITimePeriodContainer {

    /**
     * 대상 ITimePeriod 기간에 속하는 기간이 있다면 true를 반환합니다.
     */
    def hasInsidePeriods(target: ITimePeriod): Boolean =
        periods.exists(x => Times.hasInside(x, target))

    /**
     * 대상 ITimePeriod 기간과 교집합이 존재하면 true를 반환합니다.
     */
    def hasOverlapPeriods(target: ITimePeriod): Boolean =
        periods.exists(x => Times.overlapsWith(x, target))

    /**
     * 대상 시각과 교집합이 존재하면 true를 반환합니다.
     */
    def hasIntersectionPeriods(moment: DateTime): Boolean =
        periods.exists(x => Times.hasInside(x, moment))

    /**
     * 대상 ITimePeriod 기간과 교집합이 존재하면 true를 반환합니다.
     */
    def hasIntersectionPeriods(target: ITimePeriod): Boolean =
        periods.exists(x => Times.intersectWith(x, target))

    /**
     * 대상 ITimePeriod 기간을 포함하는 ITimePeriod 들을 열거합니다.
     */
    def insidePeriods(target: ITimePeriod): Seq[ITimePeriod] =
        periods.filter(x => Times.hasInside(x, target))

    def overlapPeriods(target: ITimePeriod): Seq[ITimePeriod] =
        periods.filter(x => Times.overlapsWith(x, target))

    /**
     * 지정한 moment 시각과 교집합이 존재하는 TimePeriod를 열거합니다.
     */
    def intersectionPeriods(moment: DateTime): Seq[ITimePeriod] =
        periods.filter(x => Times.hasInside(x, moment))

    /**
     * 지정한 target 기간과 교집합이 존재하는 TimePeriod를 열거합니다.
     */
    def intersectionPeriods(target: ITimePeriod): Seq[ITimePeriod] =
        periods.filter(x => Times.intersectWith(x, target))

    /**
     * 대상 ITimePeriod 와 특정 관계를 가지는 ITimePeriod 요소들을 열거합니다.
     */
    def relationPeriods(target: ITimePeriod,
                        relation: PeriodRelation,
                        relations: PeriodRelation*): Seq[ITimePeriod] = {

        val filteringRelation = ArrayBuffer[PeriodRelation]()
        filteringRelation += relation
        if (relations != null && relations.size > 0)
            filteringRelation ++= relations

        periods.filter(x => filteringRelation.contains(Times.relation(x, target)))
    }
}

@SerialVersionUID(106296570654143822L)
class TimePeriodCollection extends TimePeriodContainer with ITimePeriodCollection {

    override def toString: String = periods.mkString(",")

}

object TimePeriodCollection {

    def apply(): TimePeriodCollection = new TimePeriodCollection

    def apply(periods: ITimePeriod*): TimePeriodCollection = {
        val tpc = new TimePeriodCollection()
        tpc.addAll(periods: _*)
        tpc
    }

    def apply(collection: Iterable[ITimePeriod]): TimePeriodCollection = {
        val tpc = new TimePeriodCollection()
        tpc.addAll(collection)
        tpc
    }
}
