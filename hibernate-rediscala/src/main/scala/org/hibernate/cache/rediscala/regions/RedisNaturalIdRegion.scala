package org.hibernate.cache.rediscala.regions

import java.util.Properties
import org.hibernate.cache.rediscala.client.HibernateRedisCache
import org.hibernate.cache.rediscala.strategy.RedisAccessStrategyFactory
import org.hibernate.cache.spi.access.{NaturalIdRegionAccessStrategy, AccessType}
import org.hibernate.cache.spi.{NaturalIdRegion, CacheDataDescription}
import org.hibernate.cfg.Settings

/**
 * org.hibernate.cache.rediscala.regions.RedisNatualIdRegion
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 21. 오후 1:12
 */
class RedisNaturalIdRegion(private[this] val _accessStrategyFactory: RedisAccessStrategyFactory,
                           private[this] val _cache: HibernateRedisCache,
                           private[this] val _regionName: String,
                           private[this] val _settings: Settings,
                           private[this] val _metadata: CacheDataDescription,
                           private[this] val _props: Properties)
  extends RedisTransactionalDataRegion(_accessStrategyFactory,
    _cache,
    _regionName,
    _settings,
    _metadata,
    _props) with NaturalIdRegion {

  def buildAccessStrategy(accessType: AccessType): NaturalIdRegionAccessStrategy =
    accessStrategyFactory.createNaturalIdRegionAccessStrategy(this, accessType)

}
