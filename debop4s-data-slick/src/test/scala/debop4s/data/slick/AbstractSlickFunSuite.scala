package debop4s.data.slick

import com.typesafe.slick.testkit.util.TestDB
import org.scalatest._
import org.slf4j.LoggerFactory

import scala.slick.driver.JdbcProfile
import scala.slick.profile._

/**
 * AbstractSlickFunSuite
 * @author sunghyouk.bae@gmail.com 15. 3. 22.
 */
abstract class AbstractSlickFunSuite
  extends FunSuite with Matchers with BeforeAndAfter with BeforeAndAfterAll with OptionValues {

  protected lazy val LOG = LoggerFactory.getLogger(getClass)

  lazy val driver = SlickContext.driver
  lazy val profile = driver.profile

  override protected def beforeAll(): Unit = {
    initialize()
  }

  private def initialize(): Unit = {
    SlickContext.init("slick-h2", "slick")
    // SlickContext.init("slick-mariadb-master-slaves", "slick")
  }

  def capabilities = profile.capabilities
  def rcap = RelationalProfile.capabilities
  def scap = SqlProfile.capabilities
  def jcap = JdbcProfile.capabilities
  def tcap = TestDB.capabilities

  def ifCap[T](caps: Capability*)(f: => T): Unit =
    if (caps.forall(capabilities.contains)) f

  def ifNotCap[T](caps: Capability*)(f: => T): Unit =
    if (!caps.forall(capabilities.contains)) f

}
