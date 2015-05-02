package org.hibernate.cache.rediscala.serializer

import net.jpountz.lz4.LZ4Factory

/**
 * LZ4RedisSerializer
 * @author sunghyouk.bae@gmail.com
 */
object LZ4RedisSerializer {
  def apply[T](inner: RedisSerializer[T] = new FstRedisSerializer[T]): LZ4RedisSerializer[T] =
    new LZ4RedisSerializer[T](inner)
}

class LZ4RedisSerializer[@miniboxed T](val inner: RedisSerializer[T] = new FstRedisSerializer[T])
  extends RedisSerializer[T] {

  private lazy val factory = LZ4Factory.fastestInstance()
  private lazy val compressor = factory.fastCompressor()
  private lazy val decompressor = factory.safeDecompressor()

  /**
   * 객체를 직렬화 합니다.
   * @param graph serialized 될 객체
   * @return serialized 된 정보를 압축한 데이터
   */
  override def serialize(graph: T): Array[Byte] = {
    if (graph == null)
      return EMPTY_BYTES

    compressor.compress(inner.serialize(graph))
  }
  /**
   * 객체를 역 직렬화 합니다.
   * @param bytes 압축된 serialized 된 정보
   * @return 원본 객체
   */
  override def deserialize(bytes: Array[Byte]): T = {
    inner.deserialize(decompressor.decompress(bytes, bytes.length * 1000)).asInstanceOf[T]
  }
}
