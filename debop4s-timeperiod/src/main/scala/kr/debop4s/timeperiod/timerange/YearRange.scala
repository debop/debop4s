package kr.debop4s.timeperiod.timerange

import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

/**
 * kr.debop4s.timeperiod.timerange.YearRange
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 30. 오전 10:32
 */
@SerialVersionUID(709289105887324670L)
class YearRange(private[this] val _year: Int,
                private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
    extends YearTimeRange(_year, 1, _calendar) {

    def year = startYear

    def addYears(years: Int): YearRange = {
        new YearRange(startYear + years, calendar)
    }

    def nextYear: YearRange = addYears(1)

    def previousYear: YearRange = addYears(-1)
}

object YearRange {

    def apply(): YearRange = apply(Times.currentYear, DefaultTimeCalendar)

    def apply(year: Int): YearRange = apply(year, DefaultTimeCalendar)

    def apply(year: Int, calendar: ITimeCalendar): YearRange =
        new YearRange(year, calendar)

    def apply(moment: DateTime): YearRange =
        apply(moment, DefaultTimeCalendar)

    def apply(moment: DateTime, calendar: ITimeCalendar): YearRange =
        new YearRange(moment.getYear, calendar)
}
