package debop4s.rediscala.config

import com.typesafe.config.Config
import debop4s.config.server.RedisSupport

/**
 * AppConfig
 * @author sunghyouk.bae@gmail.com
 */
case class AppConfig(override val config: Config) extends RedisSupport