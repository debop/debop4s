package debop4s.config.server

import java.util.Properties

import com.typesafe.config.Config
import debop4s.config._
import debop4s.config.base._

/**
 * Database 정보를 나타내는 환경설정 정보를 지원합니다.
 * {{{
 *   database {
 *      driverClass = "com.mysql.jdbc.Driver"
 *      url="jdbc:mysql://localhost:3306/test"
 *      username="root"
 *      password="root"
 *
 *      maxPoolSize = 32  # default = processCount * 16
 *      minIdleSize = 4   # default = 2
 *   }
 * }}}
 */
trait DatabaseSupport extends ConfigElementSupport {

  val database = new DatabaseElement(config.getConfig("database"))

}

class DatabaseElement(override val config: Config) extends CredentialElementSupport {

  /** database driver class name */
  val driverClass: String = config.getString("driverClass")

  /** jdbc url */
  val url: String = config.getString("url")

  /** maximum pool size (default = processCount * 16) */
  val maxPoolSize = config.tryGetInt("maxPoolSize", MAX_POOL_SIZE)

  /** mininum idle size (default = 2) */
  val minIdleSize = config.tryGetInt("minIdleSize", MIN_IDLE_SIZE)

  /** Database 부가 설정 정보 */
  lazy val properties: Properties = config.asProperties()

  /** Database 설정 정보를 나타내는 DTO */
  lazy val dbSetting = DatabaseSetting(driverClass, url, username, password, maxPoolSize, minIdleSize, properties)
}

case class DatabaseSetting(driverClass: String,
                           url: String,
                           username: String = "sa",
                           password: String = "",
                           maxPoolSize: Int = MAX_POOL_SIZE,
                           minIdleSize: Int = MIN_IDLE_SIZE,
                           props: Properties = new Properties())
