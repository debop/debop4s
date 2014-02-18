package kr.debop4s.timeperiod

import java.util
import kr.debop4s.core.NotSupportedException
import kr.debop4s.timeperiod.OrderDirection.OrderDirection
import kr.debop4s.timeperiod.utils.Times
import org.joda.time.{Duration, DateTime}
import scala.annotation.varargs
import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer


/**
 * kr.debop4s.timeperiod.TimePeriodContainer
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 14. 오후 11:45
 */
trait ITimePeriodContainer extends util.List[ITimePeriod] with ITimePeriod {

    def periods: ArrayBuffer[ITimePeriod]

    def getPeriods: ArrayBuffer[ITimePeriod]


    /** 시작시각을 설정합니다. */
    def setStart(start: DateTime)

    /** 완료시각을 설정합니다. */
    def setEnd(end: DateTime)

    /** 읽기전용 여부 */
    def isReadonly: Boolean

    def apply(index: Int) = periods(index)

    def get(index: Int) = periods(index)

    def containsPeriod(target: ITimePeriod): Boolean = {
        if (target == null) false
        else periods.contains(target)
    }

    def addAll(elems: Iterable[_ <: ITimePeriod]): Boolean = {
        periods ++= elems
        true
    }

    @varargs
    def addAll(elems: ITimePeriod*): Boolean = {
        periods ++= elems
        true
    }

    def sortByStart(sortDir: OrderDirection) {
        var sorted: ArrayBuffer[ITimePeriod] = null
        if (sortDir == OrderDirection.ASC) {
            sorted = periods.sortWith((x, y) => x.start.compareTo(y.start) < 0)
        } else {
            sorted = periods.sortWith((x, y) => x.start.compareTo(y.start) > 0)
        }
        periods.clear()
        periods ++= sorted
    }

    def sortByEnd(sortDir: OrderDirection) {
        var sorted: ArrayBuffer[ITimePeriod] = null
        if (sortDir == OrderDirection.ASC) {
            sorted = periods.sortWith((x, y) => x.end.compareTo(y.end) < 0)
        } else {
            sorted = periods.sortWith((x, y) => x.end.compareTo(y.end) > 0)
        }
        periods.clear()
        periods ++= sorted
    }

    def sortByDuration(sortDir: OrderDirection) {
        var sorted: ArrayBuffer[ITimePeriod] = null
        if (sortDir == OrderDirection.ASC) {
            sorted = periods.sortWith((x, y) => x.duration.compareTo(y.duration) < 0)
        } else {
            sorted = periods.sortWith((x, y) => x.duration.compareTo(y.duration) > 0)
        }
        periods.clear()
        periods ++= sorted
    }

    def size = periods.size

    def isEmpty = periods.isEmpty

    def contains(elem: Any): Boolean = periods.contains(elem)

    def iterator = periods.asJava.iterator


    def add(x: ITimePeriod): Boolean = {
        periods += x
        true
    }

    def remove(x: Any): Boolean = {
        if (periods.contains(x)) {
            periods -= x.asInstanceOf[ITimePeriod]
            true
        } else {
            false
        }
    }

    def containsAll(elems: util.Collection[_]): Boolean = {
        elems.asScala.foreach(x => {
            if (!periods.contains(x))
                false
        })
        true
    }

    def addAll(elems: util.Collection[_ <: ITimePeriod]): Boolean = {
        elems.asScala.foreach(x => periods += x.asInstanceOf[ITimePeriod])
        true
    }

    def addAll(index: Int, elems: util.Collection[_ <: ITimePeriod]): Boolean = {
        periods.insert(index, elems.asScala.toSeq: _*)
        true
    }

    def removeAll(elems: util.Collection[_]): Boolean = {
        elems.asScala.foreach(c => periods -= c.asInstanceOf[ITimePeriod])
        true
    }

    def retainAll(elems: util.Collection[_]): Boolean = {
        periods.clear()
        elems.asScala.foreach {
            case elem: ITimePeriod =>
                periods += elem
            case _ =>
        }
        true
    }


    def set(index: Int, elem: ITimePeriod) = {
        periods.update(index, elem)
        periods(index)
    }

    def add(index: Int, elem: ITimePeriod) {
        periods.insert(index, elem)
    }

    def clear() = periods.clear()

    def remove(index: Int) = periods.remove(index)

    def indexOf(o: Any): Int = periods.indexOf(o)

    def lastIndexOf(o: Any): Int = periods.lastIndexOf(o)

    def listIterator(): util.ListIterator[ITimePeriod] = {
        periods.asJava.listIterator()
    }

    def listIterator(index: Int): util.ListIterator[ITimePeriod] = {
        periods.asJava.listIterator(index)
    }

    def subList(fromIndex: Int, toIndex: Int): util.List[ITimePeriod] = {
        periods.slice(fromIndex, toIndex).asJava
    }

    def compare(x: ITimePeriod, y: ITimePeriod): Int = x.start.compareTo(y.start)

    def toArray: Array[AnyRef] = periods.asJava.toArray

    def toArray[T](a: Array[T with Object]): Array[T with Object] =
        periods.asJava.toArray[T](a)

}

@SerialVersionUID(-7112720659283751048L)
class TimePeriodContainer extends ITimePeriodContainer {

    implicit val dateTimeOrdering = new DateTimeOrdering()

    val _periods = ArrayBuffer[ITimePeriod]()

    def this(periods: ITimePeriod*) {
        this()
        _periods ++= periods
    }

    def this(collection: Iterable[_ <: ITimePeriod]) {
        this()
        _periods ++= collection
    }

    def periods = _periods

    def getPeriods = periods

    def start: DateTime = {
        if (size == 0) MinPeriodTime
        else if (periods.isEmpty) MinPeriodTime
        else periods.minBy(x => x.start).start
    }

    def getStart = start

    def end: DateTime = {
        if (size == 0) MaxPeriodTime
        else if (periods.isEmpty) MaxPeriodTime
        else periods.maxBy(x => x.end).end
    }

    def getEnd = end

    def start_=(x: DateTime) {
        if (size > 0)
            move(new Duration(start, x))
    }

    def setStart(x: DateTime) {
        start_=(x)
    }

    def end_=(x: DateTime) {
        if (size > 0) {
            move(new Duration(getEnd, x))
        }
    }

    def setEnd(x: DateTime) {
        end_=(x)
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

    def getRelation(other: ITimePeriod) = Times.getRelation(this, other)

    def getIntersection(other: ITimePeriod) = Times.getIntersectionRange(this, other)

    def getUnion(other: ITimePeriod) = Times.getUnionRange(this, other)
}
