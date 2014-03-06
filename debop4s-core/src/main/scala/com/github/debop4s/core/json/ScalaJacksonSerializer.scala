package com.github.debop4s.core.json

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule


/**
 * ScalaJacksonSerializer
 * Created by debop on 2014. 2. 22.
 */
class ScalaJacksonSerializer(val mapper: ObjectMapper) extends JsonSerializer {

    override def serialize(graph: Any): Array[Byte] =
        mapper.writeValueAsBytes(graph)

    override def serializeToText(graph: Any): String =
        mapper.writeValueAsString(graph)

    override def deserialize[T](data: Array[Byte], clazz: Class[T]): T =
        mapper.readValue[T](data, clazz)

    override def deserializeFromText[T](text: String, clazz: Class[T]): T =
        mapper.readValue[T](text, clazz)
}

object ScalaJacksonSerializer {

    def apply(): ScalaJacksonSerializer = new ScalaJacksonSerializer(createObjectMapper())

    def apply(mapper: ObjectMapper): ScalaJacksonSerializer = {
        new ScalaJacksonSerializer(mapper)
    }

    def createObjectMapper(): ObjectMapper = {
        val mapper = new ObjectMapper()

        // NOTE: JodaModule 은 joda-time의 형식을 Timestamp 형식으로 변환하기 위해 필요합니다.
        mapper.registerModule(new JodaModule)
        mapper.registerModule(DefaultScalaModule)

        // HINT: Single Value 도 ARRAY 로 인식하게끔 합니다.
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
        mapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true)
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)

        mapper
    }
}
