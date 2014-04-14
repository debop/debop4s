package org.hibernate.cache.rediscala.regions

import java.util
import java.util.Properties
import org.hibernate.cache.rediscala.client.HibernateRedisCache
import org.hibernate.cache.rediscala.strategy.RedisAccessStrategyFactory
import org.hibernate.cache.rediscala.{Promises, HibernateRedisUtil}
import org.hibernate.cache.spi.Region


/**
 * RedisDataRegion
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 21. 오전 9:25
 */
abstract class RedisDataRegion(protected val accessStrategyFactory: RedisAccessStrategyFactory,
                               val cache: HibernateRedisCache,
                               val regionName: String,
                               val props: Properties) extends Region {

    val expireInSeconds = HibernateRedisUtil.expireInSeconds(regionName)

    override def getName: String = regionName

    @volatile var regionDeleted = false

    override def destroy(): Unit = synchronized {
        if (regionDeleted) return

        try {
            Promises.await(cache.deleteRegion(regionName))
        } finally {
            regionDeleted = true
        }
    }

    override def contains(key: Any): Boolean = {
        Promises.await(cache.exists(regionName, key.toString))
    }

    override def getSizeInMemory: Long = {
        Promises.await(cache.dbSize)
    }

    override def getElementCountInMemory: Long = {
        Promises.await(cache.keySizeInRegion(regionName))
    }

    override def getElementCountOnDisk: Long = -1L

    override def toMap: util.Map[Any, Any] = {
        val map = Promises.await(cache.getAll(regionName))

        val results = new util.HashMap[Any, Any]()
        map.foreach { case (k, v) =>
            results.put(k.asInstanceOf[Any], v.asInstanceOf[Any])
        }
        results
    }

    override def nextTimestamp: Long = HibernateRedisUtil.nextTimestamp()

    override def getTimeout: Int = 0

}
