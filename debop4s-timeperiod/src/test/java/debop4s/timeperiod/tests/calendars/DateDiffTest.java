package debop4s.timeperiod.tests.calendars;

import debop4s.timeperiod.TimeSpec;
import debop4s.timeperiod.calendars.DateDiff;
import debop4s.timeperiod.tests.TimePeriodTestBase;
import debop4s.timeperiod.utils.Durations;
import debop4s.timeperiod.utils.Times;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * debop4s.timeperiod.test.calendars.DateDiffFunSuite
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 5. 26. 오후 6:55
 */
@Slf4j
public class DateDiffTest extends TimePeriodTestBase {

    @Test
    public void emptyDataDiff() {
        DateTime now = Times.now();
        DateDiff dateDiff = new DateDiff(now, now);

        assertThat(dateDiff.isEmpty()).isTrue();
        assertThat(dateDiff.difference()).isEqualTo(Durations.Zero());

        assertThat(dateDiff.years()).isEqualTo(0);
        assertThat(dateDiff.quarters()).isEqualTo(0);
        assertThat(dateDiff.months()).isEqualTo(0);
        assertThat(dateDiff.weeks()).isEqualTo(0);
        assertThat(dateDiff.days()).isEqualTo(0);
        assertThat(dateDiff.hours()).isEqualTo(0);
        assertThat(dateDiff.minutes()).isEqualTo(0);
        assertThat(dateDiff.seconds()).isEqualTo(0);

        assertThat(dateDiff.elapsedYears()).isEqualTo(0);
        assertThat(dateDiff.elapsedMonths()).isEqualTo(0);
        assertThat(dateDiff.elapsedDays()).isEqualTo(0);
        assertThat(dateDiff.elapsedHours()).isEqualTo(0);
        assertThat(dateDiff.elapsedMinutes()).isEqualTo(0);
        assertThat(dateDiff.elapsedSeconds()).isEqualTo(0);
    }

    @Test
    public void differenceTest() {
        DateTime date1 = new DateTime(2008, 10, 12, 15, 32, 44, 243);
        DateTime date2 = new DateTime(2010, 1, 3, 23, 22, 9, 345);

        DateDiff dateDiff = new DateDiff(date1, date2);

        assertThat(dateDiff.difference()).isEqualTo(new Duration(date1, date2));
    }

