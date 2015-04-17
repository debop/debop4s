package org.hibernate.cache.rediscala.strategy

import org.hibernate.cache.rediscala.regions._
import org.hibernate.cache.spi.access._
import org.hibernate.cfg.Settings


class NonStrictReadWriteRedisCollectionRegionAccessStrategy(private[this] val _region: RedisCollectionRegion,
                                                            private[this] val _settings: Settings)
  extends AbstractRedisAccessStrategy(_region, _settings)
  with CollectionRegionAccessStrategy {

  override def getRegion = region

  override def get(key: Any, txTimestamp: Long): AnyRef =
    region.get(key).asInstanceOf[AnyRef]

  override def putFromLoad(key: Any,
                           value: Any,
                           txTimestamp: Long,
                           version: Any,
                           minimalPutOverride: Boolean): Boolean = {
    if (minimalPutOverride && region.contains(key)) {
      return false
    }

    region.put(key, value)
    true
  }

  override def lockItem(key: Any, version: Any): SoftLock = null

  override def unlockItem(key: Any, lock: SoftLock) {
    region.remove(key)
  }
}

class NonStrictReadWriteRedisEntityRegionAccessStrategy(private[this] val _region: RedisEntityRegion,
                                                        private[this] val _settings: Settings)
  extends AbstractRedisAccessStrategy(_region, _settings)
  with EntityRegionAccessStrategy {

  override def getRegion = region

  override def get(key: Any, txTimestamp: Long): AnyRef =
    region.get(key).asInstanceOf[AnyRef]

  override def putFromLoad(key: Any,
                           value: Any,
                           txTimestamp: Long,
                           version: Any,
                           minimalPutOverride: Boolean): Boolean = {
    if (minimalPutOverride && region.contains(key)) {
      return false
    }

    region.put(key, value)
    true
  }

  override def lockItem(key: Any, version: Any): SoftLock = null

  override def unlockItem(key: Any, lock: SoftLock) {
    region.remove(key)
  }

  override def insert(key: Any, value: Any, version: Any): Boolean = false

  override def afterInsert(key: Any, value: Any, version: Any): Boolean = false

  override def update(key: Any, value: Any, currentVersion: Any, previousVersion: Any): Boolean = {
    remove(key)
    true
  }

  override def afterUpdate(key: Any,
                           value: Any,
                           currentVersion: Any,
                           previousVersion: Any,
                           lock: SoftLock): Boolean = {
    unlockItem(key, lock)
    true
  }
}

class NonStrictReadWriteRedisNatualIdRegionAccessStrategy(private[this] val _region: RedisNaturalIdRegion,
                                                          private[this] val _settings: Settings)
  extends AbstractRedisAccessStrategy(_region, _settings)
  with NaturalIdRegionAccessStrategy {

  override def getRegion = region

  override def get(key: Any, txTimestamp: Long) =
    region.get(key).asInstanceOf[AnyRef]

  override def putFromLoad(key: Any,
                           value: Any,
                           txTimestamp: Long,
                           version: Any,
                           minimalPutOverride: Boolean): Boolean = {
    if (minimalPutOverride && region.contains(key)) {
      return false
    }

    region.put(key, value)
    true
  }

  override def lockItem(key: Any, version: Any): SoftLock = null

  override def unlockItem(key: Any, lock: SoftLock) {
    region.remove(key)
  }

  def insert(key: Any, value: Any): Boolean = false

  def afterInsert(key: Any, value: Any): Boolean = false

  def update(key: Any, value: Any): Boolean = {
    remove(key)
    true
  }

  def afterUpdate(key: Any, value: Any, lock: SoftLock): Boolean = {
    unlockItem(key, lock)
    true
  }
}
