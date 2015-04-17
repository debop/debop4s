package org.hibernate.cache.rediscala.config

import com.typesafe.config.Config

import scala.collection.JavaConverters._
import scala.util.control.NonFatal

/**
 * Hibernate-Redis 의 환경설정 정보를 제공합니다.
 *
 * {{{
 *   hibernate-redis {
 *      master {
 *        host = "127.0.0.1"
 *        port = 6379
 *        database = 1
 *      }
 *
 *      # slaves 가 없는 경우에는 slaves = [] 로 하시면 됩니다.
 *      slaves = [
 *        # Local Server 에 port 6380을 사용합니다.
 * {
 * host = "127.0.0.1"
 * port = 6379
 * database = 1
 * }
 * # VMWare로 Ubuntu 를 설치하고, redis-server 를 구동했습니다. (각자 다를 수 있습니다)
 * {
 * host = "192.168.99.135"
 * port = 6379
 * database = 1
 * }
 * ]
 *
 * expiryInSeconds {
 * default = 120
 * hibernate.common = 0
 * hibernate.account = 1200
 * }
 * }
 * }}}
 * @author sunghyouk.bae@gmail.com
 */
case class RedisConfig(config: Config) {

  val master = RedisServerElement(config.getConfig("master"))

  val slaves: Seq[RedisServerElement] = {
    try {
      val slaves = config.getConfigList("slaves")

      if (slaves == null || slaves.size() == 0) Seq[RedisServerElement]()
      else slaves.asScala.map(slave => RedisServerElement(slave))
    } catch {
      case NonFatal(e) => Seq[RedisServerElement]()
    }
  }

  val expiryInSeconds = {
    config.getConfig("expiryInSeconds").entrySet.asScala.map {
      entry => (entry.getKey, entry.getValue.unwrapped().toString.toInt)
    }.toMap
  }

  val existExpiryInSeconds: Boolean = expiryInSeconds.nonEmpty
}

/**
 * Redis-Server 의 주소 정보를 나타냅니다.
 */
case class RedisServerElement(config: Config) {
  val host = config.getString("host")
  val port = config.getInt("port")
  val database = try { config.getInt("database") } catch { case _: Throwable => 1 }
}