    @Test
    public void yearsTest() {
        int[] years = new int[] { 1, 3, 15 };

        for (int year : years) {
            DateTime date1 = Times.now();
            DateTime date2 = date1.plusYears(year);
            DateTime date3 = date1.plusYears(-year);

            log.trace("date1=[{}], date2=[{}], date3=[{}]", date1, date2, date3);

            DateDiff dateDiff12 = new DateDiff(date1, date2);

            assertThat(dateDiff12.elapsedYears()).isEqualTo(year);
            assertThat(dateDiff12.elapsedMonths()).isEqualTo(0);
            assertThat(dateDiff12.elapsedDays()).isEqualTo(0);
            assertThat(dateDiff12.elapsedHours()).isEqualTo(0);
            assertThat(dateDiff12.elapsedMinutes()).isEqualTo(0);
            assertThat(dateDiff12.elapsedSeconds()).isEqualTo(0);

            assertThat(dateDiff12.years()).isEqualTo(year);
            assertThat(dateDiff12.quarters()).isEqualTo(year * TimeSpec.QuartersPerYear);
            assertThat(dateDiff12.months()).isEqualTo(year * TimeSpec.MonthsPerYear);

            int date12Days = (int) Durations.create(date1, date2).getStandardDays();

            assertThat(dateDiff12.days()).isEqualTo(date12Days);
            assertThat(dateDiff12.hours()).isEqualTo(date12Days * TimeSpec.HoursPerDay);
            assertThat(dateDiff12.minutes()).isEqualTo(date12Days * TimeSpec.HoursPerDay * TimeSpec.MinutesPerHour);
            assertThat(dateDiff12.seconds()).isEqualTo(date12Days * TimeSpec.HoursPerDay * TimeSpec.MinutesPerHour * TimeSpec.SecondsPerMinute);

            DateDiff dateDiff13 = new DateDiff(date1, date3);

            assertThat(dateDiff13.elapsedYears()).isEqualTo(-year);
            assertThat(dateDiff13.elapsedMonths()).isEqualTo(0);
            assertThat(dateDiff13.elapsedDays()).isEqualTo(0);
            assertThat(dateDiff13.elapsedHours()).isEqualTo(0);
            assertThat(dateDiff13.elapsedMinutes()).isEqualTo(0);
            assertThat(dateDiff13.elapsedSeconds()).isEqualTo(0);

            assertThat(dateDiff13.years()).isEqualTo(-year);
            assertThat(dateDiff13.quarters()).isEqualTo(-year * TimeSpec.QuartersPerYear);
            assertThat(dateDiff13.months()).isEqualTo(-year * TimeSpec.MonthsPerYear);

            int date13Days = (int) Durations.create(date1, date3).getStandardDays();

            assertThat(dateDiff13.days()).isEqualTo(date13Days);
            assertThat(dateDiff13.hours()).isEqualTo(date13Days * TimeSpec.HoursPerDay);
            assertThat(dateDiff13.minutes()).isEqualTo(date13Days * TimeSpec.HoursPerDay * TimeSpec.MinutesPerHour);
            assertThat(dateDiff13.seconds()).isEqualTo(date13Days * TimeSpec.HoursPerDay * TimeSpec.MinutesPerHour * TimeSpec.SecondsPerMinute);
        }
    }

    @Test
    public void quartersTest() {
        DateTime date1 = new DateTime(2011, 5, 15, 15, 32, 44, 245);
        DateTime date2 = date1.plusMonths(TimeSpec.MonthsPerQuarter);
        DateTime date3 = date1.plusMonths(-TimeSpec.MonthsPerQuarter);

        DateDiff dateDiff12 = new DateDiff(date1, date2);
        int days12 = (int) Durations.create(date1, date2).getStandardDays();

        assertThat(dateDiff12.elapsedYears()).isEqualTo(0);
        assertThat(dateDiff12.elapsedMonths()).isEqualTo(TimeSpec.MonthsPerQuarter);
        assertThat(dateDiff12.elapsedDays()).isEqualTo(0);
        assertThat(dateDiff12.elapsedHours()).isEqualTo(0);
        assertThat(dateDiff12.elapsedMinutes()).isEqualTo(0);
        assertThat(dateDiff12.elapsedSeconds()).isEqualTo(0);

        assertThat(dateDiff12.years()).isEqualTo(0);
        assertThat(dateDiff12.quarters()).isEqualTo(1);
        assertThat(dateDiff12.months()).isEqualTo(TimeSpec.MonthsPerQuarter);
        assertThat(dateDiff12.weeks()).isEqualTo(14);
        assertThat(dateDiff12.days()).isEqualTo(days12);
        assertThat(dateDiff12.hours()).isEqualTo(days12 * TimeSpec.HoursPerDay);
        assertThat(dateDiff12.minutes()).isEqualTo(days12 * TimeSpec.HoursPerDay * TimeSpec.MinutesPerHour);
        assertThat(dateDiff12.seconds()).isEqualTo(days12 * TimeSpec.HoursPerDay * TimeSpec.MinutesPerHour * TimeSpec.SecondsPerMinute);

        DateDiff dateDiff13 = new DateDiff(date1, date3);
        int days13 = (int) Durations.create(date1, date3).getStandardDays();

        assertThat(dateDiff13.elapsedYears()).isEqualTo(0);
        assertThat(dateDiff13.elapsedMonths()).isEqualTo(-TimeSpec.MonthsPerQuarter);
        assertThat(dateDiff13.elapsedDays()).isEqualTo(0);
        assertThat(dateDiff13.elapsedHours()).isEqualTo(0);
        assertThat(dateDiff13.elapsedMinutes()).isEqualTo(0);
        assertThat(dateDiff13.elapsedSeconds()).isEqualTo(0);

        assertThat(dateDiff13.years()).isEqualTo(0);
        assertThat(dateDiff13.quarters()).isEqualTo(-1);
        assertThat(dateDiff13.months()).isEqualTo(-TimeSpec.MonthsPerQuarter);
        assertThat(dateDiff13.weeks()).isEqualTo(-12);
        assertThat(dateDiff13.days()).isEqualTo(days13);
        assertThat(dateDiff13.hours()).isEqualTo(days13 * TimeSpec.HoursPerDay);
        assertThat(dateDiff13.minutes()).isEqualTo(days13 * TimeSpec.HoursPerDay * TimeSpec.MinutesPerHour);
        assertThat(dateDiff13.seconds()).isEqualTo(days13 * TimeSpec.HoursPerDay * TimeSpec.MinutesPerHour * TimeSpec.SecondsPerMinute);
    }

