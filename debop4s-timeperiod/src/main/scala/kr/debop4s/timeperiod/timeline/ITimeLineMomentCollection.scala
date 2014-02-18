package kr.debop4s.timeperiod.timeline

import kr.debop4s.timeperiod.ITimePeriod
import org.joda.time.DateTime
import scala.collection.mutable

/**
 * kr.debop4s.timeperiod.timeline.ITimeLineMomentCollection
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 31. 오후 8:17
 */
trait ITimeLineMomentCollection extends Seq[ITimeLineMoment] with Serializable {

    def size: Int

    def isEmpty: Boolean

    def min: ITimeLineMoment

    def max: ITimeLineMoment

    def get(index: Int): ITimeLineMoment

    def apply(index: Int): ITimeLineMoment

    def add(period: ITimePeriod)

    def addAll(periods: Iterable[_ <: ITimePeriod])

    def remove(period: ITimePeriod)

    def find(moment: DateTime): ITimeLineMoment

    def contains(moment: DateTime): Boolean
}
