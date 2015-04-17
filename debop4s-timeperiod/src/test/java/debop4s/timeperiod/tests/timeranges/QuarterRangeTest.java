package debop4s.timeperiod.tests.timeranges;

import debop4s.timeperiod.Quarter;
import debop4s.timeperiod.TimeCalendar;
import debop4s.timeperiod.TimeSpec;
import debop4s.timeperiod.tests.TimePeriodTestBase;
import debop4s.timeperiod.timerange.MonthRange;
import debop4s.timeperiod.timerange.QuarterRange;
import debop4s.timeperiod.utils.Times;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * kr.hconnect.timeperiod.test.timeranges.QuarterRangeFunSuite
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 5. 25. 오후 11:18
 */
@Slf4j
public class QuarterRangeTest extends TimePeriodTestBase {

    @Test
    public void initValues() {
        DateTime now = Times.now();
        DateTime firstQuarter = Times.startTimeOfQuarter(now.getYear(), Quarter.First);
        DateTime secondQuarter = Times.startTimeOfQuarter(now.getYear(), Quarter.Second);

        QuarterRange quarterRange = new QuarterRange(now.getYear(), Quarter.First, TimeCalendar.getEmptyOffset());

        assertThat(quarterRange.getStart().getYear()).isEqualTo(firstQuarter.getYear());
        assertThat(quarterRange.getStart().getMonthOfYear()).isEqualTo(firstQuarter.getMonthOfYear());
        assertThat(quarterRange.getStart().getDayOfMonth()).isEqualTo(firstQuarter.getDayOfMonth());
        assertThat(quarterRange.getStart().getHourOfDay()).isEqualTo(0);
        assertThat(quarterRange.getStart().getMinuteOfHour()).isEqualTo(0);
        assertThat(quarterRange.getStart().getMinuteOfHour()).isEqualTo(0);
        assertThat(quarterRange.getStart().getSecondOfMinute()).isEqualTo(0);
        assertThat(quarterRange.getStart().getMillisOfSecond()).isEqualTo(0);

        assertThat(quarterRange.getEnd().getYear()).isEqualTo(secondQuarter.getYear());
        assertThat(quarterRange.getEnd().getMonthOfYear()).isEqualTo(secondQuarter.getMonthOfYear());
        assertThat(quarterRange.getEnd().getDayOfMonth()).isEqualTo(secondQuarter.getDayOfMonth());
        assertThat(quarterRange.getEnd().getHourOfDay()).isEqualTo(0);
        assertThat(quarterRange.getEnd().getMinuteOfHour()).isEqualTo(0);
        assertThat(quarterRange.getEnd().getMinuteOfHour()).isEqualTo(0);
        assertThat(quarterRange.getEnd().getSecondOfMinute()).isEqualTo(0);
        assertThat(quarterRange.getEnd().getMillisOfSecond()).isEqualTo(0);
    }

    @Test
    public void defaultCalendarTest() {
        DateTime yearStart = Times.startTimeOfYear(Times.currentYear());

        for (Quarter quarter : Quarter.values()) {
            int offset = quarter.getValue() - 1;
            QuarterRange quarterRange = new QuarterRange(yearStart.plusMonths(TimeSpec.MonthsPerQuarter * offset));

            assertThat(quarterRange.getStart())
                    .isEqualTo(quarterRange.getTimeCalendar().mapStart(yearStart.plusMonths(TimeSpec.MonthsPerQuarter * offset)));
            assertThat(quarterRange.getEnd())
                    .isEqualTo(quarterRange.getTimeCalendar().mapEnd(yearStart.plusMonths(TimeSpec.MonthsPerQuarter * (offset + 1))));
        }
    }

