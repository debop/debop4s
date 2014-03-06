package org.hibernate.cache.redis.regions

import com.github.debop4s.core.parallels.Promises
import java.util
import java.util.Properties
import org.hibernate.cache.redis.HibernateRedisUtil
import org.hibernate.cache.redis.client.HibernateRedisCache
import org.hibernate.cache.redis.strategy.RedisAccessStrategyFactory
import org.hibernate.cache.spi.Region
import org.slf4j.LoggerFactory

/**
 * org.hibernate.cache.redis.regions.RedisDataRegion 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 21. 오전 9:25
 */
abstract class RedisDataRegion(protected val accessStrategyFactory: RedisAccessStrategyFactory,
                               val cache: HibernateRedisCache,
                               val regionName: String,
                               val props: Properties) extends Region {


    lazy val log = LoggerFactory.getLogger(getClass)

    val expireInSeconds = HibernateRedisUtil.expireInSeconds(regionName)

    override def getName: String = regionName

    var regionDeleted = false

    override def destroy(): Unit = synchronized {
        if (regionDeleted)
            return

        log.debug(s"delete cache region. region=$regionName")

        try {
            cache.deleteRegion(regionName)
        } catch {
            case ignored: Throwable =>
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
        map.foreach(x => results.put(x._1.asInstanceOf[Any], x._2.asInstanceOf[Any]))
        results
    }

    override def nextTimestamp: Long = HibernateRedisUtil.nextTimestamp()

    override def getTimeout: Int = 0

}
