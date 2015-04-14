package debop4s.core.http

import java.util.{List => JList}

import debop4s.core.json.JacksonSerializer
import debop4s.core.utils.Charsets
import debop4s.core.utils.Closer._
import java.net.URI
import java.nio.charset.Charset
import org.apache.http._
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.{HttpPost, HttpGet}
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.{HttpClients, CloseableHttpClient}
import org.apache.http.impl.conn.{DefaultProxyRoutePlanner, PoolingHttpClientConnectionManager}
import org.apache.http.util.EntityUtils
import org.slf4j.LoggerFactory
import scala.annotation.varargs
import scala.collection.JavaConversions._
import scala.util.Try
import scala.util.control.NonFatal

/**
 * Http Client
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 13. 오전 9:57
 */
class HttpClient extends AutoCloseable {

  private lazy val log = LoggerFactory.getLogger(getClass)

  private lazy val connectionManager = new PoolingHttpClientConnectionManager()

  lazy val serializer = JacksonSerializer()

  var proxy: HttpHost = null

  def createHttpClient(): CloseableHttpClient = {
    val builder = HttpClients.custom.setConnectionManager(connectionManager)
    if (proxy != null)
      builder.setRoutePlanner(new DefaultProxyRoutePlanner(proxy))
    builder.build()
  }

  @varargs
  def get(uriString: String, headers: Header*): Try[String] =
    get(new URI(uriString), Charsets.UTF_8, headers: _*)

  @varargs
  def get(uriString: String, cs: Charset, headers: Header*): Try[String] =
    get(new URI(uriString), cs, headers: _*)

  @varargs
  def get(uri: URI, headers: Header*): Try[String] =
    get(uri, Charsets.UTF_8, headers: _*)

  @varargs
  def get(uri: URI, cs: Charset, headers: Header*): Try[String] = Try {
    assert(uri != null)

    using(createHttpClient()) { client =>
      val httpget = new HttpGet(uri)

      if (headers != null)
        headers.foreach(httpget.addHeader)

      val response = client.execute(httpget)
      EntityUtils.toString(response.getEntity, cs)
    }
  }

  @varargs
  def post(uriString: String, nvps: Seq[NameValuePair], headers: Header*): Try[String] =
    post(uriString, nvps, Charsets.UTF_8, headers: _*)

  @varargs
  def post(uriString: String, nvps: Seq[NameValuePair], cs: Charset, headers: Header*): Try[String] =
    post(new URI(uriString), nvps, cs, headers: _*)

  @varargs
  def post(uri: URI, nvps: Seq[NameValuePair], headers: Header*): Try[String] =
    post(uri, nvps, Charsets.UTF_8, headers: _*)

  @varargs
  def post(uri: URI, nvps: Seq[NameValuePair], cs: Charset, headers: Header*): Try[String] = Try {
    assert(uri != null)

    using(createHttpClient()) { client =>
      val httppost = new HttpPost(uri)

      if (nvps != null)
        httppost.setEntity(new UrlEncodedFormEntity(nvps, cs))

      if (headers != null)
        headers.foreach(httppost.addHeader)

      val response = client.execute(httppost)
      EntityUtils.toString(response.getEntity, cs)
    }
  }

  @varargs
  def postJson[T](uri: URI, entity: T, headers: Header*): Try[String] =
    postJson[T](uri, entity, Charsets.UTF_8, headers: _*)


  @varargs
  def postJson[T](uri: URI, entity: T, cs: Charset, headers: Header*): Try[String] = Try {
    assert(uri != null)

    using(createHttpClient()) { client =>
      val httppost = new HttpPost(uri)

      if (entity != null) {
        val text = serializer.serializeToText(entity)
        httppost.setEntity(new StringEntity(text, cs))
        httppost.addHeader("content-kind", "application/json")
      }
      if (headers != null)
        headers.foreach(httppost.addHeader)

      val response = client.execute(httppost)
      EntityUtils.toString(response.getEntity, cs)
    }
  }

  def close() {
    try {
      connectionManager.shutdown()
    } catch {
      case NonFatal(ignored) =>
        log.debug("Fail to shutdown connectionManager", ignored)
    }
  }
}
