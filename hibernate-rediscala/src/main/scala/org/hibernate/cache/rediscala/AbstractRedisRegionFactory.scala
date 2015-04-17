package org.hibernate.cache.rediscala

import java.util.Properties
import java.util.concurrent.ConcurrentSkipListSet

import org.hibernate.cache.rediscala.client.RedisCache
import org.hibernate.cache.rediscala.regions._
import org.hibernate.cache.rediscala.strategy._
import org.hibernate.cache.spi._
import org.hibernate.cache.spi.access.AccessType
import org.hibernate.cfg.Settings
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
import scala.util.control.NonFatal

/**
 * AbstractRedisRegionFactory
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 20. 오후 2:16
 */
abstract class AbstractRedisRegionFactory(val props: Properties) extends RegionFactory {

  private lazy val log = LoggerFactory.getLogger(getClass)

  protected var settings: Settings = null
  protected val accessStrategyFactory = RedisAccessStrategyFactory()
  protected val regionNames = new ConcurrentSkipListSet[String]()

  protected var cache: RedisCache = null
  protected var expirationThread: Thread = null

  override def isMinimalPutsEnabledByDefault: Boolean = true

  override def getDefaultAccessType: AccessType = AccessType.READ_WRITE

  override def nextTimestamp(): Long = System.currentTimeMillis()


  override def buildEntityRegion(regionName: String,
                                 properties: Properties,
                                 metadata: CacheDataDescription): EntityRegion = {
    regionNames.add(regionName)
    new RedisEntityRegion(accessStrategyFactory,
                           cache,
                           regionName,
                           settings,
                           metadata,
                           properties)
  }

  override def buildCollectionRegion(regionName: String,
                                     properties: Properties,
                                     metadata: CacheDataDescription): CollectionRegion = {
    regionNames.add(regionName)
    new RedisCollectionRegion(accessStrategyFactory,
                               cache,
                               regionName,
                               settings,
                               metadata,
                               properties)
  }

  override def buildNaturalIdRegion(regionName: String,
                                    properties: Properties,
                                    metadata: CacheDataDescription): NaturalIdRegion = {
    regionNames.add(regionName)
    new RedisNaturalIdRegion(accessStrategyFactory,
                              cache,
                              regionName,
                              settings,
                              metadata,
                              properties)
  }

  override def buildQueryResultsRegion(regionName: String,
                                       properties: Properties): QueryResultsRegion = {
    regionNames.add(regionName)
    new RedisQueryResultsRegion(accessStrategyFactory,
                                 cache,
                                 regionName,
                                 properties)
  }

  override def buildTimestampsRegion(regionName: String, properties: Properties): TimestampsRegion = {
    new RedisTimestampsRegion(accessStrategyFactory,
                               cache,
                               regionName,
                               properties)
  }

  protected def manageExpiration(cache: RedisCache): Unit = synchronized {
    if (expirationThread != null && expirationThread.isAlive)
      return

    expirationThread = new Thread(new Runnable() {
      override def run() {
        while (true) {
          try {
            Thread.sleep(3000)
            if (cache != null && regionNames.size > 0) {
              regionNames.asScala.par.foreach { region =>
                cache.expire(region)
              }
            }
          } catch {
            case ignored: InterruptedException =>
            case NonFatal(e) =>
              log.debug(s"Error occurred in expiration management thread. but it was ignored", e)
          }
        }
      }
    })
    expirationThread.setDaemon(true)
    expirationThread.start()
  }

}
