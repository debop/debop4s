package org.hibernate.cache.rediscala.regions

import java.util.Properties

import org.hibernate.cache.rediscala._
import org.hibernate.cache.rediscala.client.RedisCache
import org.hibernate.cache.rediscala.strategy.RedisAccessStrategyFactory
import org.hibernate.cache.spi.GeneralDataRegion

import scala.util.control.NonFatal

/**
 * RedisGeneralDataRegion
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 21. 오후 1:28
 */
class RedisGeneralDataRegion(private[this] val _accessStrategyFactory: RedisAccessStrategyFactory,
                             private[this] val _cache: RedisCache,
                             private[this] val _regionName: String,
                             private[this] val _props: Properties)
  extends RedisDataRegion(_accessStrategyFactory,
                           _cache,
                           _regionName,
                           _props)
  with GeneralDataRegion {


  override def get(key: Any): AnyRef = {
    try {
      cache.get(regionName, key.toString, expireInSeconds).map(_.asInstanceOf[AnyRef]).await
    } catch {
      case NonFatal(e) => null
    }
  }

  override def put(key: Any, value: Any) {
    cache.set(regionName, key.toString, value, expireInSeconds).await
  }

  override def evict(key: Any) {
    cache.delete(regionName, key.toString).await
  }

  override def evictAll() {
    cache.deleteRegion(regionName).await
  }
}
