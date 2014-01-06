package kr.debop4s.timeperiod.timerange

import kr.debop4s.timeperiod._
import org.joda.time.DateTime
import scala.collection.mutable.ArrayBuffer

/**
 * kr.debop4s.timeperiod.timerange.YearRangeCollection
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 30. 오전 10:36
 */
@SerialVersionUID(6717411713272815855L)
class YearRangeCollection(private val _year: Int,
                          private val _yearCount: Int,
                          private val _calendar: ITimeCalendar = DefaultTimeCalendar)
    extends YearTimeRange(_year, _yearCount, _calendar) {

    def this(moment: DateTime, yearCount: Int, calendar: ITimeCalendar) {
        this(moment.getYear, yearCount, calendar)
    }

    def this(moment: DateTime, yearCount: Int) {
        this(moment, yearCount, DefaultTimeCalendar)
    }

    def getYears: Seq[YearRange] = {
        val years = ArrayBuffer[YearRange]()
        for (y <- 0 until yearCount) {
            years += new YearRange(startYear + y, calendar)
        }
        years
    }

}
