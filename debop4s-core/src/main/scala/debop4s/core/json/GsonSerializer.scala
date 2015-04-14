package debop4s.core.json

import com.google.gson.Gson


/**
 * GsonSerializer companion object
 */
object GsonSerializer {

  def apply(gson: Gson = new Gson()) =
    new GsonSerializer(gson)
}

/**
 * Gson Library 를 사용하는 Serializer
 * Created by debop on 2014. 4. 18.
 */
class GsonSerializer(val gson: Gson) extends AbstractJsonSerializer {

  def this() = this(new Gson())

  /**
   * JSON 포맷으로 직렬화하여 Json Text 형식의 문자열로 반환합니다.
   *
   * @param graph 직렬화할 객체
   * @return JSON으로 직렬화한 문자열, 객체가 Null이면 null 반환
   */
  override def serializeToText[T](graph: T): String = {
    gson.toJson(graph)
  }

  /**
   * Json Text 형식의 문자열을 역직렬화하여, 객체로 빌드합니다.
   *
   * @param text    JSON으로 직렬화한 문자열
   * @return 역직렬화 한 객체
   */
  override def deserializeFromText[T: Manifest](text: String): T = {
    gson.fromJson[T](text, manifest[T].runtimeClass)
  }

  /**
   * Json Text 형식의 문자열을 역직렬화하여, 객체로 빌드합니다.
   *
   * @param text    JSON으로 직렬화한 문자열
   * @return 역직렬화 한 객체
   */
  override def deserializeFromText[T](text: String, clazz: Class[T]): T =
    gson.fromJson[T](text, clazz)
}
