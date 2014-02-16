package kr.debop4s.core.http

import java.lang.String
import java.net.URI
import java.security.KeyStore
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import kr.debop4s.core.parallels.Parallels
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods._
import org.apache.http.conn.ssl.{TrustSelfSignedStrategy, SSLContexts}
import org.apache.http.impl.nio.client.{CloseableHttpAsyncClient, HttpAsyncClients}
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy
import org.apache.http.nio.reactor.ConnectingIOReactor
import org.apache.http.{HttpException, HttpResponse}
import org.slf4j.LoggerFactory

/**
 * kr.debop4s.core.http.AsyncHttpClient
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 13. 오전 9:57
 */
class AsyncHttpClient {

  lazy val log = LoggerFactory.getLogger(getClass)
  lazy val requestConfig = RequestConfig.custom.setSocketTimeout(3000).setConnectTimeout(3000).build()


  def execute(request: HttpUriRequest): HttpResponse = {
    log.trace(s"Http Request를 수행합니다. request=[$request]")
    val client = HttpAsyncClients.createDefault()
    try {
      client.start()
      val future = client.execute(request, null)
      future.get(15, TimeUnit.SECONDS)
    } finally {
      client.close()
    }
  }

  def executeSSL(request: HttpUriRequest): HttpResponse = {
    log.trace(s"Http Request를 수행합니다. request=[$request]")
    val client = createHttpAsyncClient(request.getURI)
    try {
      client.start()
      val future = client.execute(request, null)
      future.get(15, TimeUnit.SECONDS)
    } finally {
      client.close()
    }
  }

  def get(httpget: HttpGet): HttpResponse = execute(httpget)

  def getSSL(httpget: HttpGet): HttpResponse = executeSSL(httpget)

  def getAsParallel(httpgets: HttpGet*): List[HttpResponse] =
    Parallels.callEach(httpgets)(request => get(request))

  def getSSLAsParallel(httpgets: HttpGet*): List[HttpResponse] =
    Parallels.callEach(httpgets)(request => getSSL(request))

  def post(httppost: HttpPost): HttpResponse = execute(httppost)

  def postSSL(httppost: HttpPost): HttpResponse = executeSSL(httppost)

  def postAsParallel(httpposts: HttpPost*): List[HttpResponse] =
    Parallels.callEach(httpposts)(request => execute(request))

  def postSSLAsParallel(httpposts: HttpPost*): List[HttpResponse] =
    Parallels.callEach(httpposts)(request => executeSSL(request))

  def delete(httpdelete: HttpDelete): HttpResponse = execute(httpdelete)

  def deleteSSL(httpdelete: HttpDelete): HttpResponse = executeSSL(httpdelete)

  def deleteAsParallel(httpdeletes: HttpDelete*): List[HttpResponse] =
    Parallels.callEach(httpdeletes)(request => execute(request))

  def deleteSSLAsParallel(httpdeletes: HttpDelete*): List[HttpResponse] =
    Parallels.callEach(httpdeletes)(request => executeSSL(request))

  def put(httpput: HttpPut): HttpResponse = execute(httpput)

  def putSSL(httpput: HttpPut): HttpResponse = executeSSL(httpput)

  def putAsParallel(httpputs: HttpPut*): List[HttpResponse] =
    Parallels.callEach(httpputs)(request => execute(request))

  def putSSLAsParallel(httpputs: HttpPut*): List[HttpResponse] =
    Parallels.callEach(httpputs)(request => executeSSL(request))

  def patch(patch: HttpPatch): HttpResponse = execute(patch)

  def patchSSL(patch: HttpPatch): HttpResponse = executeSSL(patch)

  def patchAsParallel(patchs: HttpPatch*): List[HttpResponse] =
    Parallels.callEach(patchs)(request => execute(request))

  def patchSSLAsParallel(patchs: HttpPatch*): List[HttpResponse] =
    Parallels.callEach(patchs)(request => executeSSL(request))

  def head(head: HttpHead): HttpResponse = execute(head)

  def headSSL(head: HttpHead): HttpResponse = executeSSL(head)

  def headAsParallel(heads: HttpHead*): List[HttpResponse] =
    Parallels.callEach(heads)(request => execute(request))

  def headSSLAsParallel(heads: HttpHead*): List[HttpResponse] =
    Parallels.callEach(heads)(request => executeSSL(request))

  private def createConnectionIOReactor(): ConnectingIOReactor = new DefaultConnectingIOReactor()

  private def shutdownConnectionManager(connectionManager: PoolingNHttpClientConnectionManager) {
    if (connectionManager != null) {
      connectionManager.shutdown()
    }
  }

  private def createHttpAsyncClient(uri: URI): CloseableHttpAsyncClient = {
    if (uri != null && uri.getScheme != null) {
      val scheme: String = uri.getScheme
      if (scheme.toLowerCase == "https") {
        try {
          val strategy: SSLIOSessionStrategy = createSslIOSessionStrategy
          return HttpAsyncClients.custom.setSSLStrategy(strategy).build
        }
        catch {
          case e: Exception =>
            log.error("HttpAsyncClient 를 생성하는데 실패했습니다.", e)
        }
      }
    }
    HttpAsyncClients.createDefault
  }

  private def createSslIOSessionStrategy: SSLIOSessionStrategy = {
    try {
      log.trace("SSLIOSessionStrategy를 생성합니다...")
      val trustStore: KeyStore = KeyStore.getInstance(KeyStore.getDefaultType)
      trustStore.load(null, null)

      val sslcontext: SSLContext =
        SSLContexts.custom
        .loadTrustMaterial(trustStore, new TrustSelfSignedStrategy)
        .build
      new SSLIOSessionStrategy(sslcontext,
                                Array[String]("TLSv1"),
                                null,
                                SSLIOSessionStrategy.ALLOW_ALL_HOSTNAME_VERIFIER)
    }
    catch {
      case e: Exception =>
        throw new HttpException("SSLIOSessionStrategy를 빌드하는데 실패했습니다.", e)
    }
  }
}


