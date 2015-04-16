package debop4s.core.tools;

import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

/**
 * 날짜 관련 헬퍼 클래스. (더 만은 메소드는 hconnect-timeperiod 의 Times 클래스를 사용하세요)
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @see {@link debop4s.timeperiods.Times} 을 사용하세요.
 * @since 13. 6. 28. 오후 11:40
 */
@Slf4j
public final class Dates {
    private Dates() { }

    /**
     * 일자의 하루의 시작 시각 (date part만 가지게 합니다.)
     */
    public static DateTime startOfDay(DateTime moment) {
        return moment.withTimeAtStartOfDay();
    }

    /**
     * 지정한 일자의 마지막 시각 (12:59:59:999)
     */
    public static DateTime endOfDay(DateTime moment) {
        return startOfDay(moment).plusDays(1).minusMillis(1);
    }

    /**
     * 일자가 속한 주의 시작 시각 (월요일이 시작입니다)
     */
    public static DateTime startOfWeek(DateTime moment) {
        return startOfDay(moment).minusDays(moment.getDayOfWeek() - DateTimeConstants.MONDAY);
    }

    /**
     * 일자가 속한 주의 종료 시각 (일요일 밤 11:59:59:999)
     */
    public static DateTime endOfWeek(DateTime moment) {
        return startOfWeek(moment).plusDays(7).minusMillis(1);
    }

    /**
     * Start Day of Month
     */
    public static DateTime startOfMonth(DateTime moment) {
        return moment.withDate(moment.getYear(), moment.getMonthOfYear(), 1);
    }

    /**
     * End day of Month
     */
    public static DateTime endOfMonth(DateTime moment) {
        return startOfMonth(moment).plusMonths(1).minusMillis(1);
    }

    /**
     * Start day of year
     */
    public static DateTime startOfYear(int year) {
        return new DateTime(year, 1, 1, 0, 0);
    }

    /**
     * End day of year
     */
    public static DateTime endOfYear(int year) {
        return startOfYear(year + 1).minusMillis(1);
    }

    /**
     * 해당 일자의 주차 (Week of year)
     */
    public static int getWeekOfYear(DateTime moment) {
        return moment.getWeekOfWeekyear();
    }

    /**
     * 해당 일자의 월 주차 (week of month)
     *
     * @see #getMonthAndWeekOfMonth(org.joda.time.DateTime) 를 사용하세요.
     */
    @Deprecated
    public static int getWeekOfMonth(DateTime moment) {
        int result = getWeekOfYear(moment) - getWeekOfYear(startOfMonth(moment)) + 1;
        return (result > 0) ? result : getWeekOfYear(moment);
    }

    /**
     * 해당 일자의 월 주차 (week of month)
     */
    public static Pair<Integer, Integer> getMonthAndWeekOfMonth(DateTime moment) {
        int result = getWeekOfYear(moment) - getWeekOfYear(startOfMonth(moment)) + 1;
        return (result > 0)
                ? new Pair<Integer, Integer>(moment.getMonthOfYear(), result)
                : new Pair<Integer, Integer>(moment.plusMonths(1).getMonthOfYear(), 1);
    }
}
