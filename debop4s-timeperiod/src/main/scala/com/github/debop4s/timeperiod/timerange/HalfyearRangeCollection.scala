package com.github.debop4s.timeperiod.timerange

import com.github.debop4s.timeperiod.Halfyear.Halfyear
import com.github.debop4s.timeperiod._
import com.github.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime
import scala.collection.immutable.IndexedSeq
import scala.collection.SeqView

/**
 * com.github.debop4s.timeperiod.timerange.HalfyearRangeCollection
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 29. 오후 6:20
 */
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


class HalfyearRangeCollection(private val _year: Int,
                              private val _halfyear: Halfyear,
                              private val _halfyearCount: Int,
                              private val _calendar: ITimeCalendar = DefaultTimeCalendar)
    extends HalfyearTimeRange(_year, _halfyear, _halfyearCount, _calendar) {

    @inline
    def halfyears: IndexedSeq[HalfyearRange] = {
        for {
            x <- 0 until halfyearCount
            v = Times.addHalfyear(startYear, startHalfyear, x)
        } yield new HalfyearRange(v.year, v.halfyear, calendar)
    }

    def halfyearsView: SeqView[HalfyearRange, Seq[_]] = {
        (0 until halfyearCount).view.map { x =>
            val v = Times.addHalfyear(startYear, startHalfyear, x)
            HalfyearRange(v.year, v.halfyear, calendar)
        }
    }

}
