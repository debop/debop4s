package debop4s

import org.joda.time.{Duration => JDuration, _}

/**
 * Time Period 관련 상수 및 Implicit 정의
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 26. 오후 1:47
 */
package object timeperiod {

  class DateTimeOrdering extends Ordering[DateTime] {
    def compare(x: DateTime, y: DateTime): Int = x.compareTo(y)
  }

  val dateTimeOrdering = new DateTimeOrdering()

  class DateTimeReverseOrdering extends Ordering[DateTime] {
    def compare(x: DateTime, y: DateTime): Int = -x.compareTo(y)
  }

  val dateTimeReverseOrdering = new DateTimeReverseOrdering()
}
