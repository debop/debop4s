package debop4s.timeperiod.tests.timeranges;

import debop4s.core.JAction1;
import debop4s.core.parallels.JParallels;
import debop4s.timeperiod.TimeCalendar;
import debop4s.timeperiod.tests.TimePeriodTestBase;
import debop4s.timeperiod.timerange.YearRange;
import debop4s.timeperiod.utils.Times;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * debop4s.timeperiod.test.timeranges.YearRangeFunSuite
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 5. 26. 오전 12:23
 */
@Slf4j
public class YearRangeTest extends TimePeriodTestBase {

    @Test
    public void initValuesTest() {
        DateTime now = Times.now();
        DateTime thisYear = Times.startTimeOfYear(now);
        DateTime nextYear = thisYear.plusYears(1);

        YearRange yearRange = new YearRange(now, TimeCalendar.getEmptyOffset());

        assertThat(yearRange.getStart().getYear()).isEqualTo(thisYear.getYear());
        assertThat(yearRange.getStart()).isEqualTo(thisYear);
        assertThat(yearRange.getEnd()).isEqualTo(nextYear);
    }

    @Test
    public void startYear() {
        int currentYear = Times.currentYear().getYear();

        assertThat(new YearRange(Times.asDate(2008, 7, 28)).getYear()).isEqualTo(2008);
    }

    @Test
    public void yearIndex() {
        int yearIndex = 1994;
        YearRange yearRange = new YearRange(yearIndex, TimeCalendar.getEmptyOffset());
        assertThat(yearRange.isReadonly()).isTrue();
        assertThat(yearRange.getStart()).isEqualTo(Times.startTimeOfYear(yearIndex));
        assertThat(yearRange.getEnd()).isEqualTo(Times.startTimeOfYear(yearIndex + 1));
    }

    @Test
    public void addYearsTest() {

        final DateTime now = Times.now();
        final DateTime startYear = Times.startTimeOfYear(now);
        final YearRange yearRange = new YearRange(now);


        assertThat(yearRange.previousYear().getStart()).isEqualTo(startYear.plusYears(-1));
        assertThat(yearRange.nextYear().getStart()).isEqualTo(startYear.plusYears(1));

        assertThat(yearRange.addYears(0)).isEqualTo(yearRange);

        JParallels.run(-60, 120, new JAction1<Integer>() {
            @Override
            public void perform(Integer y) {
                assertThat(yearRange.addYears(y).getStart()).isEqualTo(startYear.plusYears(y));
            }
        });
    }
}
