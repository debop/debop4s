package kr.debop4s.timeperiod.timerange

import kr.debop4s.timeperiod.Halfyear.Halfyear
import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

/**
 * kr.debop4s.timeperiod.timerange.HalfyearRange
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 29. 오후 6:16
 */
class HalfyearRange(private val _year: Int,
                    private val _halfyear: Halfyear,
                    private val _calendar: ITimeCalendar = DefaultTimeCalendar)
    extends HalfyearTimeRange(_year, _halfyear, 1, _calendar) {

    def this(moment: DateTime, calendar: ITimeCalendar) {
        this(moment.getYear, Times.halfyearOf(moment), calendar)
    }

    def this(moment: DateTime) {
        this(moment.getYear, Times.halfyearOf(moment), DefaultTimeCalendar)
    }

    def addHalfyears(count: Int): HalfyearRange = {
        val yhy = Times.addHalfyear(startYear, startHalfyear, count)
        new HalfyearRange(yhy.year, yhy.halfyear, calendar)
    }

    def nextHalfyear: HalfyearRange = addHalfyears(1)

    def previousHalfyear: HalfyearRange = addHalfyears(-1)

}
