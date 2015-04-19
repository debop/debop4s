package debop4s.timeperiod.tests.tools;

import debop4s.timeperiod.Halfyear;
import debop4s.timeperiod.Quarter;
import debop4s.timeperiod.TimeSpec;
import debop4s.timeperiod.tests.TimePeriodTestBase;
import debop4s.timeperiod.utils.Durations;
import debop4s.timeperiod.utils.Times;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Test;

import java.util.Locale;

import static org.fest.assertions.Assertions.assertThat;

/**
 * debop4s.timeperiod.test.tools.DurationsFunSuite
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 5. 21. 오전 11:24
 */
@Slf4j
public class DurationsTest extends TimePeriodTestBase {

    private static final int currentYear = DateTime.now().getYear();
    private static final Locale currentLocale = Locale.getDefault();

    @Test
    public void yearTest() {
        assertThat(Durations.year(currentYear)).isEqualTo(new Duration(Times.startTimeOfYear(currentYear), Times.startTimeOfYear(currentYear + 1)));
        assertThat(Durations.year(currentYear + 1)).isEqualTo(new Duration(Times.startTimeOfYear(currentYear + 1), Times.startTimeOfYear(currentYear + 2)));
        assertThat(Durations.year(currentYear - 1)).isEqualTo(new Duration(Times.startTimeOfYear(currentYear - 1), Times.startTimeOfYear(currentYear)));


        assertThat(Durations.year(currentYear)).isEqualTo(Durations.days(Times.daysOfYear(currentYear)));
        assertThat(Durations.year(currentYear + 1)).isEqualTo(Durations.days(Times.daysOfYear(currentYear + 1)));
        assertThat(Durations.year(currentYear - 1)).isEqualTo(Durations.days(Times.daysOfYear(currentYear - 1)));
    }

    @Test
    public void halfyearTest() {
        for (Halfyear halfyear : Halfyear.values()) {
            int[] months = Times.monthsOfHalfyear(halfyear);
            Duration duration = Durations.Zero();

            for (int month : months) {
                int daysInMonth = Times.daysInMonth(currentYear, month);
                duration = duration.plus(daysInMonth * TimeSpec.MillisPerDay);
            }
            assertThat(Durations.halfyear(currentYear, halfyear)).isEqualTo(duration);
        }
    }

    @Test
    public void quarterTest() {
        for (Quarter quarter : Quarter.values()) {
            int[] months = Times.monthsOfQuarter(quarter);
            Duration duration = Durations.Zero();

            for (int month : months) {
                int daysInMonth = Times.daysInMonth(currentYear, month);
                duration = duration.plus(daysInMonth * TimeSpec.MillisPerDay);
            }
            assertThat(Durations.quarter(currentYear, quarter)).isEqualTo(duration);
        }
    }

    @Test
    public void monthTest() {
        for (int i = 1; i <= TimeSpec.MonthsPerYear; i++) {
            assertThat(Durations.month(currentYear, i)).isEqualTo(Duration.millis(Times.daysInMonth(currentYear, i) * TimeSpec.MillisPerDay));
        }
    }

    @Test
    public void weekTest() {
        assertThat(Durations.Week()).isEqualTo(Duration.millis(TimeSpec.DaysPerWeek * TimeSpec.MillisPerDay));

        assertThat(Durations.weeks(0)).isEqualTo(Durations.Zero());
        assertThat(Durations.weeks(1).getMillis()).isEqualTo(TimeSpec.DaysPerWeek * TimeSpec.MillisPerDay);
        assertThat(Durations.weeks(2).getMillis()).isEqualTo(TimeSpec.DaysPerWeek * TimeSpec.MillisPerDay * 2);

        assertThat(Durations.weeks(-1).getMillis()).isEqualTo(TimeSpec.DaysPerWeek * TimeSpec.MillisPerDay * -1);
        assertThat(Durations.weeks(-2).getMillis()).isEqualTo(TimeSpec.DaysPerWeek * TimeSpec.MillisPerDay * -2);
    }

