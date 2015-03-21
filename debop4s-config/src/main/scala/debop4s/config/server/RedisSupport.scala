package debop4s.config.server

import com.typesafe.config.Config
import debop4s.config._
import debop4s.config.base._

import scala.collection.JavaConverters._
import scala.util.control.NonFatal

/**
 * RedisSupport
 * @author sunghyouk.bae@gmail.com 15. 3. 21.
 */
trait RedisSupport extends ConfigElementSupport {

  val redis = new RedisServerElement(config.getConfig("redis"))

}

/**
 * Redis 서버에 대한 환경설정를 나타냅니다.
 * {{{
 *   redis {
 *      master {
 *        host = "localhost"
 *        port = 6379
 *        database = 0
 *      }
 *      slaves = [
 * {
 * host = "slave-ip-02"
 * port = 6379
 * database = 0
 * }
 * {
 * host = "slave-ip-01"
 * port = 6379
 * database = 0
 * }
 * ]
 * }
 * }}}
 */
class RedisServerElement(override val config: Config) extends ConfigElementSupport {
  val master = new RedisElement(config.getConfig("master"))

  val slaves: Seq[RedisElement] = {
    try {
      val slaves = config.getConfigList("slaves")
      if (slaves == null || slaves.size() == 0) Seq[RedisElement]()
      else slaves.asScala.map(slave => new RedisElement(slave))
    } catch {
      case NonFatal(e) => Seq()
    }
  }
}

class RedisElement(override val config: Config) extends ServerAddressElementSupport {
  val database: Int = config.tryGetInt("database", 0)
}
