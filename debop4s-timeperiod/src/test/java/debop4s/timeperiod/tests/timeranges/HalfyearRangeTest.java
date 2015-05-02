package debop4s.timeperiod.tests.timeranges;

import debop4s.core.JAction1;
import debop4s.core.parallels.JParallels;
import debop4s.timeperiod.Halfyear;
import debop4s.timeperiod.Quarter;
import debop4s.timeperiod.TimeCalendar;
import debop4s.timeperiod.TimeSpec;
import debop4s.timeperiod.tests.TimePeriodTestBase;
import debop4s.timeperiod.timerange.HalfyearRange;
import debop4s.timeperiod.timerange.MonthRange;
import debop4s.timeperiod.timerange.QuarterRange;
import debop4s.timeperiod.utils.Times;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;


@Slf4j
public class HalfyearRangeTest extends TimePeriodTestBase {

    @Test
    public void initValues() {
        DateTime now = Times.now();
        DateTime firstHalfyear = Times.startTimeOfHalfyear(now.getYear(), Halfyear.First);
        DateTime secondHalfyear = Times.startTimeOfHalfyear(now.getYear(), Halfyear.Second);

        HalfyearRange hyr = new HalfyearRange(now.getYear(), Halfyear.First, TimeCalendar.getEmptyOffset());

        assertThat(hyr.getStart().getYear()).isEqualTo(firstHalfyear.getYear());
        assertThat(hyr.getStart().getMonthOfYear()).isEqualTo(firstHalfyear.getMonthOfYear());
        assertThat(hyr.getStart().getDayOfMonth()).isEqualTo(firstHalfyear.getDayOfMonth());
        assertThat(hyr.getStart().getHourOfDay()).isEqualTo(0);
        assertThat(hyr.getStart().getMinuteOfHour()).isEqualTo(0);
        assertThat(hyr.getStart().getMinuteOfHour()).isEqualTo(0);
        assertThat(hyr.getStart().getSecondOfMinute()).isEqualTo(0);
        assertThat(hyr.getStart().getMillisOfSecond()).isEqualTo(0);

        assertThat(hyr.getEnd().getYear()).isEqualTo(secondHalfyear.getYear());
        assertThat(hyr.getEnd().getMonthOfYear()).isEqualTo(secondHalfyear.getMonthOfYear());
        assertThat(hyr.getEnd().getDayOfMonth()).isEqualTo(secondHalfyear.getDayOfMonth());
        assertThat(hyr.getEnd().getHourOfDay()).isEqualTo(0);
        assertThat(hyr.getEnd().getMinuteOfHour()).isEqualTo(0);
        assertThat(hyr.getEnd().getMinuteOfHour()).isEqualTo(0);
        assertThat(hyr.getEnd().getSecondOfMinute()).isEqualTo(0);
        assertThat(hyr.getEnd().getMillisOfSecond()).isEqualTo(0);
    }

    @Test
    public void defaultCalendarTest() {
        DateTime yearStart = Times.startTimeOfYear(Times.currentYear());

        for (Halfyear halfyear : Halfyear.values()) {
            int offset = halfyear.getValue() - 1;
            HalfyearRange hyr = new HalfyearRange(yearStart.plusMonths(TimeSpec.MonthsPerHalfyear * offset));

            assertThat(hyr.getStart()).isEqualTo(hyr.getTimeCalendar().mapStart(yearStart.plusMonths(TimeSpec.MonthsPerHalfyear * offset)));
            assertThat(hyr.getEnd()).isEqualTo(hyr.getTimeCalendar().mapEnd(yearStart.plusMonths(TimeSpec.MonthsPerHalfyear * (offset + 1))));
        }
    }

