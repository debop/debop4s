package debop4s.timeperiod.tests.tools;

import debop4s.timeperiod.*;
import debop4s.timeperiod.tests.TimePeriodTestBase;
import debop4s.timeperiod.utils.Times;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Test;

import static debop4s.timeperiod.DayOfWeek.*;
import static org.fest.assertions.Assertions.assertThat;

/**
 * debop4s.timeperiod.test.tools.TimesCalendarTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 5. 21. 오후 7:00
 */
@Slf4j
public class TimesCalendarTest extends TimePeriodTestBase {

    @Test
    public void getYearOfTest() {
        assertThat(Times.yearOf(new DateTime(2000, 1, 1, 0, 0))).isEqualTo(2000);
        assertThat(Times.yearOf(new DateTime(2000, 4, 1, 0, 0))).isEqualTo(2000);
        assertThat(Times.yearOf(2000, 12)).isEqualTo(2000);

        assertThat(Times.yearOf(testNow)).isEqualTo(testNow.getYear());
    }

    @Test
    public void nextHalfyear() {
        assertThat(Times.nextHalfyear(2000, Halfyear.First)).isEqualTo(new YearHalfyear(2000, Halfyear.Second));
        assertThat(Times.nextHalfyear(2000, Halfyear.Second)).isEqualTo(new YearHalfyear(2001, Halfyear.First));
    }

    @Test
    public void prevHalfyear() {
        assertThat(Times.prevHalfyear(2000, Halfyear.First)).isEqualTo(new YearHalfyear(1999, Halfyear.Second));
        assertThat(Times.prevHalfyear(2000, Halfyear.Second)).isEqualTo(new YearHalfyear(2000, Halfyear.First));
    }

    @Test
    public void addHalfyear() {
        assertThat(Times.addHalfyear(2000, Halfyear.First, 1).halfyear()).isEqualTo(Halfyear.Second);
        assertThat(Times.addHalfyear(2000, Halfyear.Second, 1).halfyear()).isEqualTo(Halfyear.First);
        assertThat(Times.addHalfyear(2000, Halfyear.First, -1).halfyear()).isEqualTo(Halfyear.Second);
        assertThat(Times.addHalfyear(2000, Halfyear.Second, -1).halfyear()).isEqualTo(Halfyear.First);

        assertThat(Times.addHalfyear(2000, Halfyear.First, 2).halfyear()).isEqualTo(Halfyear.First);
        assertThat(Times.addHalfyear(2000, Halfyear.Second, 2).halfyear()).isEqualTo(Halfyear.Second);
        assertThat(Times.addHalfyear(2000, Halfyear.First, -2).halfyear()).isEqualTo(Halfyear.First);
        assertThat(Times.addHalfyear(2000, Halfyear.Second, -2).halfyear()).isEqualTo(Halfyear.Second);

        assertThat(Times.addHalfyear(2000, Halfyear.First, 5).halfyear()).isEqualTo(Halfyear.Second);
        assertThat(Times.addHalfyear(2000, Halfyear.Second, 5).halfyear()).isEqualTo(Halfyear.First);
        assertThat(Times.addHalfyear(2000, Halfyear.First, -5).halfyear()).isEqualTo(Halfyear.Second);
        assertThat(Times.addHalfyear(2000, Halfyear.Second, -5).halfyear()).isEqualTo(Halfyear.First);

        assertThat(Times.addHalfyear(2008, Halfyear.First, 1).year()).isEqualTo(2008);
        assertThat(Times.addHalfyear(2008, Halfyear.Second, 1).year()).isEqualTo(2009);

        assertThat(Times.addHalfyear(2008, Halfyear.First, -1).year()).isEqualTo(2007);
        assertThat(Times.addHalfyear(2008, Halfyear.Second, -1).year()).isEqualTo(2008);

        assertThat(Times.addHalfyear(2008, Halfyear.First, 2).year()).isEqualTo(2009);
        assertThat(Times.addHalfyear(2008, Halfyear.Second, 2).year()).isEqualTo(2009);

        assertThat(Times.addHalfyear(2008, Halfyear.First, 3).year()).isEqualTo(2009);
        assertThat(Times.addHalfyear(2008, Halfyear.Second, 3).year()).isEqualTo(2010);
    }

    @Test
    public void getHalfyearOfMonthTest() {
        for (int month : TimeSpec.FirstHalfyearMonths)
            assertThat(Times.halfyearOfMonth(month)).isEqualTo(Halfyear.First);

        for (int month : TimeSpec.SecondHalfyearMonths)
            assertThat(Times.halfyearOfMonth(month)).isEqualTo(Halfyear.Second);
    }

