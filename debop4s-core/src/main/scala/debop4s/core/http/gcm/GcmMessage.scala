package debop4s.core.http.gcm

import debop4s.core.ValueObject
import debop4s.core.utils.{ToStringHelper, Hashs}
import scala.collection.mutable

/**
 * 구글 GCM 푸시 메시지
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 15. 오후 2:18
 */
@SerialVersionUID(-8489854059420880664L)
class GcmMessage extends ValueObject {

  val registrationIds = mutable.HashSet[String]()
  var collapseKey: String = _
  var timeToLive: Int = 0
  var delayWhileIdle: Boolean = false
  val data = mutable.HashMap[String, String]()

  override def hashCode(): Int = Hashs.compute(collapseKey, registrationIds)

  override protected def buildStringHelper: ToStringHelper =
    super.buildStringHelper
      .add("collapseKey", collapseKey)
      .add("timeToLive", timeToLive)
      .add("delayWhileIdle", delayWhileIdle)
      .add("data", data)

  class Builder {
    val registrationIds = mutable.HashSet[String]()
    var collapseKey: String = _
    var timeToLive: Int = 0
    var delayWhileIdle: Boolean = false
    val data = mutable.HashMap[String, String]()

    def addRegistrations(registrationIds: String*) = {
      this.registrationIds ++= registrationIds
      this
    }

    def setCollapseKey(collapseKey: String) = {
      this.collapseKey = collapseKey
      this
    }

    def setDelayWhileIdle(delayWhileIdle: Boolean) = {
      this.delayWhileIdle = delayWhileIdle
      this
    }

    def setTimeToLive(timeToLive: Integer) = {
      this.timeToLive = timeToLive
      this
    }

    def addData(data: Map[String, String]) = {
      this.data ++= data
      this
    }

    def build(): GcmMessage = {
      val msg = new GcmMessage()
      msg.registrationIds ++= this.registrationIds
      msg.collapseKey = this.collapseKey
      msg.delayWhileIdle = this.delayWhileIdle
      msg.timeToLive = this.timeToLive
      msg.data ++= this.data
      msg
    }
  }

}
