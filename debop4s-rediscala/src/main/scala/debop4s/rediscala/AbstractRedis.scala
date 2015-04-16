package debop4s.rediscala

import debop4s.rediscala.serializer.SnappyFstValueFormatter
import org.springframework.beans.factory.annotation.Autowired
import redis.{ByteStringFormatter, RedisClient}

/**
 * Redis 통신을 위한 기본 Client 입니다.
 *
 * @author Sunghyouk Bae
 */
abstract class AbstractRedis[T] {

  @Autowired val redis: RedisClient = null

  /**
   * Redis에 저장할 값에 대한 기본 Formatter 입니다.
   * 속도와 크기를 고려하여, FST로 직렬화하고, Snappy로 압축하도록 합니다.
   */
  implicit val valueFormatter: ByteStringFormatter[T] = new SnappyFstValueFormatter[T]()

}
