package debop4s.redis.base

import akka.util.ByteString
import debop4s.redis.serializer.SnappyRedisSerializer
import redis.RedisClient
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * 크기가 제한된 컬력센입니다.
 * @author Sunghyouk Bae
 */
class RedisCappedCollection[T](val name: String,
                               val size: Long = Long.MaxValue,
                               val redis: RedisClient = RedisCappedCollection.defaultRedis) {

  lazy private val serializer = SnappyRedisSerializer[T]()

  /**
   * 리스트에 새로운 객체를 추가합니다.
   */
  def lpush(value: T): Future[Long] = {
    val bs = ByteString(serializer.serialize(value))
    val future = redis.lpush(name, bs)

    // 리스트에 데이터
    future onSuccess {
      case x => trim()
    }
    future
  }

  /**
   * 리스트에서 객체를 조회합니다.
   */
  def get(index: Long): Future[T] = {
    assert(index >= 0 && index < size, s"index 의 범위가 벗어났습니다. [0, $size) 사이어야 합니다. index=$index")
    redis.lindex(name, index).map {
      v => serializer.deserialize(v.get.toArray)
    }
  }

  def getRange(start: Long, end: Long): Future[Iterable[T]] = {
    redis.lrange(name, start, end).map {
      list =>
        list.map(x => serializer.deserialize(x.toArray))
    }
  }

  private def trim(): Future[Boolean] = {
    redis.ltrim(name, 0, size - 1)
  }
}

object RedisCappedCollection {

  implicit val actorSystem = akka.actor.ActorSystem()

  lazy val defaultRedis = RedisClient()

  def apply[T](name: String): RedisCappedCollection[T] = {
    apply(name, Long.MaxValue)
  }

  def apply[T](name: String, size: Long): RedisCappedCollection[T] = {
    apply(name, size, RedisClient())
  }

  def apply[T](name: String, size: Long, redis: RedisClient): RedisCappedCollection[T] = {
    new RedisCappedCollection[T](name, size, redis)
  }
}
