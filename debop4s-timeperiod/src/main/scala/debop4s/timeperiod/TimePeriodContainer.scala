package debop4s.timeperiod

import debop4s.core.NotSupportedException
import debop4s.core.jodatime._
import debop4s.core.utils.ToStringHelper
import debop4s.timeperiod.OrderDirection.OrderDirection
import debop4s.timeperiod.utils.Times
import org.joda.time.{Duration, DateTime}
import org.slf4j.LoggerFactory
import scala.annotation.varargs
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer


/**
 * debop4s.timeperiod.TimePeriodContainer
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 14. 오후 11:45
 */
trait ITimePeriodContainer extends mutable.Buffer[ITimePeriod] with ITimePeriod {

    def periods: mutable.ArrayBuffer[ITimePeriod]

    override def +=:(elem: ITimePeriod): this.type = {
        elem +=: periods
        this
    }

    override def +=(elem: ITimePeriod): this.type = {
        add(elem)
        this
    }

    override def length: Int = periods.length

    override def size = periods.size

    override def isEmpty = periods.isEmpty

    //    override def contains(elem: Any): Boolean = periods.contains(elem)

    /** 시작시각을 설정합니다. */
    def start_=(x: DateTime)

    /** 완료시각을 설정합니다. */
    def end_=(x: DateTime)

    /** 읽기전용 여부 */
    override def isReadonly: Boolean

    def apply(index: Int) = periods(index)

    def get(index: Int) = periods(index)

    def clear() {
        periods.clear()
    }

    def containsPeriod(target: ITimePeriod): Boolean = {
        if (target == null) false
        else periods.contains(target)
    }

    def add(x: ITimePeriod) {
        x match {
            case container: ITimePeriodContainer => container.foreach(add)
            case _ => if (!periods.contains(x)) periods += x
        }
    }

    def addAll(elems: Iterable[ITimePeriod]) {
        elems.foreach(add)
    }

    @varargs
    def addAll(elems: ITimePeriod*) {
        elems.foreach(add)
    }

    @varargs
    override def insert(n: Int, elems: ITimePeriod*) {
        periods.insert(n, elems: _*)
    }

    override def insertAll(n: Int, elems: Traversable[ITimePeriod]) {
        periods.insertAll(n, elems)
    }

    override def iterator = periods.iterator

    def insertAll(n: Int, elems: Iterable[ITimePeriod]) = {
        periods.insert(n, elems.toSeq: _*)
    }

    def containsAll(elems: Iterable[_]): Boolean = {
        elems.filter(_.isInstanceOf[ITimePeriod]).forall(x => periods.contains(x))
    }

    def remove(x: Any): Boolean = {
        x match {
            case period: ITimePeriod if periods.contains(x) =>
                periods -= period
                true
            case _ => false
        }
    }

    def removeAll(elems: Iterable[_]): Boolean = {
        elems.foreach {
            case elem: ITimePeriod if periods.contains(elem) => remove(elem)
        }
        true
    }

    def retainAll(elems: Iterable[_]): Boolean = {
        periods.clear()
        elems.foreach {
            case elem: ITimePeriod if !periods.contains(elem) => periods += elem
            case _ =>
        }
        true
    }

    override def update(n: Int, newelem: ITimePeriod) {
        periods.update(n, newelem)
    }


    def set(index: Int, elem: ITimePeriod) = {
        periods.update(index, elem)
        periods(index)
    }

    override def remove(index: Int) = periods.remove(index)

    override def indexOf[T >: ITimePeriod](o: T): Int = periods.indexOf(o)

    override def lastIndexOf[T >: ITimePeriod](o: T): Int = periods.lastIndexOf(o)

    def sortByStart(sortDir: OrderDirection) {
        var sorted: ArrayBuffer[ITimePeriod] = null
        if (sortDir == OrderDirection.ASC) {
            sorted = periods.sortWith((x, y) => x.start < y.start)
        } else {
            sorted = periods.sortWith((x, y) => x.start > y.start)
        }
        periods.clear()
        periods ++= sorted
    }

