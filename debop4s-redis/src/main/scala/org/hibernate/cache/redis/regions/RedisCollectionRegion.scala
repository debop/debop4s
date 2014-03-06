package org.hibernate.cache.redis.regions

import java.util.Properties
import org.hibernate.cache.redis.client.HibernateRedisCache
import org.hibernate.cache.redis.strategy.RedisAccessStrategyFactory
import org.hibernate.cache.spi.access.{CollectionRegionAccessStrategy, AccessType}
import org.hibernate.cache.spi.{CollectionRegion, CacheDataDescription}
import org.hibernate.cfg.Settings

/**
 * org.hibernate.cache.redis.regions.RedisCollectionRegion 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 21. 오후 1:11
 */
class RedisCollectionRegion(private[this] val _accessStrategyFactory: RedisAccessStrategyFactory,
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
                                            _props)
    with CollectionRegion {

    def buildAccessStrategy(accessType: AccessType): CollectionRegionAccessStrategy =
        accessStrategyFactory.createCollectionRegionAccessStrategy(this, accessType)

}
