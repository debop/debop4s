package debop4s.config

import org.scalatest.{ FunSuite, Matchers }
import org.slf4j.LoggerFactory

/**
 * ApplicationConfigFunSuite
 * @author sunghyouk.bae@gmail.com 15. 3. 21.
 */
class ApplicationConfigFunSuite extends FunSuite with Matchers {

  val LOG = LoggerFactory.getLogger(getClass)

  val steps = Seq("local", "devel", "test", "prod")

  test("load configuration") {
    steps.foreach { step =>

      val cfg = ApplicationConfig(ConfigUtils.load(s"config/$step", "application"))

      cfg.database.driverClass should not equal null

      cfg.redis.master.port shouldEqual 6379
      cfg.redis.master.database shouldEqual 0

      cfg.redis.slaves.size should be > 0
      cfg.redis.slaves.head.host shouldEqual "127.0.0.1"

      cfg.email.encoding shouldEqual "UTF-8"
      cfg.email.properties.getProperty("mail.transport.protocol") shouldEqual "smtp"

      cfg.mongo.database should not be null

      cfg.hibernate.hbm2ddl should not be null
    }
  }
}