    @Test
    public void monthsTest() {
        DateTime date1 = new DateTime(2011, 5, 15, 15, 32, 44, 245);
        DateTime date2 = date1.plusMonths(1);
        DateTime date3 = date1.plusMonths(-1);

        DateDiff dateDiff12 = new DateDiff(date1, date2);
        int days12 = (int) Durations.create(date1, date2).getStandardDays();

        assertThat(dateDiff12.elapsedYears()).isEqualTo(0);
        assertThat(dateDiff12.elapsedMonths()).isEqualTo(1);
        assertThat(dateDiff12.elapsedDays()).isEqualTo(0);
        assertThat(dateDiff12.elapsedHours()).isEqualTo(0);
        assertThat(dateDiff12.elapsedMinutes()).isEqualTo(0);
        assertThat(dateDiff12.elapsedSeconds()).isEqualTo(0);

        assertThat(dateDiff12.years()).isEqualTo(0);
        assertThat(dateDiff12.quarters()).isEqualTo(0);
        assertThat(dateDiff12.months()).isEqualTo(1);
        assertThat(dateDiff12.weeks()).isEqualTo(5);
        assertThat(dateDiff12.days()).isEqualTo(days12);
        assertThat(dateDiff12.hours()).isEqualTo(days12 * TimeSpec.HoursPerDay);
        assertThat(dateDiff12.minutes()).isEqualTo(days12 * TimeSpec.HoursPerDay * TimeSpec.MinutesPerHour);
        assertThat(dateDiff12.seconds()).isEqualTo(days12 * TimeSpec.HoursPerDay * TimeSpec.MinutesPerHour * TimeSpec.SecondsPerMinute);

        DateDiff dateDiff13 = new DateDiff(date1, date3);
        int days13 = (int) Durations.create(date1, date3).getStandardDays();

        assertThat(dateDiff13.elapsedYears()).isEqualTo(0);
        assertThat(dateDiff13.elapsedMonths()).isEqualTo(-1);
        assertThat(dateDiff13.elapsedDays()).isEqualTo(0);
        assertThat(dateDiff13.elapsedHours()).isEqualTo(0);
        assertThat(dateDiff13.elapsedMinutes()).isEqualTo(0);
        assertThat(dateDiff13.elapsedSeconds()).isEqualTo(0);

        assertThat(dateDiff13.years()).isEqualTo(0);
        assertThat(dateDiff13.quarters()).isEqualTo(0);
        assertThat(dateDiff13.months()).isEqualTo(-1);
        assertThat(dateDiff13.weeks()).isEqualTo(-4);
        assertThat(dateDiff13.days()).isEqualTo(days13);
        assertThat(dateDiff13.hours()).isEqualTo(days13 * TimeSpec.HoursPerDay);
        assertThat(dateDiff13.minutes()).isEqualTo(days13 * TimeSpec.HoursPerDay * TimeSpec.MinutesPerHour);
        assertThat(dateDiff13.seconds()).isEqualTo(days13 * TimeSpec.HoursPerDay * TimeSpec.MinutesPerHour * TimeSpec.SecondsPerMinute);
    }

