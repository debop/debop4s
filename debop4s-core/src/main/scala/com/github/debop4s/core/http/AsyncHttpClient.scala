package com.github.debop4s.core.http

import com.github.debop4s.core.parallels.Parallels
import java.lang.String
import java.net.URI
import java.security.KeyStore
import java.util.concurrent.TimeUnit
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods._
import org.apache.http.conn.ssl.{TrustSelfSignedStrategy, SSLContexts}
import org.apache.http.impl.nio.client.{CloseableHttpAsyncClient, HttpAsyncClients}
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy
import org.apache.http.nio.reactor.ConnectingIOReactor
import org.apache.http.{HttpException, HttpResponse}
import scala.annotation.varargs

/**
 * 비동기 Http Client
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 13. 오전 9:57
 */
class AsyncHttpClient {

    private lazy val requestConfig = RequestConfig.custom.setSocketTimeout(3000).setConnectTimeout(3000).build()


    def execute(request: HttpUriRequest): HttpResponse = {
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

    @varargs
    def getAsParallel(httpgets: HttpGet*): Iterable[HttpResponse] =
        Parallels.callEach(httpgets)(request => get(request))

    @varargs
    def getSSLAsParallel(httpgets: HttpGet*): Iterable[HttpResponse] =
        Parallels.callEach(httpgets)(request => getSSL(request))

    def post(httppost: HttpPost): HttpResponse = execute(httppost)

    def postSSL(httppost: HttpPost): HttpResponse = executeSSL(httppost)

    @varargs
    def postAsParallel(httpposts: HttpPost*): Iterable[HttpResponse] =
        Parallels.callEach(httpposts)(request => execute(request))

    @varargs
    def postSSLAsParallel(httpposts: HttpPost*): Iterable[HttpResponse] =
        Parallels.callEach(httpposts)(request => executeSSL(request))

    def delete(httpdelete: HttpDelete): HttpResponse = execute(httpdelete)

    def deleteSSL(httpdelete: HttpDelete): HttpResponse = executeSSL(httpdelete)

    @varargs
    def deleteAsParallel(httpdeletes: HttpDelete*): Iterable[HttpResponse] =
        Parallels.callEach(httpdeletes)(request => execute(request))

    @varargs
    def deleteSSLAsParallel(httpdeletes: HttpDelete*): Iterable[HttpResponse] =
        Parallels.callEach(httpdeletes)(request => executeSSL(request))

    def put(httpput: HttpPut): HttpResponse = execute(httpput)

    def putSSL(httpput: HttpPut): HttpResponse = executeSSL(httpput)

    @varargs
    def putAsParallel(httpputs: HttpPut*): Iterable[HttpResponse] =
        Parallels.callEach(httpputs)(request => execute(request))

    @varargs
    def putSSLAsParallel(httpputs: HttpPut*): Iterable[HttpResponse] =
        Parallels.callEach(httpputs)(request => executeSSL(request))

    def patch(patch: HttpPatch): HttpResponse = execute(patch)

    def patchSSL(patch: HttpPatch): HttpResponse = executeSSL(patch)

    @varargs
    def patchAsParallel(patchs: HttpPatch*): Iterable[HttpResponse] =
        Parallels.callEach(patchs)(request => execute(request))

    def patchSSLAsParallel(patchs: HttpPatch*): Iterable[HttpResponse] =
        Parallels.callEach(patchs)(request => executeSSL(request))

    def head(head: HttpHead): HttpResponse = execute(head)

    def headSSL(head: HttpHead): HttpResponse = executeSSL(head)

    @varargs
    def headAsParallel(heads: HttpHead*): Iterable[HttpResponse] =
        Parallels.callEach(heads)(request => execute(request))

    @varargs
    def headSSLAsParallel(heads: HttpHead*): Iterable[HttpResponse] =
        Parallels.callEach(heads)(request => executeSSL(request))

    private def createConnectionIOReactor(): ConnectingIOReactor =
        new DefaultConnectingIOReactor()

    private def shutdownConnectionManager(connectionManager: PoolingNHttpClientConnectionManager) {
        if (connectionManager != null) {
            connectionManager.shutdown()
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