    @Test
    public void momentTest() {
        DateTime now = Times.now();
        int currentYear = now.getYear();

        assertThat(new QuarterRange(Times.asDate(currentYear, 1, 1)).getQuarter()).isEqualTo(Quarter.First);
        assertThat(new QuarterRange(Times.asDate(currentYear, 3, 31)).getQuarter()).isEqualTo(Quarter.First);

        assertThat(new QuarterRange(Times.asDate(currentYear, 4, 1)).getQuarter()).isEqualTo(Quarter.Second);
        assertThat(new QuarterRange(Times.asDate(currentYear, 6, 30)).getQuarter()).isEqualTo(Quarter.Second);

        assertThat(new QuarterRange(Times.asDate(currentYear, 7, 1)).getQuarter()).isEqualTo(Quarter.Third);
        assertThat(new QuarterRange(Times.asDate(currentYear, 9, 30)).getQuarter()).isEqualTo(Quarter.Third);

        assertThat(new QuarterRange(Times.asDate(currentYear, 10, 1)).getQuarter()).isEqualTo(Quarter.Fourth);
        assertThat(new QuarterRange(Times.asDate(currentYear, 12, 31)).getQuarter()).isEqualTo(Quarter.Fourth);
    }

    @Test
    public void startMonth() {
        final DateTime now = Times.now();
        final int currentYear = now.getYear();

        assertThat(new QuarterRange(currentYear, Quarter.First).getStartMonthOfYear()).isEqualTo(1);
        assertThat(new QuarterRange(currentYear, Quarter.Second).getStartMonthOfYear()).isEqualTo(4);
        assertThat(new QuarterRange(currentYear, Quarter.Third).getStartMonthOfYear()).isEqualTo(7);
        assertThat(new QuarterRange(currentYear, Quarter.Fourth).getStartMonthOfYear()).isEqualTo(10);
    }

    @Test
    public void isMultipleCalendarYearsTest() {
        final DateTime now = Times.now();
        final int currentYear = now.getYear();

        assertThat(new QuarterRange(currentYear, Quarter.First).isMultipleCalendarYears()).isFalse();
    }

    @Test
    public void calendarQuarter() {
        final DateTime now = Times.now();
        final int currentYear = now.getYear();
        final TimeCalendar calendar = TimeCalendar.getEmptyOffset();

        QuarterRange q1 = new QuarterRange(currentYear, Quarter.First, calendar);

        assertThat(q1.isReadonly()).isTrue();
        assertThat(q1.getQuarter()).isEqualTo(Quarter.First);
        assertThat(q1.getStart()).isEqualTo(Times.asDate(currentYear, 1, 1));
        assertThat(q1.getEnd()).isEqualTo(Times.asDate(currentYear, 4, 1));

        QuarterRange q2 = new QuarterRange(currentYear, Quarter.Second, calendar);

        assertThat(q2.isReadonly()).isTrue();
        assertThat(q2.getQuarter()).isEqualTo(Quarter.Second);
        assertThat(q2.getStart()).isEqualTo(Times.asDate(currentYear, 4, 1));
        assertThat(q2.getEnd()).isEqualTo(Times.asDate(currentYear, 7, 1));

        QuarterRange q3 = new QuarterRange(currentYear, Quarter.Third, calendar);

        assertThat(q3.isReadonly()).isTrue();
        assertThat(q3.getQuarter()).isEqualTo(Quarter.Third);
        assertThat(q3.getStart()).isEqualTo(Times.asDate(currentYear, 7, 1));
        assertThat(q3.getEnd()).isEqualTo(Times.asDate(currentYear, 10, 1));

        QuarterRange q4 = new QuarterRange(currentYear, Quarter.Fourth, calendar);

        assertThat(q4.isReadonly()).isTrue();
        assertThat(q4.getQuarter()).isEqualTo(Quarter.Fourth);
        assertThat(q4.getStart()).isEqualTo(Times.asDate(currentYear, 10, 1));
        assertThat(q4.getEnd()).isEqualTo(Times.asDate(currentYear + 1, 1, 1));
    }


    @Test
    public void getMonthsTest() {
        final DateTime now = Times.now();
        final int currentYear = now.getYear();
        final TimeCalendar calendar = TimeCalendar.getEmptyOffset();

        QuarterRange q1 = new QuarterRange(currentYear, Quarter.First, calendar);
        List<MonthRange> months = q1.getMonths();
        assertThat(months.size()).isEqualTo(TimeSpec.MonthsPerQuarter);

        int index = 0;
        for (MonthRange month : months) {
            assertThat(month.getStart()).isEqualTo(q1.getStart().plusMonths(index));
            assertThat(month.getEnd()).isEqualTo(calendar.mapEnd(month.getStart().plusMonths(1)));
            index++;
        }
    }

