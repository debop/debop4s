package debop4s.timeperiod.tests.timeranges;

import debop4s.timeperiod.TimeSpec;
import debop4s.timeperiod.tests.TimePeriodTestBase;
import debop4s.timeperiod.timerange.DayRange;
import debop4s.timeperiod.timerange.DayRangeCollection;
import debop4s.timeperiod.timerange.HourRange;
import debop4s.timeperiod.utils.Times;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * debop4s.timeperiod.test.timeranges.DayRangeCollectionFunSuite
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 5. 25. 오후 3:15
 */
@Slf4j
public class DayRangeCollectionTest extends TimePeriodTestBase {

    @Test
    public void singleDays() {
        final DateTime start = Times.asDate(2004, 2, 22);
        DayRangeCollection days = new DayRangeCollection(start, 1);

        assertThat(days.getDayCount()).isEqualTo(1);

        assertThat(days.getStartYear()).isEqualTo(start.getYear());
        assertThat(days.getStartMonthOfYear()).isEqualTo(start.getMonthOfYear());
        assertThat(days.getStartDayOfMonth()).isEqualTo(start.getDayOfMonth());

        assertThat(days.getEndYear()).isEqualTo(start.getYear());
        assertThat(days.getEndMonthOfYear()).isEqualTo(start.getMonthOfYear());
        assertThat(days.getEndDayOfMonth()).isEqualTo(start.getDayOfMonth());

        List<DayRange> dayList = days.getDays();
        assertThat(dayList.size()).isEqualTo(1);
        assertThat(dayList.get(0).isSamePeriod(new DayRange(start))).isTrue();
    }

    @Test
    public void calendarDays() {
        final int dayCount = 5;

        final DateTime start = Times.asDate(2004, 2, 22);
        final DateTime end = start.plusDays(dayCount - 1);
        DayRangeCollection days = new DayRangeCollection(start, dayCount);

        assertThat(days.getDayCount()).isEqualTo(dayCount);

        assertThat(days.getStartYear()).isEqualTo(start.getYear());
        assertThat(days.getStartMonthOfYear()).isEqualTo(start.getMonthOfYear());
        assertThat(days.getStartDayOfMonth()).isEqualTo(start.getDayOfMonth());

        assertThat(days.getEndYear()).isEqualTo(end.getYear());
        assertThat(days.getEndMonthOfYear()).isEqualTo(end.getMonthOfYear());
        assertThat(days.getEndDayOfMonth()).isEqualTo(end.getDayOfMonth());

        List<DayRange> dayList = days.getDays();
        assertThat(dayList.size()).isEqualTo(dayCount);

        for (int i = 0; i < dayCount; i++) {
            assertThat(dayList.get(i).isSamePeriod(new DayRange(start.plusDays(i)))).isTrue();
        }
    }

    @Test
    public void calendarHoursTest() {

        final int[] dayCounts = new int[] { 1, 6, 48, 180, 480 };

        for (int dayCount : dayCounts) {
            DateTime now = Times.now();
            DayRangeCollection days = new DayRangeCollection(now, dayCount);

            DateTime startTime = now.withTimeAtStartOfDay().plus(days.getTimeCalendar().startOffset());
            DateTime endTime = startTime.plusDays(dayCount).plus(days.getTimeCalendar().endOffset());

            assertThat(days.getStart()).isEqualTo(startTime);
            assertThat(days.getEnd()).isEqualTo(endTime);

            assertThat(days.getDayCount()).isEqualTo(dayCount);

            List<HourRange> items = days.getHours();
            assertThat(items.size()).isEqualTo(dayCount * TimeSpec.HoursPerDay);

            for (int i = 0; i < items.size(); i++) {
                assertThat(items.get(i).getStart()).isEqualTo(startTime.plusHours(i));
                assertThat(items.get(i).getEnd()).isEqualTo(days.getTimeCalendar().mapEnd(startTime.plusHours(i + 1)));
                assertThat(items.get(i).isSamePeriod(new HourRange(days.getStart().plusHours(i)))).isTrue();
            }
        }
    }
}
