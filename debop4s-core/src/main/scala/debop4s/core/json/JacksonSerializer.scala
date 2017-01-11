package debop4s.core.json

import java.lang.reflect.{ParameterizedType, Type}

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper


/**
  * JacksonSerializer
  * Created by debop on 2014. 2. 22.
  */
class JacksonSerializer(val mapper: ObjectMapper = JacksonSerializer.defaultObjectMapper) extends AbstractJsonSerializer {

  //  def this() = this(JacksonSerializer.defaultObjectMapper)

  override def serialize[@miniboxed T](graph: T): Array[Byte] =
    mapper.writeValueAsBytes(graph)

  override def serializeToText[@miniboxed T](graph: T): String =
    mapper.writeValueAsString(graph)

  override def deserialize[@miniboxed T: Manifest](data: Array[Byte]): T =
    mapper.readValue(data, JacksonSerializer.typeReference[T])

  override def deserializeFromText[@miniboxed T: Manifest](text: String): T =
    mapper.readValue(text, JacksonSerializer.typeReference[T])

  override def deserialize[@miniboxed T](data: Array[Byte], clazz: Class[T]): T =
    mapper.readValue(data, clazz)

  override def deserializeFromText[@miniboxed T](text: String, clazz: Class[T]): T =
    mapper.readValue(text, clazz)
}

object JacksonSerializer {

  def apply(mapper: ObjectMapper = defaultObjectMapper): JacksonSerializer =
    new JacksonSerializer(mapper)

  lazy val defaultObjectMapper: ObjectMapper = {
    val mapper = new ObjectMapper() with ScalaObjectMapper

    // NOTE: JodaModule 은 joda-time의 형식을 Timestamp 형식으로 변환하기 위해 필요합니다.
    mapper.registerModule(new JodaModule)
    mapper.registerModule(DefaultScalaModule)

    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)

    // HINT: Single Value 도 ARRAY 로 인식하게끔 합니다.
    mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
    mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
    mapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true)

    // HINT: 알 수 없는 속성에 대해서는 무시하도록 합니다.
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    mapper
  }

  private[json] def typeReference[@miniboxed T: Manifest] = new TypeReference[T] {
    override def getType: Type = typeFromManifest(manifest[T])
  }

  private[json] def typeFromManifest(m: Manifest[_]): Type = {
    if (m.typeArguments.isEmpty) {
      m.runtimeClass
    }
    else new ParameterizedType {
      def getRawType: Class[_] = m.runtimeClass

      def getActualTypeArguments: Array[Type] = m.typeArguments.map(typeFromManifest).toArray

      def getOwnerType: Class[_] = null
    }
  }
}
