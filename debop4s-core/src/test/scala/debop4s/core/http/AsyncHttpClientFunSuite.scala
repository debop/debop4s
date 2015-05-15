package debop4s.core.http

import java.net.URI
import java.security.KeyStore
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier

import debop4s.core.AbstractCoreFunSuite
import org.apache.http.client.methods.{ HttpGet, HttpPost }
import org.apache.http.client.utils.URIBuilder
import org.apache.http.conn.ssl.{ SSLContexts, AllowAllHostnameVerifier, TrustSelfSignedStrategy }
import org.apache.http.impl.nio.client.{ CloseableHttpAsyncClient, HttpAsyncClients }
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy
import org.apache.http.util.EntityUtils
import org.apache.http.{ HttpException, HttpStatus }

import scala.util.control.NonFatal
import scala.util.{ Failure, Success }

/**
 * debop4s.core.tests.http.AsyncHttpClientFunSuite
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 15. 오후 2:42
 */
class AsyncHttpClientFunSuite extends AbstractCoreFunSuite {

  val URI_STRING = "https://api.duckduckgo.com/"
  val TEST_COUNT = 30

  private def searchURI(searchStr: String) =
    new URIBuilder()
    // .setPath(URI_STRING + "/search")
    .setPath(URI_STRING)
    .setParameter("q", searchStr)
    .setParameter("format", "json")
    .setParameter("pretty", "1")
    .build()

  test("http get method") {
    val httpgets = (0 until TEST_COUNT).par.map(_ => new HttpGet(searchURI("배성혁"))).seq
    val httpResponses = HttpAsyncs.getAsParallel(httpgets: _*)

    httpResponses.par.foreach {
      case Success(response) =>
        response should not be null
        response.getStatusLine.getStatusCode shouldEqual HttpStatus.SC_OK
        log.debug(EntityUtils.toString(response.getEntity))
      case Failure(ex) =>
        fail(ex)
    }
  }

  test("http post method") {
    val httpposts = (0 until TEST_COUNT).par.map(_ => new HttpPost(searchURI("배성혁"))).seq
    val httpResponses = HttpAsyncs.postAsParallel(httpposts: _*)

    httpResponses.par.foreach {
      case Success(response) =>
        response should not be null
        response.getStatusLine.getStatusCode shouldEqual HttpStatus.SC_METHOD_NOT_ALLOWED
        log.debug(EntityUtils.toString(response.getEntity))
      case Failure(ex) =>
        fail(ex)
    }
  }

  test("ssl test") {
    val uri = new URIBuilder().setScheme("https").setHost("issues.apache.org").setPort(443).build()
    val client = HttpAsyncClients.createDefault()
    val httpget = new HttpGet(uri)

    try {
      client.start()
      val future = client.execute(httpget, null)
      val response = future.get(15, TimeUnit.SECONDS)
      response should not be null
      response.getStatusLine.getStatusCode shouldEqual HttpStatus.SC_OK
      log.trace(EntityUtils.toString(response.getEntity))
    } finally {
      client.close()
    }
  }

  test("ssl get simple") {
    val uri = new URIBuilder().setScheme("https").setHost("issues.apache.org").setPort(443).build()

    HttpAsyncs.get(uri) match {
      case Success(response) =>
        response should not be null
        response.getStatusLine.getStatusCode shouldEqual HttpStatus.SC_OK
        log.trace(EntityUtils.toString(response.getEntity))
      case Failure(ex) =>
        fail(ex)
    }
  }

  test("execute") {
    val uri: URI = new URIBuilder().setScheme("https").setHost("issues.apache.org").setPort(443).build

    HttpAsyncs.execute(new HttpGet(uri)) match {
      case Success(response) =>
        response should not be null
        response.getStatusLine.getStatusCode shouldEqual HttpStatus.SC_OK
        log.trace(EntityUtils.toString(response.getEntity))
      case Failure(ex) =>
        fail(ex)
    }
  }

  def createHttpAsyncClient(uri: URI): CloseableHttpAsyncClient = {
    if (uri != null && uri.getScheme != null) {
      val scheme: String = uri.getScheme
      if (scheme.toLowerCase == "https") {
        try {
          val strategy: SSLIOSessionStrategy = createSslIOSessionStrategy()
          return HttpAsyncClients.custom.setSSLStrategy(strategy).build
        }
        catch {
          case NonFatal(e) =>
            log.error("HttpAsyncClient 를 생성하는데 실패했습니다.", e)
        }
      }
    }
    HttpAsyncClients.createDefault
  }

  private def createSslIOSessionStrategy(): SSLIOSessionStrategy = {
    try {
      log.trace("SSLIOSessionStrategy를 생성합니다...")
      val trustStore: KeyStore = KeyStore.getInstance(KeyStore.getDefaultType)
      trustStore.load(null, null)

      // val sslcontext = SSLContexts.custom.loadTrustMaterial(trustStore, new TrustSelfSignedStrategy).build
      // new SSLIOSessionStrategy(sslcontext, Array[String]("TLSv1"), null, new AllowAllHostnameVerifier())
      val sslcontext = SSLContexts.custom
                       .loadTrustMaterial(trustStore, new TrustSelfSignedStrategy)
                       .build
      new SSLIOSessionStrategy(sslcontext,
                                Array[String]("TLSv1"),
                                null,
                                new AllowAllHostnameVerifier().asInstanceOf[HostnameVerifier])
    }
    catch {
      case NonFatal(e) => throw new HttpException("SSLIOSessionStrategy 를 생성하는데 실패했습니다.", e)
    }
  }
}
