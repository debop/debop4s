package debop4s.data.slick3

import com.typesafe.slick.testkit.util.TestDB
import org.scalatest._
import org.slf4j.LoggerFactory
import slick.driver.JdbcProfile
import slick.profile.{ Capability, SqlProfile, RelationalProfile }

/**
 * AbstractSlickFunSuite
 * @author sunghyouk.bae@gmail.com 15. 3. 28.
 */
abstract class AbstractSlickFunSuite
  extends FunSuite with Matchers with OptionValues with BeforeAndAfter with BeforeAndAfterAll {

  protected lazy val LOG = LoggerFactory.getLogger(getClass)

  lazy val driver = SlickContext.driver
  lazy val profile = driver.profile

  private [this] var dbInitialized = false

  lazy val db = {
    dbInitialized = true
    SlickContext.defaultDB
  }

  override protected def beforeAll(): Unit = {
    initialize()
  }

  override protected def afterAll():Unit = {
    if(dbInitialized) db.close()
  }

  private def initialize(): Unit = {
    SlickContext.init("slick-h2", "slick")
    // SlickContext.init("slick-hsqldb", "slick")
    // SlickContext.init("slick-mysql", "slick")
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
