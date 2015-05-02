package debop4s.rediscala.set

import scala.annotation.varargs

/**
 * AbstractSyncRedisSet
 * @author debop created at 2014. 5. 2.
 */
@deprecated(message = "동기방식은 삭제할 것임", since = "0.5.0")
abstract class AbstractSyncRedisSet[@miniboxed T] extends AbstractSyncRedis[T] {

  def get(key: String, field: String): Option[T] = {
    redis.hget[T](key, field)
  }

  def deleteField(key: String, field: String): Long = {
    redis.hdel(key, field)
  }

  @varargs
  def deleteFieldAll(key: String, fields: String*): Long = {
    redis.hdel(key, fields.toSeq: _*)
  }

  def delete(key: String): Long = {
    redis.del(key)
  }

  def set(key: String, field: String, value: T): Boolean = {
    redis.hset[T](key, field, value)
  }
}
