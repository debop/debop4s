package org.hibernate.cache.rediscala

import java.util.Properties
import org.hibernate.SessionFactory
import org.hibernate.cache.rediscala.client.HibernateRedisCache
import org.hibernate.cfg.AvailableSettings
import org.hibernate.internal.SessionFactoryImpl
import org.slf4j.LoggerFactory
import redis.RedisClient

/**
 * Hibernate-Redis Helper class
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 21. 오전 9:34
 */
object HibernateRedisUtil {

    implicit val akkaSystem = akka.actor.ActorSystem()

    private lazy val log = LoggerFactory.getLogger(getClass)

    val DEFAULT_PORT: Int = 6379
    val DEFAULT_SENTINEL_PORT: Int = 26379
    val DEFAULT_TIMEOUT: Int = 2000
    val DEFAULT_DATABASE: Int = 1
    val CHARSET: String = "UTF-8"

    val EXPIRE_IN_SECONDS: String = "redis.expireInSeconds"

    private var cacheProperties: Properties = _

    /**
    * [[CacheClient]] 를 생성합니다.
    */
    def createCacheClient(props: Properties): HibernateRedisCache = {
        log.info("RedisClient 인스턴스를 생성합니다...")

        val cachePath = props.getProperty(AvailableSettings.CACHE_PROVIDER_CONFIG, "hibernate-redis.properties")
        cacheProperties = loadCacheProperties(cachePath)

        if (cacheProperties != null) {
            val expiryInSeconds = Integer.decode(cacheProperties.getProperty("redis.expiryInSeconds", "0"))
            val host = cacheProperties.getProperty("redis.host", "localhost")
            val port = cacheProperties.getProperty("redis.port", String.valueOf(DEFAULT_PORT)).toInt
            val timeout = cacheProperties.getProperty("redis.timeout", String.valueOf(DEFAULT_TIMEOUT)).toInt
            val passwd = cacheProperties.getProperty("redis.password", "")
            val database = cacheProperties.getProperty("redis.database", String.valueOf(DEFAULT_DATABASE)).toInt

            HibernateRedisCache(RedisClient(host, port, Some(passwd), Some(database)))
        } else {
            HibernateRedisCache()
        }
    }

    def expireInSeconds(regionName: String): Int = {
        val defaultValue = getCacheProperty(EXPIRE_IN_SECONDS, "0").toInt
        expireInSeconds(regionName, defaultValue)
    }


    def expireInSeconds(regionName: String, defaultExpiry: Int): Int = {
        if (cacheProperties == null)
            defaultExpiry
        else
            getCacheProperty(EXPIRE_IN_SECONDS + "." + regionName, String.valueOf(defaultExpiry)).toInt
    }

    def getCacheProperty(name: String, defaultValue: String): String = {
        if (cacheProperties == null)
            return defaultValue

        try {
            cacheProperties.getProperty(name, defaultValue)
        } catch {
            case ignored: Throwable => return defaultValue
        }
    }

    def loadCacheProperties(cachePath: String): Properties = {

        val cacheProps = new Properties()
        try {
            log.debug(s"Loading cache properties... cachePath=$cachePath")
            val is = getClass.getClassLoader.getResourceAsStream(cachePath)
            cacheProps.load(is)
            log.debug(s"Load cache properties. cacheProps=$cacheProps")
        } catch {
            case e: Throwable => log.warn(s"Cache용 환경설정을 로드하는데 실패했습니다. cachePath=$cachePath", e)
        }

        cacheProps
    }

    /**
    * Returns an increasing unique value based on the System.currentTimeMillis()
    * with some additional reserved space for a counter.
    */
    def nextTimestamp(): Long = System.currentTimeMillis()

    /**
    * 엔티티의 cache region name을 반환합니다.
    */
    def getRegionName(sessionFactory: SessionFactory, entityClass: Class[_]): String = {
        val p = sessionFactory.asInstanceOf[SessionFactoryImpl].getEntityPersister(entityClass.getName)
        if (p != null && p.hasCache)
            p.getCacheAccessStrategy.getRegion.getName
        else
            ""
    }

}
