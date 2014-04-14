package debop4s.timeperiod

/**
 * debop4s.timeperiod.timeline.package
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 31. 오후 8:09
 */
package object timeline {

    class TimeLineMomentOrdering extends Ordering[ITimeLineMoment] {
        def compare(x: ITimeLineMoment, y: ITimeLineMoment): Int =
            x.moment.compareTo(y.moment)
    }

    implicit val timelineMomentOrdering = new TimeLineMomentOrdering()
}