    @Test
    public void dayTest() {
        assertThat(Durations.Day()).isEqualTo(Duration.millis(TimeSpec.MillisPerDay));

        assertThat(Durations.days(0)).isEqualTo(Durations.Zero());
        assertThat(Durations.days(1)).isEqualTo(Duration.millis(TimeSpec.MillisPerDay));
        assertThat(Durations.days(2)).isEqualTo(Duration.millis(TimeSpec.MillisPerDay * 2));
        assertThat(Durations.days(-2)).isEqualTo(Duration.millis(TimeSpec.MillisPerDay * -2));

        assertThat(Durations.days(1, 23)).isEqualTo(Duration.standardDays(1).plus(Duration.standardHours(23)));
        assertThat(Durations.days(1, 23, 22)).isEqualTo(Duration.standardDays(1).plus(Duration.standardHours(23)).plus(Duration.standardMinutes(22)));
        assertThat(Durations.days(1, 23, 22, 55)).isEqualTo(Duration.standardDays(1)
                                                                    .plus(Duration.standardHours(23))
                                                                    .plus(Duration.standardMinutes(22))
                                                                    .plus(Duration.standardSeconds(55)));
    }

    @Test
    public void hourTest() {
        assertThat(Durations.Hour()).isEqualTo(Duration.standardHours(1));

        assertThat(Durations.hours(0)).isEqualTo(Duration.standardHours(0));
        assertThat(Durations.hours(1)).isEqualTo(Duration.standardHours(1));
        assertThat(Durations.hours(2)).isEqualTo(Duration.standardHours(2));
        assertThat(Durations.hours(-1)).isEqualTo(Duration.standardHours(-1));
        assertThat(Durations.hours(-2)).isEqualTo(Duration.standardHours(-2));

        assertThat(Durations.hours(23, 22)).isEqualTo(Duration.standardHours(23).plus(Duration.standardMinutes(22)));
        assertThat(Durations.hours(23, 22, 55)).isEqualTo(Duration.standardHours(23).plus(Duration.standardMinutes(22)).plus(Duration.standardSeconds(55)));
    }

    @Test
    public void minuteTest() {
        assertThat(Durations.Minute()).isEqualTo(Duration.standardMinutes(1));

        assertThat(Durations.minutes(0)).isEqualTo(Duration.standardMinutes(0));
        assertThat(Durations.minutes(1)).isEqualTo(Duration.standardMinutes(1));
        assertThat(Durations.minutes(2)).isEqualTo(Duration.standardMinutes(2));
        assertThat(Durations.minutes(-1)).isEqualTo(Duration.standardMinutes(-1));
        assertThat(Durations.minutes(-2)).isEqualTo(Duration.standardMinutes(-2));

        assertThat(Durations.minutes(22, 55)).isEqualTo(Duration.standardMinutes(22).plus(Duration.standardSeconds(55)));
        assertThat(Durations.minutes(22, 55, 496)).isEqualTo(Duration.standardMinutes(22).plus(Duration.standardSeconds(55)).plus(496));
    }

    @Test
    public void secondTest() {
        assertThat(Durations.Second()).isEqualTo(Duration.standardSeconds(1));

        assertThat(Durations.seconds(0)).isEqualTo(Duration.standardSeconds(0));
        assertThat(Durations.seconds(1)).isEqualTo(Duration.standardSeconds(1));
        assertThat(Durations.seconds(2)).isEqualTo(Duration.standardSeconds(2));
        assertThat(Durations.seconds(-1)).isEqualTo(Duration.standardSeconds(-1));
        assertThat(Durations.seconds(-2)).isEqualTo(Duration.standardSeconds(-2));

        assertThat(Durations.seconds(55, 496)).isEqualTo(Duration.standardSeconds(55).plus(496));
    }

    @Test
    public void millisecondTest() {
        assertThat(Durations.Millisecond()).isEqualTo(Durations.millis(1));

        assertThat(Durations.millis(0)).isEqualTo(Durations.millis(0));
        assertThat(Durations.millis(1)).isEqualTo(Durations.millis(1));
        assertThat(Durations.millis(2)).isEqualTo(Durations.millis(2));
        assertThat(Durations.millis(-1)).isEqualTo(Durations.millis(-1));
        assertThat(Durations.millis(-2)).isEqualTo(Durations.millis(-2));
    }
}