    def sortByEnd(sortDir: OrderDirection) {
        var sorted: ArrayBuffer[ITimePeriod] = null
        if (sortDir == OrderDirection.ASC) {
            sorted = periods.sortWith((x, y) => x.end < y.end)
        } else {
            sorted = periods.sortWith((x, y) => x.end > y.end)
        }
        periods.clear()
        periods ++= sorted
    }

    def sortByDuration(sortDir: OrderDirection) {
        var sorted: ArrayBuffer[ITimePeriod] = null
        if (sortDir == OrderDirection.ASC) {
            sorted = periods.sortWith((x, y) => x.duration < y.duration)
        } else {
            sorted = periods.sortWith((x, y) => x.duration > y.duration)
        }
        periods.clear()
        periods ++= sorted
    }

    def subList(fromIndex: Int, toIndex: Int): Seq[ITimePeriod] = {
        periods.slice(fromIndex, toIndex)
    }

    def compare(x: ITimePeriod, y: ITimePeriod): Int = x.start.compareTo(y.start)

    override protected def buildStringHelper: ToStringHelper =
        super.buildStringHelper
        .add("periods", periods)
}

@SerialVersionUID(-7112720659283751048L)
class TimePeriodContainer extends ITimePeriodContainer {

    private lazy val log = LoggerFactory.getLogger(getClass)

    implicit val dateTimeOrdering = new DateTimeOrdering()

    val _periods = ArrayBuffer[ITimePeriod]()

    def periods = _periods

    override def start: DateTime = {
        if (size == 0) MinPeriodTime
        else if (periods.isEmpty) MinPeriodTime
        else periods.minBy(x => x.start).start
    }

    override def end: DateTime = {
        if (size == 0) MaxPeriodTime
        else if (periods.isEmpty) MaxPeriodTime
        else periods.maxBy(x => x.end).end
    }

    override def start_=(x: DateTime) {
        if (size > 0)
            move(new Duration(start, x))
    }

    override def end_=(x: DateTime) {
        if (size > 0) {
            move(new Duration(end, x))
        }
    }

    def duration: Duration = if (hasPeriod) new Duration(start, end) else MaxDuration

    def getDuration = duration

    def hasStart = start != MinPeriodTime

    def hasEnd = end != MaxPeriodTime

    def hasPeriod = hasStart && hasEnd

    def isMoment = hasStart && (start eq end)

    def isAnytime = !hasStart && !hasEnd

    def isReadonly = false

    def setup(ns: DateTime, ne: DateTime) {
        throw new NotSupportedException("TimePeriodContainer에서는 setup 메소드를 지원하지 않습니다.")
    }

    def copy(offset: Duration): ITimePeriod = {
        throw new NotSupportedException("TimePeriodContainer에서는 setup 메소드를 지원하지 않습니다.")
    }

    def move(offset: Duration) {
        if (offset != null && offset.getMillis != 0) {
            log.trace(s"모든 기간을 offset=[$offset] 만큼 이동합니다.")
            _periods.foreach(_.move(offset))
        }
    }

    def isSamePeriod(other: ITimePeriod) = (other != null) && (start eq other.start) && (end eq other.end)

    def hasInside(moment: DateTime) = Times.hasInside(this, moment)

    def hasInside(other: ITimePeriod) = Times.hasInside(this, other)

    def intersectsWith(other: ITimePeriod) = Times.intersectWith(this, other)

    def overlapsWith(other: ITimePeriod) = Times.overlapsWith(this, other)

    def reset() = periods.clear()

    def relation(other: ITimePeriod) = Times.relation(this, other)

    def intersection(other: ITimePeriod) = Times.intersectRange(this, other)

    def union(other: ITimePeriod) = Times.unionRange(this, other)

}

object TimePeriodContainer {

    def apply(collection: Iterable[ITimePeriod]): TimePeriodContainer = {
        val container = new TimePeriodContainer()
        container.addAll(collection)
        container
    }

    @varargs
    def apply(periods: ITimePeriod*): TimePeriodContainer = {
        val container = new TimePeriodContainer()
        container.addAll(periods)
        container
    }
}
