package debop4s.timeperiod.timerange

import debop4s.timeperiod._

/**
 * debop4s.timeperiod.timerange.YearCalendarTimeRange
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 27. 오후 5:05
 */
@SerialVersionUID(-7922671338410846872L)
class YearCalendarTimeRange(private val _period: ITimePeriod,
                            private val _calendar: ITimeCalendar = DefaultTimeCalendar)
    extends CalendarTimeRange(_period, _calendar) {

    def yearBaseMonth: Int = 1

    def baseYear: Int = startYear
}
