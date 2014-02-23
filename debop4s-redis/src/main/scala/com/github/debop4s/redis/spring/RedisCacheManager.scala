package com.github.debop4s.redis.spring

import java.util
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import org.springframework.cache.{Cache, CacheManager}
import redis.RedisClient
import scala.collection.JavaConversions._
import scala.collection.mutable


/**
 * RedisCacheManager
 * Created by debop on 2014. 2. 22.
 */
class RedisCacheManager(val redis: RedisClient) extends CacheManager with DisposableBean {

    private lazy val log = LoggerFactory.getLogger(getClass)

    private val caches = new mutable.HashMap[String, Cache]() with mutable.SynchronizedMap[String, Cache]

    private def names = caches.keySet.toSet

    var usePrefix = false
    val cachePrefix = RedisCachePrefix()
    var defaultExpiration = 0
    private val expires = mutable.HashMap[String, Long]()

    override def getCache(name: String): Cache = {
        log.trace(s"get cache... name=$name")

        caches.get(name)
        .getOrElse {
            val expiration = computeExpiration(name)
            val prefix = if (usePrefix) cachePrefix.prefix(name) else ""

            val cache = RedisCache(name, prefix, redis, expiration)
            caches.put(name, cache)
            cache
        }
    }

    override def getCacheNames: util.Collection[String] = names

    override def destroy(): Unit = synchronized {
        if (caches.size > 0) {
            caches.values.par.foreach(cache => cache.clear())
            caches.clear()
        }
    }

    private def computeExpiration(name: String): Long = {
        expires.get(name)
        .getOrElse(defaultExpiration)
    }
}
