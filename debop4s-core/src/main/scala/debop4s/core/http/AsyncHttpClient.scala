package debop4s.core.http

import debop4s.core.utils.Closer._
import java.lang.String
import java.net.URI
import java.security.KeyStore
import java.util.concurrent.TimeUnit
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods._
import org.apache.http.conn.ssl.{ TrustSelfSignedStrategy, SSLContexts }
import org.apache.http.impl.nio.client.{ CloseableHttpAsyncClient, HttpAsyncClients }
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy
import org.apache.http.nio.reactor.ConnectingIOReactor
import org.apache.http.{ HttpException, HttpResponse }
import scala.annotation.varargs
import scala.util.Try

/**
 * 비동기 Http Client
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 13. 오전 9:57
 */
class AsyncHttpClient {

  private lazy val requestConfig = RequestConfig.custom.setSocketTimeout(3000).setConnectTimeout(3000).build()

  val DEFAULT_TIMEOUT = 90

  def execute(request: HttpUriRequest): Try[HttpResponse] = Try {
    assert(request != null)

    using(HttpAsyncClients.createDefault()) {
      client =>
        client.start()
        val future = client.execute(request, null)
        future.get(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
    }
  }

  def executeSSL(request: HttpUriRequest): Try[HttpResponse] = Try {
    assert(request != null)

    using(createHttpAsyncClient(request.getURI)) {
      client =>
        client.start()
        val future = client.execute(request, null)
        future.get(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
    }
  }

  def get(httpget: HttpGet): Try[HttpResponse] = execute(httpget)

  def getSSL(httpget: HttpGet): Try[HttpResponse] = executeSSL(httpget)

  @varargs
  def getAsParallel(httpgets: HttpGet*): Seq[Try[HttpResponse]] = {
    httpgets.par.map(execute).seq
  }

  @varargs
  def getSSLAsParallel(httpgets: HttpGet*): Seq[Try[HttpResponse]] = {
    httpgets.par.map(executeSSL).seq
  }

  def post(httppost: HttpPost): Try[HttpResponse] = execute(httppost)

  def postSSL(httppost: HttpPost): Try[HttpResponse] = executeSSL(httppost)

  @varargs
  def postAsParallel(httpposts: HttpPost*): Seq[Try[HttpResponse]] = {
    httpposts.par.map(execute).seq
  }

  @varargs
  def postSSLAsParallel(httpposts: HttpPost*): Seq[Try[HttpResponse]] = {
    httpposts.par.map(executeSSL).seq
  }

  def delete(httpdelete: HttpDelete): Try[HttpResponse] = execute(httpdelete)

  def deleteSSL(httpdelete: HttpDelete): Try[HttpResponse] = executeSSL(httpdelete)

  @varargs
  def deleteAsParallel(httpdeletes: HttpDelete*): Seq[Try[HttpResponse]] = {
    httpdeletes.par.map(execute).seq
  }

  @varargs
  def deleteSSLAsParallel(httpdeletes: HttpDelete*): Seq[Try[HttpResponse]] = {
    httpdeletes.par.map(executeSSL).seq
  }

  def put(httpput: HttpPut): Try[HttpResponse] = execute(httpput)

  def putSSL(httpput: HttpPut): Try[HttpResponse] = executeSSL(httpput)

  @varargs
  def putAsParallel(httpputs: HttpPut*): Seq[Try[HttpResponse]] = {
    httpputs.par.map(execute).seq
  }

  @varargs
  def putSSLAsParallel(httpputs: HttpPut*): Seq[Try[HttpResponse]] = {
    httpputs.par.map(executeSSL).seq
  }

  def patch(patch: HttpPatch): Try[HttpResponse] = execute(patch)

  def patchSSL(patch: HttpPatch): Try[HttpResponse] = executeSSL(patch)

  @varargs
  def patchAsParallel(patches: HttpPatch*): Seq[Try[HttpResponse]] = {
    patches.par.map(execute).seq
  }

  def patchSSLAsParallel(patches: HttpPatch*): Seq[Try[HttpResponse]] = {
    patches.par.map(executeSSL).seq
  }

  def head(head: HttpHead): Try[HttpResponse] = execute(head)

  def headSSL(head: HttpHead): Try[HttpResponse] = executeSSL(head)

  @varargs
  def headAsParallel(heads: HttpHead*): Seq[Try[HttpResponse]] = {
    heads.par.map(execute).seq
  }

  @varargs
  def headSSLAsParallel(heads: HttpHead*): Seq[Try[HttpResponse]] = {
    heads.par.map(executeSSL).seq
  }

  private def createConnectionIOReactor(): ConnectingIOReactor = {
    new DefaultConnectingIOReactor()
  }

  private def shutdownConnectionManager(connectionManager: PoolingNHttpClientConnectionManager) {
    if (connectionManager != null) {
      Try {
        connectionManager.shutdown()
      }
    }
  }

  private def createHttpAsyncClient(uri: URI): CloseableHttpAsyncClient = {
    if (uri != null && uri.getScheme != null) {
      val scheme: String = uri.getScheme
      if (scheme.toLowerCase == "https") {
        val strategy: SSLIOSessionStrategy = createSslIOSessionStrategy
        return HttpAsyncClients.custom.setSSLStrategy(strategy).build
      }
    }
    HttpAsyncClients.createDefault
  }

  @inline
  private def createSslIOSessionStrategy: SSLIOSessionStrategy = {
    try {
      val trustStore: KeyStore = KeyStore.getInstance(KeyStore.getDefaultType)
      trustStore.load(null, null)

      val sslcontext = SSLContexts.custom
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


