package debop4s.core.utils

import org.joda.time.{DateTime, DateTimeConstants}

/**
 * 날짜 관련 헬퍼 클래스. (더 만은 메소드는 `debop4s.timeperiod.utils.Times` 클래스를 사용하세요
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 */
@deprecated(message = "use debop4s.timeperiod.utils.Times", since = "0.5.0")
object Dates {

  /**
   * 일자의 하루의 시작 시각 (date part만 가지게 합니다.)
   * @param moment 시각
   * @return 날짜만 있는 일자 정보
   */
  def startOfDay(moment: DateTime): DateTime = moment.withTimeAtStartOfDay()

  /**
   * 지정한 일자의 마지막 시각 (12:59:59.999)
   * @param moment 시각
   * @return  일자의 마지막 시각
   */
  def endOfDay(moment: DateTime): DateTime = startOfDay(moment).plusDays(1).minusMillis(1)

  /**
   * 지정한 시각이 속한 주의 첫째 날 (월요일) 의 시작 시각
   * @param moment 시각
   * @return 지정한 시각이 속한 주의 첫째날의 시작 시각 (월요일 00:00:00)
   */
  def startOfWeek(moment: DateTime): DateTime =
    startOfDay(moment).minusDays(moment.getDayOfWeek - DateTimeConstants.MONDAY)

  /**
   * 지정한 시각이 속한 주의 마지막 날 (일요일) 의 마지막 시각 (12:59:59.999)
   * @param moment 시각
   * @return 지정한 시각이 속한 주의 마지막 날(일요일)의 마지막 시각 (12:59:59.999)
   */
  def endOfWeek(moment: DateTime): DateTime =
    startOfWeek(moment).plusDays(7).minusMillis(1)

  /**
   * 해당 시각이 속한 월의 첫째 날
   */
  def startOfMonth(moment: DateTime): DateTime =
    new DateTime(moment.getYear, moment.getMonthOfYear, 1, 0, 0)

  /**
   * 해당 시각이 속한 월의 마지막 날
   */
  def endOfMonth(moment: DateTime): DateTime =
    startOfMonth(moment).plusMonths(1).minusMillis(1)

  /**
   * 해당 년도의 시작 시각
   */
  def startOfYear(year: Int): DateTime = new DateTime(year, 1, 1, 0, 0)

  /**
   * 해당 년도의 마지막 시각
   */
  def endOfYear(year: Int): DateTime = startOfYear(year + 1).minusMillis(1)

  def getWeekOfYear(moment: DateTime) = moment.getWeekOfWeekyear

  /**
   * 해당 일이 속한 월 주차 (Month-Week) 의 주차
   */
  @deprecated("getMonthAndWeekOfMonth 를 사용하세요", "2.0.0")
  def getWeekOfMonth(moment: DateTime): Int =
    moment.getWeekOfWeekyear - startOfMonth(moment).getWeekOfWeekyear + 1

  /**
   * 해당 일이 속한 월 주차 (Month-Week) 의 주차
   */
  def getMonthAndWeekOfMonth(moment: DateTime): (Int, Int) = {
    val result = getWeekOfYear(moment) - getWeekOfYear(startOfMonth(moment)) + 1

    if (result > 0) (moment.getMonthOfYear, result)
    else (moment.plusMonths(1).getMonthOfYear, 1)
  }
}