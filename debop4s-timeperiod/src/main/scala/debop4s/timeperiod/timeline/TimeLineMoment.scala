package debop4s.timeperiod.timeline

import debop4s.core.ValueObject
import debop4s.core.utils.Hashs
import debop4s.timeperiod.{ITimePeriodCollection, TimePeriodCollection}
import org.joda.time.DateTime

/**
 * ITimePeriod의 컬렉션인 periods를 필드로 가지며, moment를 기준으로 선행 기간의 수와 후행 기간의 수를 파악합니다.
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 31. 오후 8:15
 */
trait ITimeLineMoment extends Ordered[DateTime] with Serializable {

    def moment: DateTime

    def periods: ITimePeriodCollection

    def startCount: Int

    def endCount: Int

    override def compare(that: DateTime): Int = moment.compareTo(that)
}

/**
 * ITimePeriod의 컬렉션인 periods를 필드로 가지며, moment를 기준으로 선행 기간의 수와 후행 기간의 수를 파악합니다.
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 31. 오후 8:10
 */
@SerialVersionUID(8524596139661439627L)
class TimeLineMoment(private[this] val _moment: DateTime) extends ValueObject with ITimeLineMoment {

    private val _periods = new TimePeriodCollection()

    def moment: DateTime = _moment

    def periods = _periods

    def startCount: Int = _periods.count(x => x.start.equals(_moment))

    def endCount: Int = _periods.count(x => x.end.equals(_moment))

    override def hashCode() = Hashs.compute(_moment)

    override protected def buildStringHelper =
        super.buildStringHelper
        .add("moment", moment)
        .add("startCount", startCount)
        .add("endCount", endCount)
}
