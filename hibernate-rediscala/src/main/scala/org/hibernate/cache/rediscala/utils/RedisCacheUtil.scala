package org.hibernate.cache.rediscala.utils

import java.util.Properties

import com.typesafe.config.ConfigFactory
import org.hibernate.SessionFactory
import org.hibernate.cache.rediscala.client.RedisCache
import org.hibernate.cache.rediscala.config.RedisConfig
import org.hibernate.cfg.AvailableSettings
import org.hibernate.internal.SessionFactoryImpl
import org.slf4j.LoggerFactory
import redis.RedisCommands
import redis.commands.Transactions

import scala.util.control.NonFatal

/**
 * RedisFactory
 * @author sunghyouk.bae@gmail.com
 */
private[rediscala] object RedisCacheUtil {

  private lazy val log = LoggerFactory.getLogger(getClass)

  implicit val akkaSystem = akka.actor.ActorSystem("rediscala")

  var redisConfig: RedisConfig = _

  /**
   * Hibernate 환경설정 정보 중 CACHE_PROVIDER_CONFIG 정보로부터 redis server 설정 정보를 읽어와서 [[RedisCache]] 인스턴스를 생성합니다.
   * @param props hibernate properties
   * @return [[RedisCache]]
   */
  def createRedisCache(props: Properties): RedisCache = {

    log.info("RedisCache 인스턴스를 생성합니다.")

    try {
      val configPath = props.getProperty(AvailableSettings.CACHE_PROVIDER_CONFIG, "hibernate-redis.conf")
      log.info(s"hibernate-redis config path=$configPath")

      redisConfig = RedisConfig(ConfigFactory.load(configPath).getConfig("hibernate-redis"))
      RedisCache(createRedisClient(redisConfig))
    } catch {
      case NonFatal(e) =>
        log.warn(s"환경설정을 읽어오는데 실패했습니다.", e)
        RedisCache()
    }
  }

  /**
   * hibernate-redis.conf 환경설정 정보를 바탕으로 RedisClient 또는 RedisClientMasterSlaves 를 생성합니다.
   * @param redisConfig hibernate-redis.conf 의 정보
   * @return RedisClient 또는 RedisClientMasterSlaves
   */
  private def createRedisClient(redisConfig: RedisConfig): RedisCommands with Transactions = {
    log.info(s"hibernate-redis 환경설정 정보. $redisConfig")
    RedisClientFactory.createMasterSlaves(redisConfig)
  }

  lazy val defaultExpiryInSeconds: Int = {
    try {
      if (redisConfig != null && redisConfig.existExpiryInSeconds)
        redisConfig.expiryInSeconds("default")
      else
        0
    } catch {
      case NonFatal(e) => 0
    }
  }

  def expiryInSeconds(regionName: String, defaultExpiry: Int = defaultExpiryInSeconds): Int = {
    if (redisConfig != null && redisConfig.existExpiryInSeconds) {
      if (redisConfig.expiryInSeconds.contains(regionName))
        redisConfig.expiryInSeconds(regionName)
      else
        defaultExpiry
    } else {
      defaultExpiry
    }
  }

  /**
   * Returns an increasing unique value based on the System.currentTimeMillis()
   * with some additional reserved space for a counter.
   */
  def nextTimestamp: Long = System.currentTimeMillis()

  /**
   * 엔티티의 cache region name을 반환합니다.
   */
  def getRegionName(sessionFactory: SessionFactory, entityClass: Class[_]): String = {
    val p = sessionFactory.asInstanceOf[SessionFactoryImpl].getEntityPersister(entityClass.getName)
    val regionName =
      if (p != null && p.hasCache)
        p.getCacheAccessStrategy.getRegion.getName
      else
        ""

    log.debug(s"entityClass=$entityClass, regionName=$regionName")
    regionName
  }
}
