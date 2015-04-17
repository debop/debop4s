package debop4s.timeperiod

import java.lang.{Iterable => JIterable}

import debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

import scala.collection.mutable.ArrayBuffer

trait ITimePeriodCollection extends ITimePeriodContainer {

  /**
   * 대상 ITimePeriod 기간에 속하는 기간이 있다면 true를 반환합니다.
   */
  def hasInsidePeriods(target: ITimePeriod): Boolean =
    periods.par.exists(x => Times.hasInside(x, target))

  /**
   * 대상 ITimePeriod 기간과 교집합이 존재하면 true를 반환합니다.
   */
  def hasOverlapPeriods(target: ITimePeriod): Boolean =
    periods.par.exists(x => Times.overlapsWith(x, target))

  /**
   * 대상 시각과 교집합이 존재하면 true를 반환합니다.
   */
  def hasIntersectionPeriods(moment: DateTime): Boolean =
    periods.par.exists(x => Times.hasInside(x, moment))

  /**
   * 대상 ITimePeriod 기간과 교집합이 존재하면 true를 반환합니다.
   */
  def hasIntersectionPeriods(target: ITimePeriod): Boolean =
    periods.par.exists(x => Times.intersectWith(x, target))

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

    val filteringRelation = ArrayBuffer[PeriodRelation](relation)
    // filteringRelation += relation
    if (relations != null && relations.size > 0)
      filteringRelation ++= relations

    periods.filter(x => filteringRelation.contains(Times.relation(x, target)))
  }
}

@SerialVersionUID(106296570654143822L)
class TimePeriodCollection extends TimePeriodContainer with ITimePeriodCollection {

  def this(period: ITimePeriod) {
    this()
    add(period)
  }
  def this(period1: ITimePeriod, period2: ITimePeriod) {
    this()
    addAll(Seq(period1, period2): _*)
  }
  def this(period1: ITimePeriod, period2: ITimePeriod, period3: ITimePeriod) {
    this()
    addAll(Seq(period1, period2, period3): _*)
  }
  def this(period1: ITimePeriod, period2: ITimePeriod, period3: ITimePeriod, period4: ITimePeriod) {
    this()
    addAll(Seq(period1, period2, period3, period4): _*)
  }
  def this(periods: ITimePeriod*) {
    this()
    addAll(periods: _*)
  }
  def this(collection: JIterable[ITimePeriod]) {
    this()
    addAll(collection)
  }

  override def toString: String = periods.mkString(",")

}

object TimePeriodCollection {

  def apply(): TimePeriodCollection = new TimePeriodCollection

  def apply(periods: ITimePeriod*): TimePeriodCollection = {
    val tpc = new TimePeriodCollection()
    tpc.addAll(periods: _*)
    tpc
  }

  def apply(collection: JIterable[_ <: ITimePeriod]): TimePeriodCollection = {
    val tpc = new TimePeriodCollection()
    tpc.addAll(collection)
    tpc
  }
}
