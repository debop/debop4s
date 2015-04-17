package debop4s.timeperiod.tests.timeranges;

import debop4s.core.JAction1;
import debop4s.core.parallels.JParallels;
import debop4s.timeperiod.YearWeek;
import debop4s.timeperiod.tests.TimePeriodTestBase;
import debop4s.timeperiod.timerange.WeekRange;
import debop4s.timeperiod.timerange.WeekRangeCollection;
import debop4s.timeperiod.utils.Times;
import debop4s.timeperiod.utils.Weeks;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * kr.hconnect.timeperiod.test.timeranges.WeekRangeCollectionFunSuite
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 5. 25. 오후 11:49
 */
@Slf4j
public class WeekRangeCollectionTest extends TimePeriodTestBase {

    @Test
    public void singleMonths() {
        final int startYear = 2004;
        final int startWeek = 22;

        WeekRangeCollection weekRanges = new WeekRangeCollection(startYear, startWeek, 1);
        assertThat(weekRanges.getWeekCount()).isEqualTo(1);

        assertThat(weekRanges.getStartYear()).isEqualTo(startYear);
        assertThat(weekRanges.getEndYear()).isEqualTo(startYear);
        assertThat(weekRanges.getStartWeekOfYear()).isEqualTo(startWeek);
        assertThat(weekRanges.getEndWeekOfYear()).isEqualTo(startWeek);

        List<WeekRange> weeks = weekRanges.getWeeks();
        assertThat(weeks.size()).isEqualTo(1);
        assertThat(weeks.get(0).isSamePeriod(new WeekRange(startYear, startWeek))).isTrue();
    }

    @Test
    public void calenarWeeks() {
        final int startYear = 2004;
        final int startWeek = 22;
        final int weekCount = 5;

        WeekRangeCollection weekRanges = new WeekRangeCollection(startYear, startWeek, weekCount);

        assertThat(weekRanges.getWeekCount()).isEqualTo(weekCount);
        assertThat(weekRanges.getStartYear()).isEqualTo(startYear);
        assertThat(weekRanges.getStartWeekOfYear()).isEqualTo(startWeek);
        assertThat(weekRanges.getEndYear()).isEqualTo(startYear);
        assertThat(weekRanges.getEndWeekOfYear()).isEqualTo((startWeek + weekCount - 1));
    }

    @Test
    public void weeksCountsTest() {
        int[] weekCounts = new int[] { 1, 6, 48, 180, 365 };

        final DateTime now = Times.now();
        final DateTime today = Times.today();

        for (int weekCount : weekCounts) {
            final WeekRangeCollection weekRanges = new WeekRangeCollection(now, weekCount);

            final DateTime startTime = weekRanges.getTimeCalendar().mapStart(Times.startTimeOfWeek(today));
            final DateTime endTime = weekRanges.getTimeCalendar().mapEnd(startTime.plusWeeks(weekCount));

            assertThat(weekRanges.getStart()).isEqualTo(startTime);
            assertThat(weekRanges.getEnd()).isEqualTo(endTime);

            final List<WeekRange> items = weekRanges.getWeeks();

            JParallels.run(weekCount, new JAction1<Integer>() {
                @Override
                public void perform(Integer w) {
                    final WeekRange item = items.get(w);
                    assertThat(item.getStart()).isEqualTo(startTime.plusWeeks(w));
                    assertThat(item.getEnd()).isEqualTo(weekRanges.getTimeCalendar().mapEnd(startTime.plusWeeks(w + 1)));

                    assertThat(item.getUnmappedStart()).isEqualTo(startTime.plusWeeks(w));
                    assertThat(item.getUnmappedEnd()).isEqualTo(startTime.plusWeeks(w + 1));

                    assertThat(item.isSamePeriod(new WeekRange(weekRanges.getStart().plusWeeks(w)))).isTrue();

                    YearWeek yw = Weeks.addWeekOfYears(now.getWeekyear(), now.getWeekOfWeekyear(), w);
                    assertThat(item.isSamePeriod(new WeekRange(yw.weekyear(), yw.weekOfWeekyear()))).isTrue();
                }
            });
        }
    }
}
