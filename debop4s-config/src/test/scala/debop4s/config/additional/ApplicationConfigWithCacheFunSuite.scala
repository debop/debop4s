package debop4s.config.additional

import debop4s.config.ConfigUtils
import org.scalatest.{ FunSuite, Matchers }

/**
 * ApplicationConfigWithCacheFunSuite
 * @author sunghyouk.bae@gmail.com 15. 3. 21.
 */
class ApplicationConfigWithCacheFunSuite extends FunSuite with Matchers {

  val steps = Seq("local", "devel", "test", "prod")

  test("load application configiguration with cache") {

    steps.foreach { step =>
      val cfg = ApplicationConfigWithCache(ConfigUtils.load(s"config/$step", "application"))

      cfg.database.driverClass should not be null

      cfg.redis.master.port shouldEqual 6379
      cfg.redisCache.port shouldEqual 6379

      cfg.sms.database.driverClass should not be null
      cfg.sms.database.driverClass shouldEqual cfg.database.driverClass
    }
  }

}
