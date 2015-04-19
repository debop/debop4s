package debop4s.timeperiod.tests.tools;

import debop4s.core.Action1;
import debop4s.core.testing.Testing;
import debop4s.timeperiod.TimeSpec;
import debop4s.timeperiod.tests.TimePeriodTestBase;
import debop4s.timeperiod.utils.Times;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static debop4s.timeperiod.utils.Times.*;
import static org.fest.assertions.Assertions.assertThat;

/**
 * debop4s.timeperiod.test.tools.TimesTrimTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 5. 24. 오후 4:50
 */
@Slf4j
public class TimesTrimTest extends TimePeriodTestBase {

    @Test
    public void trimMonthTest() {
        assertThat(trimToMonth(testDate)).isEqualTo(asDate(testDate.getYear(), 1, 1));

        Testing.runAction(TimeSpec.MonthsPerYear,
                          new Action1<Integer>() {
                              @Override
                              public void perform(Integer m) {
                                  assertThat(Times.trimToMonth(testDate, m + 1)).isEqualTo(asDate(testDate.getYear(), m + 1, 1));
                              }
                          }
                         );
    }

    @Test
    public void trimDayTest() {
        assertThat(trimToDay(testDate)).isEqualTo(asDate(testDate.getYear(), testDate.getMonthOfYear(), 1));

        Testing.runAction(daysInMonth(testDate.getYear(), testDate.getMonthOfYear()),
                          new Action1<Integer>() {
                              @Override
                              public void perform(Integer day) {
                                  assertThat(trimToDay(testDate, day + 1)).isEqualTo(asDate(testDate.getYear(), testDate.getMonthOfYear(), day + 1));
                              }
                          }
                         );
    }

    @Test
    public void trimHourTest() {
        assertThat(trimToHour(testDate)).isEqualTo(getDate(testDate));

        Testing.runAction(TimeSpec.HoursPerDay,
                          new Action1<Integer>() {
                              @Override
                              public void perform(Integer h) {
                                  assertThat(Times.trimToHour(testDate, h))
                                          .isEqualTo(getDate(testDate).plusHours(h));
                              }
                          }
                         );
    }

    @Test
    public void trimMimuteTest() {
        assertThat(trimToMinute(testDate)).isEqualTo(getDate(testDate).plusHours(testDate.getHourOfDay()));

        Testing.runAction(TimeSpec.MinutesPerHour,
                          new Action1<Integer>() {
                              @Override
                              public void perform(Integer m) {
                                  assertThat(trimToMinute(testDate, m))
                                          .isEqualTo(getDate(testDate).plusHours(testDate.getHourOfDay()).plusMinutes(m));
                              }
                          }
                         );
    }

    @Test
    public void trimSecondTest() {
        assertThat(trimToSecond(testDate)).isEqualTo(testDate.withTime(testDate.getHourOfDay(),
                                                                       testDate.getMinuteOfHour(),
                                                                       0,
                                                                       0));

        Testing.runAction(TimeSpec.SecondsPerMinute,
                          new Action1<Integer>() {
                              @Override
                              public void perform(Integer s) {
                                  assertThat(Times.trimToSecond(testDate, s))
                                          .isEqualTo(testDate.withTime(testDate.getHourOfDay(),
                                                                       testDate.getMinuteOfHour(),
                                                                       s,
                                                                       0));
                              }
                          }
                         );
    }

    @Test
    public void trimMillisTest() {
        assertThat(trimToMillis(testDate)).isEqualTo(testDate.withTime(testDate.getHourOfDay(),
                                                                       testDate.getMinuteOfHour(),
                                                                       testDate.getSecondOfMinute(),
                                                                       0));

        Testing.runAction(TimeSpec.MillisPerSecond,
                          new Action1<Integer>() {
                              @Override
                              public void perform(Integer ms) {
                                  assertThat(trimToMillis(testDate, ms))
                                          .isEqualTo(testDate.withTime(testDate.getHourOfDay(),
                                                                       testDate.getMinuteOfHour(),
                                                                       testDate.getSecondOfMinute(),
                                                                       ms));
                              }
                          }
                         );
    }

}
