package com.github.debop4s.core.io

import org.gridgain.grid.marshaller.optimized.GridOptimizedMarshaller

/**
 * GridGainSerializer
 * Created by debop on 2014. 3. 23.
 */
class GridGainSerializer extends Serializer {

    private lazy val marshaller = new GridOptimizedMarshaller()

    /**
     * 객체를 직렬화 합니다.
     * @param graph 직렬화할 객체
     * @return 직렬화된 정보를 가진 바이트 배열
     */
    override def serialize[T](graph: T): Array[Byte] = {
        if (graph == null)
            return Array.emptyByteArray

        marshaller.marshal(graph)
    }

    /**
     * 직렬화된 바이트 배열을 역직렬화하여 객체로 변환합니다.
     * @param bytes 직렬화된 바이트 배열
     * @return 역직렬화된 객체 정보
     */
    override def deserialize[T](bytes: Array[Byte], clazz: Class[T]): T = {
        if (bytes == null || bytes.length == 0)
            return null.asInstanceOf[T]

        marshaller.unmarshal(bytes, clazz.getClassLoader)
    }
}