package debop4s.config.server

import java.util.Properties

import com.typesafe.config.Config
import debop4s.config._
import debop4s.config.base.{ ConfigElementSupport, CredentialElementSupport, ServerAddressElementSupport }

/**
 * EmailSupport
 * @author sunghyouk.bae@gmail.com 15. 3. 21.
 */
trait EmailSupport extends ConfigElementSupport {

  val email = new EmailElement(config.getConfig("email"))

}

class EmailElement(override val config: Config) extends ServerAddressElementSupport with CredentialElementSupport {

  val encoding = config.tryGetString("enconding", "UTF-8")

  val protocol: String = config.tryGetString("mail.transport.protocol", "smtp")
  val auth: Boolean = config.tryGetBoolean("mail.smtp.auth", true)
  val startTlsEnable: Boolean = config.tryGetBoolean("mail.smtp.startttls.enable", true)
  val sslTrust: String = config.tryGetString("mail.smtp.ssl.trust", "localhost")

  val properties: Properties = {
    val props = new Properties()

    props.setProperty("mail.transport.protocol", protocol)
    props.setProperty("mail.smtp.auth", auth.toString)
    props.setProperty("mail.smtp.starttls.enable", startTlsEnable.toString)
    props.setProperty("mail.smtp.ssl.trust", sslTrust)

    props
  }
}
