package debop4s.timeperiod.timeline

import debop4s.core.ToStringHelper
import debop4s.timeperiod._
import org.joda.time.DateTime
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
 * [[ITimeLineMoment]] 의 컬렉션입니다.
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 31. 오후 8:17
 */
trait ITimeLineMomentCollection extends mutable.Iterable[ITimeLineMoment] with Serializable {

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


/**
 * TimeLineMomentCollection
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 31. 오후 8:39
 */
object TimeLineMomentCollection {

  def apply(): TimeLineMomentCollection = new TimeLineMomentCollection()
}

@SerialVersionUID(-5739605965754152358L)
class TimeLineMomentCollection extends ITimeLineMomentCollection {

  private lazy val log = LoggerFactory.getLogger(getClass)

  // minBy, maxBy에서 DateTime으로 정렬하기 위해 사용합니다.
  implicit val dateTimeOrdering = new DateTimeOrdering()

  private var _moments = ArrayBuffer[ITimeLineMoment]()

  override def size: Int = _moments.size

  override def isEmpty: Boolean = _moments.isEmpty

  def min: ITimeLineMoment = if (isEmpty) null else _moments.minBy(_.moment)

  def max: ITimeLineMoment = if (isEmpty) null else _moments.maxBy(_.moment)

  def get(index: Int): ITimeLineMoment = _moments(index)

  def apply(index: Int): ITimeLineMoment = _moments(index)

  def add(period: ITimePeriod) {
    if (period != null) {
      addPeriod(period.start, period)
      addPeriod(period.end, period)
    }
  }

  def addAll(periods: Iterable[_ <: ITimePeriod]) {
    periods
    .filter(x => x != null)
    .foreach(add)
  }

  def remove(period: ITimePeriod) {
    if (period != null) {
      removePeriod(period.start, period)
      removePeriod(period.end, period)
    }
  }

  def find(moment: DateTime): ITimeLineMoment =
    _moments.find(x => x.moment.equals(moment)).getOrElse(null)


  def contains(moment: DateTime): Boolean =
    _moments.exists(x => x.moment == moment)

  def iterator: Iterator[ITimeLineMoment] = _moments.iterator

  protected def addPeriod(moment: DateTime, period: ITimePeriod) {
    var item = find(moment)

    if (item == null) {
      item = new TimeLineMoment(moment)
      _moments += item

      // 정렬을 수행한다!!!
      _moments = _moments.sortBy(_.moment)

      log.trace(s"TimeLineMoment를 추가했습니다. item=[$item]")
    }
    item.periods.add(period)
  }

  protected def removePeriod(moment: DateTime, period: ITimePeriod) {
    val item = find(moment)

    if (item != null && item.periods.contains(period)) {
      item.periods.remove(period)

      if (item.periods.size == 0)
        _moments -= item
    }
  }

  override def toString(): String =
    ToStringHelper(this)
    .add("moments", _moments)
    .toString

}
