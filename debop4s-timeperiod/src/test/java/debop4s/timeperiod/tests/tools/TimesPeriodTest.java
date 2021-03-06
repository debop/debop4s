package debop4s.timeperiod.tests.tools;

import debop4s.timeperiod.*;
import debop4s.timeperiod.tests.TimePeriodTestBase;
import debop4s.timeperiod.timerange.*;
import debop4s.timeperiod.utils.Durations;
import debop4s.timeperiod.utils.Times;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Test;

import static debop4s.timeperiod.utils.Times.*;
import static org.fest.assertions.Assertions.assertThat;

@Slf4j
public class TimesPeriodTest extends TimePeriodTestBase {

    public static final int PeriodCount = 24;

    private DateTime startTime = new DateTime(2008, 4, 10, 5, 33, 24, 345);
    private DateTime endTime = new DateTime(2018, 10, 20, 13, 43, 12, 599);
    private Duration duration = new Duration(startTime, endTime);

    @Test
    public void getTimeBlockByDuration() {
        TimeBlock block = timeBlock(startTime, duration);
        assertThat(block.getStart()).isEqualTo(startTime);
        assertThat(block.getEnd()).isEqualTo(endTime);
        assertThat(block.getDuration()).isEqualTo(duration);
    }

    @Test
    public void getTimeBlockByStartAndEnd() {
        TimeBlock block = timeBlock(startTime, endTime);
        assertThat(block.getStart()).isEqualTo(startTime);
        assertThat(block.getEnd()).isEqualTo(endTime);
        assertThat(block.getDuration()).isEqualTo(duration);
    }

    @Test
    public void getTimeRangeByDuration() {
        TimeRange range = timeRange(startTime, duration);
        assertThat(range.getStart()).isEqualTo(startTime);
        assertThat(range.getEnd()).isEqualTo(endTime);
        assertThat(range.getDuration()).isEqualTo(duration);

        range = timeRange(startTime, Durations.negate(duration));
        assertThat(range.getStart()).isEqualTo(startTime.minus(duration));
        assertThat(range.getEnd()).isEqualTo(endTime.minus(duration));
        assertThat(range.getDuration()).isEqualTo(duration);
    }

    @Test
    public void getTimeRangeByStartAndEnd() {
        TimeRange range = timeRange(startTime, endTime);
        assertThat(range.getStart()).isEqualTo(startTime);
        assertThat(range.getEnd()).isEqualTo(endTime);
        assertThat(range.getDuration()).isEqualTo(duration);
    }

    @Test
    public void getPeriodOfTest() {
        for (PeriodUnit unit : PeriodUnit.values()) {
            if (unit == PeriodUnit.All || unit == PeriodUnit.Millisecond)
                continue;

            DateTime moment = startTime;
            ITimePeriod period = Times.periodOf(moment, unit);

            assertThat(period.hasInside(moment)).isTrue();
            assertThat(period.hasInside(endTime)).isFalse();


            log.trace("[{}] : period[{}] hasInside=[{}]", unit, period, moment);
        }
    }

    @Test
    public void getPeriodOfWithCalendar() {
        for (PeriodUnit unit : PeriodUnit.values()) {
            if (unit == PeriodUnit.All || unit == PeriodUnit.Millisecond)
                continue;

            DateTime moment = startTime;
            ITimeCalendar calendar = TimeCalendar.getEmptyOffset();
            ITimePeriod period = Times.periodOf(moment, unit, calendar);

            assertThat(period.hasInside(moment)).isTrue();
            assertThat(period.hasInside(endTime)).isFalse();


            log.trace("[{}] : period[{}] hasInside=[{}]", unit, period, moment);
        }
    }