    @Test
    public void weekTest() {
        DateTime date1 = new DateTime(2011, 5, 15, 15, 32, 44, 245);
        DateTime date2 = date1.plusWeeks(1);
        DateTime date3 = date1.plusWeeks(-1);

        DateDiff dateDiff12 = new DateDiff(date1, date2);
        int days12 = (int) Durations.create(date1, date2).getStandardDays();

        assertThat(dateDiff12.years()).isEqualTo(0);
        assertThat(dateDiff12.quarters()).isEqualTo(0);
        assertThat(dateDiff12.months()).isEqualTo(0);
        assertThat(dateDiff12.weeks()).isEqualTo(1);
        assertThat(dateDiff12.days()).isEqualTo(days12);
        assertThat(dateDiff12.hours()).isEqualTo(days12 * TimeSpec.HoursPerDay);
        assertThat(dateDiff12.minutes()).isEqualTo(days12 * TimeSpec.HoursPerDay * TimeSpec.MinutesPerHour);
        assertThat(dateDiff12.seconds()).isEqualTo(days12 * TimeSpec.HoursPerDay * TimeSpec.MinutesPerHour * TimeSpec.SecondsPerMinute);

        DateDiff dateDiff13 = new DateDiff(date1, date3);
        int days13 = (int) Durations.create(date1, date3).getStandardDays();

        assertThat(dateDiff13.years()).isEqualTo(0);
        assertThat(dateDiff13.quarters()).isEqualTo(0);
        assertThat(dateDiff13.months()).isEqualTo(0);
        assertThat(dateDiff13.weeks()).isEqualTo(-1);
        assertThat(dateDiff13.days()).isEqualTo(days13);
        assertThat(dateDiff13.hours()).isEqualTo(days13 * TimeSpec.HoursPerDay);
        assertThat(dateDiff13.minutes()).isEqualTo(days13 * TimeSpec.HoursPerDay * TimeSpec.MinutesPerHour);
        assertThat(dateDiff13.seconds()).isEqualTo(days13 * TimeSpec.HoursPerDay * TimeSpec.MinutesPerHour * TimeSpec.SecondsPerMinute);
    }

