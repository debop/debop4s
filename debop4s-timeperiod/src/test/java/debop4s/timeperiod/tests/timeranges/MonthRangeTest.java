package debop4s.timeperiod.tests.timeranges;

import debop4s.core.JAction1;
import debop4s.core.parallels.JParallels;
import debop4s.timeperiod.TimeCalendar;
import debop4s.timeperiod.TimeSpec;
import debop4s.timeperiod.tests.TimePeriodTestBase;
import debop4s.timeperiod.timerange.DayRange;
import debop4s.timeperiod.timerange.MonthRange;
import debop4s.timeperiod.utils.Times;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * debop4s.timeperiod.test.timeranges.MonthRangeFunSuite
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 5. 25. 오후 11:04
 */
@Slf4j
public class MonthRangeTest extends TimePeriodTestBase {

    @Test
    public void initValues() {
        DateTime now = Times.now();
        DateTime firstMonth = Times.startTimeOfMonth(now);
        DateTime secondMonth = firstMonth.plusMonths(1);

        MonthRange monthRange = new MonthRange(now, TimeCalendar.getEmptyOffset());

        assertThat(monthRange.getStart()).isEqualTo(firstMonth);
        assertThat(monthRange.getEnd()).isEqualTo(secondMonth);
    }

    @Test
    public void defaultCalendarTest() {
        DateTime yearStart = Times.startTimeOfYear(Times.now());

        for (int m = 0; m < TimeSpec.MonthsPerYear; m++) {

            MonthRange monthRange = new MonthRange(yearStart.plusMonths(m));
            assertThat(monthRange.getYear()).isEqualTo(yearStart.getYear());
            assertThat(monthRange.getMonthOfYear()).isEqualTo(m + 1);

            assertThat(monthRange.getUnmappedStart()).isEqualTo(yearStart.plusMonths(m));
            assertThat(monthRange.getUnmappedEnd()).isEqualTo(yearStart.plusMonths(m + 1));
        }
    }


    @Test
    public void getDaysTest() {
        final DateTime now = Times.now();
        final MonthRange monthRange = new MonthRange();
        List<DayRange> days = monthRange.getDays();

        int index = 0;
        for (DayRange day : days) {
            assertThat(day.getStart()).isEqualTo(monthRange.getStart().plusDays(index));
            assertThat(day.getEnd()).isEqualTo(day.getTimeCalendar().mapEnd(day.getStart().plusDays(1)));
            index++;
        }
        assertThat(index).isEqualTo(Times.daysInMonth(now.getYear(), now.getMonthOfYear()));
    }

    @Test
    public void addMonthsTest() {

        final DateTime now = Times.now();
        final DateTime startMonth = Times.startTimeOfMonth(now);
        final MonthRange monthRange = new MonthRange(now);


        assertThat(monthRange.previousMonth().getStart()).isEqualTo(startMonth.minusMonths(1));
        assertThat(monthRange.nextMonth().getStart()).isEqualTo(startMonth.plusMonths(1));

        assertThat(monthRange.addMonths(0)).isEqualTo(monthRange);

        JParallels.run(-60, 120, new JAction1<Integer>() {
            @Override
            public void perform(Integer m) {
                assertThat(monthRange.addMonths(m).getStart()).isEqualTo(startMonth.plusMonths(m));
            }
        });
    }
}
