package org.hibernate.cache.rediscala.regions

import java.util.Properties
import org.hibernate.cache.rediscala.Promises
import org.hibernate.cache.rediscala.client.HibernateRedisCache
import org.hibernate.cache.rediscala.strategy.RedisAccessStrategyFactory
import org.hibernate.cache.spi.{ CacheDataDescription, TransactionalDataRegion }
import org.hibernate.cfg.Settings
import org.slf4j.LoggerFactory
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * RedisTransactionalDataRegion
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 21. 오후 12:00
 */
class RedisTransactionalDataRegion(private[this] val _accessStrategyFactory: RedisAccessStrategyFactory,
                                   private[this] val _cache: HibernateRedisCache,
                                   private[this] val _regionName: String,
                                   val settings: Settings,
                                   val metadata: CacheDataDescription,
                                   private[this] val _props: Properties)
  extends RedisDataRegion(_accessStrategyFactory, _cache, _regionName, _props)
  with TransactionalDataRegion {

  private lazy val log = LoggerFactory.getLogger(getClass)

  override def getCacheDataDescription: CacheDataDescription = metadata

  override def isTransactionAware: Boolean = false

  def get(key: Any): Any = {

    val task = cache.get(regionName, key.toString, expireInSeconds)
    task onFailure {
      case e: Throwable =>
        log.error(s"can't retrive cache item. key=$key", e)
        return null
    }
    Promises.await(task)
  }

  def put(key: Any, value: Any) {
    val task = cache.set(regionName, key.toString, value, expireInSeconds)

    task onFailure {
      case e: Throwable => log.warn(s"can't set cache item. key=$key", e)
    }
    Promises.await(task)
  }

  def remove(key: Any) {
    val task = cache.delete(regionName, key.toString)

    task onFailure {
      case e: Throwable => log.warn(s"Fail to delete key. region=$regionName, key=$key", e)
    }
    Promises.await(task)
  }

  def clear() {
    val task = cache.deleteRegion(regionName)

    task onFailure {
      case e: Throwable => log.warn(s"Fail to delete region [$regionName]", e)
    }
    Promises.await(task)
  }

  def evict(key: String) {
    remove(key)
  }

  def evictAll() {
    clear()
  }
}
