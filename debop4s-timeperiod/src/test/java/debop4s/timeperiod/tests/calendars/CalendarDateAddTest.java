package debop4s.timeperiod.tests.calendars;

import debop4s.core.JAction1;
import debop4s.core.parallels.JParallels;
import debop4s.timeperiod.*;
import debop4s.timeperiod.calendars.CalendarDateAdd;
import debop4s.timeperiod.tests.TimePeriodTestBase;
import debop4s.timeperiod.timerange.DayRange;
import debop4s.timeperiod.utils.Durations;
import debop4s.timeperiod.utils.Times;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

@Slf4j
public class CalendarDateAddTest extends TimePeriodTestBase {

    @Test
    public void noPeriodTest() {
        final CalendarDateAdd calendarDateAdd = new CalendarDateAdd();
        final DateTime now = Times.now();

        JParallels.run(-10, 20, new JAction1<Integer>() {
            @Override
            public void perform(Integer index) {

                int offset = index * 5;

                assertThat(calendarDateAdd.add(now, Durations.days(offset))).isEqualTo(now.plusDays(offset));
                assertThat(calendarDateAdd.add(now, Durations.days(-offset))).isEqualTo(now.plusDays(-offset));

                assertThat(calendarDateAdd.subtract(now, Durations.days(offset))).isEqualTo(now.plusDays(-offset));
                assertThat(calendarDateAdd.subtract(now, Durations.days(-offset))).isEqualTo(now.plusDays(offset));
            }
        });
    }

    @Test
    public void periodLimitsAdd() {
        DateTime test = Times.asDate(2011, 4, 12);
        ITimePeriod period1 = new TimeRange(Times.asDate(2011, 4, 20), Times.asDate(2011, 4, 25));
        ITimePeriod period2 = new TimeRange(Times.asDate(2011, 4, 30), (DateTime) null); // 4월 30일 이후

        CalendarDateAdd dateAdd = new CalendarDateAdd();

        // 예외기간을 설정합니다. 4월 20일 ~ 4월25일, 4월 30일 이후
        dateAdd.getExcludePeriods().add(period1);
        dateAdd.getExcludePeriods().add(period2);

        assertThat(dateAdd.add(test, Durations.Day())).isEqualTo(test.plus(Durations.Day()));

        // 4월 12일에 8일을 더하면 4월 20일이지만, 20~25일까지 제외되므로, 4월 25일이 된다.
        assertThat(dateAdd.add(test, Durations.days(8))).isEqualTo(period1.getEnd());

        // 4월 12에 20일을 더하면 4월 20~25일을 제외한 후 계산하면 4월 30 이후가 된다. (5월 3일).
        // 하지만 4월 30 이후는 모두 제외되므로 결과값은 null이다.
        assertThat(dateAdd.add(test, Durations.days(20))).isNull();

        assertThat(dateAdd.subtract(test, Durations.days(3))).isEqualTo(test.minus(Durations.days(3)));
    }

    @Test
    public void periodLimitsSubtract() {
        DateTime test = Times.asDate(2011, 4, 30);
        ITimePeriod period1 = new TimeRange(Times.asDate(2011, 4, 20), Times.asDate(2011, 4, 25));
        ITimePeriod period2 = new TimeRange(null, Times.asDate(2011, 4, 6)); // 4월 6일까지

        CalendarDateAdd dateAdd = new CalendarDateAdd();

        // 예외기간을 설정합니다. 4월 6일 이전, 4월 20일 ~ 4월 25일
        dateAdd.getExcludePeriods().add(period1);
        dateAdd.getExcludePeriods().add(period2);

        assertThat(dateAdd.subtract(test, Durations.Day())).isEqualTo(test.minus(Durations.Day()));

        // 4월 30일로부터 5일 전이면 4월 25일이지만, 예외기간이므로 4월20일이 된다.
        assertThat(dateAdd.subtract(test, Durations.days(5))).isEqualTo(period1.getStart());

        // 4월 30일로부터 20일 전이면, 5월 전이 4월20일이므로, 4월 5일이 된다. 근데, 4월 6일 이전은 모두 제외기간이므로 null을 반환한다.
        assertThat(dateAdd.subtract(test, Durations.days(20))).isNull();
    }