    @Test
    public void getPeriodsOfTest() {
        for (PeriodUnit unit : PeriodUnit.values()) {
            if (unit == PeriodUnit.All || unit == PeriodUnit.Millisecond)
                continue;

            for (int count = 1; count < 5; count++) {
                DateTime moment = startTime;
                ITimeCalendar calendar = TimeCalendar.getEmptyOffset();
                ITimePeriod period = Times.periodsOf(moment, unit, count, calendar);

                assertThat(period.hasPeriod()).isTrue();
                assertThat(period.hasInside(moment)).isTrue();
                assertThat(period.hasInside(endTime)).isFalse();


                log.trace("[{}] : period[{}] hasInside=[{}]", unit, period, moment);
            }
        }
    }

    @Test
    public void getYearRangeTest() {
        YearRange yearRange = yearRange(startTime, TimeCalendar.getEmptyOffset());
        DateTime start = startTimeOfYear(startTime);

        assertThat(yearRange.getStart()).isEqualTo(start);
        assertThat(yearRange.getStartYear()).isEqualTo(start.getYear());
        assertThat(yearRange.getEnd()).isEqualTo(start.plusYears(1));
        assertThat(yearRange.getEndYear()).isEqualTo(start.plusYears(1).getYear());
    }

    @Test
    public void getYearRangesTest() {
        for (int i = 1; i < PeriodCount; i++) {
            YearRangeCollection yearRanges = yearRanges(startTime, i, TimeCalendar.getEmptyOffset());
            DateTime start = startTimeOfYear(startTime);

            assertThat(yearRanges.getStart()).isEqualTo(start);
            assertThat(yearRanges.getStartYear()).isEqualTo(start.getYear());
            assertThat(yearRanges.getEnd()).isEqualTo(start.plusYears(i));
            assertThat(yearRanges.getEndYear()).isEqualTo(start.plusYears(i).getYear());
        }
    }

    @Test
    public void getHalfyearRangeTest() {
        HalfyearRange hy = halfyearRange(startTime, TimeCalendar.getEmptyOffset());

        DateTime start = startTimeOfHalfyear(startTime);
        assertThat(hy.getStart()).isEqualTo(start);
        assertThat(hy.getEnd()).isEqualTo(hy.nextHalfyear().getStart());
    }

    @Test
    public void getHalfyearRangesTest() {

        for (int i = 1; i < PeriodCount; i++) {
            HalfyearRangeCollection hys = halfyearRanges(startTime, i, TimeCalendar.getEmptyOffset());

            DateTime start = startTimeOfHalfyear(startTime);
            assertThat(hys.getStart()).isEqualTo(start);
            assertThat(hys.getEnd()).isEqualTo(start.plusMonths(i * TimeSpec.MonthsPerHalfyear));
            assertThat(hys.getHalfyearCount()).isEqualTo(i);
        }
    }

    @Test
    public void getQuarterRangeTest() {
        QuarterRange qr = quarterRange(startTime, TimeCalendar.getEmptyOffset());
        DateTime start = startTimeOfQuarter(startTime);

        assertThat(qr.getStart()).isEqualTo(start);
        assertThat(qr.getEnd()).isEqualTo(qr.nextQuarter().getStart());
    }

    @Test
    public void getQuarterRangesTest() {
        for (int i = 1; i < PeriodCount; i++) {
            QuarterRangeCollection quarters = quarterRanges(startTime, i, TimeCalendar.getEmptyOffset());
            DateTime start = startTimeOfQuarter(startTime);

            assertThat(quarters.getStart()).isEqualTo(start);
            assertThat(quarters.getEnd()).isEqualTo(start.plusMonths(i * TimeSpec.MonthsPerQuarter));
            assertThat(quarters.getQuarterCount()).isEqualTo(i);
        }
    }

    @Test
    public void getMonthRangeTest() {
        MonthRange mr = monthRange(startTime, TimeCalendar.getEmptyOffset());
        DateTime start = startTimeOfMonth(startTime);

        assertThat(mr.getStart()).isEqualTo(start);
        assertThat(mr.getEnd()).isEqualTo(mr.nextMonth().getStart());
    }

