package com.github.debop4s.core.http.gcm

import com.github.debop4s.core.http.AsyncHttpClient
import com.github.debop4s.core.json.JacksonSerializer
import com.github.debop4s.core.utils.{Tasks, Charsets}
import java.net.URI
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.message.BasicHeader
import org.apache.http.{HttpStatus, Header}
import org.slf4j.LoggerFactory
import scala.collection.mutable.ListBuffer
import scala.util.{Success, Failure}

/**
 * 구글 GCM 서버에 푸시 메시지 전송에 사용됩니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 15. 오후 2:28
 */
class GcmSender(val serverApiKey: String) {

    lazy val log = LoggerFactory.getLogger(getClass)
    lazy val serializer = new JacksonSerializer()

    @inline
    def send(msg: GcmMessage, retry: Int = 3) {
        require(msg != null)
        require(msg.registrationIds != null && msg.registrationIds.size > 0)

        val post = buildHttpPost(serverApiKey, msg)
        var isSent = false

        val asyncHttp = new AsyncHttpClient()

        Tasks.runWithRetry(retry) {
            val response = asyncHttp.post(post)
            response match {
                case Success(r) =>
                    isSent = r.getStatusLine.getStatusCode == HttpStatus.SC_OK
                    if (!isSent)
                        throw new RuntimeException(s"Fail to send push messages. statusLine=${r.getStatusLine}")
                case Failure(e) =>
                    throw new RuntimeException("Fail to connect to gcm push server.", e)
            }
        }
        if (!isSent)
            throw new RuntimeException(s"could not send message after $retry attempts")
    }


    private def buildMessage(msg: GcmMessage) = serializer.serializeToText(msg)

    private def buildHttpHeader(apiKey: String): Seq[Header] = {
        ListBuffer[Header](
            new BasicHeader("Authorization", "key=" + apiKey),
            new BasicHeader("Content-Type", "application/json")
        )
    }

    private def buildHttpPost(apiKey: String, msg: GcmMessage): HttpPost = {
        val post = new HttpPost(GcmSender.GCM_SERVER_URI)
        buildHttpHeader(apiKey).foreach(post.addHeader)

        val text = buildMessage(msg)
        post.setEntity(new StringEntity(text, Charsets.UTF_8))
        post
    }
}

object GcmSender {

    lazy val GCM_SERVER_URL: String = "https://android.googleapis.com/gcm/send"

    lazy val GCM_SERVER_URI = new URI(GCM_SERVER_URL)

    def apply(serverApiKey: String): GcmSender = new GcmSender(serverApiKey)

}
