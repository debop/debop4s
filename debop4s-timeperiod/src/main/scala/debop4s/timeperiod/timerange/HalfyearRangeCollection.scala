package debop4s.timeperiod.timerange

import java.util

import debop4s.timeperiod.TimeSpec._
import debop4s.timeperiod._
import debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

import scala.collection.SeqView


/**
 * HalfyearRangeCollection
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 29. 오후 6:20
 */
class HalfyearRangeCollection(private[this] val _year: Int,
                              private[this] val _halfyear: Halfyear,
                              private[this] val _halfyearCount: Int,
                              private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
  extends HalfyearTimeRange(_year, _halfyear, _halfyearCount, _calendar) {

  def this(year: Int, halfyear: Halfyear, halfyearCount: Int) =
    this(year, halfyear, halfyearCount, DefaultTimeCalendar)
  def this(moment: DateTime, halfyearCount: Int) =
    this(moment.getYear, Times.halfyearOf(moment), halfyearCount, DefaultTimeCalendar)
  def this(moment: DateTime, halfyearCount: Int, calendar: ITimeCalendar) =
    this(moment.getYear, Times.halfyearOf(moment), halfyearCount, calendar)

  def halfyearsView: SeqView[HalfyearRange, Seq[_]] = {
    (0 until halfyearCount).view.map { x =>
      val v = Times.addHalfyear(startYear, startHalfyear, x)
      HalfyearRange(v.year, v.halfyear, calendar)
    }
  }

  def getHalfyears: util.List[HalfyearRange] = {
    val results = new util.ArrayList[HalfyearRange](halfyearCount)
    var x = 0
    while (x < halfyearCount) {
      val v = Times.addHalfyear(startYear, startHalfyear, x)
      results add HalfyearRange(v.year, v.halfyear, calendar)
      x += 1
    }
    results
  }
}

object HalfyearRangeCollection {

  def apply(year: Int, halfyear: Halfyear, halfyearCount: Int): HalfyearRangeCollection =
    apply(year, halfyear, halfyearCount, DefaultTimeCalendar)

  def apply(year: Int, halfyear: Halfyear, halfyearCount: Int, calendar: ITimeCalendar): HalfyearRangeCollection =
    new HalfyearRangeCollection(year, halfyear, halfyearCount, calendar)

  def apply(moment: DateTime, halfyearCount: Int): HalfyearRangeCollection =
    apply(moment, halfyearCount, DefaultTimeCalendar)

  def apply(moment: DateTime, halfyearCount: Int, calendar: ITimeCalendar): HalfyearRangeCollection =
    new HalfyearRangeCollection(moment.getYear, Times.halfyearOf(moment), halfyearCount, calendar)
}