package debop4s.core.http.gcm

import java.util

import com.fasterxml.jackson.annotation.JsonProperty
import debop4s.core.utils.Hashs
import debop4s.core.{ToStringHelper, ValueObject}

import scala.collection.JavaConverters._

/**
 * 구글 GCM 푸시 메시지
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 15. 오후 2:18
 */
@SerialVersionUID(-8489854059420880664L)
@deprecated(message = "hconnect-backend-messaging 이로 이관합니다.", since = "2.0.0")
class GcmMessage extends ValueObject {

  /** 등록된 디바이스의 Id (스마트 폰의 고유 Device Id) */
  @JsonProperty("registration_ids") val registrationIds = new util.HashSet[String]()

  /** 푸시 알림의 종류를 나타내여, collapseKey가 같으면 복수의 푸시 발송 시, 기존 푸시 메시지를 덮어 쓰게 합니다. */
  @JsonProperty("collapse_key") var collapseKey: String = _

  /** 푸시 메시지의 유효 기간 */
  @JsonProperty("time_to_live") var timeToLive: Int = 0

  /** 푸시 발송 지연 여부 */
  @JsonProperty("delay_while_idle") var delayWhileIdle: Boolean = false

  /** 푸시 발송 시의 전달할 데이터 */
  @JsonProperty("data") val data = new util.HashMap[String, String]()

  override def hashCode(): Int =
    Hashs.compute(collapseKey, registrationIds)

  override protected def buildStringHelper: ToStringHelper =
    super.buildStringHelper
    .add("collapseKey", collapseKey)
    .add("timeToLive", timeToLive)
    .add("delayWhileIdle", delayWhileIdle)
    .add("data", data)
}

@deprecated(message = "hconnect-backend-messaging 이로 이관합니다.", since = "2.0.0")
class GcmMessageBuilder {
  val registrationIds = new util.HashSet[String]()
  var collapseKey: String = _
  var timeToLive: Int = 0
  var delayWhileIdle: Boolean = false
  val data = new util.HashMap[String, String]()

  def addRegistrations(registrationIds: String*) = {
    this.registrationIds.addAll(registrationIds.asJava)
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

  def addData(key: String, value: String) = {
    this.data.put(key, value)
    this
  }

  def addData(data: util.Map[String, String]) = {
    this.data.putAll(data)
    this
  }

  def build(): GcmMessage = {
    val msg = new GcmMessage()
    msg.registrationIds.addAll(this.registrationIds)
    msg.collapseKey = this.collapseKey
    msg.delayWhileIdle = this.delayWhileIdle
    msg.timeToLive = this.timeToLive
    msg.data.putAll(this.data)
    msg
  }
}
