package debop4s.rediscala.serializer

import akka.util.ByteString
import redis.ByteStringFormatter

/**
 * Redis 에 Value 를 저장할 때 사용할 Formatter 의 기본 클래스입니다.
 * @author Sunghyouk Bae
 */
abstract class AbstractRedisValueFormatter[T](val serializer: RedisSerializer[T]) extends ByteStringFormatter[T] {

  require(serializer != null)

  /**
   * `data` 를 직렬화합니다.
   * @param data 직렬화할 객체
   * @return 직렬화 한 정보를 나타내는 `ByteString`
   */
  override def serialize(data: T): ByteString = {
    data match {
      case null => ByteString.empty
      case _ => ByteString(serializer.serialize(data))
    }
  }

  /**
   * 직렬화된 정보를 나타내는 `ByteString`을 읽어 원본 객체로 역직렬화합니다.
   * @param bs 직렬화 정보를 가진 `ByteString`
   * @return 역직렬화된 객체
   */
  override def deserialize(bs: ByteString): T = {
    bs match {
      case null => null.asInstanceOf[T]
      case _ => serializer.deserialize(bs.toArray)
    }
  }
}

/**
 * Value 를 Binary Serialization 하는 Formatter 입니다.
 * @tparam T Value 수형
 */
class BinaryValueFormatter[T]
  extends AbstractRedisValueFormatter[T](new BinaryRedisSerializer[T])

/**
 * Value 를 FST Serialization 하는 Formatter 입니다.
 * @tparam T Value 수형
 */
class FstValueFormatter[T]
  extends AbstractRedisValueFormatter[T](new FstRedisSerializer[T])

/**
 * Value 를 Binary Serialization 수행 후 Snappy 로 압축하는 Formatter 입니다.
 * @tparam T Value 수형
 */
class SnappyBinaryValueFormatter[T]
  extends AbstractRedisValueFormatter[T](new SnappyRedisSerializer[T](new BinaryRedisSerializer[T]))

/**
 * Value 를 FST Serialization 수행 후 Snappy 로 압축하는 Formatter 입니다.
 * @tparam T Value 수형
 */
class SnappyFstValueFormatter[T]
  extends AbstractRedisValueFormatter[T](new SnappyRedisSerializer[T](new FstRedisSerializer[T]))

/**
 * Value 를 Binary Serialization 수행 후 LZ4 로 압축하는 Formatter 입니다.
 * @tparam T Value 수형
 */
class LZ4BinaryValueFormatter[T]
  extends AbstractRedisValueFormatter[T](new LZ4RedisSerializer[T](new BinaryRedisSerializer[T]))

/**
 * Value 를 FST Serialization 수행 후 LZ4 로 압축하는 Formatter 입니다.
 * @tparam T Value 수형
 */
class LZ4FstValueFormatter[T]
  extends AbstractRedisValueFormatter[T](new LZ4RedisSerializer[T](new FstRedisSerializer[T]))
