package debop4s.timeperiod.tests.timeranges;

import debop4s.core.JAction1;
import debop4s.core.parallels.JParallels;
import debop4s.timeperiod.TimeSpec;
import debop4s.timeperiod.YearMonth;
import debop4s.timeperiod.tests.TimePeriodTestBase;
import debop4s.timeperiod.timerange.MonthRange;
import debop4s.timeperiod.timerange.MonthRangeCollection;
import debop4s.timeperiod.utils.Times;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;


@Slf4j
public class MonthRangeCollectionTest extends TimePeriodTestBase {

    @Test
    public void singleMonths() {
        final int startYear = 2004;
        final int startMonth = 6;

        MonthRangeCollection monthRanges = new MonthRangeCollection(startYear, startMonth, 1);
        assertThat(monthRanges.getMonthCount()).isEqualTo(1);

        List<MonthRange> months = monthRanges.months();
        assertThat(months.size()).isEqualTo(1);
        assertThat(months.get(0).isSamePeriod(new MonthRange(startYear, startMonth))).isTrue();

        assertThat(monthRanges.getStartYear()).isEqualTo(startYear);
        assertThat(monthRanges.getEndYear()).isEqualTo(startYear);
        assertThat(monthRanges.getStartMonthOfYear()).isEqualTo(startMonth);
        assertThat(monthRanges.getEndMonthOfYear()).isEqualTo(startMonth);
    }

    @Test
    public void calenarMonths() {
        final int startYear = 2004;
        final int startMonth = 11;
        final int monthCount = 5;

        MonthRangeCollection monthRanges = new MonthRangeCollection(startYear, startMonth, monthCount);

        assertThat(monthRanges.getMonthCount()).isEqualTo(monthCount);
        assertThat(monthRanges.getStartYear()).isEqualTo(startYear);
        assertThat(monthRanges.getStartMonthOfYear()).isEqualTo(startMonth);
        assertThat(monthRanges.getEndYear()).isEqualTo(startYear + 1);
        assertThat(monthRanges.getEndMonthOfYear()).isEqualTo((startMonth + monthCount - 1) % TimeSpec.MonthsPerYear);
    }

    @Test
    public void monthCounts() {
        int[] monthCounts = new int[] { 1, 6, 48, 180, 365 };

        final DateTime now = Times.now();
        final DateTime today = Times.today();

        for (int monthCount : monthCounts) {
            final MonthRangeCollection monthRanges = new MonthRangeCollection(now, monthCount);

            final DateTime startTime = monthRanges.getTimeCalendar().mapStart(Times.trimToDay(today));
            final DateTime endTime = monthRanges.getTimeCalendar().mapEnd(startTime.plusMonths(monthCount));

            assertThat(monthRanges.getStart()).isEqualTo(startTime);
            assertThat(monthRanges.getEnd()).isEqualTo(endTime);

            final List<MonthRange> items = monthRanges.months();

            JParallels.run(monthCount, new JAction1<Integer>() {
                @Override
                public void perform(Integer m) {
                    final MonthRange item = items.get(m);
                    assertThat(item.getStart()).isEqualTo(startTime.plusMonths(m));
                    assertThat(item.getEnd()).isEqualTo(monthRanges.getTimeCalendar().mapEnd(startTime.plusMonths(m + 1)));

                    assertThat(item.getUnmappedStart()).isEqualTo(startTime.plusMonths(m));
                    assertThat(item.getUnmappedEnd()).isEqualTo(startTime.plusMonths(m + 1));

                    assertThat(item.isSamePeriod(new MonthRange(monthRanges.getStart().plusMonths(m)))).isTrue();

                    YearMonth ym = Times.addMonth(now.getYear(), now.getMonthOfYear(), m);
                    assertThat(item.isSamePeriod(new MonthRange(ym.year(), ym.monthOfYear()))).isTrue();
                }
            });
        }
    }
}
