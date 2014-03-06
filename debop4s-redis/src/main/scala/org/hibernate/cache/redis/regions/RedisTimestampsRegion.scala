package org.hibernate.cache.redis.regions

import java.util.Properties
import org.hibernate.cache.redis.client.HibernateRedisCache
import org.hibernate.cache.redis.strategy.RedisAccessStrategyFactory
import org.hibernate.cache.spi.TimestampsRegion

/**
 * org.hibernate.cache.redis.regions.RedisTimestampsRegion 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 21. 오후 1:38
 */
class RedisTimestampsRegion(private[this] val _accessStrategyFactory: RedisAccessStrategyFactory,
                            private[this] val _cache: HibernateRedisCache,
                            private[this] val _regionName: String,
                            private[this] val _props: Properties)
    extends RedisGeneralDataRegion(_accessStrategyFactory,
                                      _cache,
                                      _regionName,
                                      _props)
    with TimestampsRegion {
}