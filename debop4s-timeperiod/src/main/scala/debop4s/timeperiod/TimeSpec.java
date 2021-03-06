package debop4s.timeperiod;

import org.joda.time.DateTime;
import org.joda.time.Duration;

/**
 * TimeSpec
 *
 * @author debop created at 2014. 5. 12.
 */
public final class TimeSpec {

    private TimeSpec() {}

    /** 1년의 개월 수 (12) */
    public static final int MonthsPerYear = 12;
    /** 1년의 반기 수 (2) */
    public static final int HalfyearsPerYear = 2;
    /** 1년의 분기 수 (4) */
    public static final int QuartersPerYear = 4;
    /** 반기의 분기 수 (2) */
    public static final int QuartersPerHalfyear = QuartersPerYear / HalfyearsPerYear;
    /** 반기의 개월 수 (6) */
    public static final int MonthsPerHalfyear = MonthsPerYear / HalfyearsPerYear;
    /** 분기의 개월 수 (3) */
    public static final int MonthsPerQuarter = MonthsPerYear / QuartersPerYear;
    /** 1년의 최대 주차 (54주) */
    public static final int MaxWeeksPerYear = 54;
    /** 한달의 최대 일수 (31) */
    public static final int MaxDaysPerMonth = 31;
    /** 한 주의 일 수 (7) */
    public static final int DaysPerWeek = 7;
    /** 하루의 시간 (24) */
    public static final int HoursPerDay = 24;
    /** 단위 시간의 분 (60) */
    public static final int MinutesPerHour = 60;
    /** 단위 분의 초 (60) */
    public static final int SecondsPerMinute = 60;
    /** 단위 초의 밀리 초 (1000) */
    public static final int MillisPerSecond = 1000;
    /** 분당 밀리초 */
    public static final long MillisPerMinute = MillisPerSecond * 60L;
    /** 시간당 밀리초 */
    public static final long MillisPerHour = MillisPerMinute * 60L;
    /** 일당 밀리초 */
    public static final long MillisPerDay = MillisPerHour * 24L;
    //    public static long TicksPerMillisecond = 10000L;
    //    public static long TicksPerSecond = TicksPerMillisecond * 1000L;
    //    public static long TicksPerMinute = TicksPerSecond * 60L;
    //    public static long TicksPerHour = TicksPerMinute * 60L;
    //    public static long TicksPerDay = TicksPerHour * 24L;
    /** 1년의 시작 월 (1) */
    public static final int CalendarYearStartMonth = 1;
    /** 한 주의 주중 일 수 (5) */
    public static final int WeekDaysPerWeek = 5;
    /** 한 주의 주말 일 수 (2) */
    public static final int WeekEndsPerWeek = 2;

    /** 주중 요일 */
    public static final DayOfWeek[] Weekdays = new DayOfWeek[] {
            DayOfWeek.Monday,
            DayOfWeek.Tuesday,
            DayOfWeek.Wednesday,
            DayOfWeek.Thursday,
            DayOfWeek.Friday };

    /** 주말 요일 */
    public static final DayOfWeek[] Weekends = new DayOfWeek[] { DayOfWeek.Saturday, DayOfWeek.Sunday };

    /** 한 주의 첫번째 주중 요일 (월요일) */
    public static final DayOfWeek FirstWorkingDayOfWeek = DayOfWeek.Monday;

    /** 한 주의 첫번째 요일 (월요일) - ISO8601을 따른다. */
    public static final DayOfWeek FirstDayOfWeek = DayOfWeek.Monday;

    /** 전반기에 속하는 월 (1월~6월) */
    public static final int[] FirstHalfyearMonths = new int[] { 1, 2, 3, 4, 5, 6 };

