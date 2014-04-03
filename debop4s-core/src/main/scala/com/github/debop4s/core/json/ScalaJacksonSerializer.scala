package com.github.debop4s.core.json

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import scala.reflect.ClassTag
import scala.reflect.classTag


/**
 * ScalaJacksonSerializer
 * Created by debop on 2014. 2. 22.
 */
class ScalaJacksonSerializer(val mapper: ObjectMapper) extends JsonSerializer {

    override def serialize[T](graph: T): Array[Byte] =
        mapper.writeValueAsBytes(graph)

    override def serializeToText[T](graph: T): String =
        mapper.writeValueAsString(graph)

    override def deserialize[T: ClassTag](data: Array[Byte]): T =
        mapper.readValue(data, classTag[T].runtimeClass).asInstanceOf[T]

    override def deserializeFromText[T: ClassTag](text: String): T =
        mapper.readValue(text, classTag[T].runtimeClass).asInstanceOf[T]
}

object ScalaJacksonSerializer {

    def apply(): ScalaJacksonSerializer = new ScalaJacksonSerializer(defaultObjectMapper)

    def apply(mapper: ObjectMapper): ScalaJacksonSerializer = {
        new ScalaJacksonSerializer(mapper)
    }

    lazy val defaultObjectMapper: ObjectMapper = {
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
