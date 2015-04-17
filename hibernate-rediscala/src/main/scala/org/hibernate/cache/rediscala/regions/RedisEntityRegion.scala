package org.hibernate.cache.rediscala.regions

import java.util.Properties

import org.hibernate.cache.rediscala.client.RedisCache
import org.hibernate.cache.rediscala.strategy.RedisAccessStrategyFactory
import org.hibernate.cache.spi.access.{AccessType, EntityRegionAccessStrategy}
import org.hibernate.cache.spi.{CacheDataDescription, EntityRegion}
import org.hibernate.cfg.Settings

/**
 * RedisEntityRegion
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 21. 오전 9:28
 */
class RedisEntityRegion(private[this] val _accessStrategyFactory: RedisAccessStrategyFactory,
                        private[this] val _cache: RedisCache,
                        private[this] val _regionName: String,
                        private[this] val _settings: Settings,
                        private[this] val _metadata: CacheDataDescription,
                        private[this] val _props: Properties)
  extends RedisTransactionalDataRegion(
                                        _accessStrategyFactory,
                                        _cache,
                                        _regionName,
                                        _settings,
                                        _metadata,
                                        _props
                                      ) with EntityRegion {

  def buildAccessStrategy(accessType: AccessType): EntityRegionAccessStrategy =
    accessStrategyFactory.createEntityRegionAccessStrategy(this, accessType)

}