    @Test
    public void getMonthRangesTest() {
        for (int i = 1; i < PeriodCount; i++) {
            MonthRangeCollection mrs = monthRanges(startTime, i, TimeCalendar.getEmptyOffset());
            DateTime start = startTimeOfMonth(startTime);

            assertThat(mrs.getStart()).isEqualTo(start);
            assertThat(mrs.getEnd()).isEqualTo(start.plusMonths(i));
            assertThat(mrs.getMonthCount()).isEqualTo(i);
        }
    }

    @Test
    public void getWeekRangeTest() {
        WeekRange wr = Times.weekRange(startTime, TimeCalendar.getEmptyOffset());
        DateTime start = startTimeOfWeek(startTime);

        assertThat(wr.getStart()).isEqualTo(start);
        assertThat(wr.getEnd()).isEqualTo(wr.nextWeek().getStart());
    }

    @Test
    public void getWeekRangesTest() {
        for (int i = 1; i < PeriodCount; i++) {
            WeekRangeCollection wks = weekRanges(startTime, i, TimeCalendar.getEmptyOffset());
            DateTime start = startTimeOfWeek(startTime);

            assertThat(wks.getStart()).isEqualTo(start);
            assertThat(wks.getEnd()).isEqualTo(start.plusWeeks(i));
            assertThat(wks.getWeekCount()).isEqualTo(i);
        }
    }

    @Test
    public void getDayRangeTest() {
        DayRange dr = dayRange(startTime, TimeCalendar.getEmptyOffset());
        DateTime start = startTimeOfDay(startTime);

        assertThat(dr.getStart()).isEqualTo(start);
        assertThat(dr.getEnd()).isEqualTo(dr.nextDay().getStart());
    }

    @Test
    public void getDayRangesTest() {
        for (int i = 1; i < PeriodCount; i++) {
            DayRangeCollection drs = dayRanges(startTime, i, TimeCalendar.getEmptyOffset());
            DateTime start = startTimeOfDay(startTime);

            assertThat(drs.getStart()).isEqualTo(start);
            assertThat(drs.getEnd()).isEqualTo(start.plusDays(i));
            assertThat(drs.getDayCount()).isEqualTo(i);
        }
    }

    @Test
    public void getHourRangeTest() {
        HourRange hr = hourRange(startTime, TimeCalendar.getEmptyOffset());
        DateTime start = startTimeOfHour(startTime);

        assertThat(hr.getStart()).isEqualTo(start);
        assertThat(hr.getEnd()).isEqualTo(hr.nextHour().getStart());
    }

    @Test
    public void getHourRangesTest() {
        for (int i = 1; i < PeriodCount; i++) {
            HourRangeCollection drs = hourRanges(startTime, i, TimeCalendar.getEmptyOffset());
            DateTime start = startTimeOfHour(startTime);

            assertThat(drs.getStart()).isEqualTo(start);
            assertThat(drs.getEnd()).isEqualTo(start.plusHours(i));
            assertThat(drs.getHourCount()).isEqualTo(i);
        }
    }

    @Test
    public void getMinuteRangeTest() {
        MinuteRange hr = minuteRange(startTime, TimeCalendar.getEmptyOffset());
        DateTime start = startTimeOfMinute(startTime);

        assertThat(hr.getStart()).isEqualTo(start);
        assertThat(hr.getEnd()).isEqualTo(hr.nextMinute().getStart());
    }

    @Test
    public void getMinuteRangesTest() {
        for (int i = 1; i < PeriodCount; i++) {
            MinuteRangeCollection drs = minuteRanges(startTime, i, TimeCalendar.getEmptyOffset());
            DateTime start = startTimeOfMinute(startTime);

            assertThat(drs.getStart()).isEqualTo(start);
            assertThat(drs.getEnd()).isEqualTo(start.plusMinutes(i));
            assertThat(drs.getMinuteCount()).isEqualTo(i);
        }
    }

}
