package debop4s.timeperiod

import debop4s.timeperiod.utils.Times


/**
 * 월 주차를 표현합니다.
 */
case class MonthWeek(month: Int,
                     weekOfMonth: Int = 1)

/**
 * 주차를 표현합니다.
 */
case class YearWeek(weekyear: Int,
                    weekOfWeekyear: Int = 1) {
  lazy val start = Times.startTimeOfWeek(weekyear, weekOfWeekyear)
  lazy val end = Times.endTimeOfMonth(weekyear, weekOfWeekyear)
}

/**
 * 년과 월을 나타냅니다.
 */
case class YearMonth(year: Int,
                     monthOfYear: Int = 1) {
  lazy val start = Times.startTimeOfMonth(year, monthOfYear)
  lazy val end = Times.endTimeOfMonth(year, monthOfYear)
}

/**
 * 년과 분기를 나타냅니다.
 */
case class YearQuarter(year: Int,
                       quarter: Quarter = Quarter.First) {

  lazy val start = Times.startTimeOfQuarter(year, quarter)
  lazy val end = Times.endTimeOfQuarter(year, quarter)
}

/**
 * 년과 분기를 표현합니다.
 */
case class YearHalfyear(year: Int,
                        halfyear: Halfyear = Halfyear.First) {
  lazy val start = Times.startTimeOfHalfyear(year, halfyear)
  lazy val end = Times.endTimeOfHalfyear(year, halfyear)
}


