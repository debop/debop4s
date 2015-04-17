package org.hibernate.cache.rediscala.regions

import java.util
import java.util.Properties

import org.hibernate.cache.rediscala._
import org.hibernate.cache.rediscala.client.RedisCache
import org.hibernate.cache.rediscala.strategy.RedisAccessStrategyFactory
import org.hibernate.cache.rediscala.utils.RedisCacheUtil
import org.hibernate.cache.spi.Region
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._


/**
 * RedisDataRegion
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 21. 오전 9:25
 */
abstract class RedisDataRegion(protected val accessStrategyFactory: RedisAccessStrategyFactory,
                               val cache: RedisCache,
                               val regionName: String,
                               val props: Properties) extends Region {
  protected lazy val log = LoggerFactory.getLogger(getClass)

  val expireInSeconds = RedisCacheUtil.expiryInSeconds(regionName)

  override def getName: String = regionName

  @volatile var regionDeleted = true

  override def destroy(): Unit = synchronized {
    if (regionDeleted) return

    cache.deleteRegion(regionName).await
    regionDeleted = true
  }

  override def contains(key: Any): Boolean = {
    cache.exists(regionName, key.toString).await
  }

  override def getSizeInMemory: Long = {
    val sizeInMemory = cache.dbSize.await

    log.trace(s"size in memory. region=$regionName, size=$sizeInMemory")
    sizeInMemory
  }

  override def getElementCountInMemory: Long = {
    val elementCount = cache.keySizeInRegion(regionName).await

    log.trace(s"get key size in region. region=$regionName, elementCount=$elementCount")
    elementCount
  }

  override def getElementCountOnDisk: Long = -1L

  override def toMap: util.Map[Any, Any] = {
    cache.getAll(regionName).await.map { case (k, v) => (k.asInstanceOf[Any], v) }.asJava
  }

  override def nextTimestamp: Long = RedisCacheUtil.nextTimestamp

  override def getTimeout: Int = 0

}
