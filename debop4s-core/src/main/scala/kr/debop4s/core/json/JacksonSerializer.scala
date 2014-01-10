package kr.debop4s.core.json

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.datatype.joda.JodaModule
import org.slf4j.LoggerFactory

/**
 * Jackson 라이브러리를 사용한 Json Serializer 입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 11. 오후 12:43
 */
class JacksonSerializer(val mapper: ObjectMapper) extends JsonSerializer {

    assert(mapper != null)

    implicit lazy val log = LoggerFactory.getLogger(classOf[JacksonSerializer])

    def this() {
        this(JacksonSerializer.createObjectMapper())
    }

    override def serialize(graph: AnyRef): Array[Byte] =
        mapper.writeValueAsBytes(graph)

    override def serializeToText(graph: AnyRef): String =
        mapper.writeValueAsString(graph)

    override def deserialize[T <: AnyRef](data: Array[Byte], clazz: Class[T]): T =
        mapper.readValue[T](data, clazz)

    override def deserializeFromText[T <: AnyRef](text: String, clazz: Class[T]): T =
        mapper.readValue[T](text, clazz)
}

object JacksonSerializer {

    def apply(): JacksonSerializer = new JacksonSerializer(createObjectMapper())

    def apply(mapper: ObjectMapper): JacksonSerializer = {
        assert(mapper != null)
        new JacksonSerializer(mapper)
    }


    def createObjectMapper(): ObjectMapper = {
        val mapper = new ObjectMapper()

        // NOTE: JodaModule 은 joda-time의 형식을 Timestamp 형식으로 변환하기 위해 필요합니다.
        mapper.registerModule(new JodaModule)

        // HINT: Single Value 도 ARRAY 로 인식하게끔 합니다.
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
        mapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true)
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)

        mapper
    }
}
