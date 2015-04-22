package debop4s.core

import debop4s.core.conversions.jodatime._
import debop4s.core.utils.Hashs
import org.joda.time.{DateTime, DateTimeZone}

/**
 * joda-time의 DateTime 수형을 Timestamp, TimeZone, Local Time Text 로 분리해서 표현하도록 해주는 Value Object 입니다.
 * @author sunghyouk.bae@gmail.com
 */
class TimestampZoneText(val datetime: DateTime) extends ValueObject {

  def this() = this(null: DateTime)
  def this(timestamp: Long, zone: DateTimeZone) = this(new DateTime(timestamp, zone))
  def this(timestamp: Long, zoneId: String) = this(new DateTime(timestamp, DateTimeZone.forID(zoneId)))

  if (datetime != null) {
    this.timestamp = datetime.getMillis
    this.zoneId = datetime.getZone.getID
    this.timetext = datetime.asIsoFormatDateHMS
  }

  var timestamp: Long = _
  var zoneId: String = _
  var timetext: String = _

  override def hashCode: Int = Hashs.compute(timestamp, zoneId)

  override protected def buildStringHelper: ToStringHelper =
    super.buildStringHelper
    .add("timestamp", timestamp)
    .add("zoneId", zoneId)
    .add("timetext", timetext)
}
