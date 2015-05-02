package debop4s.timeperiod.tests.timeranges;

import debop4s.timeperiod.Halfyear;
import debop4s.timeperiod.tests.TimePeriodTestBase;
import debop4s.timeperiod.timerange.HalfyearRange;
import debop4s.timeperiod.timerange.HalfyearRangeCollection;
import debop4s.timeperiod.utils.Times;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * debop4s.timeperiod.test.timeranges.HalfyearRangeCollectionFunSuite
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 5. 25. 오후 4:42
 */
@Slf4j
public class HalfyearRangeCollectionTest extends TimePeriodTestBase {

    @Test
    public void yearBaseMonthTest() {

        DateTime moment = Times.asDate(2009, 2, 15);
        int year = Times.yearOf(moment.getYear(), moment.getMonthOfYear());
        HalfyearRangeCollection halfyears = new HalfyearRangeCollection(moment, 3);

        assertThat(halfyears.getStart()).isEqualTo(Times.asDate(year, 1, 1));
    }

    @Test
    public void singleHalfyearTest() {
        final int startYear = 2004;
        final Halfyear startHalfyear = Halfyear.Second;

        HalfyearRangeCollection halfyears = new HalfyearRangeCollection(startYear, startHalfyear, 1);

        assertThat(halfyears.getHalfyearCount()).isEqualTo(1);
        assertThat(halfyears.getStartHalfyear()).isEqualTo(startHalfyear);
        assertThat(halfyears.getEndYear()).isEqualTo(startYear);
        assertThat(halfyears.getEndHalfyear()).isEqualTo(startHalfyear);

        List<HalfyearRange> halfyearList = halfyears.halfyears();
        assertThat(halfyearList.size()).isEqualTo(1);
        assertThat(halfyearList.get(0).isSamePeriod(new HalfyearRange(2004, Halfyear.Second))).isTrue();
    }

    @Test
    public void firstCalendarHalfyears() {
        final int startYear = 2004;
        final Halfyear startHalfyear = Halfyear.First;
        final int halfyearCount = 3;

        HalfyearRangeCollection halfyears = new HalfyearRangeCollection(startYear, startHalfyear, halfyearCount);

        assertThat(halfyears.getHalfyearCount()).isEqualTo(halfyearCount);
        assertThat(halfyears.getStartHalfyear()).isEqualTo(startHalfyear);
        assertThat(halfyears.getEndYear()).isEqualTo(startYear + 1);
        assertThat(halfyears.getEndHalfyear()).isEqualTo(Halfyear.First);

        List<HalfyearRange> halfyearList = halfyears.halfyears();

        assertThat(halfyearList.size()).isEqualTo(halfyearCount);
        assertThat(halfyearList.get(0).isSamePeriod(new HalfyearRange(2004, Halfyear.First))).isTrue();
        assertThat(halfyearList.get(1).isSamePeriod(new HalfyearRange(2004, Halfyear.Second))).isTrue();
        assertThat(halfyearList.get(2).isSamePeriod(new HalfyearRange(2005, Halfyear.First))).isTrue();
    }

    @Test
    public void secondCalendarHalfyears() {
        final int startYear = 2004;
        final Halfyear startHalfyear = Halfyear.Second;
        final int halfyearCount = 3;

        HalfyearRangeCollection halfyears = new HalfyearRangeCollection(startYear, startHalfyear, halfyearCount);

        assertThat(halfyears.getHalfyearCount()).isEqualTo(halfyearCount);
        assertThat(halfyears.getStartHalfyear()).isEqualTo(startHalfyear);
        assertThat(halfyears.getEndYear()).isEqualTo(startYear + 1);
        assertThat(halfyears.getEndHalfyear()).isEqualTo(Halfyear.Second);

        List<HalfyearRange> halfyearList = halfyears.halfyears();

        assertThat(halfyearList.size()).isEqualTo(halfyearCount);
        assertThat(halfyearList.get(0).isSamePeriod(new HalfyearRange(2004, Halfyear.Second))).isTrue();
        assertThat(halfyearList.get(1).isSamePeriod(new HalfyearRange(2005, Halfyear.First))).isTrue();
        assertThat(halfyearList.get(2).isSamePeriod(new HalfyearRange(2005, Halfyear.Second))).isTrue();
    }

}
