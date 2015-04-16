package debop4s.rediscala.serializer

import org.xerial.snappy.Snappy


object SnappyRedisSerializer {

  def apply[T](inner: RedisSerializer[T] = new BinaryRedisSerializer[T]()): SnappyRedisSerializer[T] =
    new SnappyRedisSerializer[T](inner)
}

/**
 * Snappy 압축 알고리즘을 이용하여 serialized 된 데이터를 압축합니다.
 * @author Sunghyouk Bae
 */
class SnappyRedisSerializer[T](val inner: RedisSerializer[T] = new BinaryRedisSerializer[T])
  extends RedisSerializer[T] {

  /**
   * Snappy 압축 알고리즘을 이용하여 serialized 된 데이터를 압축합니다.
   * @param graph serialized 될 객체
   * @return serialized 된 정보를 압축한 데이터
   */
  override def serialize(graph: T): Array[Byte] = {
    if (graph == null)
      return EMPTY_BYTES

    Snappy.compress(inner.serialize(graph))
  }

  /**
   * Snappy 압축 알고리즘을 이용하여 serialized 된 데이터를 복원하고, deserialize 시킵니다.
   * @param bytes 압축된 serialized 된 정보
   * @return 원본 객체
   */
  override def deserialize(bytes: Array[Byte]): T = {
    if (bytes == null || bytes.length == 0)
      return null.asInstanceOf[T]

    inner.deserialize(Snappy.uncompress(bytes)).asInstanceOf[T]
  }
}