    /** 후반기에 속하는 월 (7월~12월) */
    public static final int[] SecondHalfyearMonths = new int[] { 7, 8, 9, 10, 11, 12 };
    /** 1분기 시작 월 (1월) */
    public static final int FirstQuarterMonth = 1;
    /** 2분기 시작 월 (4월) */
    public static final int SecondQuarterMonth = FirstQuarterMonth + MonthsPerQuarter;
    /** 3분기 시작 월 (7월) */
    public static final int ThirdQuarterMonth = SecondQuarterMonth + MonthsPerQuarter;
    /** 4분기 시작 월 (10월) */
    public static final int FourthQuarterMonth = ThirdQuarterMonth + MonthsPerQuarter;
    /** 1분기에 속하는 월 (1월~3월) */
    public static final int[] FirstQuarterMonths = new int[] { 1, 2, 3 };
    /** 2분기에 속하는 월 (4월~6월) */
    public static final int[] SecondQuarterMonths = new int[] { 4, 5, 6 };
    /** 3분기에 속하는 월 (7월~9월) */
    public static final int[] ThirdQuarterMonths = new int[] { 7, 8, 9 };
    /** 4분기에 속하는 월 (10월~12월) */
    public static final int[] FourthQuarterMonths = new int[] { 10, 11, 12 };
    /** Number of daysView in a non-leap year */
    public static final long DaysPerYear = 365L;
    /** Number of daysView in 4 yearsView */
    public static final long DaysPer4Years = DaysPerYear * 4 + 1;
    /** Number of daysView in 100 yearsView */
    public static final long DaysPer100Years = DaysPer4Years * 25 - 1;
    /** Number of daysView in 400 yearsView */
    public static final long DaysPer400Years = DaysPer100Years * 4 + 1;
    /** Number of daysView from 1/1/0001 pudding 12/31/1600 */
    public static final long DaysTo1601 = DaysPer400Years * 4;
    /** Number of daysView from 1/1/0001 pudding 12/30/1899 */
    public static final long DaysTo1899 = DaysPer400Years * 4 + DaysPer100Years * 3 - 367;
    /** Number of daysView from 1/1/0001 pudding 12/31/9999 */
    public static final long DaysTo10000 = DaysPer400Years * 25 - 366;
    public static final long ZeroMillis = 0L;
    public static final long MinMillis = 0L;
    public static final long OneMillis = 1L;
    public static final long MaxMillis = DaysTo10000 * MillisPerDay - 1;

    /** 기간 없음 (Duration.ZERO) */
    public static final Duration NoDuration = Duration.ZERO;

    /** 기간 없음 Duration.ZERO) */
    public static final Duration EmptyDuration = Duration.ZERO;

    /** 기간 없음 Duration.ZERO) */
    public static final Duration ZeroDuration = Duration.ZERO;

    /** 양(Positive)의 최소 기간 (Duration.millis(1L)) */
    public static final Duration MinPositiveDuration = Duration.millis(1L);

    /** 음(Negative)의 최소 기간 (TimeSpan(-1)) */
    public static final Duration MinNegativeDuration = Duration.millis(-1L);

    /** 최소 기간에 해당하는 일자 */
    public static final DateTime MinPeriodTime = new DateTime(MinMillis);

    /** 최대 기간에 해당하는 일자 */
    public static final DateTime MaxPeriodTime = new DateTime(MaxMillis);

    /** 최소 기간 (0입니다) */
    public static final long MinPeriodDuration = ZeroMillis;

    /** 최대 기간 MaxMillis - MinMillis */
    public static final long MaxPeriodDuration = MaxMillis;

    /** 최소 기간 (0입니다. Duration.ZERO) */
    public static final Duration MinDuration = Duration.millis(MinPeriodDuration);

    /** 최대 기간 MaxPeriodDuration - MinPeriodDuration */
    public static final Duration MaxDuration = Duration.millis(MaxPeriodDuration);

    public static final Duration DefaultStartOffset = EmptyDuration;

    public static final Duration DefaultEndOffset = MinNegativeDuration;

    public static final TimeCalendar DefaultTimeCalendar = new TimeCalendar();

    public static final TimeCalendar EmptyOffsetTimeCalendar = TimeCalendar.getEmptyOffset();
}
