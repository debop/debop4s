package org.hibernate.cache.rediscala.strategy

import org.hibernate.cache.rediscala.regions.{RedisCollectionRegion, RedisEntityRegion, RedisNaturalIdRegion}
import org.hibernate.cache.spi.access._
import org.hibernate.cfg.Settings
import org.slf4j.LoggerFactory

class TransactionalRedisCollectionAccessStrategy(private[this] val _region: RedisCollectionRegion,
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

  override def unlockItem(key: Object, lock: SoftLock) {
    // Nothing to do.
  }
}

class TransactionalRedisEntityRegionAccessStrategy(private[this] val _region: RedisEntityRegion,
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

  override def unlockItem(key: Object, lock: SoftLock) {
    // Nothing to do.
  }

  override def insert(key: Any, value: Any, version: Any): Boolean = {
    region.put(key, value)
    true
  }

  override def afterInsert(key: Any, value: Any, version: Any): Boolean = {
    false
  }

  override def update(key: Any, value: Any, currentVersion: Any, previousVersion: Any): Boolean = {
    region.put(key, value)
    true
  }

  override def afterUpdate(key: Any,
                           value: Any,
                           currentVersion: Any,
                           previousVersion: Any,
                           lock: SoftLock): Boolean = {
    false
  }

}

class TransactionalRedisNatualIdRegionAccessStrategy(private[this] val _region: RedisNaturalIdRegion,
                                                     private[this] val _settings: Settings)
  extends AbstractRedisAccessStrategy(_region, _settings)
  with NaturalIdRegionAccessStrategy {

  private lazy val log = LoggerFactory.getLogger(getClass)

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

  override def unlockItem(key: Object, lock: SoftLock) {
    // Nothing to do.
  }

  override def insert(key: Any, value: Any): Boolean = {
    region.put(key, value)
    true
  }

  override def afterInsert(key: Any, value: Any): Boolean = {
    false
  }

  override def update(key: Any, value: Any): Boolean = {
    region.put(key, value)
    true
  }

  override def afterUpdate(key: Any, value: Any, lock: SoftLock): Boolean = {
    false
  }
}


