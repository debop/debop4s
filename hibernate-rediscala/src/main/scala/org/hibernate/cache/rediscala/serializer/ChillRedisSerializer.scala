package org.hibernate.cache.rediscala.serializer

import com.twitter.chill.ScalaKryoInstantiator
import scala.reflect._

/**
 * KryoRedisSerializer
 * Created by debop on 2014. 3. 22.
 */
private[rediscala] class ChillRedisSerializer[T: ClassTag] extends RedisSerializer[T] {

    override def serialize(graph: T): Array[Byte] = {
        if (graph == null)
            return EMPTY_BYTES

        ScalaKryoInstantiator.defaultPool.toBytesWithClass(graph)
    }

    override def deserialize(bytes: Array[Byte]): T = {
        if (bytes == null || bytes.length == 0)
            return null.asInstanceOf[T]

        ScalaKryoInstantiator.defaultPool.fromBytes(bytes).asInstanceOf[T]
    }
}
