package org.hibernate.cache.rediscala.strategy

import org.hibernate.cache.rediscala.regions.{RedisNaturalIdRegion, RedisEntityRegion, RedisCollectionRegion}
import org.hibernate.cache.spi.access._
import org.hibernate.cfg.Settings
import org.slf4j.LoggerFactory


class ReadOnlyRedisCollectionRegionAccessStrategy(private[this] val _region: RedisCollectionRegion,
                                                  private[this] val _settings: Settings)
  extends AbstractRedisAccessStrategy(_region, _settings)
  with CollectionRegionAccessStrategy {

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
      log.debug(s"cancel put from load...")
      return false
    }
    region.put(key, value)
    true
  }

  override def lockItem(key: Any, version: Any): SoftLock = null

  override def unlockItem(key: Any, lock: SoftLock) {}

}

class ReadOnlyRedisEntityRegionAccessStrategy(private[this] val _region: RedisEntityRegion,
                                              private[this] val _settings: Settings)
  extends AbstractRedisAccessStrategy(_region, _settings)
  with EntityRegionAccessStrategy {

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
      log.debug(s"cancel put from load...")
      return false
    }

    region.put(key, value)
    true
  }

  override def lockItem(key: Any, version: Any): SoftLock = null

  override def unlockItem(key: Any, lock: SoftLock) {
    evict(key)
  }

  override def insert(key: Any, value: Any, version: Any): Boolean = false

  override def afterInsert(key: Any, value: Any, version: Any): Boolean = {
    region.put(key, value)
    true
  }

  override def update(key: Any, value: Any, currentVersion: Any, previousVersion: Any): Boolean = {
    throw new UnsupportedOperationException(s"Can't write to a readonly object")
  }

  override def afterUpdate(key: Any,
                           value: Any,
                           currentVersion: Any,
                           previousVersion: Any,
                           lock: SoftLock): Boolean = {
    throw new UnsupportedOperationException(s"Can't write to a readonly object")
  }
}

class ReadOnlyRedisNaturalIdRegionAccessStrategy(private[this] val _region: RedisNaturalIdRegion,
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
      log.debug(s"cancel put from load...")
      return false
    }

    region.put(key, value)
    true
  }

  override def lockItem(key: Any, version: Any): SoftLock = null

  override def unlockItem(key: Any, lock: SoftLock) {
    evict(key)
  }

  override def insert(key: Any, value: Any): Boolean = false

  override def afterInsert(key: Any, value: Any): Boolean = {
    region.put(key, value)
    true
  }

  override def update(key: Any, value: Any): Boolean = {
    throw new UnsupportedOperationException(s"Can't write to a readonly object")
  }

  override def afterUpdate(key: Any, value: Any, lock: SoftLock): Boolean = {
    throw new UnsupportedOperationException(s"Can't write to a readonly object")
  }

}

