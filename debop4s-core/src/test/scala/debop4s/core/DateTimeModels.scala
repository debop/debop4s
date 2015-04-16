package debop4s.core


/**
 * 년-주차 를 표현합니다.
 * @param weekyear   년도
 * @param weekOfWeekyear 주차
 */
private[core] case class YearWeek(weekyear: Int, weekOfWeekyear: Int = 1) {}

/**
 * 월-주차 를 표현합니다.
 * @param weekmonth   월
 * @param weekOfMonth 주차
 */
private[core] case class MonthWeek(weekmonth: Int, weekOfMonth: Int = 1) {}


/**
 * 년, 월 을 표현합니다.
 * @param year  년
 * @param month 월
 */
private[core] case class YearMonth(year: Int, month: Int = 1)
