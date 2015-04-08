package debop4s.config.server

import com.typesafe.config.Config
import debop4s.config._
import debop4s.config.base._

/**
 * MongoSupport
 * @author sunghyouk.bae@gmail.com 15. 3. 21.
 */
trait MongoSupport extends ConfigElementSupport {

  val mongo = new MongoElement(config.getConfig("mongo"))

}

class MongoElement(override val config: Config) extends ServerAddressElementSupport {

  val database = config.tryGetString("database", "test")

}


