package com.github.debop4s.core.http

import com.github.debop4s.core.json.JacksonSerializer
import com.github.debop4s.core.utils.{Strings, Charsets}
import java.net.URI
import java.nio.charset.Charset
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.{HttpDelete, HttpPost, HttpGet, HttpUriRequest}
import org.apache.http.entity.StringEntity
import org.apache.http.util.EntityUtils
import org.apache.http.{NameValuePair, Header, HttpResponse}
import org.slf4j.LoggerFactory
import scala.annotation.varargs
import scala.collection.JavaConversions._


/**
 * 비동기 방식 HTTP 통신을 수행합니다.
 *
 * TODO: spray-http 등 scala 고유의 비동기 library를 사용하도록 변경해야 한다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 13. 오후 1:05
 */
object HttpAsyncs {

    private lazy val log = LoggerFactory.getLogger(getClass)

    lazy val client = new AsyncHttpClient()
    lazy val serializer = JacksonSerializer()

    def execute(request: HttpUriRequest): HttpResponse = client.execute(request)

    def executeSSL(request: HttpUriRequest): HttpResponse =
        client.executeSSL(request)

    def buildHttpGet(uriString: String, headers: Header*): HttpGet =
        buildHttpGet(new URI(uriString), headers: _*)

    def buildHttpGet(uri: URI, headers: Header*): HttpGet = {
        val httpget = new HttpGet(uri)
        headers.foreach(httpget.addHeader)
        httpget
    }

    def get(uriString: String, headers: Header*): HttpResponse =
        client.get(buildHttpGet(uriString, headers: _*))

    def get(uriString: String, cs: Charset, headers: Header*): String =
        getContent(get(uriString, headers: _*), cs)

    def get(uri: URI, headers: Header*): HttpResponse =
        client.get(buildHttpGet(uri, headers: _*))

    def get(uri: URI, cs: Charset, headers: Header*): String =
        getContent(get(uri, headers: _*), cs)

    def get(httpget: HttpGet): HttpResponse = client.get(httpget)

    //    def getAsParallel(uriStrings: String*): List[HttpResponse] =
    //        client.getAsParallel(uriStrings.map(x => buildHttpGet(x)).toSeq: _*)

    //    def getAsParallel(cs: Charset, uriStrings: String*): List[String] =
    //        client.getAsParallel(uriStrings.map(x => buildHttpGet(x)).toSeq: _*)
    //            .map(r => getContent(r, cs))

    //    def getAsParallel(uris: URI*): List[HttpResponse] =
    //        client.getAsParallel(uris.map(x => buildHttpGet(x)).toSeq: _*)

    @varargs
    def getAsParallel(cs: Charset, uris: URI*): Iterable[String] =
        client.getAsParallel(uris.map(x => buildHttpGet(x)).toSeq: _*)
        .map(r => getContent(r, cs))

    @varargs
    def getAsParallel(httpgets: HttpGet*): Iterable[HttpResponse] =
        client.getAsParallel(httpgets: _*)

    @varargs
    def buildHttpPost(uri: URI, nvps: List[NameValuePair], headers: Header*): HttpPost =
        buildHttpPost(uri, nvps, Charsets.UTF_8, headers: _*)

    @varargs
    def buildHttpPost(uri: URI, nvps: List[NameValuePair], cs: Charset, headers: Header*): HttpPost = {
        val httppost = new HttpPost(uri)
        if (nvps != null)
            httppost.setEntity(new UrlEncodedFormEntity(nvps, cs))
        headers.foreach(httppost.addHeader)
        httppost
    }

    @varargs
    def post(uri: URI, nvps: List[NameValuePair], headers: Header*): HttpResponse =
        post(uri, nvps, Charsets.UTF_8, headers: _*)

    @varargs
    def post(uri: URI, nvps: List[NameValuePair], cs: Charset, headers: Header*): HttpResponse =
        client.post(buildHttpPost(uri, nvps, cs, headers: _*))

    @varargs
    def postAsParallel(posts: HttpPost*): Iterable[HttpResponse] =
        client.postAsParallel(posts: _*)

    @varargs
    def buildHttpPostByJson[T](uri: URI, entity: T, cs: Charset, headers: Header*): HttpPost = {
        val httppost = new HttpPost(uri)
        if (entity != null) {
            val text = serializer.serializeToText(entity)
            httppost.setEntity(new StringEntity(text, cs))
            httppost.addHeader("content-type", "application/json")
        }
        headers.foreach(httppost.addHeader)
        httppost
    }

    @varargs
    def postByJson[T](uri: URI, entity: T, headers: Header*): HttpResponse =
        postByJson(uri, entity, Charsets.UTF_8, headers: _*)

    @varargs
    def postByJson[T](uri: URI, entity: T, cs: Charset, headers: Header*): HttpResponse =
        client.post(buildHttpPostByJson(uri, entity, cs, headers: _*))

    @varargs
    def buildHttpDelete(uriString: String, headers: Header*): HttpDelete =
        buildHttpDelete(new URI(uriString), headers: _*)

    @varargs
    def buildHttpDelete(uri: URI, headers: Header*): HttpDelete = {
        val httpdelete = new HttpDelete(uri)
        headers.foreach(httpdelete.setHeader)
        httpdelete
    }

    @varargs
    def delete(uriString: String, headers: Header*): HttpResponse =
        client.delete(buildHttpDelete(uriString))

    @varargs
    def delete(uri: URI, headers: Header*): HttpResponse =
        client.delete(buildHttpDelete(uri))

    def delete(delete: HttpDelete): HttpResponse =
        client.delete(delete)

    @varargs
    def deleteAsParallel(deletes: HttpDelete*): Iterable[HttpResponse] =
        client.deleteAsParallel(deletes: _*)

    @inline
    def getContent(response: HttpResponse, cs: Charset = Charsets.UTF_8): String = {
        if (response == null || response.getEntity == null)
            return Strings.EMPTY_STR

        try {
            EntityUtils.toString(response.getEntity, cs)
        } catch {
            case e: Throwable =>
                log.error("HttpResponse에서 Content를 추출하는데 실패했습니다.", e)
                Strings.EMPTY_STR
        }
    }
}
