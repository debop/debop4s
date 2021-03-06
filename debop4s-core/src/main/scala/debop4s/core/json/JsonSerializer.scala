package debop4s.core.json

import debop4s.core.utils.Strings

/** for java compatibility (ex. Spring dependency injection) */
abstract class AbstractJsonSerializer extends JsonSerializer

/**
 * JSON Serializer 의 기본 클래스
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 11. 오전 11:26
 */
trait JsonSerializer {

  /**
   * JSON 포맷으로 직렬화하여 Json 형식의 바이트 배열로 반환합니다.
   *
   * @param graph 직렬화할 객체
   * @return JSON으로 직렬화한 바이트 배열, 객체가 Null이면 null 반환
   */
  def serialize[@miniboxed T](graph: T): Array[Byte] = Strings.getUtf8Bytes(serializeToText(graph))

  /**
   * JSON 포맷으로 직렬화하여 Json Text 형식의 문자열로 반환합니다.
   *
   * @param graph 직렬화할 객체
   * @return JSON으로 직렬화한 문자열, 객체가 Null이면 null 반환
   */
  def serializeToText[@miniboxed T](graph: T): String

  /**
   * Json 형식의 데이터을 역직렬화하여, 객체로 빌드합니다.
   *
   * @param data    JSON으로 직렬화한 바이트 배열
   * @return 역직렬화 한 객체
   */
  def deserialize[@miniboxed T: Manifest](data: Array[Byte]): T =
    deserializeFromText[T](Strings.getUtf8String(data))

  /**
   *
   * Json Text 형식의 문자열을 역직렬화하여, 객체로 빌드합니다.
   *
   * @param text    JSON으로 직렬화한 문자열
   * @return 역직렬화 한 객체
   */
  def deserializeFromText[@miniboxed T: Manifest](text: String): T

  /**
   * Json 형식의 데이터을 역직렬화하여, 객체로 빌드합니다.
   *
   * @param data    JSON으로 직렬화한 바이트 배열
   * @return 역직렬화 한 객체
   */
  def deserialize[@miniboxed T](data: Array[Byte], clazz: Class[T]): T =
    deserializeFromText[T](Strings.getUtf8String(data), clazz)

  /**
   *
   * Json Text 형식의 문자열을 역직렬화하여, 객체로 빌드합니다.
   *
   * @param text    JSON으로 직렬화한 문자열
   * @return 역직렬화 한 객체
   */
  def deserializeFromText[@miniboxed T](text: String, clazz: Class[T]): T

}
