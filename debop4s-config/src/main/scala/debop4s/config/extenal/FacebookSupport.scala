package debop4s.config.extenal

import java.util.Properties

import com.typesafe.config.Config
import debop4s.config._
import debop4s.config.base.ConfigElementSupport

/**
 * FacebookSupport
 * @author sunghyouk.bae@gmail.com 15. 3. 21.
 */
trait FacebookSupport extends ConfigElementSupport {
  val facebook = new FacebookElement(config.getConfig("sns.facebook"))
}

class FacebookElement(override val config: Config) extends ConfigElementSupport {
  val appId: String = config.tryGetString("appId")
  val appCredential = config.tryGetString("appCredential")

  val properties: Properties = config.asProperties()
}
