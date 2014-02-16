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
class TimeLineMoment(val moment: DateTime) extends ValueObject with ITimeLineMoment {

  val periods = new TimePeriodCollection()

  def getMoment: DateTime = moment

  def getPeriods: TimePeriodCollection = periods

  def getStartCount: Int = periods.count(x => x.getStart.equals(moment))

  def getEndCount: Int = periods.count(x => x.getEnd.equals(moment))

  override def hashCode() = Hashs.compute(moment)

  override protected def buildStringHelper =
    super.buildStringHelper
    .add("moment", moment)
    .add("period", periods)
    .add("startCount", getStartCount)
    .add("endCount", getEndCount)
}
