package com.github.debop4s.core.json

import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import java.lang.reflect.{Type, ParameterizedType}


/**
 * JacksonSerializer
 * Created by debop on 2014. 2. 22.
 */
class JacksonSerializer(val mapper: ObjectMapper) extends JsonSerializer {

    override def serialize[T](graph: T): Array[Byte] =
        mapper.writeValueAsBytes(graph)

    override def serializeToText[T](graph: T): String =
        mapper.writeValueAsString(graph)

    override def deserialize[T: Manifest](data: Array[Byte]): T =
        mapper.readValue(data, JacksonSerializer.typeReference[T])

    override def deserializeFromText[T: Manifest](text: String): T =
        mapper.readValue(text, JacksonSerializer.typeReference[T])
}

object JacksonSerializer {

    def apply(): JacksonSerializer = new JacksonSerializer(defaultObjectMapper)

    def apply(mapper: ObjectMapper): JacksonSerializer = {
        new JacksonSerializer(mapper)
    }

    lazy val defaultObjectMapper: ObjectMapper = {
        val mapper = new ObjectMapper() with ScalaObjectMapper

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

    private[json] def typeReference[T: Manifest] = new TypeReference[T] {
        override def getType = typeFromManifest(manifest[T])
    }

    private[json] def typeFromManifest(m: Manifest[_]): Type = {
        if (m.typeArguments.isEmpty) {m.runtimeClass}
        else new ParameterizedType {
            def getRawType = m.runtimeClass

            def getActualTypeArguments = m.typeArguments.map(typeFromManifest).toArray

            def getOwnerType = null
        }
    }
}
