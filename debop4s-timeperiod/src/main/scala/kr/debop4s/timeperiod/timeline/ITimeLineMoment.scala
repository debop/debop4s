package kr.debop4s.timeperiod.timeline

import kr.debop4s.timeperiod.ITimePeriodCollection
import org.joda.time.DateTime

/**
 * kr.debop4s.timeperiod.timeline.ITimeLineMoment
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 31. 오후 8:15
 */
trait ITimeLineMoment extends Ordered[DateTime] with Serializable {

    def moment: DateTime

    def periods: ITimePeriodCollection

    def startCount: Int

    def endCount: Int
}