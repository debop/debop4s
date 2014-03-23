package org.hibernate.cache.rediscala.serializer

import org.gridgain.grid.marshaller.optimized.GridOptimizedMarshaller
import scala.reflect._

/**
 * GridGainRedisSerializer
 * Created by debop on 2014. 3. 23.
 */
private[rediscala] class GridGainRedisSerializer[T: ClassTag] extends RedisSerializer[T] {

    val marshaller = new GridOptimizedMarshaller()

    override def serialize(graph: T): Array[Byte] = {
        if (graph == null)
            return EMPTY_BYTES
        marshaller.marshal(graph)
    }

    override def deserialize(bytes: Array[Byte]): T = {
        if (bytes == null || bytes.length == 0)
            return null.asInstanceOf[T]

        marshaller.unmarshal[T](bytes, classTag[T].runtimeClass.getClassLoader)
    }
}
