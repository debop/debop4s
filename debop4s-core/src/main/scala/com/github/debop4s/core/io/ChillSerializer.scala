package com.github.debop4s.core.io

import com.twitter.chill.ScalaKryoInstantiator

/**
 * KryoSerializer
 * Created by debop on 2014. 3. 22.
 */
class ChillSerializer extends Serializer {

    /**
     * 객체를 직렬화 합니다.
     * @param graph 직렬화할 객체
     * @return 직렬화된 정보를 가진 바이트 배열
     */
    override def serialize[T](graph: T): Array[Byte] = {
        if (graph == null)
            return Array.emptyByteArray

        ScalaKryoInstantiator.defaultPool.toBytesWithClass(graph)
    }

    /**
     * 직렬화된 바이트 배열을 역직렬화하여 객체로 변환합니다.
     * @param bytes 직렬화된 바이트 배열
     * @return 역직렬화된 객체 정보
     */
    override def deserialize[T](bytes: Array[Byte], clazz: Class[T]): T = {
        if (bytes == null || bytes.length == 0)
            return null.asInstanceOf[T]

        ScalaKryoInstantiator.defaultPool.fromBytes(bytes).asInstanceOf[T]
    }
}
