package org.hibernate.cache.rediscala.serializer

import org.xerial.snappy.Snappy

/**
 * Snappy 압축 알고리즘을 이용하여 압축을 수행합니다.
 * Created by debop on 2014. 3. 14.
 */
class SnappyRedisSerializer[T](val innerSerializer: RedisSerializer[T]) extends RedisSerializer[T] {

    override def serialize(graph: T): Array[Byte] = {
        Snappy.compress(innerSerializer.serialize(graph))
    }

    override def deserialize(bytes: Array[Byte]): T = {
        innerSerializer.deserialize(Snappy.uncompress(bytes)).asInstanceOf[T]
    }
}