    @Test
    public void excludeTest() {
        DateTime test = Times.asDate(2011, 4, 12);
        ITimePeriod period = new TimeRange(Times.asDate(2011, 4, 15), Times.asDate(2011, 4, 20));

        CalendarDateAdd dateAdd = new CalendarDateAdd();
        dateAdd.getExcludePeriods().add(period);

        assertThat(dateAdd.add(test, Durations.Zero())).isEqualTo(test);
        assertThat(dateAdd.add(test, Durations.days(1))).isEqualTo(test.plusDays(1));
        assertThat(dateAdd.add(test, Durations.days(2))).isEqualTo(test.plusDays(2));
        assertThat(dateAdd.add(test, Durations.days(3))).isEqualTo(period.getEnd());
        assertThat(dateAdd.add(test, Durations.days(3, 0, 0, 0, 1))).isEqualTo(period.getEnd().plusMillis(1));
        assertThat(dateAdd.add(test, Durations.days(5))).isEqualTo(period.getEnd().plusDays(2));
    }

    @Test
    public void excludeSplit() {
        DateTime test = Times.asDate(2011, 4, 12);
        ITimePeriod period1 = new TimeRange(Times.asDate(2011, 4, 15), Times.asDate(2011, 4, 20));
        ITimePeriod period2 = new TimeRange(Times.asDate(2011, 4, 22), Times.asDate(2011, 4, 25));

        CalendarDateAdd dateAdd = new CalendarDateAdd();
        dateAdd.getExcludePeriods().add(period1);
        dateAdd.getExcludePeriods().add(period2);

        assertThat(dateAdd.add(test, Durations.Zero())).isEqualTo(test);
        assertThat(dateAdd.add(test, Durations.days(1))).isEqualTo(test.plusDays(1));
        assertThat(dateAdd.add(test, Durations.days(2))).isEqualTo(test.plusDays(2));
        assertThat(dateAdd.add(test, Durations.days(3))).isEqualTo(period1.getEnd());
        assertThat(dateAdd.add(test, Durations.days(4))).isEqualTo(period1.getEnd().plusDays(1));
        assertThat(dateAdd.add(test, Durations.days(5))).isEqualTo(period2.getEnd());
        assertThat(dateAdd.add(test, Durations.days(6))).isEqualTo(period2.getEnd().plusDays(1));
        assertThat(dateAdd.add(test, Durations.days(7))).isEqualTo(period2.getEnd().plusDays(2));
    }

    @Test
    public void calendarDateAddSeekBoundaryMode() {
        CalendarDateAdd dateAdd = new CalendarDateAdd();

        dateAdd.addWorkingWeekDays();
        dateAdd.getExcludePeriods().add(new DayRange(2011, 4, 4, dateAdd.calendar()));
        dateAdd.workingHours().$plus$eq(new HourRangeInDay(8, 18));

        DateTime start = new DateTime(2011, 4, 1, 9, 0);

        assertThat(dateAdd.add(start, Durations.hours(29), SeekBoundaryMode.Fill)).isEqualTo(new DateTime(2011, 4, 6, 18, 0, 0));
        assertThat(dateAdd.add(start, Durations.hours(29), SeekBoundaryMode.Next)).isEqualTo(new DateTime(2011, 4, 7, 8, 0, 0));
        assertThat(dateAdd.add(start, Durations.hours(29))).isEqualTo(new DateTime(2011, 4, 7, 8, 0, 0));
    }

