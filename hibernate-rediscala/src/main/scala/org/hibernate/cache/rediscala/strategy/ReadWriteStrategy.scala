package org.hibernate.cache.rediscala.strategy

import org.hibernate.cache.rediscala.regions.{RedisCollectionRegion, RedisEntityRegion, RedisNaturalIdRegion}
import org.hibernate.cache.spi.access.{CollectionRegionAccessStrategy, EntityRegionAccessStrategy, NaturalIdRegionAccessStrategy, SoftLock}
import org.hibernate.cfg.Settings

class ReadWriteRedisCollectionRegionAccessStrategy(private[this] val _region: RedisCollectionRegion,
                                                   private[this] val _settings: Settings)
  extends AbstractReadWriteRedisAccessStrategy(_region, _settings)
  with CollectionRegionAccessStrategy {

  def getRegion = region

  override def get(key: Any, txTimestamp: Long): AnyRef =
    super.get(key, txTimestamp)
}

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

