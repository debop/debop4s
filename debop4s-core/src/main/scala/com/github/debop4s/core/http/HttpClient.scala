package com.github.debop4s.core.http

import com.github.debop4s.core.json.JacksonSerializer
import com.github.debop4s.core.utils.Charsets
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

/**
 * com.github.debop4s.core.http.HttpClient
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 13. 오전 9:57
 */
class HttpClient extends AutoCloseable {

    private lazy val log = LoggerFactory.getLogger(getClass)

    private lazy val connectionManager = new PoolingHttpClientConnectionManager()

    val serializer = JacksonSerializer()

    var proxy: HttpHost = _

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
    @inline
    def get(uri: URI, cs: Charset, headers: Header*): Try[String] = Try {
        val client = createHttpClient()
        val httpget = new HttpGet(uri)

        try {
            if (headers != null)
                headers.foreach(httpget.addHeader)

            val response = client.execute(httpget)
            EntityUtils.toString(response.getEntity, cs)
        } finally {
            client.close()
        }
    }

    @varargs
    def post(uriString: String, nvps: List[NameValuePair], headers: Header*): Try[String] =
        post(uriString, nvps, Charsets.UTF_8, headers: _*)

    @varargs
    def post(uriString: String, nvps: List[NameValuePair], cs: Charset, headers: Header*): Try[String] =
        post(new URI(uriString), nvps, cs, headers: _*)

    @varargs
    def post(uri: URI, nvps: List[NameValuePair], headers: Header*): Try[String] =
        post(uri, nvps, Charsets.UTF_8, headers: _*)

    @varargs
    @inline
    def post(uri: URI, nvps: List[NameValuePair], cs: Charset, headers: Header*): Try[String] = Try {
        assert(uri != null)
        val client = createHttpClient()
        val httppost = new HttpPost(uri)

        try {
            if (nvps != null)
                httppost.setEntity(new UrlEncodedFormEntity(nvps, cs))

            if (headers != null)
                headers.foreach(httppost.addHeader)

            val response = client.execute(httppost)
            EntityUtils.toString(response.getEntity, cs)
        } finally {
            client.close()
        }
    }

    @varargs
    def postJson[T](uri: URI, entity: T, headers: Header*): Try[String] = {
        postJson[T](uri, entity, Charsets.UTF_8, headers: _*)
    }

    @varargs
    @inline
    def postJson[T](uri: URI, entity: T, cs: Charset, headers: Header*): Try[String] = Try {
        assert(uri != null)
        val client = createHttpClient()
        val httppost = new HttpPost(uri)

        try {
            if (entity != null) {
                val text = serializer.serializeToText(entity)
                httppost.setEntity(new StringEntity(text, cs))
                httppost.addHeader("content-type", "application/json")
            }
            if (headers != null)
                headers.foreach(httppost.addHeader)

            val response = client.execute(httppost)
            EntityUtils.toString(response.getEntity, cs)
        } finally {
            client.close()
        }
    }

    def close() {
        try {
            connectionManager.shutdown()
        } catch {
            case ignored: Throwable => log.debug("Fail to shutdown connectionManager", ignored)
        }
    }
}
