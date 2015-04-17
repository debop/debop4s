package debop4s.timeperiod.tests.timeranges;

import debop4s.core.JAction1;
import debop4s.core.parallels.JParallels;
import debop4s.timeperiod.TimeCalendar;
import debop4s.timeperiod.tests.TimePeriodTestBase;
import debop4s.timeperiod.timerange.HourRange;
import debop4s.timeperiod.timerange.HourRangeCollection;
import debop4s.timeperiod.utils.Times;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * kr.hconnect.timeperiod.test.timeranges.HourRangeCollectionFunSuite
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 5. 25. 오후 6:33
 */
@Slf4j
public class HourRangeCollectionTest extends TimePeriodTestBase {

    @Test
    public void singleHour() {
        DateTime startTime = new DateTime(2004, 2, 22, 17, 0);
        HourRangeCollection hours = new HourRangeCollection(startTime, 1, TimeCalendar.getEmptyOffset());

        assertThat(hours.getHourCount()).isEqualTo(1);
        assertThat(hours.getStartYear()).isEqualTo(startTime.getYear());
        assertThat(hours.getStartMonthOfYear()).isEqualTo(startTime.getMonthOfYear());
        assertThat(hours.getStartDayOfMonth()).isEqualTo(startTime.getDayOfMonth());
        assertThat(hours.getStartHourOfDay()).isEqualTo(startTime.getHourOfDay());

        assertThat(hours.getEndYear()).isEqualTo(startTime.getYear());
        assertThat(hours.getEndMonthOfYear()).isEqualTo(startTime.getMonthOfYear());
        assertThat(hours.getEndDayOfMonth()).isEqualTo(startTime.getDayOfMonth());
        assertThat(hours.getEndHourOfDay()).isEqualTo(startTime.getHourOfDay() + 1);

        final List<HourRange> items = hours.getHours();
        assertThat(items.size()).isEqualTo(1);
        assertThat(items.get(0).isSamePeriod(new HourRange(startTime, TimeCalendar.getEmptyOffset()))).isTrue();
    }

    @Test
    public void calendarHoursTeset() {
        final DateTime startTime = new DateTime(2004, 2, 11, 22, 0);
        final int hourCount = 4;
        HourRangeCollection hours = new HourRangeCollection(startTime, hourCount, TimeCalendar.getEmptyOffset());

        assertThat(hours.getHourCount()).isEqualTo(hourCount);
        assertThat(hours.getStartYear()).isEqualTo(startTime.getYear());
        assertThat(hours.getStartMonthOfYear()).isEqualTo(startTime.getMonthOfYear());
        assertThat(hours.getStartDayOfMonth()).isEqualTo(startTime.getDayOfMonth());
        assertThat(hours.getStartHourOfDay()).isEqualTo(startTime.getHourOfDay());

        assertThat(hours.getEndYear()).isEqualTo(startTime.getYear());
        assertThat(hours.getEndMonthOfYear()).isEqualTo(startTime.getMonthOfYear());
        assertThat(hours.getEndDayOfMonth()).isEqualTo(startTime.getDayOfMonth() + 1);
        assertThat(hours.getEndHourOfDay()).isEqualTo((startTime.getHourOfDay() + hourCount) % 24);

        final List<HourRange> items = hours.getHours();
        assertThat(items.size()).isEqualTo(hourCount);
        for (int h = 0; h < hourCount; h++) {
            assertThat(items.get(h).isSamePeriod(new HourRange(startTime.plusHours(h), TimeCalendar.getEmptyOffset()))).isTrue();
        }
    }

    @Test
    public void hoursTest() {

        final int[] hourCounts = new int[] { 1, 24, 48, 64, 128 };
        final DateTime now = Times.now();

        for (int hourCount : hourCounts) {

            final HourRangeCollection hours = new HourRangeCollection(now, hourCount);
            final DateTime startTime = Times.trimToMinute(now).plus(hours.getTimeCalendar().startOffset());
            final DateTime endTime = startTime.plusHours(hourCount).plus(hours.getTimeCalendar().endOffset());

            assertThat(hours.getStart()).isEqualTo(startTime);
            assertThat(hours.getEnd()).isEqualTo(endTime);
            assertThat(hours.getHourCount()).isEqualTo(hourCount);

            final List<HourRange> items = hours.getHours();
            assertThat(items.size()).isEqualTo(hourCount);

            JParallels.run(hourCount, new JAction1<Integer>() {
                @Override
                public void perform(Integer h) {
                    assertThat(items.get(h).getStart()).isEqualTo(startTime.plusHours(h));
                    assertThat(items.get(h).getEnd()).isEqualTo(hours.getTimeCalendar().mapEnd(startTime.plusHours(h + 1)));
                    assertThat(items.get(h).getUnmappedEnd()).isEqualTo(startTime.plusHours(h + 1));
                }
            });
        }
    }
}
