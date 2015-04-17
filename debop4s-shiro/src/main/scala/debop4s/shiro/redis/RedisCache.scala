package debop4s.shiro.redis

import java.util.{Collection => JCollection, List => JList, Set => JSet}

import debop4s.rediscala.client.RedisSyncClient
import debop4s.rediscala.serializer.FstValueFormatter
import org.apache.shiro.cache.Cache
import redis.ByteStringFormatter

import scala.collection.JavaConverters._

/**
 * Redis 서버를 캐시 저장소로 사용하는 캐시입니다.
 * @author sunghyouk.bae@gmail.com
 */
class RedisCache[K, V](val redis: RedisSyncClient,
                       val prefix: String)
  extends Cache[K, V] {

  assert(redis != null)

  implicit val valueFormatter: ByteStringFormatter[V] = new FstValueFormatter[V]()

  private def cacheKey(key: K) = {
    prefix + key.toString
  }

  override def get(key: K): V = {
    redis.get[V](cacheKey(key)).getOrElse(null.asInstanceOf[V])
  }

  override def put(key: K, value: V): V = {
    redis.set(cacheKey(key), value)
    value
  }

  override def clear(): Unit = {
    redis.del(redis.keys(prefix + "*"): _*)
  }

  override def values(): JCollection[V] = {
    redis.mget[V](redis.keys(prefix + "*"): _*)
    .map(v => v.getOrElse(null.asInstanceOf[V]))
    .asJavaCollection
  }

  override def size(): Int = {
    redis.keys(prefix + "*").size
  }

  override def remove(key: K): V = {
    val removed = get(key)
    if (removed != null)
      redis.del(cacheKey(key))
    removed
  }

  override def keys(): JSet[K] = {
    redis.keys(prefix + "*").map(_.asInstanceOf[K]).toSet.asJava
  }

}
