package org.hibernate.cache.redis.regions

import com.github.debop4s.core.parallels.Promises
import java.util.Properties
import org.hibernate.cache.redis.client.RedisHibernateCache
import org.hibernate.cache.redis.strategy.RedisAccessStrategyFactory
import org.hibernate.cache.spi.GeneralDataRegion

/**
 * org.hibernate.cache.redis.regions.RedisGeneralDataRegion 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 21. 오후 1:28
 */
class RedisGeneralDataRegion(private[this] val _accessStrategyFactory: RedisAccessStrategyFactory,
                             private[this] val _cache: RedisHibernateCache,
                             private[this] val _regionName: String,
                             private[this] val _props: Properties)
    extends RedisDataRegion(_accessStrategyFactory,
                               _cache,
                               _regionName,
                               _props)
    with GeneralDataRegion {


    override def get(key: Any): AnyRef = {
        Promises.await(cache.get(regionName, key.toString, expireInSeconds)).asInstanceOf[AnyRef]
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
