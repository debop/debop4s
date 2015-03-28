package org.hibernate.cache.rediscala.strategy

import java.util.concurrent.atomic.AtomicLong
import org.hibernate.cache.rediscala.regions.RedisTransactionalDataRegion
import org.hibernate.cache.spi.access.SoftLock
import org.hibernate.cfg.Settings

/**
 * org.hibernate.cache.rediscala.strategy.AbstractReadWriteRedisAccessStrategy
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 21. 오후 1:47
 */
class AbstractReadWriteRedisAccessStrategy[T <: RedisTransactionalDataRegion]
(private[this] val _region: T, private[this] val _settings: Settings)
  extends AbstractRedisAccessStrategy[T](_region, _settings) {

  private val nextLockId = new AtomicLong()

  def get(key: Any, txTimestamp: Long): AnyRef =
    region.get(key).asInstanceOf[AnyRef]

  override def putFromLoad(key: Any,
                           value: Any,
                           txTimestamp: Long,
                           version: Any,
                           minimalPutOverride: Boolean): Boolean = {
    region.put(key, value)
    true
  }

  def lockItem(key: Any, version: Any): SoftLock = {
    region.remove(key)
    null
  }

  def unlockItem(key: Any, lock: SoftLock) {
    region.remove(key)
  }
}
