package debop4s.config.additional

import com.typesafe.config.Config
import debop4s.config.base.ConfigElementSupport
import debop4s.config.server.RedisElement

/**
 * RedisCacheSupport
 * @author sunghyouk.bae@gmail.com 15. 3. 21.
 */
trait RedisCacheSupport extends ConfigElementSupport {
  val redisCache = new RedisCacheElement(config.getConfig("redis.cache"))
}

/**
 * Redis 를 Cache로 사용하는 경우 설정 정보입니다.
 * {{{
 *  redis {
 *    cache {
 *      host = "127.0.0.1"
 *      port = 6379
 *      database = 2
 *    }
 *  }
 * }}}
 * @param config
 */
class RedisCacheElement(override val config: Config) extends RedisElement(config)
