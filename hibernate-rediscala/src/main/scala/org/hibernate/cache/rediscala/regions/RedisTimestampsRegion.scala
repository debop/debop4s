package org.hibernate.cache.rediscala.regions

import java.util.Properties

import org.hibernate.cache.rediscala.client.RedisCache
import org.hibernate.cache.rediscala.strategy.RedisAccessStrategyFactory
import org.hibernate.cache.spi.TimestampsRegion

/**
 * RedisTimestampsRegion
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 21. 오후 1:38
 */
class RedisTimestampsRegion(private[this] val _accessStrategyFactory: RedisAccessStrategyFactory,
                            private[this] val _cache: RedisCache,
                            private[this] val _regionName: String,
                            private[this] val _props: Properties)
  extends RedisGeneralDataRegion(_accessStrategyFactory, _cache, _regionName, _props)
  with TimestampsRegion {
}