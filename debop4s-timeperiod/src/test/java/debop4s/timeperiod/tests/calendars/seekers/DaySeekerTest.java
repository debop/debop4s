package debop4s.timeperiod.tests.calendars.seekers;

import debop4s.core.JAction1;
import debop4s.core.parallels.JParallels;
import debop4s.timeperiod.SeekDirection;
import debop4s.timeperiod.TimeSpec;
import debop4s.timeperiod.calendars.CalendarVisitorFilter;
import debop4s.timeperiod.calendars.seeker.DaySeeker;
import debop4s.timeperiod.tests.TimePeriodTestBase;
import debop4s.timeperiod.timerange.DayRange;
import debop4s.timeperiod.timerange.DayRangeCollection;
import debop4s.timeperiod.utils.Times;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * debop4s.timeperiod.test.calendars.seekers.DaySeekerFunSuite
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 5. 27. 오후 6:16
 */
@Slf4j
public class DaySeekerTest extends TimePeriodTestBase {

    @Test
    public void simpleForward() {

        final DayRange start = new DayRange();
        final DaySeeker daySeeker = new DaySeeker();

        DayRange day1 = daySeeker.findDay(start, 0);
        assertThat(day1.isSamePeriod(start)).isTrue();

        DayRange day2 = daySeeker.findDay(start, 1);
        assertThat(day2.isSamePeriod(start.nextDay())).isTrue();

        JParallels.run(-10, 20, new JAction1<Integer>() {
            @Override
            public void perform(Integer i) {
                Integer offset = i * 5;
                DayRange day = daySeeker.findDay(start, offset);
                assertThat(day.isSamePeriod(start.addDays(offset))).isTrue();
            }
        });
    }

    @Test
    public void simpleBackward() {

        final DayRange start = new DayRange();
        final DaySeeker daySeeker = new DaySeeker(SeekDirection.Backward);

        DayRange day1 = daySeeker.findDay(start, 0);
        assertThat(day1.isSamePeriod(start)).isTrue();

        DayRange day2 = daySeeker.findDay(start, 1);
        assertThat(day2.isSamePeriod(start.previousDay())).isTrue();

        JParallels.run(-10, 20, new JAction1<Integer>() {
            @Override
            public void perform(Integer i) {
                Integer offset = i * 5;
                DayRange day = daySeeker.findDay(start, offset);
                assertThat(day.isSamePeriod(start.addDays(-offset))).isTrue();
            }
        });
    }

    @Test
    public void seekDirectionTest() {
        final DayRange start = new DayRange();
        final DaySeeker daySeeker = new DaySeeker();

        JParallels.run(-10, 20, new JAction1<Integer>() {
            @Override
            public void perform(Integer i) {
                Integer offset = i * 5;
                DayRange day = daySeeker.findDay(start, offset);
                assertThat(day.isSamePeriod(start.addDays(offset))).isTrue();
            }
        });

        final DaySeeker backwardSeeker = new DaySeeker(SeekDirection.Backward);

        JParallels.run(-10, 20, new JAction1<Integer>() {
            @Override
            public void perform(Integer i) {
                Integer offset = i * 5;
                DayRange day = backwardSeeker.findDay(start, offset);
                assertThat(day.isSamePeriod(start.addDays(-offset))).isTrue();
            }
        });
    }

    @Test
    public void minDateTest() {
        DaySeeker daySeeker = new DaySeeker();
        DayRange day = daySeeker.findDay(new DayRange(TimeSpec.MinPeriodTime), -10);
        assertThat(day).isNull();
    }

    @Test
    public void maxDateTest() {
        DaySeeker daySeeker = new DaySeeker();
        DayRange day = daySeeker.findDay(new DayRange(TimeSpec.MaxPeriodTime), 10);
        assertThat(day).isNull();
    }

    @Test
    public void seekWeekendHolidayTest() {

        DayRange start = new DayRange(Times.asDate(2011, 2, 15));

        CalendarVisitorFilter filter = new CalendarVisitorFilter();
        filter.addWorkingWeekdays();
        filter.getExcludePeriods().add(new DayRangeCollection(2011, 2, 27, 14)); // 14 days -> week 9 and week 10

        DaySeeker daySeeker = new DaySeeker(filter);

        DayRange day1 = daySeeker.findDay(start, 3);
        assertThat(day1).isEqualTo(new DayRange(2011, 2, 18));

        DayRange day2 = daySeeker.findDay(start, 4);                // 주말 (19, 20) 제외
        assertThat(day2).isEqualTo(new DayRange(2011, 2, 21));

        DayRange day3 = daySeeker.findDay(start, 10);                // 주말 (19, 20) 제외, 2.27부터 14일간 휴가
        assertThat(day3).isEqualTo(new DayRange(2011, 3, 15));
    }

}
