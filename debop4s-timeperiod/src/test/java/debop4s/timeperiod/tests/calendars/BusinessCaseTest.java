package debop4s.timeperiod.tests.calendars;

import debop4s.core.JAction1;
import debop4s.core.parallels.JParallels;
import debop4s.timeperiod.TimeRange;
import debop4s.timeperiod.tests.TimePeriodTestBase;
import debop4s.timeperiod.timerange.*;
import debop4s.timeperiod.utils.Times;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

@Slf4j
public class BusinessCaseTest extends TimePeriodTestBase {

    @Test
    public void timeRangeCalendarTimeRange() {
        final DateTime now = Times.now();

        JParallels.run(500, new JAction1<Integer>() {
            @Override
            public void perform(Integer i) {
                DateTime current = now.plusDays(i);
                TimeRange currentFiveSeconds = new TimeRange(Times.trimToSecond(current, 15), Times.trimToSecond(current, 20));

                assertThat(new YearRange(current).hasInside(currentFiveSeconds)).isTrue();
                assertThat(new HalfyearRange(current).hasInside(currentFiveSeconds)).isTrue();
                assertThat(new QuarterRange(current).hasInside(currentFiveSeconds)).isTrue();
                assertThat(new MonthRange(current).hasInside(currentFiveSeconds)).isTrue();
                assertThat(new WeekRange(current).hasInside(currentFiveSeconds)).isTrue();
                assertThat(new DayRange(current).hasInside(currentFiveSeconds)).isTrue();
                assertThat(new HourRange(current).hasInside(currentFiveSeconds)).isTrue();
                assertThat(new MinuteRange(current).hasInside(currentFiveSeconds)).isTrue();
            }
        });

        TimeRange anytime = new TimeRange();

        assertThat(new YearRange().hasInside(anytime)).isFalse();
        assertThat(new HalfyearRange().hasInside(anytime)).isFalse();
        assertThat(new QuarterRange().hasInside(anytime)).isFalse();
        assertThat(new MonthRange().hasInside(anytime)).isFalse();
        assertThat(new WeekRange().hasInside(anytime)).isFalse();
        assertThat(new DayRange().hasInside(anytime)).isFalse();
        assertThat(new HourRange().hasInside(anytime)).isFalse();
        assertThat(new MinuteRange().hasInside(anytime)).isFalse();
    }
}
