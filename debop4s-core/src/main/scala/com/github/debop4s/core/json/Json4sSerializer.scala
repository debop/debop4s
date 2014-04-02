package com.github.debop4s.core.json

import org.json4s._
import org.json4s.native.Serialization

/**
 * Json4sSerializer
 * @author Sunghyouk Bae
 */
class Json4sSerializer extends JsonSerializer {

    implicit val formats = Serialization.formats(NoTypeHints)

    /**
     * JSON 포맷으로 직렬화하여 Json Text 형식의 문자열로 반환합니다.
     *
     * @param graph 직렬화할 객체
     * @return JSON으로 직렬화한 문자열, 객체가 Null이면 null 반환
     */
    override def serializeToText(graph: Any): String = {
        Serialization.write[AnyRef](graph.asInstanceOf[AnyRef])
    }

    /**
     * Json Text 형식의 문자열을 역직렬화하여, 객체로 빌드합니다.
     *
     * @param text    JSON으로 직렬화한 문자열
     * @return 역직렬화 한 객체
     */
    override def deserializeFromText[T: Manifest](text: String, clazz: Class[T]): T = {
        Serialization.read[T](text)
    }
}

object Json4sSerializer {

    def apply(): Json4sSerializer = new Json4sSerializer()
}
