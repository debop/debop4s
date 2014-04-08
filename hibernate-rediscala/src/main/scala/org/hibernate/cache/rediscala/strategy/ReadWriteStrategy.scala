package org.hibernate.cache.rediscala.strategy

import org.hibernate.cache.rediscala.regions.{RedisNaturalIdRegion, RedisEntityRegion, RedisCollectionRegion}
import org.hibernate.cache.spi.access.{SoftLock, NaturalIdRegionAccessStrategy, EntityRegionAccessStrategy, CollectionRegionAccessStrategy}
import org.hibernate.cfg.Settings

/**
 * org.hibernate.cache.rediscala.strategy.ReadWriteRedisCollectionRegionAccessStrategy
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 21. 오후 1:19
 */
class ReadWriteRedisCollectionRegionAccessStrategy(private[this] val _region: RedisCollectionRegion,
                                                   private[this] val _settings: Settings)
    extends AbstractReadWriteRedisAccessStrategy(_region, _settings)
    with CollectionRegionAccessStrategy {

    def getRegion = region

    override def get(key: Any, txTimestamp: Long): AnyRef =
        super.get(key, txTimestamp)
}

/**
 * org.hibernate.cache.rediscala.strategy.ReadWriteRedisEntityRegionAccessStrategy
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 21. 오후 1:17
 */
class ReadWriteRedisEntityRegionAccessStrategy(private[this] val _region: RedisEntityRegion,
                                               private[this] val _settings: Settings)
    extends AbstractReadWriteRedisAccessStrategy(_region, _settings)
    with EntityRegionAccessStrategy {

    def getRegion = region

    override def insert(key: Any, value: Any, version: Any): Boolean = {
        region.put(key, value)
        true
    }

    override def afterInsert(key: Any, value: Any, version: Any): Boolean = {
        region.put(key, value)
        true
    }

    override def update(key: Any,
                        value: Any,
                        currentVersion: Any,
                        previousVersion: Any): Boolean = {
        region.put(key, value)
        true
    }

    override def afterUpdate(key: Any,
                             value: Any,
                             currentVersion: Any,
                             previousVersion: Any,
                             lock: SoftLock): Boolean = {
        region.put(key, value)
        true
    }
}

/**
 * org.hibernate.cache.rediscala.strategy.ReadWriteRedisNaturalIdRegionAccessStrategy
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 21. 오후 1:22
 */
class ReadWriteRedisNaturalIdRegionAccessStrategy(private[this] val _region: RedisNaturalIdRegion,
                                                  private[this] val _settings: Settings)
    extends AbstractReadWriteRedisAccessStrategy(_region, _settings)
    with NaturalIdRegionAccessStrategy {

    def getRegion = region

    override def insert(key: Any, value: Any): Boolean = {
        region.put(key, value)
        true
    }

    override def afterInsert(key: Any, value: Any): Boolean = {
        region.put(key, value)
        true
    }

    override def update(key: Any, value: Any): Boolean = {
        region.put(key, value)
        true
    }

    override def afterUpdate(key: Any, value: Any, lock: SoftLock): Boolean = {
        region.put(key, value)
        true
    }
}

