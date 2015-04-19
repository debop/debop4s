package debop4s.timeperiod.tests.timeranges;

import debop4s.core.JAction1;
import debop4s.core.parallels.JParallels;
import debop4s.timeperiod.TimeCalendar;
import debop4s.timeperiod.TimeSpec;
import debop4s.timeperiod.tests.TimePeriodTestBase;
import debop4s.timeperiod.timerange.DayRange;
import debop4s.timeperiod.timerange.WeekRange;
import debop4s.timeperiod.utils.Times;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * debop4s.timeperiod.test.timeranges.WeekRangeFunSuite
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 5. 26. 오전 12:00
 */
@Slf4j
public class WeekRangeTest extends TimePeriodTestBase {

    @Test
    public void initValues() {
        DateTime now = Times.now();
        DateTime firstWeek = Times.startTimeOfWeek(now);
        DateTime secondWeek = firstWeek.plusWeeks(1);

        WeekRange weekRange = new WeekRange(now, TimeCalendar.getEmptyOffset());

        assertThat(weekRange.getStart()).isEqualTo(firstWeek);
        assertThat(weekRange.getEnd()).isEqualTo(secondWeek);
    }

    @Test
    public void defaultCalendarTest() {
        DateTime yearStart = Times.startTimeOfYear(Times.now());

        for (int w = 1; w < 50; w++) {

            WeekRange weekRange = new WeekRange(yearStart.plusWeeks(w));
            assertThat(weekRange.year()).isEqualTo(yearStart.getYear());
            assertThat(weekRange.weekOfWeekyear()).isEqualTo(w + 1);

            assertThat(weekRange.getUnmappedStart()).isEqualTo(Times.startTimeOfWeek(yearStart.plusWeeks(w)));
            assertThat(weekRange.getUnmappedEnd()).isEqualTo(Times.startTimeOfWeek(yearStart.plusWeeks(w)).plusWeeks(1));
        }
    }


    @Test
    public void getDaysTest() {
        final DateTime now = Times.now();
        final WeekRange weekRange = new WeekRange();
        List<DayRange> days = weekRange.getDays();

        int index = 0;
        for (DayRange day : days) {
            assertThat(day.getStart()).isEqualTo(weekRange.getStart().plusDays(index));
            assertThat(day.getEnd()).isEqualTo(day.getTimeCalendar().mapEnd(day.getStart().plusDays(1)));
            index++;
        }
        assertThat(index).isEqualTo(TimeSpec.DaysPerWeek);
    }

    @Test
    public void addMonthsTest() {

        final DateTime now = Times.now();
        final DateTime startWeek = Times.startTimeOfWeek(now);
        final WeekRange weekRange = new WeekRange(now);

        log.debug("startWeek=[{}], weekRange=[{}]", startWeek, weekRange);

        assertThat(weekRange.previousWeek().getStart()).isEqualTo(startWeek.minusWeeks(1));
        assertThat(weekRange.nextWeek().getStart()).isEqualTo(startWeek.plusWeeks(1));

        assertThat(weekRange.addWeeks(0)).isEqualTo(weekRange);

        JParallels.run(-60, 120, new JAction1<Integer>() {
            @Override
            public void perform(Integer m) {
                assertThat(weekRange.addWeeks(m).getStart()).isEqualTo(startWeek.plusWeeks(m));
            }
        });
    }
}
