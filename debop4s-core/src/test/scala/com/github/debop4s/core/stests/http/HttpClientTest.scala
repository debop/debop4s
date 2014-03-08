package com.github.debop4s.core.stests.http

import com.github.debop4s.core.http.HttpClient
import com.github.debop4s.core.parallels.Parallels
import com.github.debop4s.core.utils.{Strings, Charsets}
import java.net.URI
import org.apache.http.client.ResponseHandler
import org.apache.http.client.fluent.{Form, Request}
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.{HttpClients, BasicResponseHandler}
import org.apache.http.impl.nio.client.HttpAsyncClients
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import org.apache.http.{NameValuePair, HttpStatus, HttpResponse}
import org.fest.assertions.Assertions._
import org.junit.{Ignore, Test}
import org.scalatest.junit.JUnitSuite
import org.slf4j.LoggerFactory
import scala.Predef.String
import scala.collection.mutable.ArrayBuffer

/**
 * com.github.debop4s.core.tests.http.HttpClientTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 15. 오후 2:42
 */
class HttpClientTest extends JUnitSuite {

  lazy val log = LoggerFactory.getLogger(getClass)

  private final val URI_STRING: String = "https://www.google.co.kr"

  @Test
  def httpGetMethod() {
    val httpClient = new HttpClient()
    try {
      val responseStr = httpClient.get(URI_STRING)
      log.debug(s"RESPONSE HTML=$responseStr")
    } finally {
      httpClient.close()
    }
  }

  @Test
  def httpGetWithParams() {
    val uri: URI = new URIBuilder().setPath(URI_STRING + "/search").setParameter("q", "배성혁").setParameter("oq", "배성혁").build
    val httpClient = new HttpClient()
    try {
      val responseStr = httpClient.get(URI_STRING)
      assert(Strings.isNotEmpty(responseStr))
      log.debug(responseStr)
    } finally {
      httpClient.close()
    }
  }

  @Test
  @Ignore("POST 메소드 처리를 해주는 URL이 있어야 합니다.")
  def postWithParams() {
    val httpClient = new HttpClient()

    val uri: URI = new URIBuilder(URI_STRING).build
    val nvps = new ArrayBuffer[NameValuePair]()
    nvps += new BasicNameValuePair("q", "배성혁")
    val responseStr = httpClient.post(uri, nvps.toList)
    assert(Strings.isNotEmpty(responseStr))
    log.debug(responseStr)

  }

  @Test
  def responseHandler() {
    val uri: URI = new URIBuilder().setPath(URI_STRING + "/search").setParameter("q", "배성혁").setParameter("oq", "배성혁").build
    val httpGet: HttpGet = new HttpGet(uri)
    val responseHandler: ResponseHandler[String] = new BasicResponseHandler
    val responseBody = HttpClients.createDefault.execute(httpGet, responseHandler)
    assert(responseBody != null)
    log.debug(responseBody)
  }

  @Test
  def fluentGet() {
    val response: HttpResponse = Request.Get(URI_STRING).execute.returnResponse
    log.debug(EntityUtils.toString(response.getEntity))
    assertThat(response.getStatusLine.getStatusCode).isEqualTo(HttpStatus.SC_OK)
  }

  @Test
  @Ignore("POST 메소드 처리를 해주는 URL이 있어야 합니다.")
  def fluentPost() {
    val response =
      Request.Post(URI_STRING)
        .bodyForm(Form.form.add("username", "vip").add("password", "secret").build)
        .execute
        .returnResponse
    assert(response != null)
    assert(response.getStatusLine.getStatusCode == HttpStatus.SC_OK)
    log.debug(EntityUtils.toString(response.getEntity, Charsets.UTF_8))
  }

  @Test
  def asyncGet() {
    val client = HttpAsyncClients.createDefault()

    client.start()
    val uri: URI = new URIBuilder().setPath(URI_STRING + "/search").setParameter("q", "배성혁").setParameter("oq", "배성혁").build
    val httpGet: HttpGet = new HttpGet(uri)
    val futureResponse = client.execute(httpGet, null)
    while (!futureResponse.isDone) {
      log.debug("...")
      Thread.sleep(1L)
    }
    val response = futureResponse.get
    assert(response != null)
    log.debug(EntityUtils.toString(response.getEntity, Charsets.UTF_8))
  }

  @Test
  def asyncMultipleGet() {
    val connectionManager: PoolingNHttpClientConnectionManager = new PoolingNHttpClientConnectionManager(new DefaultConnectingIOReactor)
    val client = HttpAsyncClients.custom.setConnectionManager(connectionManager).build
    try {
      client.start()
      Parallels.runAction(10) {
        try {
          val uri: URI = new URIBuilder().setPath(URI_STRING + "/search").setParameter("q", "배성혁")
            .setParameter("oq", "배성혁").build
          val httpGet: HttpGet = new HttpGet(uri)
          val futureResponse = client.execute(httpGet, null)
          val response = futureResponse.get
          assert(response != null)
          log.debug(EntityUtils.toString(response.getEntity))
        }
        catch {
          case e: Exception =>
            log.error("예외가 발생했습니다.", e)
        }
      }
    }
    finally {
      client.close()
      connectionManager.shutdown()
    }
  }

}
