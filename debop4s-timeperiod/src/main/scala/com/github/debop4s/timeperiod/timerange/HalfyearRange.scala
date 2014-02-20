package com.github.debop4s.timeperiod.timerange

import com.github.debop4s.timeperiod.Halfyear.Halfyear
import com.github.debop4s.timeperiod._
import com.github.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

/**
 * com.github.debop4s.timeperiod.timerange.HalfyearRange
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 29. 오후 6:16
 */
object HalfyearRange {

    def apply(): HalfyearRange = apply(Times.today)

    def apply(calendar: ITimeCalendar): HalfyearRange = apply(Times.today, calendar)

    def apply(year: Int, halfyear: Halfyear): HalfyearRange =
        new HalfyearRange(year, halfyear)

    def apply(year: Int, halfyear: Halfyear, calendar: ITimeCalendar): HalfyearRange =
        new HalfyearRange(year, halfyear, calendar)

    def apply(moment: DateTime): HalfyearRange = {
        new HalfyearRange(moment.getYear, Times.halfyearOf(moment))
    }

    def apply(moment: DateTime, calendar: ITimeCalendar): HalfyearRange =
        new HalfyearRange(moment.getYear, Times.halfyearOf(moment), calendar)
}

class HalfyearRange(private val _year: Int,
                    private val _halfyear: Halfyear,
                    private val _calendar: ITimeCalendar = DefaultTimeCalendar)
    extends HalfyearTimeRange(_year, _halfyear, 1, _calendar) {

    def addHalfyears(count: Int): HalfyearRange = {
        val yhy = Times.addHalfyear(startYear, startHalfyear, count)
        new HalfyearRange(yhy.year, yhy.halfyear, calendar)
    }

    def nextHalfyear: HalfyearRange = addHalfyears(1)

    def previousHalfyear: HalfyearRange = addHalfyears(-1)

}