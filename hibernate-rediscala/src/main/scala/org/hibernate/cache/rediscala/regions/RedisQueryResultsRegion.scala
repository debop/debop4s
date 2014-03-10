package org.hibernate.cache.rediscala.regions

import java.util.Properties
import org.hibernate.cache.rediscala.client.HibernateRedisCache
import org.hibernate.cache.rediscala.strategy.RedisAccessStrategyFactory
import org.hibernate.cache.spi.QueryResultsRegion


/**
 * org.hibernate.cache.rediscala.regions.RedisQueryResultsRegion
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 21. 오후 1:37
 */
class RedisQueryResultsRegion(private[this] val _accessStrategyFactory: RedisAccessStrategyFactory,
                              private[this] val _cache: HibernateRedisCache,
                              private[this] val _regionName: String,
                              private[this] val _props: Properties)
  extends RedisGeneralDataRegion(_accessStrategyFactory,
    _cache,
    _regionName,
    _props)
  with QueryResultsRegion {
}