    @Test
    public void calendarDateAdd1() {
        CalendarDateAdd dateAdd = new CalendarDateAdd();

        dateAdd.addWorkingWeekDays();
        dateAdd.getExcludePeriods().add(new DayRange(2011, 4, 4, dateAdd.calendar()));
        dateAdd.workingHours().$plus$eq(new HourRangeInDay(8, 18));

        DateTime start = new DateTime(2011, 4, 1, 9, 0);

        assertThat(dateAdd.add(start, Durations.hours(22))).isEqualTo(new DateTime(2011, 4, 6, 11, 0, 0));
        assertThat(dateAdd.add(start, Durations.hours(22), SeekBoundaryMode.Fill)).isEqualTo(new DateTime(2011, 4, 6, 11, 0, 0));

        assertThat(dateAdd.add(start, Durations.hours(29))).isEqualTo(new DateTime(2011, 4, 7, 8, 0, 0));
        assertThat(dateAdd.add(start, Durations.hours(29), SeekBoundaryMode.Fill)).isEqualTo(new DateTime(2011, 4, 6, 18, 0, 0));
    }

    @Test
    public void calendarDateAdd2() {
        CalendarDateAdd dateAdd = new CalendarDateAdd();

        dateAdd.addWorkingWeekDays();
        dateAdd.getExcludePeriods().add(new DayRange(2011, 4, 4, dateAdd.calendar()));
        dateAdd.workingHours().$plus$eq(new HourRangeInDay(8, 12));
        dateAdd.workingHours().$plus$eq(new HourRangeInDay(13, 18));

        DateTime start = new DateTime(2011, 4, 1, 9, 0);

        assertThat(dateAdd.add(start, Durations.hours(3))).isEqualTo(new DateTime(2011, 4, 1, 13, 0, 0));
        assertThat(dateAdd.add(start, Durations.hours(4))).isEqualTo(new DateTime(2011, 4, 1, 14, 0, 0));
        assertThat(dateAdd.add(start, Durations.hours(8))).isEqualTo(new DateTime(2011, 4, 5, 8, 0, 0));
    }

    @Test
    public void calendarDateAdd3() {
        CalendarDateAdd dateAdd = new CalendarDateAdd();

        dateAdd.addWorkingWeekDays();
        dateAdd.getExcludePeriods().add(new DayRange(2011, 4, 4, dateAdd.calendar()));
        dateAdd.workingHours().$plus$eq(new HourRangeInDay(new Timepart(8, 30), new Timepart(12)));
        dateAdd.workingHours().$plus$eq(new HourRangeInDay(new Timepart(13, 30), new Timepart(18)));

        DateTime start = new DateTime(2011, 4, 1, 9, 0);

        assertThat(dateAdd.add(start, Durations.hours(3))).isEqualTo(new DateTime(2011, 4, 1, 13, 30, 0));
        assertThat(dateAdd.add(start, Durations.hours(4))).isEqualTo(new DateTime(2011, 4, 1, 14, 30, 0));
        assertThat(dateAdd.add(start, Durations.hours(8))).isEqualTo(new DateTime(2011, 4, 5, 9, 0, 0));
    }

    @Test
    public void emptyStartWeek() {
        CalendarDateAdd dateAdd = new CalendarDateAdd();

        // 주중(월~금)을 working time 으로 추가
        dateAdd.addWorkingWeekDays();

        DateTime start = new DateTime(2011, 4, 2, 13, 0, 0);
        Duration offset = Durations.hours(20);

        // 4월 2일(토), 4월 3일(일) 제외하면 4월 4일 0시부터 20시간
        assertThat(dateAdd.add(start, Durations.hours(20))).isEqualTo(new DateTime(2011, 4, 4, 20, 0, 0));

        // 4월 2일(토), 4월 3일(일) 제외하면 4월 4일 0시부터 24시간
        assertThat(dateAdd.add(start, Durations.hours(24))).isEqualTo(new DateTime(2011, 4, 5, 0, 0, 0));

        // 4월 2일(토), 4월 3일(일) 제외하면, 4월 4일부터 5일이면 주말인 4월 9일(토), 4월 10일(일) 제외한 4월 11일!!!
        assertThat(dateAdd.add(start, Durations.days(5))).isEqualTo(new DateTime(2011, 4, 11, 0, 0, 0));
    }

}
