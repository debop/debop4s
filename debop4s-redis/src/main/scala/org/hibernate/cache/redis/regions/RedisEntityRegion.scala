package org.hibernate.cache.redis.regions

import java.util.Properties
import org.hibernate.cache.redis.client.RedisHibernateCache
import org.hibernate.cache.redis.strategy.RedisAccessStrategyFactory
import org.hibernate.cache.spi.access.{EntityRegionAccessStrategy, AccessType}
import org.hibernate.cache.spi.{CacheDataDescription, EntityRegion}
import org.hibernate.cfg.Settings

/**
 * org.hibernate.cache.redis.regions.RedisEntityRegion 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 21. 오전 9:28
 */
class RedisEntityRegion(private[this] val _accessStrategyFactory: RedisAccessStrategyFactory,
                        private[this] val _cache: RedisHibernateCache,
                        private[this] val _regionName: String,
                        private[this] val _settings: Settings,
                        private[this] val _metadata: CacheDataDescription,
                        private[this] val _props: Properties)
    extends RedisTransactionalDataRegion(_accessStrategyFactory,
                                            _cache,
                                            _regionName,
                                            _settings,
                                            _metadata,
                                            _props) with EntityRegion {

    def buildAccessStrategy(accessType: AccessType): EntityRegionAccessStrategy =
        accessStrategyFactory.createEntityRegionAccessStrategy(this, accessType)

}
