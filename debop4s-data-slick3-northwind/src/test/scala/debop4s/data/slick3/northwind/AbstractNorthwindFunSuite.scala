package debop4s.data.slick3.northwind

import debop4s.data.slick3.SlickContext
import org.scalatest._
import org.slf4j.LoggerFactory

/**
 * AbstractNorthwindFunSuite
 * @author sunghyouk.bae@gmail.com
 */
abstract class AbstractNorthwindFunSuite
  extends FunSuite with Matchers with BeforeAndAfter with BeforeAndAfterAll with OptionValues {

  protected lazy val log = LoggerFactory.getLogger(getClass)

  lazy val driver = SlickContext.driver
  lazy val profile = driver.profile

  override protected def beforeAll(): Unit = {
    initialize()
  }

  /**
   * Slick 사용 시 제일 처음 Database 관련 설정을 지정해 줘야 합니다.
   */
  private def initialize(): Unit = {
    SlickContext.init("northwind-mariadb-master-slaves")
    // SlickContext.init("northwind-mariadb")
    // SlickContext.init("northwind-postgres")
  }

}
