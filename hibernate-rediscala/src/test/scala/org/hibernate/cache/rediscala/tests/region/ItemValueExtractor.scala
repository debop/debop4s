package org.hibernate.cache.rediscala.tests.region

import org.hibernate.cache.rediscala.regions.RedisTransactionalDataRegion
import org.hibernate.cache.rediscala.strategy.AbstractReadWriteRedisAccessStrategy
import org.hibernate.cfg.Settings

/**
 * ItemValueExtractor 
 *
 * @author sunghyouk.bae@gmail.com
 * @since 2014. 2. 28.
 */
class ItemValueExtractor(private val _region: RedisTransactionalDataRegion,
                         private val _settings: Settings)
    extends AbstractReadWriteRedisAccessStrategy[RedisTransactionalDataRegion](_region, _settings) {

    def getValue[V](entry: Any): V = entry.asInstanceOf[V]

}
