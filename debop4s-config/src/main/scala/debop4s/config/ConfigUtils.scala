package debop4s.config

import com.typesafe.config.{ Config, ConfigFactory }

/**
 * ConfigUtils
 * @author sunghyouk.bae@gmail.com 15. 3. 21.
 */
object ConfigUtils {

  def load(resourceBasename: String, rootPath: String = "application"): Config = {
    val config = ConfigFactory.load(resourceBasename)
    config.getConfig(rootPath)
  }

  def load(loader: ClassLoader, resourceBasename: String, rootPath: String): Config = {
    val config = ConfigFactory.load(loader, resourceBasename)
    config.getConfig(rootPath)
  }

}
