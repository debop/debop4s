package org.hibernate.cache.rediscala.serializer

import org.xerial.snappy.Snappy

/**
 * Snappy 압축 알고리즘을 이용하여 압축을 수행합니다.
 * Created by debop on 2014. 3. 14.
 */
class SnappyRedisSerializer[T](val innerSerializer: RedisSerializer[T]) extends RedisSerializer[T] {

    override def serialize(graph: T): Array[Byte] = {
        if (graph == null)
            return EMPTY_BYTES
        Snappy.compress(innerSerializer.serialize(graph))
    }

    override def deserialize(bytes: Array[Byte]): T = {
        if (bytes == null || bytes.length == 0)
            return null.asInstanceOf[T]
        innerSerializer.deserialize(Snappy.uncompress(bytes)).asInstanceOf[T]
    }
}
