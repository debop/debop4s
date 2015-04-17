package debop4s.timeperiod

import java.lang.{Iterable => JIterable}

import debop4s.core.NotSupportedException
import debop4s.core.conversions.jodatime._
import debop4s.timeperiod.TimeSpec._
import debop4s.timeperiod.utils.Times
import org.joda.time.{DateTime, Duration}

import scala.annotation.varargs
import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

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

  // def contains(elem: Any): Boolean = periods.contains(elem)

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

  def addAll(elems: JIterable[_ <: ITimePeriod]) {
    elems.asScala.foreach(add)
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

  def containsAll(elems: JIterable[_]): Boolean = {
    elems.asScala.filter(_.isInstanceOf[ITimePeriod]).forall(x => periods.contains(x))
  }

  def remove(x: Any): Boolean = {
    x match {
      case period: ITimePeriod if periods.contains(x) =>
        periods -= period
        true
      case _ => false
    }
  }

  def removeAll(elems: JIterable[_]): Boolean = {
    elems.asScala.foreach {
      case elem: ITimePeriod if periods.contains(elem) => remove(elem)
    }
    true
  }

  def retainAll(elems: JIterable[_]): Boolean = {
    periods.clear()
    elems.asScala.foreach {
      case elem: ITimePeriod if !periods.contains(elem) => periods += elem
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
    var sorted = null: ArrayBuffer[ITimePeriod]

    if (sortDir == OrderDirection.ASC) {
      sorted = periods.sortBy(_.start)
    } else {
      sorted = periods.sortBy(-_.start.getMillis)
    }

    periods.clear()
    periods ++= sorted
  }

  def sortByEnd(sortDir: OrderDirection) {
    var sorted = null: ArrayBuffer[ITimePeriod]

    if (sortDir == OrderDirection.ASC) {
      sorted = periods.sortBy(_.end)
    } else {
      sorted = periods.sortBy(-_.end.getMillis)
    }

    periods.clear()
    periods ++= sorted
  }

  def sortByDuration(sortDir: OrderDirection) {
    var sorted = null: ArrayBuffer[ITimePeriod]

    if (sortDir == OrderDirection.ASC) {
      sorted = periods.sortBy(_.duration) // periods.sortWith((x, y) => x.duration < y.duration)
    } else {
      sorted = periods.sortBy(-_.duration.getMillis) // periods.sortWith((x, y) => x.duration > y.duration)
    }

    periods.clear()
    periods ++= sorted
  }

  def subList(fromIndex: Int, toIndex: Int): Seq[ITimePeriod] = {
    periods.slice(fromIndex, toIndex)
  }

  def compare(x: ITimePeriod, y: ITimePeriod): Int = x.start.compareTo(y.start)

  override protected def buildStringHelper =
    super.buildStringHelper
    .add("periods", periods)
}

@SerialVersionUID(-7112720659283751048L)
class TimePeriodContainer extends ITimePeriodContainer {

  implicit val dateTimeOrdering = new DateTimeOrdering()

  val _periods = ArrayBuffer[ITimePeriod]()

  def periods = _periods

  override def start: DateTime = {
    if (size == 0) MinPeriodTime
    else if (periods.isEmpty) MinPeriodTime
    else periods.par.minBy(x => x.start).start
  }

  override def end: DateTime = {
    if (size == 0) MaxPeriodTime
    else if (periods.isEmpty) MaxPeriodTime
    else periods.par.maxBy(x => x.end).end
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
      _periods.par.foreach(_.move(offset))
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

  def apply(collection: JIterable[_ <: ITimePeriod]): TimePeriodContainer = {
    val container = new TimePeriodContainer()
    container.addAll(collection)
    container
  }

  @varargs
  def apply(periods: ITimePeriod*): TimePeriodContainer = {
    val container = new TimePeriodContainer()
    container.addAll(periods: _*)
    container
  }
}
