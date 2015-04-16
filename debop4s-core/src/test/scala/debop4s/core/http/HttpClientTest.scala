package debop4s.core.http

import java.net.URI

import debop4s.core.AbstractCoreFunSuite
import debop4s.core.parallels.Parallels
import debop4s.core.utils.{Charsets, Strings}
import org.apache.http.client.ResponseHandler
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.{BasicResponseHandler, HttpClients}
import org.apache.http.impl.nio.client.HttpAsyncClients
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils

import scala.util.control.NonFatal

/**
 * debop4s.core.tests.http.HttpClientTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 15. 오후 2:42
 */
class HttpClientTest extends AbstractCoreFunSuite {

  val URI_STRING: String = "https://api.duckduckgo.com/"

  private def searchURI(searchStr: String) =
    new URIBuilder()
    // .setPath(URI_STRING + "/search")
    .setPath(URI_STRING)
    .setParameter("q", searchStr)
    .setParameter("format", "json")
    .setParameter("pretty", "1")
    .build()

  test("http get") {
    val httpClient = new HttpClient()
    try {
      val responseStr = httpClient.get(URI_STRING)
      log.trace(s"RESPONSE HTML=$responseStr")
    } finally {
      httpClient.close()
    }
  }

  test("http get with parameters") {
    val uri = searchURI("scala")
    val httpClient = new HttpClient()
    try {
      val responseStr = httpClient.get(URI_STRING)
      responseStr.isSuccess shouldEqual true
      Strings.isNotEmpty(responseStr.getOrElse("")) shouldEqual true
      log.trace(responseStr.getOrElse(""))
    } finally {
      httpClient.close()
    }
  }

  test("http post with parameters") {
    val httpClient = new HttpClient()

    val uri: URI = new URIBuilder(URI_STRING).build
    val nvps = Seq(new BasicNameValuePair("q", "scala"))

    val responseStr = httpClient.post(uri, nvps)
    responseStr.isSuccess shouldEqual true
    Strings.isNotEmpty(responseStr.getOrElse("")) shouldEqual true
    log.trace(responseStr.getOrElse(""))

  }

  test("response header") {
    val uri = searchURI("scala")
    val httpGet: HttpGet = new HttpGet(uri)
    val responseHandler: ResponseHandler[String] = new BasicResponseHandler
    val responseBody = HttpClients.createDefault.execute(httpGet, responseHandler)
    responseBody should not be null
    log.trace(responseBody)
  }

  //    @Test
  //    def fluentGet() {
  //        val response: HttpResponse = Request.Get(URI_STRING).execute.returnResponse
  //        log.trace(EntityUtils.toString(response.getEntity))
  //        response.getStatusLine.getStatusCode shouldEqual HttpStatus.SC_OK
  //    }
  //
  //    @Test
  //    @Ignore("POST 메소드 처리를 해주는 URL이 있어야 합니다.")
  //    def fluentPost() {
  //        val response =
  //            Request.Post(URI_STRING)
  //            .bodyForm(Form.form.add("username", "vip").add("password", "secret").build)
  //            .execute
  //            .returnResponse
  //        response should not be null
  //        response.getStatusLine.getStatusCode shouldEqual HttpStatus.SC_OK
  //        log.trace(EntityUtils.toString(response.getEntity, Charsets.UTF_8))
  //    }

  test("async get") {
    val client = HttpAsyncClients.createDefault()

    client.start()
    val uri = searchURI("scala")
    val httpGet: HttpGet = new HttpGet(uri)
    val futureResponse = client.execute(httpGet, null)
    while (!futureResponse.isDone) {
      log.trace("...")
      Thread.sleep(1L)
    }
    val response = futureResponse.get
    response should not be null
    log.trace(EntityUtils.toString(response.getEntity, Charsets.UTF_8))
  }

  test("async multiple get") {
    val connectionManager = new PoolingNHttpClientConnectionManager(new DefaultConnectingIOReactor)
    val client = HttpAsyncClients.custom.setConnectionManager(connectionManager).build

    try {
      client.start()
      Parallels.runAction(10) {
        try {
          val uri = new URIBuilder()
                    .setPath(URI_STRING + "/search")
                    .setParameter("q", "배성혁")
                    .setParameter("oq", "배성혁")
                    .build

          val httpGet: HttpGet = new HttpGet(uri)
          val futureResponse = client.execute(httpGet, null)
          val response = futureResponse.get
          response should not be null
          log.trace(EntityUtils.toString(response.getEntity))
        }
        catch {
          case NonFatal(e) => log.error("예외가 발생했습니다.", e)
        }
      }
    }
    finally {
      client.close()
      connectionManager.shutdown()
    }
  }
}
