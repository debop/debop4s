package debop4s.redis.serializer

import org.xerial.snappy.Snappy

/**
 * SnappyRedisSerializer
 * @author Sunghyouk Bae
 */
class SnappyRedisSerializer[T](val inner: RedisSerializer[T]) extends RedisSerializer[T] {

  override def serialize(graph: T): Array[Byte] = {
    if (graph == null)
      return EMPTY_BYTES

    Snappy.compress(inner.serialize(graph))
  }

  override def deserialize(bytes: Array[Byte]): T = {
    if (bytes == null || bytes.length == 0)
      return null.asInstanceOf[T]

    inner.deserialize(Snappy.uncompress(bytes)).asInstanceOf[T]
  }
}

object SnappyRedisSerializer {

  def apply[T](): SnappyRedisSerializer[T] = {
    apply(new BinaryRedisSerializer[T]())
  }

  def apply[T](inner: RedisSerializer[T]): SnappyRedisSerializer[T] = {
    require(inner != null)
    new SnappyRedisSerializer[T](inner)
  }
}
