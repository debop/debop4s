package debop4s.rediscala.spring

import java.util
import java.util.concurrent.ConcurrentHashMap

import debop4s.core._
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import org.springframework.cache.{Cache, CacheManager}
import redis.RedisClient

import scala.beans.BeanProperty
import scala.collection.JavaConverters._
import scala.util.control.NonFatal


object RedisCacheManager {

  def apply(): RedisCacheManager =
    apply(RedisClient())

  def apply(redis: RedisClient): RedisCacheManager =
    new RedisCacheManager(redis)
}

/**
 * RedisCacheManager
 * Created by debop on 2014. 2. 22.
 */
class RedisCacheManager(val redis: RedisClient) extends CacheManager with DisposableBean {

  def this() = this(RedisClient())

  private lazy val log = LoggerFactory.getLogger(getClass)

  private val caches = new ConcurrentHashMap[String, RedisCache]()
  private val expires = new ConcurrentHashMap[String, Long]()

  @BeanProperty var usePrefix = false
  /**
   * Milliseconds 단위입니다.
   */
  @BeanProperty var defaultExpiration = 60000L

  private var expirationWorker: Thread = {
    val worker = createExpirationWorker()
    worker.setDaemon(true)
    worker.start()
    worker
  }

  val cachePrefix = RedisCachePrefix()

  private def names = caches.keySet

  override def getCache(name: String): Cache = {
    log.trace(s"get cache... name=$name")

    if (caches.contains(name)) {
      caches.get(name)
    } else {
      val expiration = computeExpiration(name)
      val prefix = if (usePrefix) cachePrefix.prefix(name) else ""

      val cache = RedisCache(name, prefix, redis, expiration)
      caches.put(name, cache)
      cache
    }
  }

  override def getCacheNames: util.Collection[String] = names

  override def destroy(): Unit = synchronized {
    // 이제 Expiration 이 있기 때문에 굳이 삭제할 필요가 없다.
    //    if (caches.size > 0) {
    //      caches.values.asScala.foreach(cache => cache.clear())
    //      caches.clear()
    //    }
    try {
      if (expirationWorker != null) {
        expirationWorker.interrupt()
        expirationWorker = null
      }
    } catch {
      case NonFatal(e) =>
        log.warn(s"Spring Cache Expiration 관리 Worker Thread를 중단하는데 예외가 발생했습니다. 무시합니다.", e)
    }
  }

  private def computeExpiration(name: String): Long = {
    expires.asScala.getOrElse(name, defaultExpiration)
  }

  /**
   * `RedisCache` 가 관리하는 캐시 항목 중 expire 된 캐시들을 삭제하도록 합니다.
   * @return
   */
  private def createExpirationWorker(): Thread = createThread {
    while (true) {
      try {
        Thread.sleep(5000)
        caches.values().asScala.foreach {
          cache => cache.deleteExpiredItems()
        }
      } catch {
        case ignored: InterruptedException =>
        case NonFatal(e) =>
          log.warn(s"Spring Cache의 Expiration 관리 작업에서 예외가 발생했습니다. 무시합니다.", e)
      }
    }
  }
}
