package org.hibernate.cache.rediscala.regions

import java.util.Properties
import org.hibernate.cache.rediscala.Promises
import org.hibernate.cache.rediscala.client.HibernateRedisCache
import org.hibernate.cache.rediscala.strategy.RedisAccessStrategyFactory
import org.hibernate.cache.spi.GeneralDataRegion

/**
 * org.hibernate.cache.rediscala.regions.RedisGeneralDataRegion
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 21. 오후 1:28
 */
class RedisGeneralDataRegion(private[this] val _accessStrategyFactory: RedisAccessStrategyFactory,
                             private[this] val _cache: HibernateRedisCache,
                             private[this] val _regionName: String,
                             private[this] val _props: Properties)
  extends RedisDataRegion(_accessStrategyFactory,
    _cache,
    _regionName,
    _props)
  with GeneralDataRegion {


  override def get(key: Any): AnyRef = {
    try {
      val value = Promises.await(cache.get(regionName, key.toString, expireInSeconds)).asInstanceOf[AnyRef]
      log.trace(s"cache item key=$key, value=$value")
      value
    } catch {
      case e: Throwable => null
    }
  }

  override def put(key: Any, value: Any) {
    cache.set(regionName, key.toString, value, expireInSeconds)
  }

  override def evict(key: Any) {
    cache.delete(regionName, key.toString)
  }

  override def evictAll() {
    cache.deleteRegion(regionName)
  }
}
