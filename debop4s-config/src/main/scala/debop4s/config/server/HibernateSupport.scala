package debop4s.config.server

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

  val hbm2ddl = config.tryGetString("hbm2ddl", "none")

  val showSql = config.tryGetBoolean("showSql", defaultValue = false)

  val useSecondCache = config.tryGetBoolean("useSecondCache", false)

  val cacheProviderConfig = config.tryGetString("cacheProviderConfig", "")

  val properties = config.asProperties()
}
