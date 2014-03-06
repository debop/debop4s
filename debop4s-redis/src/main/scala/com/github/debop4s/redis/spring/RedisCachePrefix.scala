package com.github.debop4s.redis.spring

/**
 * RedisCachePrefix
 * Created by debop on 2014. 2. 22.
 */
trait RedisCachePrefix {

    def prefix(cacheName: String): String
}

object RedisCachePrefix {

    val DEFAULT_DELIMETER = ":"

    def apply(): RedisCachePrefix = new DefaultRedisCachePrefix()

    def apply(delimeter: Option[String]) = new DefaultRedisCachePrefix(delimeter)
}

class DefaultRedisCachePrefix(val delimeter: Option[String] = Some(RedisCachePrefix.DEFAULT_DELIMETER))
    extends RedisCachePrefix {

    override def prefix(cacheName: String): String = {
        delimeter.map(d => cacheName.concat(d)).getOrElse(cacheName)
    }
}
