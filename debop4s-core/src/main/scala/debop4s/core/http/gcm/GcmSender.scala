package debop4s.core.http.gcm

import java.net.URI

import debop4s.core.http.HttpAsyncs
import debop4s.core.json.JacksonSerializer
import debop4s.core.utils.Charsets
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.message.BasicHeader
import org.apache.http.{Header, HttpStatus}
import org.slf4j.LoggerFactory

import scala.util.control.NonFatal

object GcmSender {

  /** GCM 서버 URL */
  lazy val GCM_SERVER_URL: String = "https://android.googleapis.com/gcm/send"

  lazy val GCM_SERVER_URI = new URI(GCM_SERVER_URL)

  def apply(): GcmSender = new GcmSender()

}

/**
 * 구글 GCM 서버에 푸시 메시지 전송에 사용됩니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 15. 오후 2:28
 */
@deprecated(message = "hconnect-backend-messaging.push.google.GcmSender 으로  이관합니다.", since = "2.0.0")
class GcmSender {

  lazy val log = LoggerFactory.getLogger(getClass)
  lazy val json = JacksonSerializer()

  /**
   * GCM 에 푸시 메시지를 전송합니다. 실패시 retry 횟수만큼 재시도 합니다.
   */
  def send(serverApiKey: String, msg: GcmMessage, retry: Int = 3): Int = {
    require(msg != null)
    require(msg.registrationIds != null && msg.registrationIds.size > 0)

    val post = buildHttpPost(serverApiKey, msg)
    var isSent = false

    var attempts = 0

    while (attempts < retry) {
      try {
        val response = HttpAsyncs.post(post).get
        if (response != null) {
          val statusCode = response.getStatusLine.getStatusCode
          isSent = statusCode == HttpStatus.SC_OK
          if (!isSent) {
            throw new RuntimeException(s"Fail to send push messages. statusLine=${ response.getStatusLine }")
          }
          return statusCode
        } else {
          throw new RuntimeException("Fail to connect to gcm push server.")
        }
      } catch {
        case NonFatal(e) =>
          log.warn(s"Fail to send push message", e)
      }
      attempts += 1
    }
    HttpStatus.SC_SERVICE_UNAVAILABLE
  }

  private def buildHttpPost(apiKey: String, msg: GcmMessage): HttpPost = {
    val post = new HttpPost(GcmSender.GCM_SERVER_URI)

    buildHttpHeader(apiKey).foreach(post.addHeader)

    val jsonText = buildMessage(msg)
    log.debug(s"Gcm Message=$jsonText")

    post.setEntity(new StringEntity(jsonText, Charsets.UTF_8))
    post
  }

  private def buildHttpHeader(apiKey: String): Seq[Header] = {
    Seq(
      new BasicHeader("Authorization", "key=" + apiKey),
      new BasicHeader("Content-Type", "application/json")
    )
  }

  private def buildMessage(msg: GcmMessage): String = json.serializeToText(msg)

}


