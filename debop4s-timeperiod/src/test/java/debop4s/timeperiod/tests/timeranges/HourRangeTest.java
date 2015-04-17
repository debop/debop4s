package debop4s.timeperiod.tests.timeranges;

import debop4s.timeperiod.TimeCalendar;
import debop4s.timeperiod.TimeSpec;
import debop4s.timeperiod.tests.TimePeriodTestBase;
import debop4s.timeperiod.timerange.HourRange;
import debop4s.timeperiod.timerange.MinuteRange;
import debop4s.timeperiod.utils.Times;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

@Slf4j
public class HourRangeTest extends TimePeriodTestBase {

    @Test
    public void initValues() {
        DateTime now = Times.now();
        DateTime firstHour = Times.trimToMinute(now);
        DateTime secondHour = firstHour.plusHours(1);

        HourRange hourRange = new HourRange(now, TimeCalendar.getEmptyOffset());

        assertThat(hourRange.getStart().getYear()).isEqualTo(firstHour.getYear());
        assertThat(hourRange.getStart().getMonthOfYear()).isEqualTo(firstHour.getMonthOfYear());
        assertThat(hourRange.getStart().getDayOfMonth()).isEqualTo(firstHour.getDayOfMonth());
        assertThat(hourRange.getStart().getMinuteOfHour()).isEqualTo(0);
        assertThat(hourRange.getStart().getSecondOfMinute()).isEqualTo(0);
        assertThat(hourRange.getStart().getMillisOfSecond()).isEqualTo(0);

        assertThat(hourRange.getEnd().getYear()).isEqualTo(secondHour.getYear());
        assertThat(hourRange.getEnd().getMonthOfYear()).isEqualTo(secondHour.getMonthOfYear());
        assertThat(hourRange.getEnd().getDayOfMonth()).isEqualTo(secondHour.getDayOfMonth());
        assertThat(hourRange.getEnd().getMinuteOfHour()).isEqualTo(0);
        assertThat(hourRange.getEnd().getSecondOfMinute()).isEqualTo(0);
        assertThat(hourRange.getEnd().getMillisOfSecond()).isEqualTo(0);
    }

    @Test
    public void defaultCalendar() {
        DateTime now = Times.now();
        DateTime today = Times.today();

        for (int h = 0; h < TimeSpec.HoursPerDay; h++) {
            HourRange hourRange = new HourRange(today.plusHours(h));

            assertThat(hourRange.getYear()).isEqualTo(today.getYear());
            assertThat(hourRange.getMonthOfYear()).isEqualTo(today.getMonthOfYear());
            assertThat(hourRange.getDayOfMonth()).isEqualTo(today.getDayOfMonth());
            assertThat(hourRange.getHourOfDay()).isEqualTo(h);
            assertThat(hourRange.getStart()).isEqualTo(hourRange.getTimeCalendar().mapStart(today.plusHours(h)));
            assertThat(hourRange.getEnd()).isEqualTo(hourRange.getTimeCalendar().mapEnd(today.plusHours(h + 1)));
        }
    }

    @Test
    public void constructorTest() {
        DateTime now = Times.now();

        HourRange hourRange = new HourRange(now);
        assertThat(hourRange.getYear()).isEqualTo(now.getYear());
        assertThat(hourRange.getMonthOfYear()).isEqualTo(now.getMonthOfYear());
        assertThat(hourRange.getDayOfMonth()).isEqualTo(now.getDayOfMonth());
        assertThat(hourRange.getHourOfDay()).isEqualTo(now.getHourOfDay());

        hourRange = new HourRange(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), now.getHourOfDay());
        assertThat(hourRange.getYear()).isEqualTo(now.getYear());
        assertThat(hourRange.getMonthOfYear()).isEqualTo(now.getMonthOfYear());
        assertThat(hourRange.getDayOfMonth()).isEqualTo(now.getDayOfMonth());
        assertThat(hourRange.getHourOfDay()).isEqualTo(now.getHourOfDay());
    }

    @Test
    public void addHourTest() {
        HourRange hourRange = new HourRange();

        assertThat(hourRange.previousHour().getHourOfDay()).isEqualTo(hourRange.getStart().plusHours(-1).getHourOfDay());
        assertThat(hourRange.nextHour().getHourOfDay()).isEqualTo(hourRange.getStart().plusHours(1).getHourOfDay());

        hourRange = new HourRange(TimeCalendar.getEmptyOffset());

        assertThat(hourRange.addHours(0)).isEqualTo(hourRange);

        HourRange prevRange = hourRange.previousHour();
        assertThat(hourRange.addHours(-1).getYear()).isEqualTo(prevRange.getYear());
        assertThat(hourRange.addHours(-1).getMonthOfYear()).isEqualTo(prevRange.getMonthOfYear());
        assertThat(hourRange.addHours(-1).getDayOfMonth()).isEqualTo(prevRange.getDayOfMonth());
        assertThat(hourRange.addHours(-1).getHourOfDay()).isEqualTo(prevRange.getHourOfDay());

        HourRange nextRange = hourRange.nextHour();
        assertThat(hourRange.addHours(1).getYear()).isEqualTo(nextRange.getYear());
        assertThat(hourRange.addHours(1).getMonthOfYear()).isEqualTo(nextRange.getMonthOfYear());
        assertThat(hourRange.addHours(1).getDayOfMonth()).isEqualTo(nextRange.getDayOfMonth());
        assertThat(hourRange.addHours(1).getHourOfDay()).isEqualTo(nextRange.getHourOfDay());

        for (int h = -100; h < 100; h += 5) {
            HourRange range = hourRange.addHours(h);
            HourRange range2 = hourRange.addHours(h);
            assertThat(range.getYear()).isEqualTo(range2.getYear());
            assertThat(range.getMonthOfYear()).isEqualTo(range2.getMonthOfYear());
            assertThat(range.getDayOfMonth()).isEqualTo(range2.getDayOfMonth());
            assertThat(range.getHourOfDay()).isEqualTo(range2.getHourOfDay());
        }
    }

    @Test
    public void getMinutesTest() {
        HourRange hourRange = new HourRange();
        List<MinuteRange> minutes = hourRange.getMinutes();

        assertThat(minutes.size()).isEqualTo(TimeSpec.MinutesPerHour);

        for (int i = 0; i < TimeSpec.MinutesPerHour; i++) {
            MinuteRange minute = minutes.get(i);

            assertThat(minute.getStart()).isEqualTo(hourRange.getStart().plusMinutes(i));
            assertThat(minute.getUnmappedStart()).isEqualTo(hourRange.getStart().plusMinutes(i));

            assertThat(minute.getEnd()).isEqualTo(minute.getTimeCalendar().mapEnd(hourRange.getStart().plusMinutes(i + 1)));
            assertThat(minute.getUnmappedEnd()).isEqualTo(hourRange.getStart().plusMinutes(i + 1));
        }
    }
}
