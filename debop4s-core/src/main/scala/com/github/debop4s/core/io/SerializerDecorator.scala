package com.github.debop4s.core.io

/**
 * com.github.debop4s.core.io.SerializerDecorator
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 10. 오후 5:46
 */
abstract class SerializerDecorator(val serializer: Serializer) extends Serializer {

    require(serializer != null, "serializer가 null 이면 안됩니다.")

    /**
     * 객체를 직렬화 합니다.
     * @param graph 직렬화할 객체
     * @return 직렬화된 정보를 가진 바이트 배열
     */
    def serialize[T <: AnyRef](graph: T): Array[Byte] = {
        serializer.serialize(graph)
    }

    /**
     * 직렬화된 바이트 배열을 역직렬화하여 객체로 변환합니다.
     * @param bytes 직렬화된 바이트 배열
     * @return 역직렬화된 객체 정보
     */
    def deserialize[T <: AnyRef](bytes: Array[Byte], clazz: Class[T]): T = {
        serializer.deserialize(bytes, clazz)
    }
}
