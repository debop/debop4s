package kr.debop4s.core.http

import java.net.URI
import java.nio.charset.Charset
import kr.debop4s.core.json.JacksonSerializer
import kr.debop4s.core.logging.Logger
import kr.debop4s.core.utils.Charsets
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.{HttpDelete, HttpPost, HttpGet, HttpUriRequest}
import org.apache.http.entity.StringEntity
import org.apache.http.util.EntityUtils
import org.apache.http.{NameValuePair, Header, HttpResponse}
import scala.collection.JavaConversions._


/**
 * kr.debop4s.core.http.HttpAsyncs
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 13. 오후 1:05
 */
object HttpAsyncs {

    implicit lazy val log = Logger(getClass)

    lazy val client = new AsyncHttpClient()
    lazy val serializer = JacksonSerializer()

    def execute(request: HttpUriRequest): HttpResponse = client.execute(request)

    def executeSSL(request: HttpUriRequest): HttpResponse =
        client.executeSSL(request)

    def buildHttpGet(uriString: String, headers: Header*): HttpGet =
        buildHttpGet(new URI(uriString), headers: _*)

    def buildHttpGet(uri: URI, headers: Header*): HttpGet = {
        val httpget = new HttpGet(uri)
        headers.foreach(h => httpget.addHeader(h))
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

    def getAsParallel(cs: Charset, uris: URI*): List[String] =
        client.getAsParallel(uris.map(x => buildHttpGet(x)).toSeq: _*)
            .map(r => getContent(r, cs))

    def getAsParallel(httpgets: HttpGet*): List[HttpResponse] =
        client.getAsParallel(httpgets: _*)

    def buildHttpPost(uri: URI, nvps: List[NameValuePair], headers: Header*): HttpPost = {
        buildHttpPost(uri, nvps, Charsets.UTF_8, headers: _*)
    }

    def buildHttpPost(uri: URI, nvps: List[NameValuePair], cs: Charset, headers: Header*): HttpPost = {
        val httppost = new HttpPost(uri)
        if (nvps != null)
            httppost.setEntity(new UrlEncodedFormEntity(nvps, cs))
        headers.foreach(h => httppost.addHeader(h))
        httppost
    }

    def post(uri: URI, nvps: List[NameValuePair], headers: Header*): HttpResponse =
        post(uri, nvps, Charsets.UTF_8, headers: _*)

    def post(uri: URI, nvps: List[NameValuePair], cs: Charset, headers: Header*): HttpResponse =
        client.post(buildHttpPost(uri, nvps, cs, headers: _*))

    def postAsParallel(posts: HttpPost*): List[HttpResponse] =
        client.postAsParallel(posts: _*)

    def buildHttpPostByJson[T <: AnyRef](uri: URI, entity: T, cs: Charset, headers: Header*): HttpPost = {
        val httppost = new HttpPost(uri)
        if (entity != null) {
            val text = serializer.serializeToText(entity)
            httppost.setEntity(new StringEntity(text, cs))
            httppost.addHeader("content-type", "application/json")
        }
        headers.foreach(h => httppost.addHeader(h))
        httppost
    }

    def postByJson[T <: AnyRef](uri: URI, entity: T, headers: Header*): HttpResponse =
        postByJson(uri, entity, Charsets.UTF_8, headers: _*)

    def postByJson[T <: AnyRef](uri: URI, entity: T, cs: Charset, headers: Header*): HttpResponse =
        client.post(buildHttpPostByJson(uri, entity, cs, headers: _*))

    def buildHttpDelete(uriString: String, headers: Header*): HttpDelete =
        buildHttpDelete(new URI(uriString), headers: _*)

    def buildHttpDelete(uri: URI, headers: Header*): HttpDelete = {
        val httpdelete = new HttpDelete(uri)
        headers.foreach(h => httpdelete.setHeader(h))
        httpdelete
    }

    def delete(uriString: String, headers: Header*): HttpResponse =
        client.delete(buildHttpDelete(uriString))

    def delete(uri: URI, headers: Header*): HttpResponse =
        client.delete(buildHttpDelete(uri))

    def delete(delete: HttpDelete): HttpResponse =
        client.delete(delete)


    //    def deleteAsParallel(uriStrings: String*): List[HttpResponse] =
    //        deleteAsParallel(uriStrings.map(x => buildHttpDelete(x)).toSeq: _*)

    //    def deleteAsParallel(uris: URI*): List[HttpResponse] =
    //        deleteAsParallel(uris.map(x => buildHttpDelete(x)).toSeq: _*)

    def deleteAsParallel(deletes: HttpDelete*): List[HttpResponse] =
        client.deleteAsParallel(deletes: _*)

    def getContent(response: HttpResponse, cs: Charset = Charsets.UTF_8): String = {
        if (response == null || response.getEntity == null)
            null
        try {
            EntityUtils.toString(response.getEntity, cs)
        } catch {
            case e: Throwable => log.error("HttpResponse에서 Content를 추출하는데 실패했습니다.", e)
        }
        null
    }
}
