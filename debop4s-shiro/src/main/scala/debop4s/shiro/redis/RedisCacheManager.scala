package debop4s.shiro.redis

import java.util.concurrent.ConcurrentHashMap

import debop4s.rediscala.client.RedisSyncClient
import org.apache.shiro.cache.{Cache, CacheManager}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import scala.beans.BeanProperty

/**
 * Redis 를 캐시 서버로 사용하도록 해주는 Apache Shiro의 CacheManager입니다.
 * @author sunghyouk.bae@gmail.com
 */
@Component
class RedisCacheManager extends CacheManager {

  private lazy val log = LoggerFactory.getLogger(getClass)

  private val caches = new ConcurrentHashMap[String, Cache[_, _]]()

  @Autowired val redis: RedisSyncClient = null

  @BeanProperty var keyPrefix: String = "shiro:cache:"

  override def getCache[K, V](name: String): Cache[K, V] = synchronized {
    var cache = caches.get(name).asInstanceOf[Cache[K, V]]
    if (cache == null) {
      log.trace(s"create cache. name=$name")
      cache = new RedisCache[K, V](redis, keyPrefix).asInstanceOf[Cache[K, V]]
      caches.putIfAbsent(name, cache)
      // caches.put(name, cache)
    }
    cache
  }
}

