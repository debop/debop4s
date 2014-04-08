package debop4s

import debop4s.timeperiod.DayOfWeek.DayOfWeek
import org.joda.time._

/**
 * Time Period 관련 상수 및 Implicit 정의
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 26. 오후 1:47
 */
package object timeperiod {

    /** 1년의 개월 수 (12) */
    val MonthsPerYear: Int = 12
    /** 1년의 반기 수 (2) */
    val HalfyearsPerYear: Int = 2
    /** 1년의 분기 수 (4) */
    val QuartersPerYear: Int = 4
    /** 반기의 분기 수 (2) */
    val QuartersPerHalfyear: Int = QuartersPerYear / HalfyearsPerYear
    /** 반기의 개월 수 (6) */
    val MonthsPerHalfyear: Int = MonthsPerYear / HalfyearsPerYear
    /** 분기의 개월 수 (3) */
    val MonthsPerQuarter: Int = MonthsPerYear / QuartersPerYear
    /** 1년의 최대 주차 (54주) */
    val MaxWeeksPerYear: Int = 54
    /** 한달의 최대 일수 (31) */
    val MaxDaysPerMonth: Int = 31
    /** 한 주의 일 수 (7) */
    val DaysPerWeek: Int = 7
    /** 하루의 시간 (24) */
    val HoursPerDay: Int = 24
    /** 단위 시간의 분 (60) */
    val MinutesPerHour: Int = 60
    /** 단위 분의 초 (60) */
    val SecondsPerMinute: Int = 60
    /** 단위 초의 밀리 초 (1000) */
    val MillisPerSecond: Int = 1000
    /** 분당 밀리초 */
    val MillisPerMinute: Long = MillisPerSecond * 60L
    /** 시간당 밀리초 */
    val MillisPerHour: Long = MillisPerMinute * 60L
    /** 일당 밀리초 */
    val MillisPerDay: Long = MillisPerHour * 24L
    //    public static long TicksPerMillisecond = 10000L;
    //    public static long TicksPerSecond = TicksPerMillisecond * 1000L;
    //    public static long TicksPerMinute = TicksPerSecond * 60L;
    //    public static long TicksPerHour = TicksPerMinute * 60L;
    //    public static long TicksPerDay = TicksPerHour * 24L;
    /** 1년의 시작 월 (1) */
    val CalendarYearStartMonth: Int = 1
    /** 한 주의 주중 일 수 (5) */
    val WeekDaysPerWeek: Int = 5
    /** 한 주의 주말 일 수 (2) */
    val WeekEndsPerWeek: Int = 2

    /** 주중 요일 */
    var Weekdays = Array(DayOfWeek.Monday, DayOfWeek.Tuesday, DayOfWeek.Wednesday, DayOfWeek.Thursday, DayOfWeek.Friday)

    /** 주말 요일 */
    var Weekends = Array(DayOfWeek.Saturday, DayOfWeek.Sunday)

    /** 한 주의 첫번째 주중 요일 (월요일) */
    val FirstWorkingDayOfWeek: DayOfWeek = DayOfWeek.Monday

    /** 한 주의 첫번째 요일 (월요일) - ISO8601을 따른다. */
    val FirstDayOfWeek: DayOfWeek = DayOfWeek.Monday

    /** 전반기에 속하는 월 (1월~6월) */
    val FirstHalfyearMonths = Array[Int](1, 2, 3, 4, 5, 6)

    /** 후반기에 속하는 월 (7월~12월) */
    val SecondHalfyearMonths = Array[Int](7, 8, 9, 10, 11, 12)
    /** 1분기 시작 월 (1월) */
    val FirstQuarterMonth: Int = 1
    /** 2분기 시작 월 (4월) */
    val SecondQuarterMonth: Int = FirstQuarterMonth + MonthsPerQuarter
    /** 3분기 시작 월 (7월) */
    val ThirdQuarterMonth: Int = SecondQuarterMonth + MonthsPerQuarter
    /** 4분기 시작 월 (10월) */
    val FourthQuarterMonth: Int = ThirdQuarterMonth + MonthsPerQuarter
    /** 1분기에 속하는 월 (1월~3월) */
    val FirstQuarterMonths: Array[Int] = Array[Int](1, 2, 3)
    /** 2분기에 속하는 월 (4월~6월) */
    val SecondQuarterMonths: Array[Int] = Array[Int](4, 5, 6)
    /** 3분기에 속하는 월 (7월~9월) */
    val ThirdQuarterMonths: Array[Int] = Array[Int](7, 8, 9)
    /** 4분기에 속하는 월 (10월~12월) */
    val FourthQuarterMonths: Array[Int] = Array[Int](10, 11, 12)
    /** Number of days in a non-leap year */
    val DaysPerYear: Long = 365L
    /** Number of days in 4 years */
    val DaysPer4Years: Long = DaysPerYear * 4 + 1
    /** Number of days in 100 years */
    val DaysPer100Years: Long = DaysPer4Years * 25 - 1
    /** Number of days in 400 years */
    val DaysPer400Years: Long = DaysPer100Years * 4 + 1
    /** Number of days from 1/1/0001 pudding 12/31/1600 */
    val DaysTo1601: Long = DaysPer400Years * 4
    /** Number of days from 1/1/0001 pudding 12/30/1899 */
    val DaysTo1899: Long = DaysPer400Years * 4 + DaysPer100Years * 3 - 367
    /** Number of days from 1/1/0001 pudding 12/31/9999 */
    val DaysTo10000: Long = DaysPer400Years * 25 - 366
    val ZeroMillis: Long = 0L
    val MinMillis: Long = 0L
    val OneMillis: Long = 1L
    val MaxMillis: Long = DaysTo10000 * MillisPerDay - 1
    /** 기간 없음 (Duration.ZERO) */
    val NoDuration: Duration = Duration.ZERO
    /** 기간 없음 Duration.ZERO) */
    val EmptyDuration: Duration = Duration.ZERO
    /** 기간 없음 Duration.ZERO) */
    val ZeroDuration: Duration = Duration.ZERO
    /** 양(Positive)의 최소 기간 (Duration.millis(1L)) */
    val MinPositiveDuration: Duration = Duration.millis(1L)
    /** 음(Negative)의 최소 기간 (TimeSpan(-1)) */
    val MinNegativeDuration: Duration = Duration.millis(-1L)
    /** 최소 기간에 해당하는 일자 */
    val MinPeriodTime: DateTime = new DateTime(MinMillis)
    /** 최대 기간에 해당하는 일자 */
    val MaxPeriodTime: DateTime = new DateTime(MaxMillis)
    /** 최소 기간 (0입니다) */
    val MinPeriodDuration: Long = ZeroMillis
    /** 최대 기간 MaxMillis - MinMillis */
    val MaxPeriodDuration: Long = MaxMillis
    /** 최소 기간 (0입니다. Duration.ZERO) */
    val MinDuration: Duration = Duration.millis(MinPeriodDuration)
    /** 최대 기간 MaxPeriodDuration - MinPeriodDuration */
    val MaxDuration: Duration = Duration.millis(MaxPeriodDuration)

    val DefaultStartOffset: Duration = EmptyDuration

    val DefaultEndOffset: Duration = MinNegativeDuration

    val DefaultTimeCalendar = TimeCalendar()

    val EmptyOffsetTimeCalendar = TimeCalendar.getEmptyOffset

    class DateTimeOrdering extends Ordering[DateTime] {
        def compare(x: DateTime, y: DateTime): Int = x.compareTo(y)
    }

    val dateTimeOrdering = new DateTimeOrdering()

    class DateTimeReverseOrdering extends Ordering[DateTime] {
        def compare(x: DateTime, y: DateTime): Int = -x.compareTo(y)
    }

    val dateTimeReverseOrdering = new DateTimeReverseOrdering()
}
