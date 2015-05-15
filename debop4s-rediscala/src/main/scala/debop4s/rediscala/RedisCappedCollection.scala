package debop4s.rediscala

import debop4s.core.concurrent._
import debop4s.rediscala.serializer.SnappyFstValueFormatter
import debop4s.rediscala.utils.RedisHelper
import redis.RedisClient

import scala.concurrent.Future

object RedisCappedCollection {

  lazy val defaultRedis = RedisHelper.createRedisClient()

  def apply[T](name: String): RedisCappedCollection[T] = {
    apply(name, Long.MaxValue)
  }

  def apply[T](name: String, size: Long): RedisCappedCollection[T] = {
    apply(name, size, defaultRedis)
  }

  def apply[T](name: String, size: Long, redis: RedisClient): RedisCappedCollection[T] = {
    new RedisCappedCollection[T](name, size, redis)
  }
}

/**
 * 크기가 제한된 컬력센입니다.
 * @author Sunghyouk Bae
 */
class RedisCappedCollection[@miniboxed T](val name: String,
                                          val size: Long = Long.MaxValue,
                                          val redis: RedisClient = RedisCappedCollection.defaultRedis) {

  implicit val valueFormatter = new SnappyFstValueFormatter[T]()

  /** 리스트에 새로운 객체를 추가합니다. */
  def lpush(value: T): Long = {
    // 리스트에 데이터 삽입한 후, 제한된 크기 이상의 데이터는 버린다.
    val count = for {
      count <- redis.lpush(name, value)
      _ <- trim()
    } yield count

    count.await
  }

  /** 리스트에 새로운 객체들을 추가합니다. */
  def lpushAll(values: T*): Future[Long] = {
    values.map { v => redis.lpush(name, v) }.stayAll
    trim().stay
    redis.llen(name)
  }


  /**
   * 리스트에서 객체를 조회합니다.
   */
  def get(index: Long): Future[Option[T]] = {
    assert(index >= 0 && index < size, s"index 의 범위가 벗어났습니다. [0, $size) 사이어야 합니다. index=$index")
    redis.lindex[T](name, index)
  }

  def getRange(start: Long, end: Long): Future[Iterable[T]] = {
    redis.lrange[T](name, start, end)
  }

  private def trim(): Future[Boolean] = {
    redis.ltrim(name, 0, size - 1)
  }
}


