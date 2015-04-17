package debop4s.web.spring.utils

import java.nio.charset.Charset
import java.util.{Map => JMap, Set => JSet}

import debop4s.core.json.JacksonSerializer
import debop4s.core.utils.Strings
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType

/**
 * Spring MVC 를 이용하는 Web Application 을 위한 Helper class 입니다.
 * @author Sunghyouk Bae
 */
object WebUtil {

  private val log = LoggerFactory.getLogger(getClass)

  val APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType,
    MediaType.APPLICATION_JSON.getSubtype,
    Charset.forName("utf8"))

  lazy val jacksonSerializer = new JacksonSerializer()

  /**
   * 객체를 Json 직렬화를 수행하고, 바이트 배열로 반환합니다.
   * @param obj Json 직렬화할 객체
   * @return  Json 직렬화된 정보를 담은 바이트 배열
   */
  def convertObjectToJsonBytes(obj: Any): Array[Byte] = {
    jacksonSerializer.serialize(obj)
  }

  /**
   * 객체가 Map 인 경우, Map의 정보를 HTTP GET 방식의 URL 을 만듭니다.
   * @param obj 객체
   * @return
   */
  def convertObjectToFormUrlEncodedBytes(obj: Any): Array[Byte] = {

    val mapper = JacksonSerializer.defaultObjectMapper

    val props = mapper.convertValue(obj, classOf[JMap[_, _]]).asInstanceOf[JMap[String, Any]]
    val nameIter = props.keySet().iterator()

    val formUrlEncoded = new StringBuilder()

    while (nameIter.hasNext) {
      val key = nameIter.next()
      val value = props.get(key)
      formUrlEncoded.append(key).append("=").append(value)
      if (nameIter.hasNext)
        formUrlEncoded.append("&")
    }

    Strings.getUtf8Bytes(formUrlEncoded.toString())
  }

  def getPageNo(page: Integer): Int = {
    if (page == null) 0
    else page.toInt max 0
  }

  def getPageSize(size: Integer, defaultSize: Int): Int = {
    if (size <= 0) defaultSize
    else size.toInt
  }
}
