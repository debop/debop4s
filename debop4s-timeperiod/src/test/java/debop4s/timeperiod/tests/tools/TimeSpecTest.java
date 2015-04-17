package debop4s.timeperiod.tests.tools;

import debop4s.timeperiod.TimeSpec;
import debop4s.timeperiod.tests.TimePeriodTestBase;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.Duration;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

@Slf4j
public class TimeSpecTest extends TimePeriodTestBase {

    @Test
    public void dateUnitTest() {
        assertThat(TimeSpec.MonthsPerYear).isEqualTo(12);
        assertThat(TimeSpec.HalfyearsPerYear).isEqualTo(2);
        assertThat(TimeSpec.QuartersPerYear).isEqualTo(4);
        assertThat(TimeSpec.QuartersPerHalfyear).isEqualTo(2);
        assertThat(TimeSpec.MaxWeeksPerYear).isEqualTo(54);
        assertThat(TimeSpec.MonthsPerHalfyear).isEqualTo(6);
        assertThat(TimeSpec.MonthsPerQuarter).isEqualTo(3);
        assertThat(TimeSpec.MaxDaysPerMonth).isEqualTo(31);
        assertThat(TimeSpec.DaysPerWeek).isEqualTo(7);
        assertThat(TimeSpec.HoursPerDay).isEqualTo(24);
        assertThat(TimeSpec.MinutesPerHour).isEqualTo(60);
        assertThat(TimeSpec.SecondsPerMinute).isEqualTo(60);
        assertThat(TimeSpec.MillisPerSecond).isEqualTo(1000);
    }

    @Test
    public void halfyearTest() {
        assertThat(TimeSpec.FirstHalfyearMonths.length).isEqualTo(TimeSpec.MonthsPerHalfyear);

        for (int i = 0; i < TimeSpec.FirstHalfyearMonths.length; i++)
            assertThat(TimeSpec.FirstHalfyearMonths[i]).isEqualTo(i + 1);

        assertThat(TimeSpec.SecondHalfyearMonths.length).isEqualTo(TimeSpec.MonthsPerHalfyear);

        for (int i = 0; i < TimeSpec.SecondHalfyearMonths.length; i++)
            assertThat(TimeSpec.SecondHalfyearMonths[i]).isEqualTo(i + 7);
    }

    @Test
    public void quarterTest() {
        assertThat(TimeSpec.FirstQuarterMonth).isEqualTo(1);
        assertThat(TimeSpec.SecondQuarterMonth).isEqualTo(TimeSpec.FirstQuarterMonth + TimeSpec.MonthsPerQuarter);
        assertThat(TimeSpec.ThirdQuarterMonth).isEqualTo(TimeSpec.SecondQuarterMonth + TimeSpec.MonthsPerQuarter);
        assertThat(TimeSpec.FourthQuarterMonth).isEqualTo(TimeSpec.ThirdQuarterMonth + TimeSpec.MonthsPerQuarter);

        assertThat(TimeSpec.FirstQuarterMonths.length).isEqualTo(TimeSpec.MonthsPerQuarter);

        for (int i = 0; i < TimeSpec.FirstQuarterMonths.length; i++) {
            assertThat(TimeSpec.FirstQuarterMonths[i]).isEqualTo(i + 1);
            assertThat(TimeSpec.SecondQuarterMonths[i]).isEqualTo(i + 1 + TimeSpec.MonthsPerQuarter);
            assertThat(TimeSpec.ThirdQuarterMonths[i]).isEqualTo(i + 1 + 2 * TimeSpec.MonthsPerQuarter);
            assertThat(TimeSpec.FourthQuarterMonths[i]).isEqualTo(i + 1 + 3 * TimeSpec.MonthsPerQuarter);
        }
    }

    @Test
    public void durationTest() {
        assertThat(TimeSpec.NoDuration).isEqualTo(Duration.ZERO);
        assertThat(TimeSpec.EmptyDuration).isEqualTo(Duration.ZERO);
        assertThat(TimeSpec.ZeroDuration).isEqualTo(Duration.ZERO);
        assertThat(TimeSpec.MinPositiveDuration).isEqualTo(Duration.millis(1));
        assertThat(TimeSpec.MinNegativeDuration).isEqualTo(Duration.millis(-1));
    }
}
