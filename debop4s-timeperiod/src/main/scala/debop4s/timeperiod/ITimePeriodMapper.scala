package debop4s.timeperiod

import org.joda.time.DateTime

trait ITimePeriodMapper extends Serializable {

  def mapStart(moment: DateTime): DateTime

  def mapEnd(moment: DateTime): DateTime

  def unmapStart(moment: DateTime): DateTime

  def unmapEnd(moment: DateTime): DateTime
}
