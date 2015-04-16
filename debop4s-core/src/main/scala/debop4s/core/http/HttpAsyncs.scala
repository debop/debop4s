package debop4s.core.http

import java.net.URI
import java.nio.charset.Charset
import java.util.{List => JList}

import debop4s.core.json.JacksonSerializer
import debop4s.core.utils.Charsets
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.{HttpDelete, HttpGet, HttpPost, HttpUriRequest}
import org.apache.http.entity.StringEntity
import org.apache.http.util.EntityUtils
import org.apache.http.{Header, HttpResponse, NameValuePair}
import org.slf4j.LoggerFactory

import scala.annotation.varargs
import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}


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

  def execute(request: HttpUriRequest): Try[HttpResponse] =
    client.execute(request)

  def executeSSL(request: HttpUriRequest): Try[HttpResponse] =
    client.executeSSL(request)

  @varargs
  def buildHttpGet(uriString: String, headers: Header*): HttpGet =
    buildHttpGet(new URI(uriString), headers: _*)

  @varargs
  def buildHttpGet(uri: URI, headers: Header*): HttpGet = {
    val httpget = new HttpGet(uri)
    headers.foreach(httpget.addHeader)
    httpget
  }

  @varargs
  def get(uriString: String, headers: Header*): Try[HttpResponse] = {
    client.get(buildHttpGet(uriString, headers: _*))
  }

  @varargs
  def get(uriString: String, cs: Charset, headers: Header*): String = {
    getContent(get(uriString, headers: _*), cs)
  }

  @varargs
  def get(uri: URI, headers: Header*): Try[HttpResponse] =
    client.get(buildHttpGet(uri, headers: _*))

  def get(uri: URI, cs: Charset, headers: Header*): String = {
    getContent(get(uri, headers: _*), cs)
  }

  def get(httpget: HttpGet): Try[HttpResponse] = client.get(httpget)

  @varargs
  def getAsParallel(cs: Charset, uris: URI*): Seq[String] = {
    getAsParallel(uris.map(x => buildHttpGet(x)).toSeq: _*)
    .map(r => getContent(r, cs))
  }

  @varargs
  def getAsParallel(httpgets: HttpGet*): Seq[Try[HttpResponse]] = {
    client.getAsParallel(httpgets: _*)
  }

  @varargs
  def buildHttpPost(uri: URI, nvps: Seq[NameValuePair], headers: Header*): HttpPost = {
    buildHttpPost(uri, nvps, Charsets.UTF_8, headers: _*)
  }

  @varargs
  def buildHttpPost(uri: URI, nvps: Seq[NameValuePair], cs: Charset, headers: Header*): HttpPost = {
    val httppost = new HttpPost(uri)
    if (nvps != null)
      httppost.setEntity(new UrlEncodedFormEntity(nvps.asJava, cs))

    headers.foreach(httppost.addHeader)
    httppost
  }


  @varargs
  def post(uri: URI, nvps: Seq[NameValuePair], headers: Header*): Try[HttpResponse] =
    post(uri, nvps, Charsets.UTF_8, headers: _*)

  @varargs
  def post(uri: URI, nvps: Seq[NameValuePair], cs: Charset, headers: Header*): Try[HttpResponse] =
    client.post(buildHttpPost(uri, nvps, cs, headers: _*))


  def post(httpPost: HttpPost): Try[HttpResponse] = client.post(httpPost)

  @varargs
  def postAsParallel(posts: HttpPost*): Seq[Try[HttpResponse]] = {
    client.postAsParallel(posts: _*)
  }

  @varargs
  def buildHttpPostByJson[T](uri: URI, entity: T, cs: Charset, headers: Header*): HttpPost = {
    val httppost = new HttpPost(uri)
    if (entity != null) {
      val text = serializer.serializeToText(entity)
      httppost.setEntity(new StringEntity(text, cs))
      httppost.addHeader("content-kind", "application/json")
    }

    headers.foreach(httppost.addHeader)
    httppost
  }

  @varargs
  def postByJson[T](uri: URI, entity: T, headers: Header*): Try[HttpResponse] =
    postByJson(uri, entity, Charsets.UTF_8, headers: _*)

  @varargs
  def postByJson[T](uri: URI, entity: T, cs: Charset, headers: Header*): Try[HttpResponse] =
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
  def delete(uriString: String, headers: Header*): Try[HttpResponse] =
    client.delete(buildHttpDelete(uriString))

  @varargs
  def delete(uri: URI, headers: Header*): Try[HttpResponse] =
    client.delete(buildHttpDelete(uri))

  def delete(delete: HttpDelete): Try[HttpResponse] =
    client.delete(delete)

  @varargs
  def deleteAsParallel(deletes: HttpDelete*): Seq[Try[HttpResponse]] =
    client.deleteAsParallel(deletes: _*)

  def getContent(response: Try[HttpResponse], cs: Charset = Charsets.UTF_8): String = {
    response match {
      case Success(res) => EntityUtils.toString(res.getEntity, cs)
      case Failure(e) =>
        log.warn(s"Http 통신에 예외가 발생했습니다.", e)
        ""
    }
  }
}
