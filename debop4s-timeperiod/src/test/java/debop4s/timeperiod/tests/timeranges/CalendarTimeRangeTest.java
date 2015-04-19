package debop4s.timeperiod.tests.timeranges;

import debop4s.timeperiod.ITimeCalendar;
import debop4s.timeperiod.TimeCalendar;
import debop4s.timeperiod.TimeRange;
import debop4s.timeperiod.tests.TimePeriodTestBase;
import debop4s.timeperiod.timerange.CalendarTimeRange;
import debop4s.timeperiod.utils.Times;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * debop4s.timeperiod.test.timeranges.CalendarTimeRangeFunSuite
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 5. 25. 오후 2:24
 */
@Slf4j
public class CalendarTimeRangeTest extends TimePeriodTestBase {

    @Test
    public void calendarTest() {
        ITimeCalendar calendar = new TimeCalendar();
        CalendarTimeRange timeRange = new CalendarTimeRange(TimeRange.Anytime(), calendar);

        assertThat(timeRange.getTimeCalendar()).isEqualTo(calendar);
        assertThat(timeRange.isAnytime()).isTrue();
    }

    @Test(expected = AssertionError.class)
    public void momentTest() {
        DateTime today = Times.today();
        new CalendarTimeRange(today, today);
    }
}