    @Test
    public void getMonthsOfHalfyear() {
        assertThat(Times.monthsOfHalfyear(Halfyear.First)).isEqualTo(TimeSpec.FirstHalfyearMonths);
        assertThat(Times.monthsOfHalfyear(Halfyear.Second)).isEqualTo(TimeSpec.SecondHalfyearMonths);
    }

    @Test
    public void nextQuarterTest() {
        assertThat(Times.nextQuarter(2000, Quarter.First).quarter()).isEqualTo(Quarter.Second);
        assertThat(Times.nextQuarter(2000, Quarter.Second).quarter()).isEqualTo(Quarter.Third);
        assertThat(Times.nextQuarter(2000, Quarter.Third).quarter()).isEqualTo(Quarter.Fourth);
        assertThat(Times.nextQuarter(2000, Quarter.Fourth).quarter()).isEqualTo(Quarter.First);
    }

    @Test
    public void previousQuarterTest() {
        assertThat(Times.prevQuarter(2000, Quarter.First).quarter()).isEqualTo(Quarter.Fourth);
        assertThat(Times.prevQuarter(2000, Quarter.Second).quarter()).isEqualTo(Quarter.First);
        assertThat(Times.prevQuarter(2000, Quarter.Third).quarter()).isEqualTo(Quarter.Second);
        assertThat(Times.prevQuarter(2000, Quarter.Fourth).quarter()).isEqualTo(Quarter.Third);
    }

    @Test
    public void addQuarterTest() {

        assertThat(Times.addQuarter(2000, Quarter.First, 1).quarter()).isEqualTo(Quarter.Second);
        assertThat(Times.addQuarter(2000, Quarter.Second, 1).quarter()).isEqualTo(Quarter.Third);
        assertThat(Times.addQuarter(2000, Quarter.Third, 1).quarter()).isEqualTo(Quarter.Fourth);
        assertThat(Times.addQuarter(2000, Quarter.Fourth, 1).quarter()).isEqualTo(Quarter.First);

        assertThat(Times.addQuarter(2000, Quarter.First, -1).quarter()).isEqualTo(Quarter.Fourth);
        assertThat(Times.addQuarter(2000, Quarter.Second, -1).quarter()).isEqualTo(Quarter.First);
        assertThat(Times.addQuarter(2000, Quarter.Third, -1).quarter()).isEqualTo(Quarter.Second);
        assertThat(Times.addQuarter(2000, Quarter.Fourth, -1).quarter()).isEqualTo(Quarter.Third);

        assertThat(Times.addQuarter(2000, Quarter.First, 2).quarter()).isEqualTo(Quarter.Third);
        assertThat(Times.addQuarter(2000, Quarter.Second, 2).quarter()).isEqualTo(Quarter.Fourth);
        assertThat(Times.addQuarter(2000, Quarter.Third, 2).quarter()).isEqualTo(Quarter.First);
        assertThat(Times.addQuarter(2000, Quarter.Fourth, 2).quarter()).isEqualTo(Quarter.Second);

        assertThat(Times.addQuarter(2000, Quarter.First, -2).quarter()).isEqualTo(Quarter.Third);
        assertThat(Times.addQuarter(2000, Quarter.Second, -2).quarter()).isEqualTo(Quarter.Fourth);
        assertThat(Times.addQuarter(2000, Quarter.Third, -2).quarter()).isEqualTo(Quarter.First);
        assertThat(Times.addQuarter(2000, Quarter.Fourth, -2).quarter()).isEqualTo(Quarter.Second);
    }

    @Test
    public void getQuarterOfMonthTest() {
        for (int m : TimeSpec.FirstQuarterMonths)
            assertThat(Times.quarterOfMonth(m)).isEqualTo(Quarter.First);

        for (int m : TimeSpec.SecondQuarterMonths)
            assertThat(Times.quarterOfMonth(m)).isEqualTo(Quarter.Second);

        for (int m : TimeSpec.ThirdQuarterMonths)
            assertThat(Times.quarterOfMonth(m)).isEqualTo(Quarter.Third);

        for (int m : TimeSpec.FourthQuarterMonths)
            assertThat(Times.quarterOfMonth(m)).isEqualTo(Quarter.Fourth);
    }

    @Test
    public void getMonthsOfQuarterTest() {
        assertThat(Times.monthsOfQuarter(Quarter.First)).isEqualTo(TimeSpec.FirstQuarterMonths);
        assertThat(Times.monthsOfQuarter(Quarter.Second)).isEqualTo(TimeSpec.SecondQuarterMonths);
        assertThat(Times.monthsOfQuarter(Quarter.Third)).isEqualTo(TimeSpec.ThirdQuarterMonths);
        assertThat(Times.monthsOfQuarter(Quarter.Fourth)).isEqualTo(TimeSpec.FourthQuarterMonths);
    }

