package debop4s.timeperiod.tests.tools;

import debop4s.core.JAction1;
import debop4s.core.parallels.JParallels;
import debop4s.timeperiod.TimeSpec;
import debop4s.timeperiod.YearWeek;
import debop4s.timeperiod.tests.TimePeriodTestBase;
import debop4s.timeperiod.timerange.WeekRange;
import debop4s.timeperiod.utils.Times;
import debop4s.timeperiod.utils.Weeks;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * debop4s.timeperiod.test.tools.WeeksFunSuite
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 5. 25. 오후 12:55
 */
@Slf4j
public class WeeksTest extends TimePeriodTestBase {

    public static final DateTime[] testTimes = new DateTime[] {
            Times.asDate(2003, 12, 28)
    };

    @Test
    public void getYearAndWeek() {
        for (DateTime moment : testTimes) {
            YearWeek yw = Weeks.yearAndWeek(moment);
            assertThat(yw.weekyear()).isEqualTo(moment.getWeekyear());
            assertThat(yw.weekOfWeekyear()).isEqualTo(moment.getWeekOfWeekyear());
        }
    }

    @Test
    public void getYearAndWeekTest() {

        JParallels.run(2000, 2100, new JAction1<Integer>() {
            @Override
            public void perform(Integer year) {
                DateTime startDay = Times.startTimeOfYear(year);
                DateTime endDay = Times.endTimeOfYear(year - 1);

                YearWeek startYW = Weeks.yearAndWeek(startDay);
                YearWeek endYW = Weeks.yearAndWeek(endDay);

                if (startDay.getDayOfWeek() == TimeSpec.FirstDayOfWeek.getValue())
                    assertThat(endYW.equals(startYW)).isFalse();
                else
                    assertThat(endYW.equals(startYW)).isTrue();
            }
        });
    }

    @Test
    public void getStartWeekRangeOfYear() {
        JParallels.run(2000, 2100, new JAction1<Integer>() {
            @Override
            public void perform(Integer year) {
                WeekRange startWeekRange = Weeks.startWeekRangeOfYear(year);


                log.trace("year=[{}], startWeek=[{}]", year, startWeekRange.startDayStart());

                assertThat(new Duration(Times.asDate(year - 1, 12, 28), startWeekRange.startDayStart()).getStandardDays()).isGreaterThan(0);
                assertThat(new Duration(Times.asDate(year, 1, 3), startWeekRange.endDayStart()).getStandardDays()).isGreaterThan(0);
            }
        });
    }

    @Test
    public void getEndYearAndWeekTest() {
        JParallels.run(1980, 2200, new JAction1<Integer>() {
            @Override
            public void perform(Integer year) {
                YearWeek yw = Weeks.endYearAndWeek(year);

                assertThat(year).isEqualTo(yw.weekyear());
                assertThat(yw.weekOfWeekyear()).isGreaterThanOrEqualTo(52);
            }
        });
    }

    @Test
    public void getEndWeekRangeOfYear() {
        JParallels.run(2000, 2100, new JAction1<Integer>() {
            @Override
            public void perform(Integer year) {
                WeekRange startWeekRange = Weeks.startWeekRangeOfYear(year);
                WeekRange endWeekRange = Weeks.endWeekRangeOfYear(year - 1);


                log.trace("year=[{}], startWeek=[{}], endWeek=[{}]",
                          year, startWeekRange.startDayStart(), endWeekRange.startDayStart());

                assertThat(new Duration(Times.asDate(year - 1, 12, 28), startWeekRange.startDayStart()).getStandardDays()).isGreaterThan(0);
                assertThat(new Duration(Times.asDate(year, 1, 3), startWeekRange.endDayStart()).getStandardDays()).isGreaterThan(0);

                assertThat(endWeekRange.startDayStart().plusWeeks(1)).isEqualTo(startWeekRange.startDayStart());
                assertThat(endWeekRange.endDayStart().plusDays(1)).isEqualTo(startWeekRange.startDayStart());

            }
        });
    }

    @Test
    public void getWeekRangeTest() {
        JParallels.run(2000, 2100, new JAction1<Integer>() {
            @Override
            public void perform(Integer year) {
                DateTime endDay = Times.endTimeOfYear(year - 1);
                DateTime startDay = Times.startTimeOfYear(year);

                YearWeek endDayYearWeek = Weeks.yearAndWeek(endDay);
                assertThat(endDayYearWeek.weekyear()).isGreaterThanOrEqualTo(year - 1);

                YearWeek startDayYearWeek = Weeks.yearAndWeek(startDay);
                assertThat(startDayYearWeek.weekyear()).isLessThanOrEqualTo(year);

                // 해당일자가 속한 주차의 일자들을 구한다. 년말/년초 구간은 꼭 7일이 아닐 수 있다.
                WeekRange endDayWeekRange = Weeks.weekRange(endDayYearWeek);
                WeekRange startDayWeekRange = Weeks.weekRange(startDayYearWeek);

                assertThat(endDayWeekRange.hasPeriod()).isTrue();
                assertThat(startDayWeekRange.hasPeriod()).isTrue();

                log.trace("start day weeksView=[{}]", startDayWeekRange);

                if (endDayYearWeek.equals(startDayYearWeek)) {
                    assertThat(startDayWeekRange).isEqualTo(endDayWeekRange);
                } else {
                    assertThat(startDayWeekRange).isNotEqualTo(endDayWeekRange);
                }
            }
        });
    }

    @Test
    public void addWeekOfYearsTest() {
        JParallels.run(2000, 2100, new JAction1<Integer>() {
            @Override
            public void perform(Integer year) {

                final int step = 2;
                final int maxAddWeeks = 40;

                YearWeek prevResult = null;
                YearWeek maxWeek = Weeks.endYearAndWeek(year);

                for (int week = 1; week < maxWeek.weekOfWeekyear(); week += step) {
                    for (int addWeeks = -maxAddWeeks; addWeeks <= maxAddWeeks; addWeeks += step) {
                        YearWeek current = new YearWeek(year, week);
                        YearWeek result = Weeks.addWeekOfYears(current, addWeeks);

                        if (addWeeks != 0 && prevResult != null) {
                            if (result.weekyear() == prevResult.weekyear())
                                assertThat(result.weekOfWeekyear()).isEqualTo(prevResult.weekOfWeekyear() + step);
                        }

                        assertThat(result.weekOfWeekyear()).isGreaterThan(0);
                        assertThat(result.weekOfWeekyear()).isLessThanOrEqualTo(TimeSpec.MaxWeeksPerYear);

                        prevResult = result;
                    }
                }
            }
        });
    }
}
