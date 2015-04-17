package org.hibernate.cache.rediscala.serializer

import org.xerial.snappy.Snappy


private[rediscala] object SnappyRedisSerializer {

  def apply[T](inner: RedisSerializer[T] = FstRedisSerializer[T]()): SnappyRedisSerializer[T] = {
    new SnappyRedisSerializer[T](inner)
  }
}

/**
 * Snappy 압축 알고리즘을 이용하여 객체를 직렬화 한 후 압축을 수행합니다.
 * Created by debop on 2014. 3. 14.
 */
private[rediscala] class SnappyRedisSerializer[T](val innerSerializer: RedisSerializer[T])
  extends RedisSerializer[T] {

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


