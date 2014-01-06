package kr.debop4s.timeperiod.timerange

import kr.debop4s.timeperiod.Halfyear.Halfyear
import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime
import scala.collection.mutable.ArrayBuffer

/**
 * kr.debop4s.timeperiod.timerange.HalfyearRangeCollection
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 29. 오후 6:20
 */
class HalfyearRangeCollection(private val _year: Int,
                              private val _halfyear: Halfyear,
                              private val _halfyearCount: Int,
                              private val _calendar: ITimeCalendar = DefaultTimeCalendar)
    extends HalfyearTimeRange(_year, _halfyear, _halfyearCount, _calendar) {

    def this(moment: DateTime, halfyearCount: Int, calendar: ITimeCalendar) {
        this(moment.getYear, Times.halfyearOf(moment), halfyearCount, calendar)
    }

    def this(moment: DateTime, halfyearCount: Int) {
        this(moment.getYear, Times.halfyearOf(moment), halfyearCount, DefaultTimeCalendar)
    }


    def getHalfyears: Seq[HalfyearRange] = {
        val halfyears = ArrayBuffer[HalfyearRange]()

        for (x <- 0 until halfyearCount) {
            val yhy = Times.addHalfyear(startYear, startHalfyear, x)
            halfyears += new HalfyearRange(yhy.year, yhy.halfyear, calendar)
        }

        halfyears
    }

}
