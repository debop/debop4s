package debop4s.config.additional

import com.typesafe.config.Config
import debop4s.config.base.ConfigElementSupport
import debop4s.config.server.DatabaseElement

/**
 * SmsSupport
 * @author sunghyouk.bae@gmail.com 15. 3. 21.
 */
trait SmsSupport extends ConfigElementSupport {

  val sms = new SmsElement(config.getConfig("sms"))

}
/**
 * SMS 용 Database 에 대한 설정
 * {{{
 * sms {
 *   database {
 *     driverClass = ${application.database.driverClass}
 *     url = "jdbc:mysql://"${application.database.host}":3306/sms_dbro?useUnicode=true&characterEncoding=UTF-8"
 *     username = "sms"
 *     password = "rhrlwntpdy"
 *   }
 * }
 * }}}
 * @param config
 */
class SmsElement(override val config: Config) extends ConfigElementSupport {

  val database = new DatabaseElement(config.getConfig("database"))
}
