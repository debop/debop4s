package kr.debop4s.timeperiod

/**
 * kr.debop4s.timeperiod.timeline.package
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 31. 오후 8:09
 */
package object timeline {

  class TimeLineMomentOrdering extends Ordering[ITimeLineMoment] {
    def compare(x: ITimeLineMoment, y: ITimeLineMoment): Int =
      x.getMoment.compareTo(y.getMoment)
  }

  implicit val timelineMomentOrdering = new TimeLineMomentOrdering()
}
