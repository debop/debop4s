package debop4s.rediscala.serializer

import net.jpountz.lz4.LZ4Factory

/**
 * LZ4 압축
 * @author sunghyouk.bae@gmail.com
 */
object LZ4RedisSerializer {
  def apply[T](inner: RedisSerializer[T] = new FstRedisSerializer[T]): LZ4RedisSerializer[T] =
    new LZ4RedisSerializer[T](inner)
}

/**
 * LZ4 압축 알고리즘을 이용하여, `inner` 로 직렬화한 정보를 압축합니다.
 * @param inner 실제 직렬화를 수행하는 serializer
 * @tparam T 직렬화할 대상 객체의 수형 (`Any`, `Object` 를 사용하면 모든 객체가 된다)
 */
class LZ4RedisSerializer[T](val inner: RedisSerializer[T] = new FstRedisSerializer[T])
  extends RedisSerializer[T] {

  require(inner != null)

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
    // TODO: LZ4에서는 decompress 시에 압축률을 알 수 없으므로, 충분히 큰 값을 지정해 줘야 한다. 이 부분은 이해가 안된다.
    val length = bytes.take(8).toSeq.toArray

    inner.deserialize(decompressor.decompress(bytes, bytes.length * 1000)).asInstanceOf[T]
  }
}