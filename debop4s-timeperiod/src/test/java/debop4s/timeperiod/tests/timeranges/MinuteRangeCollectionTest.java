package debop4s.timeperiod.tests.timeranges;

import debop4s.core.JAction1;
import debop4s.core.parallels.JParallels;
import debop4s.timeperiod.TimeCalendar;
import debop4s.timeperiod.tests.TimePeriodTestBase;
import debop4s.timeperiod.timerange.MinuteRange;
import debop4s.timeperiod.timerange.MinuteRangeCollection;
import debop4s.timeperiod.utils.Times;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

@Slf4j
public class MinuteRangeCollectionTest extends TimePeriodTestBase {

    @Test
    public void singleMinutes() {
        DateTime now = Times.now();
        MinuteRangeCollection minutes = new MinuteRangeCollection(now, 1, TimeCalendar.getEmptyOffset());

        DateTime startTime = Times.trimToSecond(now);
        DateTime endTime = Times.trimToSecond(now).plusMinutes(1);

        assertThat(minutes.getMinuteCount()).isEqualTo(1);

        assertThat(minutes.getStart()).isEqualTo(startTime);
        assertThat(minutes.getEnd()).isEqualTo(endTime);

        List<MinuteRange> mins = minutes.minutes();

        assertThat(mins.size()).isEqualTo(1);
        assertThat(mins.get(0).getStart()).isEqualTo(startTime);
        assertThat(mins.get(0).getEnd()).isEqualTo(endTime);
    }

    @Test
    public void calendarMinutes() {
        DateTime now = Times.now();

        for (int m = 1; m < 97; m += 5) {
            MinuteRangeCollection minutes = new MinuteRangeCollection(now, m);

            DateTime startTime = Times.trimToSecond(now);
            DateTime endTime = Times.trimToSecond(now).plusMinutes(m).plus(minutes.getTimeCalendar().endOffset());

            assertThat(minutes.getMinuteCount()).isEqualTo(m);
            assertThat(minutes.getStart()).isEqualTo(startTime);
            assertThat(minutes.getEnd()).isEqualTo(endTime);

            List<MinuteRange> items = minutes.minutes();

            for (int i = 0; i < m; i++) {
                assertThat(items.get(i).getStart()).isEqualTo(startTime.plusMinutes(i));
                assertThat(items.get(i).getUnmappedStart()).isEqualTo(startTime.plusMinutes(i));

                assertThat(items.get(i).getEnd()).isEqualTo(minutes.getTimeCalendar().mapEnd(startTime.plusMinutes(i + 1)));
                assertThat(items.get(i).getUnmappedEnd()).isEqualTo(startTime.plusMinutes(i + 1));
            }
        }
    }

    @Test
    public void minutesTest() {

        final int[] minuteCounts = new int[] { 1, 24, 48, 64, 128 };
        final DateTime now = Times.now();

        for (int minuteCount : minuteCounts) {

            final MinuteRangeCollection minuteRanges = new MinuteRangeCollection(now, minuteCount);
            final DateTime startTime = Times.trimToSecond(now).plus(minuteRanges.getTimeCalendar().startOffset());
            final DateTime endTime = startTime.plusMinutes(minuteCount).plus(minuteRanges.getTimeCalendar().endOffset());

            assertThat(minuteRanges.getStart()).isEqualTo(startTime);
            assertThat(minuteRanges.getEnd()).isEqualTo(endTime);
            assertThat(minuteRanges.getMinuteCount()).isEqualTo(minuteCount);

            final List<MinuteRange> items = minuteRanges.minutes();
            assertThat(items.size()).isEqualTo(minuteCount);

            JParallels.run(minuteCount, new JAction1<Integer>() {
                @Override
                public void perform(Integer m) {
                    assertThat(items.get(m).getStart()).isEqualTo(startTime.plusMinutes(m));
                    assertThat(items.get(m).getEnd()).isEqualTo(minuteRanges.getTimeCalendar().mapEnd(startTime.plusMinutes(m + 1)));
                    assertThat(items.get(m).getUnmappedEnd()).isEqualTo(startTime.plusMinutes(m + 1));
                }
            });
        }
    }
}
