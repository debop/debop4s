package org.hibernate.cache.rediscala

import java.util.Properties
import org.hibernate.cache.CacheException
import org.hibernate.cfg.Settings

/**
 * RedisRegionFactory
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 21. 오후 3:33
 */
class RedisRegionFactory(private[this] val _props: Properties) extends AbstractRedisRegionFactory(_props) {

    override def start(settings: Settings, properties: Properties): Unit = synchronized {
        log.info("Redis를 2차 캐시 저장소로 사용하는 RedisRegionFactory를 시작합니다...")

        this.settings = settings
        try {
            if (cache == null) {
                this.cache = HibernateRedisUtil.createCacheClient(properties)
                manageExpiration(cache)
            }
            log.info("RedisRegionFactory를 시작합니다.")
        } catch {
            case e: Exception => throw new CacheException(e)
        }
    }

    override def stop(): Unit = synchronized {
        if (this.cache == null)
            return

        log.trace("RedisRegionFactory를 중지합니다...")
        try {
            if (expirationThread != null) {
                expirationThread.interrupt()
                expirationThread = null
            }
            this.cache = null
            log.info("RedisRegionFactory를 중지했습니다.")
        } catch {
            case e: Exception => log.error("jedisClient region factory fail to stop.", e)
        }
    }
}
