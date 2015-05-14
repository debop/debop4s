package org.hibernate.cache.rediscala.client

import java.util.concurrent.TimeUnit

import org.hibernate.cache.rediscala._
import org.hibernate.cache.rediscala.serializer.SnappyFstCacheEntryFormatter
import org.slf4j.LoggerFactory
import redis.RedisCommands
import redis.api.Limit

import scala.annotation.varargs
import scala.async.Async._
import scala.concurrent._
import scala.concurrent.duration._
import scala.util.control.NonFatal

/**
 * Rediscala 를 이용하여 Redis-Server와 통신하는 기능을 제공하는 Trait 입니다.
 * @author sunghyouk.bae@gmail.com
 */
trait RedisCacheSupport {

  protected val log = LoggerFactory.getLogger(getClass)

  /** RedisClient */
  def redis: RedisCommands

  /**
   * Cache Value 를 Fast-Serialization 을 이용하면 속도가 5배 정도 빨라지고, Snappy/LZ4를 이용하여 압축을 수행하면 15배 정도 빨라진다.
   */
  implicit val cacheEntryFormatter = new SnappyFstCacheEntryFormatter[Any]()

  /**
   * 캐시 영역별로 expiration 정보를 가지도록 하는 redis key 값입니다.
   */
  def regionExpireKey(region: String) = region + ":expire"

  /** ping */
  def ping: Future[String] = redis.ping()

  /** database size */
  def dbSize: Future[Long] = redis.dbsize()

  /**
   * Cache item 존재 유무 확인
   */
  def exists(region: String, key: String): Future[Boolean] = {
    redis.hexists(region, key)
  }

  /**
   * Get client item
   *
   * @param region           region name
   * @param key              client key
   * @param expireInSeconds  expiration timeout value
   * @return return cached entity, if not exists return null.
   */
  def get(region: String, key: String, expireInSeconds: Long = 0): Future[Any] = {

    // 값을 가져오고, 값이 있고, expiration이 설정되어 있다면 갱신합니다.
    async {
      val value = await(redis.hget[Any](region, key).map(_.orNull))

      if (value != null && expireInSeconds > 0 && !region.contains("UpdateTimestampsCache")) {
        val score = System.currentTimeMillis + expireInSeconds * 1000L
        await(redis.zadd(regionExpireKey(region), (score, key)))
      }
      value
    }
  }

  /**
   * 지정한 region에 있는 모든 캐시 키를 조회합니다.
   */
  def keysInRegion(region: String): Future[Seq[String]] = {
    redis.hkeys(region)
  }

  /** get key size in region */
  def keySizeInRegion(region: String): Future[Long] = redis.hlen(region)

  /** get all cache key in given region */
  def getAll(region: String): Future[Map[String, Any]] = {
    redis.hgetall[Any](region)
  }

  /** get multiple cache items
    * @param region region name
    * @param keys cache keys
    */
  @varargs
  def multiGet(region: String, keys: String*): Future[Seq[Any]] = {
    redis.hmget[Any](region, keys: _*).map { x => x.flatten }
  }

  /** get multiple cache items
    * @param region region name
    * @param keys cache keys
    */
  def multiGet(region: String, keys: Iterable[String]): Future[Seq[Any]] = {
    redis.hmget[Any](region, keys.toSeq: _*).map { x => x.flatten }
  }

  /**
   * 캐시 항목을 저장합니다.
   * @param region 영역
   * @param key cache key
   * @param value cache value
   * @param expiry expiry
   * @param unit time unit
   * @return if saved return true, else false
   */
  def set(region: String, key: String, value: Any, expiry: Long = 0, unit: TimeUnit = TimeUnit.SECONDS): Future[Boolean] = {
    async {
      val result = await(redis.hset(region, key, value))

      if (expiry > 0) {
        val score = System.currentTimeMillis() + unit.toSeconds(expiry) * 1000L
        await(redis.zadd(regionExpireKey(region), (score, key)))
      }
      result
    }
  }

  /**
   * 지정한 영역의 캐시 항목 중 expire 된 것들을 모두 삭제한다.
   * @param region region name
   */
  def expire(region: String) {
    try {
      val regionExpire = regionExpireKey(region)
      val score = System.currentTimeMillis()

      val results = redis.zrangebyscore[String](regionExpire, Limit(0), Limit(score))
      val keysToExpire: Seq[String] = Await.result(results, 100 seconds)

      if (keysToExpire != null && keysToExpire.nonEmpty) {
        log.trace(s"cache item들을 expire 시킵니다. region=$region")

        keysToExpire.foreach { key =>
          redis.hdel(region, key)
        }
        redis.zremrangebyscore(regionExpire, Limit(0), Limit(score))

      }
    } catch {
      case NonFatal(e) => log.warn(s"Region을 삭제하는데 실패했습니다.", e)
    }
  }

  /**
   * delete cache item.
   * @param region region name
   * @param key cache key to delete
   */
  def delete(region: String, key: String): Future[Long] = {
    async {
      await(redis.zrem(regionExpireKey(region), key))
      await(redis.hdel(region, key))
    }
  }

  /**
   * delete cache items.
   * @param region region name
   * @param keys cache keys to delete
   */
  @varargs
  def multiDelete(region: String, keys: String*): Future[Boolean] = {
    if (keys == null || keys.isEmpty)
      return Future { false }

    val regionExpire = regionExpireKey(region)

    val results = keys.map { key =>
      async {
        val rc = await(redis.hdel(region, key))
        val ec = await(redis.zrem(regionExpire, key))
        rc > 0 || ec > 0
      }
    }
    Future {
      results.awaitAll.forall(identity)
    }
  }

  /**
   * delete cache items.
   * @param region region name
   * @param keys cache keys to delete
   */
  def multiDelete(region: String, keys: Iterable[String]): Future[Boolean] = {
    multiDelete(region, keys.toSeq: _*)
  }

  /**
   * delete region
   * @param region region name
   */
  def deleteRegion(region: String): Future[Long] = {
    async {
      await(redis.del(region))
      await(redis.del(regionExpireKey(region)))
    }
  }

  /** Flush db */
  def flushDb(): Future[Boolean] = {
    log.info(s"hibernate cache를 담은 Redis의 DB 전체를 flush 합니다...")
    redis.flushdb()
  }

}
