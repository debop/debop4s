package kr.debop4s.timeperiod

import org.joda.time.DateTime

/**
 * kr.debop4s.timeperiod.TimeLineMoment
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 26. 오후 1:43
 */
trait TimeLineMoment extends Serializable {

    def getMoment: DateTime

    def getPeriods: TimePeriodCollection

    def getStartCount: Int

    def getEndCount: Int

}
