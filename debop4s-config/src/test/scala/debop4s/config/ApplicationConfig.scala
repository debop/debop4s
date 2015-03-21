package debop4s.config

import com.typesafe.config.Config
import debop4s.config.server._

/**
 * ApplicationConfig
 * @author sunghyouk.bae@gmail.com 15. 3. 21.
 */
case class ApplicationConfig(override val config: Config)
  extends DatabaseSupport
  with RedisSupport
  with MongoSupport
  with EmailSupport
  with HibernateSupport