package com.github.debop4s.redis.spring

import java.util
import org.springframework.beans.factory.DisposableBean
import org.springframework.cache.{Cache, CacheManager}

/**
 * RedisCacheManager
 * Created by debop on 2014. 2. 22.
 */
class RedisCacheManager extends CacheManager with DisposableBean {


    override def getCache(name: String): Cache = ???

    override def getCacheNames: util.Collection[String] = ???

    override def destroy(): Unit = ???
}
