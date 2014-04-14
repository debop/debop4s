package debop4s.timeperiod.timerange

import debop4s.timeperiod._
import debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

/**
 * debop4s.timeperiod.timerange.YearTimeRange
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 27. 오후 6:48
 */
@SerialVersionUID(1604523513628691621L)
class YearTimeRange(private[this] val _year: Int,
                    val yearCount: Int,
                    private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
    extends YearCalendarTimeRange(Times.relativeYearPeriod(Times.startTimeOfYear(_year), yearCount), _calendar) {

    @inline
    def halfyears = {
        for {
            y <- (0 until yearCount).view
            hy <- Halfyear.values.view
        } yield {
            HalfyearRange(startYear + y, hy, calendar)
        }
    }

    @inline
    def quarters = {
        for {
            y <- (0 until yearCount).view
            q <- Quarter.values.view
        } yield {
            QuarterRange(startYear + y, q, calendar)
        }
    }

    @inline
    def months = {
        for {
            y <- (0 until yearCount).view
            baseTime = start.plusYears(y)
            m <- (0 until MonthsPerYear).view
        } yield {
            MonthRange(baseTime.plusMonths(m), calendar)
        }
    }
}

object YearTimeRange {

    def apply(year: Int, yearCount: Int): YearTimeRange =
        apply(year, yearCount, DefaultTimeCalendar)

    def apply(year: Int, yearCount: Int, calendar: ITimeCalendar): YearTimeRange =
        new YearTimeRange(year, yearCount, calendar)

    def apply(moment: DateTime, yearCount: Int): YearTimeRange =
        apply(moment, yearCount, DefaultTimeCalendar)


    def apply(moment: DateTime, yearCount: Int, calendar: ITimeCalendar): YearTimeRange =
        new YearTimeRange(moment.getYear, yearCount, calendar)
}