    @Test
    public void addQuatersTest() {
        final DateTime now = Times.now();
        final int currentYear = now.getYear();
        final TimeCalendar calendar = TimeCalendar.getEmptyOffset();

        QuarterRange q1 = new QuarterRange(currentYear, Quarter.First, calendar);

        QuarterRange prevQ1 = q1.addQuarters(-1);
        assertThat(prevQ1.getQuarter()).isEqualTo(Quarter.Fourth);
        assertThat(prevQ1.getStart()).isEqualTo(q1.getStart().plusMonths(-TimeSpec.MonthsPerQuarter));
        assertThat(prevQ1.getEnd()).isEqualTo(q1.getStart());

        prevQ1 = q1.addQuarters(-2);
        assertThat(prevQ1.getQuarter()).isEqualTo(Quarter.Third);
        assertThat(prevQ1.getStart()).isEqualTo(q1.getStart().plusMonths(-2 * TimeSpec.MonthsPerQuarter));
        assertThat(prevQ1.getEnd()).isEqualTo(q1.getStart().plusMonths(-1 * TimeSpec.MonthsPerQuarter));

        prevQ1 = q1.addQuarters(-3);
        assertThat(prevQ1.getQuarter()).isEqualTo(Quarter.Second);
        assertThat(prevQ1.getStart()).isEqualTo(q1.getStart().plusMonths(-3 * TimeSpec.MonthsPerQuarter));
        assertThat(prevQ1.getEnd()).isEqualTo(q1.getStart().plusMonths(-2 * TimeSpec.MonthsPerQuarter));

        prevQ1 = q1.addQuarters(-4);
        assertThat(prevQ1.getQuarter()).isEqualTo(Quarter.First);
        assertThat(prevQ1.getStart()).isEqualTo(q1.getStart().plusMonths(-4 * TimeSpec.MonthsPerQuarter));
        assertThat(prevQ1.getEnd()).isEqualTo(q1.getStart().plusMonths(-3 * TimeSpec.MonthsPerQuarter));

        QuarterRange nextQ1 = q1.addQuarters(1);
        assertThat(nextQ1.getQuarter()).isEqualTo(Quarter.Second);
        assertThat(nextQ1.getStart()).isEqualTo(q1.getStart().plusMonths(TimeSpec.MonthsPerQuarter));
        assertThat(nextQ1.getEnd()).isEqualTo(q1.getStart().plusMonths(2 * TimeSpec.MonthsPerQuarter));

        nextQ1 = q1.addQuarters(2);
        assertThat(nextQ1.getQuarter()).isEqualTo(Quarter.Third);
        assertThat(nextQ1.getStart()).isEqualTo(q1.getStart().plusMonths(2 * TimeSpec.MonthsPerQuarter));
        assertThat(nextQ1.getEnd()).isEqualTo(q1.getStart().plusMonths(3 * TimeSpec.MonthsPerQuarter));

        nextQ1 = q1.addQuarters(3);
        assertThat(nextQ1.getQuarter()).isEqualTo(Quarter.Fourth);
        assertThat(nextQ1.getStart()).isEqualTo(q1.getStart().plusMonths(3 * TimeSpec.MonthsPerQuarter));
        assertThat(nextQ1.getEnd()).isEqualTo(q1.getStart().plusMonths(4 * TimeSpec.MonthsPerQuarter));

        nextQ1 = q1.addQuarters(4);
        assertThat(nextQ1.getQuarter()).isEqualTo(Quarter.First);
        assertThat(nextQ1.getStart()).isEqualTo(q1.getStart().plusMonths(4 * TimeSpec.MonthsPerQuarter));
        assertThat(nextQ1.getEnd()).isEqualTo(q1.getStart().plusMonths(5 * TimeSpec.MonthsPerQuarter));

    }
}
