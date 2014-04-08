package debop4s.core.utils

import org.joda.time.{DateTimeConstants, DateTime}
import org.slf4j.LoggerFactory

/**
 * 날짜 관련 헬퍼 클래스. (더 만은 메소드는 [[debop4s.timeperiod.Times]] 클래스를 사용하세요)
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 12. 오전 11:30
 */
object Dates {

    private lazy val log = LoggerFactory.getLogger(getClass)

    def startOfDay(moment: DateTime): DateTime = moment.withTimeAtStartOfDay()

    def endOfDay(moment: DateTime): DateTime = startOfDay(moment).plusDays(1).minusMillis(1)

    def startOfWeek(moment: DateTime): DateTime =
        startOfDay(moment).minusDays(moment.getDayOfWeek - DateTimeConstants.MONDAY)

    def endOfWeek(moment: DateTime): DateTime =
        startOfWeek(moment).plusDays(7).minusMillis(1)

    def startOfMonth(moment: DateTime): DateTime =
        new DateTime(moment.getYear, moment.getMonthOfYear, 1, 0, 0)

    def endOfMonth(moment: DateTime): DateTime =
        startOfMonth(moment).plusMonths(1).minusMillis(1)

    def startOfYear(year: Int): DateTime = new DateTime(year, 1, 1, 0, 0)

    def endOfYear(year: Int): DateTime = startOfYear(year + 1).minusMillis(1)

    /**
     * 월 주차.
     */
    def getWeekOfMonth(moment: DateTime): Int =
        moment.getWeekOfWeekyear - startOfMonth(moment).getWeekOfWeekyear + 1

}