    @Test
    public void momentTest() {
        DateTime now = Times.now();
        int currentYear = now.getYear();
        assertThat(new HalfyearRange().getHalfyear()).isEqualTo(
                now.getMonthOfYear() < 7 ? Halfyear.First : Halfyear.Second);

        assertThat(new HalfyearRange(Times.asDate(currentYear, 1, 1)).getHalfyear()).isEqualTo(Halfyear.First);
        assertThat(new HalfyearRange(Times.asDate(currentYear, 6, 30)).getHalfyear()).isEqualTo(Halfyear.First);
        assertThat(new HalfyearRange(Times.asDate(currentYear, 7, 1)).getHalfyear()).isEqualTo(Halfyear.Second);
        assertThat(new HalfyearRange(Times.asDate(currentYear, 12, 31)).getHalfyear()).isEqualTo(Halfyear.Second);
    }

    @Test
    public void startMonth() {
        final DateTime now = Times.now();
        final int currentYear = now.getYear();

        JParallels.run(0, TimeSpec.MonthsPerYear, new JAction1<Integer>() {
            @Override
            public void perform(Integer m) {
                int month = m + 1;
                assertThat(new HalfyearRange(currentYear, Halfyear.First).getStartMonthOfYear()).isEqualTo(1);
                assertThat(new HalfyearRange(currentYear, Halfyear.Second).getStartMonthOfYear()).isEqualTo(7);
            }
        });
    }

    @Test
    public void isMultipleCalendarYearsTest() {
        final DateTime now = Times.now();
        final int currentYear = now.getYear();

        assertThat(new HalfyearRange(currentYear, Halfyear.First).isMultipleCalendarYears()).isFalse();
    }

    @Test
    public void calendarHalfyear() {
        final DateTime now = Times.now();
        final int currentYear = now.getYear();
        final TimeCalendar calendar = TimeCalendar.getEmptyOffset();

        HalfyearRange h1 = new HalfyearRange(currentYear, Halfyear.First, calendar);

        assertThat(h1.isReadonly()).isTrue();
        assertThat(h1.getHalfyear()).isEqualTo(Halfyear.First);
        assertThat(h1.getStart()).isEqualTo(Times.asDate(currentYear, 1, 1));
        assertThat(h1.getEnd()).isEqualTo(Times.asDate(currentYear, 7, 1));

        HalfyearRange h2 = new HalfyearRange(currentYear, Halfyear.Second, calendar);

        assertThat(h2.isReadonly()).isTrue();
        assertThat(h2.getHalfyear()).isEqualTo(Halfyear.Second);
        assertThat(h2.getStart()).isEqualTo(Times.asDate(currentYear, 7, 1));
        assertThat(h2.getEnd()).isEqualTo(Times.asDate(currentYear + 1, 1, 1));
    }

    @Test
    public void getQuartersTest() {
        final DateTime now = Times.now();
        final int currentYear = now.getYear();
        final TimeCalendar calendar = TimeCalendar.getEmptyOffset();

        HalfyearRange h1 = new HalfyearRange(currentYear, Halfyear.First, calendar);
        List<QuarterRange> h1Quarters = h1.quarters();

        int h1Index = 0;
        for (QuarterRange h1Quarter : h1Quarters) {
            log.trace("h1Quarter[{}] = [{}]", h1Index, h1Quarter);
            assertThat(h1Quarter.getQuarter()).isEqualTo(h1Index == 0 ? Quarter.First : Quarter.Second);
            assertThat(h1Quarter.getStart()).isEqualTo(h1.getStart().plusMonths(h1Index * TimeSpec.MonthsPerQuarter));
            assertThat(h1Quarter.getEnd()).isEqualTo(h1Quarter.getTimeCalendar().mapEnd(h1Quarter.getStart().plusMonths(TimeSpec.MonthsPerQuarter)));
            h1Index++;
        }

        HalfyearRange h2 = new HalfyearRange(currentYear, Halfyear.Second, calendar);
        List<QuarterRange> h2Quarters = h2.quarters();

        int h2Index = 0;
        for (QuarterRange h2Quarter : h2Quarters) {
            log.trace("h2Quarter[{}] = [{}]", h2Index, h2Quarter);
            assertThat(h2Quarter.getQuarter()).isEqualTo(h2Index == 0 ? Quarter.Third : Quarter.Fourth);
            assertThat(h2Quarter.getStart()).isEqualTo(h2.getStart().plusMonths(h2Index * TimeSpec.MonthsPerQuarter));
            assertThat(h2Quarter.getEnd()).isEqualTo(h2Quarter.getTimeCalendar().mapEnd(h2Quarter.getStart().plusMonths(TimeSpec.MonthsPerQuarter)));
            h2Index++;
        }
    }

