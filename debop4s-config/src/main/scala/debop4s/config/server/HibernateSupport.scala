package debop4s.config.server

import java.util.Properties

import com.typesafe.config.Config
import debop4s.config._
import debop4s.config.base.ConfigElementSupport

/**
 * HibernateSupport
 * @author sunghyouk.bae@gmail.com 15. 3. 21.
 */
trait HibernateSupport extends ConfigElementSupport {

  val hibernate = new HibernateElement(config.getConfig("hibernate"))

}

/**
 * hibernate 환경설정 정보를 나타내는 class 입니다.
 * {{{
 *  # hibernate properties
 *  hibernate {
 *    hbm2ddl = "create-drop"
 *    showSql = true
 *    cacheProviderConfig = "hibernate-redis.properties"
 *  }
 * }}}
 */
class HibernateElement(override val config: Config) extends ConfigElementSupport {

  val hbm2ddl: String = config.tryGetString("hbm2ddl", "none")
  val showSql: Boolean = config.tryGetBoolean("showSql", defaultValue = false)
  val useSecondCache: Boolean = config.tryGetBoolean("useSecondCache", false)
  val cacheProviderConfig: String = config.tryGetString("cacheProviderConfig", "")
  val properties: Properties = config.asProperties()
}
