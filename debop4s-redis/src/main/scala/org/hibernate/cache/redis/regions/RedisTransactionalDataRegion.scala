package org.hibernate.cache.redis.regions

import com.github.debop4s.core.parallels.Promises
import java.util.Properties
import org.hibernate.cache.redis.client.HibernateRedisCache
import org.hibernate.cache.redis.strategy.RedisAccessStrategyFactory
import org.hibernate.cache.spi.{CacheDataDescription, TransactionalDataRegion}
import org.hibernate.cfg.Settings

/**
 * org.hibernate.cache.redis.regions.RedisTransactionalDataRegion 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 21. 오후 12:00
 */
class RedisTransactionalDataRegion(private[this] val _accessStrategyFactory: RedisAccessStrategyFactory,
                                   private[this] val _cache: HibernateRedisCache,
                                   private[this] val _regionName: String,
                                   val settings: Settings,
                                   val metadata: CacheDataDescription,
                                   private[this] val _props: Properties)
  extends RedisDataRegion(_accessStrategyFactory, _cache, _regionName, _props)
          with TransactionalDataRegion {


  override def getCacheDataDescription: CacheDataDescription = metadata

  override def isTransactionAware: Boolean = false

  def get(key: Any): Any = {
    try {
      val value = Promises.await(cache.get(regionName, key.toString, expireInSeconds))
      log.trace(s"cache item key=$key, value=$value")
      value
    } catch {
      case e: Throwable =>
        log.warn(s"Fail to get cache item... key=$key", e)
        return null
    }
  }

  def put(key: Any, value: Any) {
    cache.set(regionName, key.toString, value, expireInSeconds)
  }

  def remove(key: Any) {
    cache.delete(regionName, key.toString)
  }

  def clear() {
    cache.deleteRegion(regionName)
  }

  def evict(key: String) {
    remove(key)
  }

  def evictAll() {
    clear()
  }
}