    @Test
    public void daysTest() {
        int[] days = new int[] { 1, 3 };

        for (int day : days) {

            DateTime date1 = new DateTime(2011, 5, 19, 15, 32, 44, 245);
            DateTime date2 = date1.plusDays(day);
            DateTime date3 = date1.plusDays(-day);

            DateDiff dateDiff12 = new DateDiff(date1, date2);
            int days12 = (int) Durations.create(date1, date2).getStandardDays();

            assertThat(dateDiff12.elapsedYears()).isEqualTo(0);
            assertThat(dateDiff12.elapsedMonths()).isEqualTo(0);
            assertThat(dateDiff12.elapsedDays()).isEqualTo(day);
            assertThat(dateDiff12.elapsedHours()).isEqualTo(0);
            assertThat(dateDiff12.elapsedMinutes()).isEqualTo(0);
            assertThat(dateDiff12.elapsedSeconds()).isEqualTo(0);

            assertThat(dateDiff12.years()).isEqualTo(0);
            assertThat(dateDiff12.quarters()).isEqualTo(0);
            assertThat(dateDiff12.months()).isEqualTo(0);
            assertThat(dateDiff12.weeks()).isEqualTo(0);
            assertThat(dateDiff12.days()).isEqualTo(days12);
            assertThat(dateDiff12.hours()).isEqualTo(days12 * TimeSpec.HoursPerDay);
            assertThat(dateDiff12.minutes()).isEqualTo(days12 * TimeSpec.HoursPerDay * TimeSpec.MinutesPerHour);
            assertThat(dateDiff12.seconds()).isEqualTo(days12 * TimeSpec.HoursPerDay * TimeSpec.MinutesPerHour * TimeSpec.SecondsPerMinute);

            DateDiff dateDiff13 = new DateDiff(date1, date3);
            int days13 = (int) Durations.create(date1, date3).getStandardDays();

            assertThat(dateDiff13.elapsedYears()).isEqualTo(0);
            assertThat(dateDiff13.elapsedMonths()).isEqualTo(0);
            assertThat(dateDiff13.elapsedDays()).isEqualTo(-day);
            assertThat(dateDiff13.elapsedHours()).isEqualTo(0);
            assertThat(dateDiff13.elapsedMinutes()).isEqualTo(0);
            assertThat(dateDiff13.elapsedSeconds()).isEqualTo(0);

            assertThat(dateDiff13.years()).isEqualTo(0);
            assertThat(dateDiff13.quarters()).isEqualTo(0);
            assertThat(dateDiff13.months()).isEqualTo(0);
            assertThat(dateDiff13.weeks()).isEqualTo(0);
            assertThat(dateDiff13.days()).isEqualTo(days13);
            assertThat(dateDiff13.hours()).isEqualTo(days13 * TimeSpec.HoursPerDay);
            assertThat(dateDiff13.minutes()).isEqualTo(days13 * TimeSpec.HoursPerDay * TimeSpec.MinutesPerHour);
            assertThat(dateDiff13.seconds()).isEqualTo(days13 * TimeSpec.HoursPerDay * TimeSpec.MinutesPerHour * TimeSpec.SecondsPerMinute);
        }
    }

    @Test
    public void hoursTest() {

        int[] hours = new int[] { 1, 3, 5 };

        for (int hour : hours) {

            DateTime date1 = new DateTime(2011, 5, 19, 15, 32, 44, 245);
            DateTime date2 = date1.plusHours(hour);
            DateTime date3 = date1.plusHours(-hour);

            DateDiff dateDiff12 = new DateDiff(date1, date2);
            int hours12 = (int) Durations.create(date1, date2).getStandardHours();

            assertThat(dateDiff12.elapsedYears()).isEqualTo(0);
            assertThat(dateDiff12.elapsedMonths()).isEqualTo(0);
            assertThat(dateDiff12.elapsedDays()).isEqualTo(0);
            assertThat(dateDiff12.elapsedHours()).isEqualTo(hour);
            assertThat(dateDiff12.elapsedMinutes()).isEqualTo(0);
            assertThat(dateDiff12.elapsedSeconds()).isEqualTo(0);

            assertThat(dateDiff12.years()).isEqualTo(0);
            assertThat(dateDiff12.quarters()).isEqualTo(0);
            assertThat(dateDiff12.months()).isEqualTo(0);
            assertThat(dateDiff12.weeks()).isEqualTo(0);
            assertThat(dateDiff12.days()).isEqualTo(0);
            assertThat(dateDiff12.hours()).isEqualTo(hours12);
            assertThat(dateDiff12.minutes()).isEqualTo(hours12 * TimeSpec.MinutesPerHour);
            assertThat(dateDiff12.seconds()).isEqualTo(hours12 * TimeSpec.MinutesPerHour * TimeSpec.SecondsPerMinute);

            DateDiff dateDiff13 = new DateDiff(date1, date3);
            int hours13 = (int) Durations.create(date1, date3).getStandardHours();

            assertThat(dateDiff13.elapsedYears()).isEqualTo(0);
            assertThat(dateDiff13.elapsedMonths()).isEqualTo(0);
            assertThat(dateDiff13.elapsedDays()).isEqualTo(0);
            assertThat(dateDiff13.elapsedHours()).isEqualTo(-hour);
            assertThat(dateDiff13.elapsedMinutes()).isEqualTo(0);
            assertThat(dateDiff13.elapsedSeconds()).isEqualTo(0);

            assertThat(dateDiff13.years()).isEqualTo(0);
            assertThat(dateDiff13.quarters()).isEqualTo(0);
            assertThat(dateDiff13.months()).isEqualTo(0);
            assertThat(dateDiff13.weeks()).isEqualTo(0);
            assertThat(dateDiff13.days()).isEqualTo(0);
            assertThat(dateDiff13.hours()).isEqualTo(hours13);
            assertThat(dateDiff13.minutes()).isEqualTo(hours13 * TimeSpec.MinutesPerHour);
            assertThat(dateDiff13.seconds()).isEqualTo(hours13 * TimeSpec.MinutesPerHour * TimeSpec.SecondsPerMinute);
        }
    }