    @Test
    public void nextMonthTest() {
        for (int i = 1; i <= TimeSpec.MonthsPerYear; i++)
            assertThat(Times.nextMonth(2000, i).monthOfYear()).isEqualTo(i % TimeSpec.MonthsPerYear + 1);
    }

    @Test
    public void prevMonthTest() {
        for (int i = 1; i <= TimeSpec.MonthsPerYear; i++)
            assertThat(Times.prevMonth(2000, i).monthOfYear()).isEqualTo(
                    (i - 1) <= 0 ? TimeSpec.MonthsPerYear + i - 1 : i - 1);
    }

    @Test
    public void addMonthTest() {

        for (int i = 1; i <= TimeSpec.MonthsPerYear; i++)
            assertThat(Times.addMonth(2000, i, 1).monthOfYear()).isEqualTo(i % TimeSpec.MonthsPerYear + 1);

        for (int i = 1; i <= TimeSpec.MonthsPerYear; i++)
            assertThat(Times.addMonth(2000, i, -1).monthOfYear()).isEqualTo(
                    (i - 1) <= 0 ? TimeSpec.MonthsPerYear + i - 1 : i - 1);

        final int threeYears = 3 * TimeSpec.MonthsPerYear;

        for (int i = 1; i <= threeYears; i++) {
            YearMonth ym = Times.addMonth(2013, 1, i);
            assertThat(ym.year()).isEqualTo(2013 + i / TimeSpec.MonthsPerYear);
            assertThat(ym.monthOfYear()).isEqualTo(i % TimeSpec.MonthsPerYear + 1);
        }
    }

    @Test
    public void weekOfYearTest() {

        ITimePeriod period = new TimeRange(Times.asDate(2007, 12, 31), Times.asDate(2009, 12, 31));

        for (ITimePeriod p : Times.foreachDays(period)) {
            DateTime moment = p.getStart();
            YearWeek expected = new YearWeek(moment.getWeekyear(), moment.getWeekOfWeekyear());
            YearWeek actual = Times.weekOfYear(moment);

            assertThat(actual).isEqualTo(expected);
        }
    }

    @Test
    public void dayStartTest() {
        assertThat(Times.dayStart(testDate)).isEqualTo(testDate.withTimeAtStartOfDay());
        assertThat(Times.dayStart(testDate).getMillisOfDay()).isEqualTo(0);

        assertThat(Times.dayStart(testNow)).isEqualTo(testNow.withTimeAtStartOfDay());
        assertThat(Times.dayStart(testNow).getMillisOfDay()).isEqualTo(0);
    }

    @Test
    public void nextDayOfWeekTest() {
        assertThat(Times.nextDayOfWeek(Monday)).isEqualTo(Tuesday);
        assertThat(Times.nextDayOfWeek(Tuesday)).isEqualTo(Wednesday);
        assertThat(Times.nextDayOfWeek(Wednesday)).isEqualTo(Thursday);
        assertThat(Times.nextDayOfWeek(Thursday)).isEqualTo(Friday);
        assertThat(Times.nextDayOfWeek(Friday)).isEqualTo(Saturday);
        assertThat(Times.nextDayOfWeek(Saturday)).isEqualTo(Sunday);
        assertThat(Times.nextDayOfWeek(Sunday)).isEqualTo(Monday);
    }

    @Test
    public void previousDayOfWeekTest() {
        assertThat(Times.prevDayOfWeek(Monday)).isEqualTo(Sunday);
        assertThat(Times.prevDayOfWeek(Tuesday)).isEqualTo(Monday);
        assertThat(Times.prevDayOfWeek(Wednesday)).isEqualTo(Tuesday);
        assertThat(Times.prevDayOfWeek(Thursday)).isEqualTo(Wednesday);
        assertThat(Times.prevDayOfWeek(Friday)).isEqualTo(Thursday);
        assertThat(Times.prevDayOfWeek(Saturday)).isEqualTo(Friday);
        assertThat(Times.prevDayOfWeek(Sunday)).isEqualTo(Saturday);
    }

    @Test
    public void addDayOfWeektest() {
        assertThat(Times.addDayOfWeek(Monday, 6)).isEqualTo(Sunday);
        assertThat(Times.addDayOfWeek(Monday, 7)).isEqualTo(Monday);
        assertThat(Times.addDayOfWeek(Monday, 8)).isEqualTo(Tuesday);

        assertThat(Times.addDayOfWeek(Monday, 14)).isEqualTo(Monday);
        assertThat(Times.addDayOfWeek(Tuesday, 14)).isEqualTo(Tuesday);

        assertThat(Times.addDayOfWeek(Monday, -14)).isEqualTo(Monday);
        assertThat(Times.addDayOfWeek(Tuesday, -14)).isEqualTo(Tuesday);
    }
}
