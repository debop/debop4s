package debop4s.core.http

import debop4s.core.http.HttpAsyncs
import java.net.URI
import java.security.KeyStore
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import org.apache.http.client.methods.{HttpGet, HttpPost}
import org.apache.http.client.utils.URIBuilder
import org.apache.http.conn.ssl.{TrustSelfSignedStrategy, SSLContexts}
import org.apache.http.impl.nio.client.{CloseableHttpAsyncClient, HttpAsyncClients}
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy
import org.apache.http.util.EntityUtils
import org.apache.http.{HttpException, HttpHeaders, HttpResponse, HttpStatus}
import org.scalatest.FunSuite
import org.slf4j.LoggerFactory

/**
 * debop4s.core.tests.http.AsyncHttpClientTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 15. 오후 2:42
 */
class AsyncHttpClientTest extends FunSuite {

    lazy val log = LoggerFactory.getLogger(getClass)

    val URI_STRING = "https://www.google.co.kr"
    val TEST_COUNT = 30

    private def googleSearchURI(searchStr: String) =
        new URIBuilder()
        .setPath(URI_STRING + "/search")
        .setParameter("q", searchStr)
        .build()

    test("http get method") {
        val httpgets = (0 until TEST_COUNT).map(_ => new HttpGet(googleSearchURI("배성혁")))
        val httpResponses = HttpAsyncs.getAsParallel(httpgets: _*)

        httpResponses.par.foreach {
            response =>
                assert(response != null)
                assert(response.isSuccess)
                assert(response.get.getStatusLine.getStatusCode == HttpStatus.SC_OK)
                log.trace(EntityUtils.toString(response.get.getEntity))
        }
    }

    test("http post method") {
        val httpposts = (0 until TEST_COUNT).map(_ => new HttpPost(googleSearchURI("배성혁")))
        val httpResponses = HttpAsyncs.postAsParallel(httpposts: _*)

        httpResponses.par.foreach {
            response =>
                assert(response != null)
                assert(response.isSuccess)
                assert(response.get.getStatusLine.getStatusCode == HttpStatus.SC_METHOD_NOT_ALLOWED)
                log.trace(EntityUtils.toString(response.get.getEntity))
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
            assert(response != null)
            assert(response.getStatusLine.getStatusCode == HttpStatus.SC_OK)
            log.trace(EntityUtils.toString(response.getEntity))
        } finally {
            client.close()
        }
    }

    test("ssl get simple") {
        val uri = new URIBuilder().setScheme("https").setHost("issues.apache.org").setPort(443).build()

        val response = HttpAsyncs.get(uri)
        assert(response != null)
        assert(response.isSuccess)
        assert(response.get.getStatusLine.getStatusCode == HttpStatus.SC_OK)
        log.trace(EntityUtils.toString(response.get.getEntity))
    }

    test("ssl get sdg") {
        val uri: URI = new URIBuilder()
                       .setScheme("https")
                       .setHost("sdg.sktelecom.com")
                       .setPort(443)
                       .setPath("/api/1-0/devices")
                       .build

        val httpGet: HttpGet = new HttpGet(uri)
        httpGet.setHeader("BP_Access_Token",
            "DCEOB729f3iK8yhbWvRrqHsq5PleOL8EL8C-4WaR6jE6Y47xbJ56zqXVbVHLhPplunETg71Iw0koOITrU3I8LV")

        httpGet.setHeader(HttpHeaders.ACCEPT, "application/json")
        val client: AsyncHttpClient = new AsyncHttpClient
        val response = client.getSSL(httpGet)
        assert(response != null)
        assert(response.isSuccess)
        assert(response.get.getStatusLine.getStatusCode == HttpStatus.SC_OK)
        log.trace(EntityUtils.toString(response.get.getEntity))
    }

    test("execute") {
        val uri: URI = new URIBuilder().setScheme("https").setHost("issues.apache.org").setPort(443).build
        val response = HttpAsyncs.execute(new HttpGet(uri))
        assert(response != null)
        assert(response.isSuccess)
        assert(response.get.getStatusLine.getStatusCode == HttpStatus.SC_OK)
        log.trace(EntityUtils.toString(response.get.getEntity))
    }

    test("execute ssl") {
        val uri: URI = new URIBuilder()
                       .setScheme("https")
                       .setHost("sdg.sktelecom.com")
                       .setPort(443)
                       .setPath("/api/1-0/devices")
                       .build

        val httpGet: HttpGet = new HttpGet(uri)

        httpGet.setHeader("BP_Access_Token",
            "DCEOB729f3iK8yhbWvRrqHsq5PleOL8EL8C-4WaR6jE6Y47xbJ56zqXVbVHLhPplunETg71Iw0koOITrU3I8LV")

        httpGet.setHeader(HttpHeaders.ACCEPT, "application/json")

        val response = HttpAsyncs.executeSSL(httpGet)
        assert(response != null)
        assert(response.isSuccess)
        assert(response.get.getStatusLine.getStatusCode == HttpStatus.SC_OK)
        log.trace(EntityUtils.toString(response.get.getEntity))
    }

    test("custom ssl") {
        val uri = new URIBuilder()
                  .setScheme("https")
                  .setHost("sdg.sktelecom.com")
                  .setPort(443)
                  .setPath("/api/1-0/devices")
                  .build

        val httpGet: HttpGet = new HttpGet(uri)

        httpGet.setHeader("BP_Access_Token",
            "DCEOB729f3iK8yhbWvRrqHsq5PleOL8EL8C-4WaR6jE6Y47xbJ56zqXVbVHLhPplunETg71Iw0koOITrU3I8LV")
        httpGet.setHeader(HttpHeaders.ACCEPT, "application/json")

        val client: CloseableHttpAsyncClient = createHttpAsyncClient(uri)

        try {
            client.start()
            val future = client.execute(httpGet, null)
            val response: HttpResponse = future.get(15, TimeUnit.SECONDS)
            assert(response != null)
            assert(response.getStatusLine.getStatusCode == HttpStatus.SC_OK)
            log.trace(EntityUtils.toString(response.getEntity))
        }
        finally {
            client.close()
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
                    case e: Exception => {
                        log.error("HttpAsyncClient 를 생성하는데 실패했습니다.", e)
                    }
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
            val sslcontext: SSLContext = SSLContexts.custom.loadTrustMaterial(trustStore, new TrustSelfSignedStrategy).build
            new SSLIOSessionStrategy(sslcontext, Array[String]("TLSv1"), null, SSLIOSessionStrategy.ALLOW_ALL_HOSTNAME_VERIFIER)
        }
        catch {
            case e: Exception => throw new HttpException("SSLIOSessionStrategy 를 생성하는데 실패했습니다.", e)
        }
    }
}
