package debop4s.config.additional

import com.typesafe.config.Config
import debop4s.config.server._

/**
 * ApplicationConfigWithCache
 * @author sunghyouk.bae@gmail.com 15. 3. 21.
 */
case class ApplicationConfigWithCache(override val config: Config)
  extends DatabaseSupport
  with RedisSupport
  with EmailSupport
  with HibernateSupport
  with RedisCacheSupport
  with SmsSupport

