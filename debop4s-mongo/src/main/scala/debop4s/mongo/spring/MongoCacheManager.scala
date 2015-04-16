package debop4s.mongo.spring

import java.util
import java.util.concurrent.ConcurrentHashMap

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import org.springframework.cache.{Cache, CacheManager}
import org.springframework.data.mongodb.core.MongoTemplate

object MongoCacheManager {
  def apply(mongo: MongoTemplate, expiration: Long = 0): MongoCacheManager = {
    new MongoCacheManager(mongo, expiration)
  }
}

/**
 * MongoDB를 저장소로 사용하는 Spring @Cacheable용 Cache 관리자입니다.
 * Spring Application Context 에 MongoCacheManager를 Bean으로 등록하셔야 합니다.
 *
 * {{{
 * @Bean
 * public MongoCacheMaanger mongoCacheManager() {
 * return new MongoCacheManager(mongo, 120);
 * }
 * }}}
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 10. 25. 오후 9:10
 */
class MongoCacheManager(val mongo: MongoTemplate,
                        val expiration: Long = 0)
  extends CacheManager with DisposableBean {

  private lazy val log = LoggerFactory.getLogger(getClass)

  private val caches = new ConcurrentHashMap[String, Cache]()
  private val expires = new ConcurrentHashMap[String, Long]()

  private def names = caches.keySet

  var usePrefix = false
  val cachePrefix = MongoCachePrefix()
  var defaultExpiration = 0


  override def getCache(name: String): Cache = synchronized {
    log.trace(s"get cache... name=$name")

    val cache = caches.get(name)
    if (cache != null) {
      cache
    } else {
      val expiration = computeExpiration(name)
      val prefix = if (usePrefix) cachePrefix.prefix(name) else ""

      val newCache = MongoCache(name, prefix, mongo, expiration)
      caches.put(name, newCache)
      newCache
    }
  }

  override def getCacheNames: util.Collection[String] = {
    names
  }

  override def destroy(): Unit = synchronized {
    //    if (caches.size > 0) {
    //      caches.values.asScala.par.foreach(cache => cache.clear())
    //      caches.clear()
    //    }
  }

  private def computeExpiration(name: String): Long = {
    if (expires.containsKey(name)) expires.get(name)
    else defaultExpiration
  }
}
