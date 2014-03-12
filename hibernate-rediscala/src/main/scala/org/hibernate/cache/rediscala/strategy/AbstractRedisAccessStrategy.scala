package org.hibernate.cache.rediscala.strategy

import org.hibernate.cache.rediscala.regions.RedisTransactionalDataRegion
import org.hibernate.cache.spi.access.SoftLock
import org.hibernate.cfg.Settings
import org.slf4j.LoggerFactory

/**
 * org.hibernate.cache.rediscala.strategy.AbstractRedisAccessStrategy
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 21. 오전 9:28
 */
abstract class AbstractRedisAccessStrategy[T <: RedisTransactionalDataRegion](val region: T, val settings: Settings) {

    private lazy val log = LoggerFactory.getLogger(getClass)

    def putFromLoad(key: Any, value: Any, txTimestamp: Long, version: Any): Boolean =
        putFromLoad(key, value, txTimestamp, version, settings.isMinimalPutsEnabled)

    def putFromLoad(key: Any, value: Any, txTimestamp: Long, version: Any, minimalPutOverride: Boolean): Boolean

    def lockRegion: SoftLock = null

    def unlockRegion(lock: SoftLock) {
        region.clear()
    }

    def remove(key: Any) {
        region.remove(key)
    }

    def removeAll() {
        region.clear()
    }

    def evict(key: Any) {
        region.remove(key)
    }

    def evictAll() {
        removeAll()
    }
}
