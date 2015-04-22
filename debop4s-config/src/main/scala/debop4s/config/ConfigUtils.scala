package debop4s.config

import com.typesafe.config.{ Config, ConfigFactory }
import org.slf4j.LoggerFactory

/**
 * ConfigUtils
 * @author sunghyouk.bae@gmail.com 15. 3. 21.
 */
object ConfigUtils {

  private val log = LoggerFactory.getLogger(getClass)

  def load(resourceBasename: String, rootPath: String = "application"): Config = {
    log.info(s"Load configuration. resourceBasename=$resourceBasename, rootPath=$rootPath")

    val config = ConfigFactory.load(resourceBasename)
    config.getConfig(rootPath)
  }

  def load(loader: ClassLoader, resourceBasename: String, rootPath: String): Config = {
    log.info(s"Load configuration. resourceBasename=$resourceBasename, rootPath=$rootPath")

    val config = ConfigFactory.load(loader, resourceBasename)
    config.getConfig(rootPath)
  }

}
