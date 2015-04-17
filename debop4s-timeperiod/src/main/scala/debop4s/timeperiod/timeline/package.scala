package debop4s.timeperiod

/**
 * timeline package
 * Created by debop on 2014. 4. 19.
 */
package object timeline {

  class TimeLineMomentOrdering extends Ordering[ITimeLineMoment] {
    def compare(x: ITimeLineMoment, y: ITimeLineMoment): Int =
      x.moment.compareTo(y.moment)
  }

  implicit val timelineMomentOrdering = new TimeLineMomentOrdering()
}
