package debop4s.timeperiod.tests.timeranges;

import debop4s.timeperiod.Quarter;
import debop4s.timeperiod.tests.TimePeriodTestBase;
import debop4s.timeperiod.timerange.QuarterRange;
import debop4s.timeperiod.timerange.QuarterRangeCollection;
import debop4s.timeperiod.utils.Times;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * debop4s.timeperiod.test.timeranges.QuarterRangeCollectionFunSuite
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 5. 25. 오후 11:18
 */
@Slf4j
public class QuarterRangeCollectionTest extends TimePeriodTestBase {

    @Test
    public void yearBaseMonthTest() {

        DateTime moment = Times.asDate(2009, 2, 15);
        int year = Times.yearOf(moment.getYear(), moment.getMonthOfYear());
        QuarterRangeCollection quarterRanges = new QuarterRangeCollection(moment, 3);

        assertThat(quarterRanges.getStart()).isEqualTo(Times.asDate(year, 1, 1));
    }

    @Test
    public void singleQuarterTest() {
        final int startYear = 2004;
        final Quarter startQuarter = Quarter.Second;

        QuarterRangeCollection quarterRanges = new QuarterRangeCollection(startYear, startQuarter, 1);

        assertThat(quarterRanges.getQuarterCount()).isEqualTo(1);
        assertThat(quarterRanges.getStartQuarter()).isEqualTo(startQuarter);
        assertThat(quarterRanges.getEndYear()).isEqualTo(startYear);
        assertThat(quarterRanges.getEndQuarter()).isEqualTo(startQuarter);

        List<QuarterRange> quarters = quarterRanges.getQuarters();
        assertThat(quarters.size()).isEqualTo(1);
        assertThat(quarters.get(0).isSamePeriod(new QuarterRange(2004, Quarter.Second))).isTrue();
    }

    @Test
    public void firstCalendarHalfyears() {

        final int startYear = 2004;
        final Quarter startQuarter = Quarter.First;
        final int quarterCount = 5;

        QuarterRangeCollection quarterRanges = new QuarterRangeCollection(startYear, startQuarter, quarterCount);

        assertThat(quarterRanges.getQuarterCount()).isEqualTo(quarterCount);
        assertThat(quarterRanges.getStartQuarter()).isEqualTo(startQuarter);
        assertThat(quarterRanges.getEndYear()).isEqualTo(startYear + 1);
        assertThat(quarterRanges.getEndQuarter()).isEqualTo(Quarter.First);

        List<QuarterRange> quarters = quarterRanges.getQuarters();

        assertThat(quarters.size()).isEqualTo(quarterCount);
        assertThat(quarters.get(0).isSamePeriod(new QuarterRange(2004, Quarter.First))).isTrue();
        assertThat(quarters.get(1).isSamePeriod(new QuarterRange(2004, Quarter.Second))).isTrue();
        assertThat(quarters.get(2).isSamePeriod(new QuarterRange(2004, Quarter.Third))).isTrue();
        assertThat(quarters.get(3).isSamePeriod(new QuarterRange(2004, Quarter.Fourth))).isTrue();
        assertThat(quarters.get(4).isSamePeriod(new QuarterRange(2005, Quarter.First))).isTrue();
    }

    @Test
    public void secondCalendarHalfyears() {
        final int startYear = 2004;
        final Quarter startQuarter = Quarter.Second;
        final int quarterCount = 5;

        QuarterRangeCollection quarterRanges = new QuarterRangeCollection(startYear, startQuarter, quarterCount);

        assertThat(quarterRanges.getQuarterCount()).isEqualTo(quarterCount);
        assertThat(quarterRanges.getStartQuarter()).isEqualTo(startQuarter);
        assertThat(quarterRanges.getEndYear()).isEqualTo(startYear + 1);
        assertThat(quarterRanges.getEndQuarter()).isEqualTo(Quarter.Second);

        List<QuarterRange> quarters = quarterRanges.getQuarters();

        assertThat(quarters.size()).isEqualTo(quarterCount);
        assertThat(quarters.get(0).isSamePeriod(new QuarterRange(2004, Quarter.Second))).isTrue();
        assertThat(quarters.get(1).isSamePeriod(new QuarterRange(2004, Quarter.Third))).isTrue();
        assertThat(quarters.get(2).isSamePeriod(new QuarterRange(2004, Quarter.Fourth))).isTrue();
        assertThat(quarters.get(3).isSamePeriod(new QuarterRange(2005, Quarter.First))).isTrue();
        assertThat(quarters.get(4).isSamePeriod(new QuarterRange(2005, Quarter.Second))).isTrue();
    }
}
