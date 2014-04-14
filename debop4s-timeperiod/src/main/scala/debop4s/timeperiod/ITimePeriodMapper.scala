package debop4s.timeperiod

import org.joda.time.DateTime

/**
 * debop4s.timeperiod.TimePeriodMapper
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 26. 오후 1:45
 */
trait ITimePeriodMapper extends Serializable {

    def mapStart(moment: DateTime): DateTime

    def mapEnd(moment: DateTime): DateTime

    def unmapStart(moment: DateTime): DateTime

    def unmapEnd(moment: DateTime): DateTime
}
