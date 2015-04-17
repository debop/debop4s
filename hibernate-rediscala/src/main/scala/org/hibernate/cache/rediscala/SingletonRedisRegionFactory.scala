package org.hibernate.cache.rediscala

import java.util.Properties
import java.util.concurrent.atomic.AtomicInteger

import org.hibernate.cache.CacheException
import org.hibernate.cache.rediscala.utils.RedisCacheUtil
import org.hibernate.cfg.Settings
import org.slf4j.LoggerFactory

import scala.util.control.NonFatal

/**
 * SingletonRedisRegionFactory
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 21. 오후 3:54
 */
class SingletonRedisRegionFactory(private[this] val _props: Properties) extends AbstractRedisRegionFactory(_props) {

  private lazy val log = LoggerFactory.getLogger(getClass)

  val referenceCount = new AtomicInteger()

  override def start(settings: Settings, properties: Properties): Unit = synchronized {
    log.info("Redis를 2차 캐시 저장소로 사용하는 RedisRegionFactory를 시작합니다...")

    this.settings = settings
    try {
      if (cache == null) {
        this.cache = RedisCacheUtil.createRedisCache(properties)
        manageExpiration(cache)
      }
      referenceCount.incrementAndGet()
      log.info("RedisRegionFactory를 시작합니다.")
    } catch {
      case NonFatal(e) => throw new CacheException(e)
    }
  }

  override def stop(): Unit = synchronized {
    if (this.cache == null)
      return

    if (referenceCount.decrementAndGet() == 1) {
      log.trace("RedisRegionFactory를 중지합니다...")
      try {
        if (expirationThread != null) {
          expirationThread.interrupt()
          expirationThread = null
        }
        this.cache = null
        log.info("RedisRegionFactory를 중지했습니다.")
      } catch {
        case NonFatal(e) => log.error("jedisClient region factory fail to stop.", e)
      }
    }
  }
}
