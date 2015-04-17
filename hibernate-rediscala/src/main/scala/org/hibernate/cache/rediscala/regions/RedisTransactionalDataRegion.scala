package org.hibernate.cache.rediscala.regions

import java.util.Properties

import org.hibernate.cache.rediscala._
import org.hibernate.cache.rediscala.client.RedisCache
import org.hibernate.cache.rediscala.strategy.RedisAccessStrategyFactory
import org.hibernate.cache.spi.{CacheDataDescription, TransactionalDataRegion}
import org.hibernate.cfg.Settings

import scala.util.control.NonFatal

/**
 * RedisTransactionalDataRegion
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 21. 오후 12:00
 */
class RedisTransactionalDataRegion(private[this] val _accessStrategyFactory: RedisAccessStrategyFactory,
                                   private[this] val _cache: RedisCache,
                                   private[this] val _regionName: String,
                                   val settings: Settings,
                                   val metadata: CacheDataDescription,
                                   private[this] val _props: Properties)
  extends RedisDataRegion(_accessStrategyFactory, _cache, _regionName, _props)
  with TransactionalDataRegion {

  override def getCacheDataDescription: CacheDataDescription = metadata

  override def isTransactionAware: Boolean = false

  def get(key: Any): Any = {
    try {
      cache.get(regionName, key.toString, expireInSeconds).await
    } catch {
      case NonFatal(e) =>
        log.error(s"can't retrive cache item. key=$key", e)
        null
    }
  }

  def put(key: Any, value: Any) {
    try {
      cache.set(regionName, key.toString, value, expireInSeconds).await
    } catch {
      case NonFatal(e) =>
        log.warn(s"can't set cache item. key=$key", e)

    }
  }

  def remove(key: Any) {
    try {
      cache.delete(regionName, key.toString).await
    } catch {
      case NonFatal(e) =>
        log.warn(s"Fail to delete key. region=$regionName, key=$key", e)
    }
  }

  def clear() {
    try {
      cache.deleteRegion(regionName).await
    } catch {
      case NonFatal(e) =>
        log.warn(s"Fail to delete region [$regionName]", e)
    }
  }

  def evict(key: String) {
    remove(key)
  }

  def evictAll() {
    clear()
  }
}
