package kr.debop4s.timeperiod.timeline

import kr.debop4s.core.ValueObject
import kr.debop4s.core.utils.Hashs
import kr.debop4s.timeperiod.TimePeriodCollection
import org.joda.time.DateTime
import scala.collection.JavaConversions._

/**
 * kr.debop4s.timeperiod.timeline.TimeLineMoment
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 31. 오후 8:10
 */
@SerialVersionUID(8524596139661439627L)
class TimeLineMoment(private val _moment: DateTime) extends ValueObject with ITimeLineMoment {

    private val _periods = new TimePeriodCollection()

    def moment: DateTime = _moment

    def periods = _periods

    def startCount: Int = _periods.count(x => x.start.equals(_moment))

    def endCount: Int = _periods.count(x => x.end.equals(_moment))

    override def compare(that: DateTime): Int = moment.compareTo(that)

    override def hashCode() = Hashs.compute(_moment)

    override protected def buildStringHelper =
        super.buildStringHelper
            .add("moment", moment)
            .add("startCount", startCount)
            .add("endCount", endCount)
}
