package kr.debop4s.timeperiod.timeline

import kr.debop4s.timeperiod.ITimePeriodCollection
import org.joda.time.DateTime

/**
 * kr.debop4s.timeperiod.timeline.ITimeLineMoment
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 31. 오후 8:15
 */
trait ITimeLineMoment extends Serializable {

    def getMoment: DateTime

    def getPeriods: ITimePeriodCollection

    def getStartCount: Int

    def getEndCount: Int
}