    @Test
    public void minutesTest() {

        int[] minutes = new int[] { 1, 3, 5 };

        for (int minute : minutes) {

            DateTime date1 = new DateTime(2011, 5, 19, 15, 32, 44, 245);
            DateTime date2 = date1.plusMinutes(minute);
            DateTime date3 = date1.plusMinutes(-minute);

            DateDiff dateDiff12 = new DateDiff(date1, date2);
            int minute12 = (int) Durations.create(date1, date2).getStandardMinutes();

            assertThat(dateDiff12.elapsedYears()).isEqualTo(0);
            assertThat(dateDiff12.elapsedMonths()).isEqualTo(0);
            assertThat(dateDiff12.elapsedDays()).isEqualTo(0);
            assertThat(dateDiff12.elapsedHours()).isEqualTo(0);
            assertThat(dateDiff12.elapsedMinutes()).isEqualTo(minute);
            assertThat(dateDiff12.elapsedSeconds()).isEqualTo(0);

            assertThat(dateDiff12.years()).isEqualTo(0);
            assertThat(dateDiff12.quarters()).isEqualTo(0);
            assertThat(dateDiff12.months()).isEqualTo(0);
            assertThat(dateDiff12.weeks()).isEqualTo(0);
            assertThat(dateDiff12.days()).isEqualTo(0);
            assertThat(dateDiff12.hours()).isEqualTo(0);
            assertThat(dateDiff12.minutes()).isEqualTo(minute12);
            assertThat(dateDiff12.seconds()).isEqualTo(minute12 * TimeSpec.SecondsPerMinute);

            DateDiff dateDiff13 = new DateDiff(date1, date3);
            int minute13 = (int) Durations.create(date1, date3).getStandardMinutes();

            assertThat(dateDiff13.elapsedYears()).isEqualTo(0);
            assertThat(dateDiff13.elapsedMonths()).isEqualTo(0);
            assertThat(dateDiff13.elapsedDays()).isEqualTo(0);
            assertThat(dateDiff13.elapsedHours()).isEqualTo(0);
            assertThat(dateDiff13.elapsedMinutes()).isEqualTo(-minute);
            assertThat(dateDiff13.elapsedSeconds()).isEqualTo(0);

            assertThat(dateDiff13.years()).isEqualTo(0);
            assertThat(dateDiff13.quarters()).isEqualTo(0);
            assertThat(dateDiff13.months()).isEqualTo(0);
            assertThat(dateDiff13.weeks()).isEqualTo(0);
            assertThat(dateDiff13.days()).isEqualTo(0);
            assertThat(dateDiff13.hours()).isEqualTo(0);
            assertThat(dateDiff13.minutes()).isEqualTo(minute13);
            assertThat(dateDiff13.seconds()).isEqualTo(minute13 * TimeSpec.SecondsPerMinute);
        }
    }


