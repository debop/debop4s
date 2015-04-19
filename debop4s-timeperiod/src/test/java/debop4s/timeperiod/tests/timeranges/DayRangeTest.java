package debop4s.timeperiod.tests.timeranges;

import debop4s.core.JAction1;
import debop4s.core.parallels.JParallels;
import debop4s.timeperiod.TimeCalendar;
import debop4s.timeperiod.TimeSpec;
import debop4s.timeperiod.tests.TimePeriodTestBase;
import debop4s.timeperiod.timerange.DayRange;
import debop4s.timeperiod.timerange.HourRange;
import debop4s.timeperiod.utils.Times;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.List;

import static debop4s.timeperiod.utils.Times.*;
import static org.fest.assertions.Assertions.assertThat;

/**
 * debop4s.timeperiod.test.timeranges.DayRangeFunSuite
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 5. 25. 오후 4:04
 */
@Slf4j
public class DayRangeTest extends TimePeriodTestBase {

    @Test
    public void initValues() {
        DateTime now = Times.now();
        DateTime firstDay = Times.startTimeOfDay(now);
        DateTime secondDay = firstDay.plusDays(1);

        DayRange dr = new DayRange(now, TimeCalendar.getEmptyOffset());

        assertThat(dr.getStart()).isEqualTo(firstDay);
        assertThat(dr.getEnd()).isEqualTo(secondDay);
    }

    @Test
    public void defaultCalendarTest() {
        DateTime yearStart = startTimeOfYear(Times.now());

        for (int m = 1; m <= TimeSpec.MonthsPerYear; m++) {
            DateTime monthStart = asDate(yearStart.getYear(), m, 1);
            DateTime monthEnd = endTimeOfMonth(yearStart.getYear(), m);

            for (int day = monthStart.getDayOfMonth(); day < monthEnd.getDayOfMonth(); day++) {

                DayRange dr = new DayRange(monthStart.plusDays(day - monthStart.getDayOfMonth()));

                assertThat(dr.getYear()).isEqualTo(yearStart.getYear());
                assertThat(dr.getMonthOfYear()).isEqualTo(monthStart.getMonthOfYear());
            }
        }
    }

    @Test
    public void constructorTest() {
        DateTime now = Times.now();

        DayRange dr = new DayRange(now);
        assertThat(dr.getStart()).isEqualTo(now.withTimeAtStartOfDay());

        dr = new DayRange(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth());
        assertThat(dr.getStart()).isEqualTo(now.withTimeAtStartOfDay());
    }

    @Test
    public void dayOfWeekTest() {
        DateTime now = Times.now();
        DayRange dr = new DayRange(now, TimeCalendar.getDefault());
        assertThat(dr.getDayOfWeek()).isEqualTo(TimeCalendar.getDefault().dayOfWeek(now));
    }

    @Test
    public void addDaysTest() {

        final DateTime now = Times.now();
        final DateTime today = Times.today();
        final DayRange dr = new DayRange(now);


        assertThat(dr.previousDay().getStart()).isEqualTo(today.minusDays(1));
        assertThat(dr.nextDay().getStart()).isEqualTo(today.plusDays(1));

        assertThat(dr.addDays(0)).isEqualTo(dr);

        JParallels.run(-60, 120, new JAction1<Integer>() {
            @Override
            public void perform(Integer i) {
                assertThat(dr.addDays(i).getStart()).isEqualTo(today.plusDays(i));
            }
        });
    }

    @Test
    public void getHoursTest() {
        DayRange dr = new DayRange();
        List<HourRange> hours = dr.getHours();

        int index = 0;
        for (HourRange hour : hours) {
            assertThat(hour.getStart()).isEqualTo(dr.getStart().plusHours(index));
            assertThat(hour.getEnd()).isEqualTo(hour.getTimeCalendar().mapEnd(hour.getStart().plusHours(1)));
            index++;
        }
        assertThat(index).isEqualTo(TimeSpec.HoursPerDay);
    }
}
