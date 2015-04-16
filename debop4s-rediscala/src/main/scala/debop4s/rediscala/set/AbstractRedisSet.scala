package debop4s.rediscala.set

import debop4s.rediscala.AbstractRedis

import scala.annotation.varargs
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

/**
 * Redis 의 Hash Set을 이용한 작업을 수행합니다.
 * @author Sunghyouk Bae
 */
abstract class AbstractRedisSet[T <: java.io.Serializable] extends AbstractRedis[T] {

  def get(key: String, field: String): Future[Option[T]] = {
    redis.hget[T](key, field)
  }

  def deleteField(key: String, field: String): Future[Long] = {
    redis.hdel(key, field)
  }

  @varargs
  def deleteFieldAll(key: String, fields: String*): Future[Long] = {
    redis.hdel(key, fields: _*)
  }

  def delete(key: String): Future[Long] = {
    redis.del(key)
  }

  def set(key: String, field: String, value: T): Future[Boolean] = {
    if (key == null)
      return Future(false)

    redis.hset[T](key, field, value)
  }
}