    @Test
    public void secondsTest() {

        int[] seconds = new int[] { 1, 3, 5 };

        for (int second : seconds) {

            DateTime date1 = new DateTime(2011, 5, 19, 15, 32, 44, 245);
            DateTime date2 = date1.plusSeconds(second);
            DateTime date3 = date1.plusSeconds(-second);

            DateDiff dateDiff12 = new DateDiff(date1, date2);
            int second12 = (int) Durations.create(date1, date2).getStandardSeconds();

            assertThat(dateDiff12.elapsedYears()).isEqualTo(0);
            assertThat(dateDiff12.elapsedMonths()).isEqualTo(0);
            assertThat(dateDiff12.elapsedDays()).isEqualTo(0);
            assertThat(dateDiff12.elapsedHours()).isEqualTo(0);
            assertThat(dateDiff12.elapsedMinutes()).isEqualTo(0);
            assertThat(dateDiff12.elapsedSeconds()).isEqualTo(second);

            assertThat(dateDiff12.years()).isEqualTo(0);
            assertThat(dateDiff12.quarters()).isEqualTo(0);
            assertThat(dateDiff12.months()).isEqualTo(0);
            assertThat(dateDiff12.weeks()).isEqualTo(0);
            assertThat(dateDiff12.days()).isEqualTo(0);
            assertThat(dateDiff12.hours()).isEqualTo(0);
            assertThat(dateDiff12.minutes()).isEqualTo(0);
            assertThat(dateDiff12.seconds()).isEqualTo(second12);

            DateDiff dateDiff13 = new DateDiff(date1, date3);
            int second13 = (int) Durations.create(date1, date3).getStandardSeconds();

            assertThat(dateDiff13.elapsedYears()).isEqualTo(0);
            assertThat(dateDiff13.elapsedMonths()).isEqualTo(0);
            assertThat(dateDiff13.elapsedDays()).isEqualTo(0);
            assertThat(dateDiff13.elapsedHours()).isEqualTo(0);
            assertThat(dateDiff13.elapsedMinutes()).isEqualTo(0);
            assertThat(dateDiff13.elapsedSeconds()).isEqualTo(-second);

            assertThat(dateDiff13.years()).isEqualTo(0);
            assertThat(dateDiff13.quarters()).isEqualTo(0);
            assertThat(dateDiff13.months()).isEqualTo(0);
            assertThat(dateDiff13.weeks()).isEqualTo(0);
            assertThat(dateDiff13.days()).isEqualTo(0);
            assertThat(dateDiff13.hours()).isEqualTo(0);
            assertThat(dateDiff13.minutes()).isEqualTo(0);
            assertThat(dateDiff13.seconds()).isEqualTo(second13);
        }
    }

    @Test
    public void positiveDurationTest() {
        int[] diffs = new int[] { 1, 3, 5 };

        for (int diff : diffs) {
            DateTime date1 = Times.now();
            DateTime date2 = date1.plusYears(diff).plusMonths(diff).plusDays(diff).plusHours(diff).plusMinutes(diff).plusSeconds(diff);
            DateTime date3 = date1.minusYears(diff).minusMonths(diff).minusDays(diff).minusHours(diff).minusMinutes(diff).minusSeconds(diff);

            DateDiff dateDiff12 = new DateDiff(date1, date2);

            assertThat(dateDiff12.elapsedYears()).isEqualTo(diff);
            assertThat(dateDiff12.elapsedMonths()).isEqualTo(diff);
            assertThat(dateDiff12.elapsedDays()).isEqualTo(diff);
            assertThat(dateDiff12.elapsedHours()).isEqualTo(diff);
            assertThat(dateDiff12.elapsedMinutes()).isEqualTo(diff);
            assertThat(dateDiff12.elapsedSeconds()).isEqualTo(diff);

            DateDiff dateDiff13 = new DateDiff(date1, date3);

            assertThat(dateDiff13.elapsedYears()).isEqualTo(-diff);
            assertThat(dateDiff13.elapsedMonths()).isEqualTo(-diff);
            assertThat(dateDiff13.elapsedDays()).isEqualTo(-diff);
            assertThat(dateDiff13.elapsedHours()).isEqualTo(-diff);
            assertThat(dateDiff13.elapsedMinutes()).isEqualTo(-diff);
            assertThat(dateDiff13.elapsedSeconds()).isEqualTo(-diff);

        }
    }
}
