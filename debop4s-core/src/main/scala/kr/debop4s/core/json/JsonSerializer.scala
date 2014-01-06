package kr.debop4s.core.json

import kr.debop4s.core.utils.Strings

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
    def serialize(graph: AnyRef): Array[Byte] = Strings.getBytesUtf8(serializeToText(graph))

    /**
     * JSON 포맷으로 직렬화하여 Json Text 형식의 문자열로 반환합니다.
     *
     * @param graph 직렬화할 객체
     * @return JSON으로 직렬화한 문자열, 객체가 Null이면 null 반환
     */
    def serializeToText(graph: AnyRef): String

    /**
     * Json 형식의 데이터을 역직렬화하여, 객체로 빌드합니다.
     *
     * @param data    JSON으로 직렬화한 바이트 배열
     * @return 역직렬화 한 객체
     */
    def deserialize[T <: AnyRef](data: Array[Byte], clazz: Class[T]): T =
        deserializeFromText(Strings.getStringUtf8(data), clazz)

    /**
     * Json Text 형식의 문자열을 역직렬화하여, 객체로 빌드합니다.
     *
     * @param jsonText    JSON으로 직렬화한 문자열
     * @return 역직렬화 한 객체
     */
    def deserializeFromText[T <: AnyRef](text: String, clazz: Class[T]): T

}
