package debop4s.timeperiod.tests.tools;

import com.google.common.collect.Iterables;
import debop4s.core.Func1;
import debop4s.timeperiod.ITimePeriod;
import debop4s.timeperiod.PeriodUnit;
import debop4s.timeperiod.TimeRange;
import debop4s.timeperiod.TimeSpec;
import debop4s.timeperiod.tests.TimePeriodTestBase;
import debop4s.timeperiod.utils.Times;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import scala.Tuple2;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.fest.assertions.Assertions.assertThat;

/**
 * kr.hconnect.timeperiod.test.tools.TimesForEachTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 5. 24. 오전 12:22
 */
@Slf4j
public class TimesForEachTest extends TimePeriodTestBase {

    private DateTime startTime = new DateTime(2008, 4, 10, 5, 33, 24, 345);
    private DateTime endTime = new DateTime(2012, 10, 20, 13, 43, 12, 599);

    @Getter private TimeRange period = new TimeRange(startTime, endTime);

    @Test
    public void foreachYearsTest() {
        int count = 0;
        for (ITimePeriod p : Times.foreachYears(period)) {
            log.trace("year [{}] = [{}]", count++, p.getStart().getYear());
        }
        assertThat(count).isEqualTo(period.getEnd().getYear() - period.getStart().getYear() + 1);
    }

    @Test
    public void foreachYearsInSameYearTest() throws Exception {
        ITimePeriod period = Times.relativeWeekPeriod(startTime, 1);

        List<ITimePeriod> years = Times.foreachYears(period);
        assertThat(years.size()).isEqualTo(1);
    }

    @Test
    public void foreachMonthsTest() {
        int count = 0;
        for (ITimePeriod p : Times.foreachMonths(period)) {
            log.trace("month [{}] = [{}]", count++, p.getStart().getMonthOfYear());
        }

        int months = (int) (period.getDuration().getMillis() / (TimeSpec.MaxDaysPerMonth * TimeSpec.MillisPerDay)) + 2;
        assertThat(count).isEqualTo(months);
    }

    @Test
    public void foreachWeeksTest() {
        int count = 0;

        DateTimeFormatter shortDate = DateTimeFormat.shortDate();
        List<ITimePeriod> weeks = Times.foreachWeeks(period);
        for (ITimePeriod p : weeks) {
            log.trace("week[{}] = [{}]~[{}], WeekOfYear=({},{})",
                      count++, shortDate.print(p.getStart()), shortDate.print(p.getEnd()), p.getStart().getWeekyear(), p.getStart().getWeekOfWeekyear());
        }

        assertThat(weeks.get(0).getStart()).isEqualTo(period.getStart());
        assertThat(weeks.get(weeks.size() - 1).getEnd()).isEqualTo(period.getEnd());
    }

    @Test
    public void foreachDaysTest() {
        List<ITimePeriod> days = Times.foreachDays(period);

        assertThat(days.get(0).getStart()).isEqualTo(period.getStart());
        assertThat(days.get(days.size() - 1).getEnd()).isEqualTo(period.getEnd());

        log.trace("end-1=[{}]", days.get(days.size() - 2));
        log.trace("end  =[{}]", days.get(days.size() - 1));
    }

    @Test
    public void foreachHoursTest() {
        List<ITimePeriod> hours = Times.foreachHours(period);

        assertThat(hours.get(0).getStart()).isEqualTo(period.getStart());
        assertThat(hours.get(hours.size() - 1).getEnd()).isEqualTo(period.getEnd());
        assertThat(hours.get(hours.size() - 1).getStart().getMillis()).isGreaterThan(hours.get(hours.size() - 2).getEnd().getMillis());
    }

    @Test
    public void foreachMinuteTest() {
        List<ITimePeriod> minutes = Times.foreachMinutes(period);

        assertThat(minutes.get(0).getStart()).isEqualTo(period.getStart());
        assertThat(minutes.get(minutes.size() - 1).getEnd()).isEqualTo(period.getEnd());
        assertThat(minutes.get(minutes.size() - 1).getStart().getMillis()).isGreaterThan(minutes.get(minutes.size() - 2).getEnd().getMillis());
    }

    @Test
    public void foreachPeriodsTest() {

        for (PeriodUnit periodUnit : PeriodUnit.values()) {
            if (periodUnit == PeriodUnit.All ||
                periodUnit == PeriodUnit.Second ||
                periodUnit == PeriodUnit.Millisecond)
                continue;

            int count = 0;
            for (ITimePeriod p : Times.foreachPeriods(period, periodUnit)) {
                count++;
                if (count == 1000)
                    break;
            }
        }
    }

    @Test
    public void runPeriodTest() {

        for (PeriodUnit periodUnit : PeriodUnit.values()) {
            if (periodUnit == PeriodUnit.All ||
                periodUnit == PeriodUnit.Second ||
                periodUnit == PeriodUnit.Millisecond)
                continue;

            final int[] count = { 0 };
            Iterable<Integer> results = Times.runPeriods(period, periodUnit, new Func1<ITimePeriod, Integer>() {
                @Override
                public Integer execute(ITimePeriod arg) {
                    return count[0]++;
                }
            });
            assertThat(Iterables.size(results)).isEqualTo(count[0]);
        }
    }

    @Test
    public void runPeriodAsParallelTest() {

        for (PeriodUnit periodUnit : PeriodUnit.values()) {
            if (periodUnit == PeriodUnit.All ||
                periodUnit == PeriodUnit.Second ||
                periodUnit == PeriodUnit.Millisecond)
                continue;

            final AtomicInteger count = new AtomicInteger(0);
            Iterable<Tuple2<ITimePeriod, Integer>> results =
                    Times.runPeriodsAsParallel(period, periodUnit, new Func1<ITimePeriod, Integer>() {
                        @Override
                        public Integer execute(ITimePeriod arg) {
                            return count.incrementAndGet();
                        }
                    });

            assertThat(Iterables.size(results)).isEqualTo(count.get());
        }
    }
}