    @Test
    public void getMonthsTest() {
        final DateTime now = Times.now();
        final int currentYear = now.getYear();
        final TimeCalendar calendar = TimeCalendar.getEmptyOffset();

        HalfyearRange h1 = new HalfyearRange(currentYear, Halfyear.First, calendar);
        List<MonthRange> months = h1.months();
        assertThat(months.size()).isEqualTo(TimeSpec.MonthsPerHalfyear);

        int index = 0;
        for (MonthRange month : months) {
            assertThat(month.getStart()).isEqualTo(h1.getStart().plusMonths(index));
            assertThat(month.getEnd()).isEqualTo(calendar.mapEnd(month.getStart().plusMonths(1)));
            index++;
        }
    }

    @Test
    public void addHalfyearsTest() {
        final DateTime now = Times.now();
        final int currentYear = now.getYear();
        final TimeCalendar calendar = TimeCalendar.getEmptyOffset();

        HalfyearRange h1 = new HalfyearRange(currentYear, Halfyear.First, calendar);

        HalfyearRange prevH1 = h1.addHalfyears(-1);
        assertThat(prevH1.getHalfyear()).isEqualTo(Halfyear.Second);
        assertThat(prevH1.getStart()).isEqualTo(h1.getStart().plusMonths(-TimeSpec.MonthsPerHalfyear));
        assertThat(prevH1.getEnd()).isEqualTo(h1.getStart());

        prevH1 = h1.addHalfyears(-2);
        assertThat(prevH1.getHalfyear()).isEqualTo(Halfyear.First);
        assertThat(prevH1.getStart()).isEqualTo(h1.getStart().plusMonths(-2 * TimeSpec.MonthsPerHalfyear));
        assertThat(prevH1.getEnd()).isEqualTo(h1.getStart().plusMonths(-1 * TimeSpec.MonthsPerHalfyear));

        prevH1 = h1.addHalfyears(-3);
        assertThat(prevH1.getHalfyear()).isEqualTo(Halfyear.Second);
        assertThat(prevH1.getStart()).isEqualTo(h1.getStart().plusMonths(-3 * TimeSpec.MonthsPerHalfyear));
        assertThat(prevH1.getEnd()).isEqualTo(h1.getStart().plusMonths(-2 * TimeSpec.MonthsPerHalfyear));

        HalfyearRange nextH1 = h1.addHalfyears(1);
        assertThat(nextH1.getHalfyear()).isEqualTo(Halfyear.Second);
        assertThat(nextH1.getStart()).isEqualTo(h1.getStart().plusMonths(TimeSpec.MonthsPerHalfyear));
        assertThat(nextH1.getEnd()).isEqualTo(h1.getStart().plusMonths(2 * TimeSpec.MonthsPerHalfyear));

        nextH1 = h1.addHalfyears(2);
        assertThat(nextH1.getHalfyear()).isEqualTo(Halfyear.First);
        assertThat(nextH1.getStart()).isEqualTo(h1.getStart().plusMonths(2 * TimeSpec.MonthsPerHalfyear));
        assertThat(nextH1.getEnd()).isEqualTo(h1.getStart().plusMonths(3 * TimeSpec.MonthsPerHalfyear));

        nextH1 = h1.addHalfyears(3);
        assertThat(nextH1.getHalfyear()).isEqualTo(Halfyear.Second);
        assertThat(nextH1.getStart()).isEqualTo(h1.getStart().plusMonths(3 * TimeSpec.MonthsPerHalfyear));
        assertThat(nextH1.getEnd()).isEqualTo(h1.getStart().plusMonths(4 * TimeSpec.MonthsPerHalfyear));

    }
}
