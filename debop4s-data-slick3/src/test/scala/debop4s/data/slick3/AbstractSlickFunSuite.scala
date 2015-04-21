package debop4s.data.slick3

import com.typesafe.slick.testkit.util.TestDB
import debop4s.data.slick3.SlickContext.driver.api._
import org.scalatest._
import org.slf4j.LoggerFactory
import slick.driver.JdbcProfile
import slick.profile.{Capability, RelationalProfile, SqlProfile}

import scala.concurrent.Future

/**
 * AbstractSlickFunSuite
 * @author sunghyouk.bae@gmail.com 15. 3. 28.
 */
abstract class AbstractSlickFunSuite
  extends FunSuite with Matchers with OptionValues with BeforeAndAfter with BeforeAndAfterAll {

  protected lazy val LOG = LoggerFactory.getLogger(getClass)

  implicit val executionContext = scala.concurrent.ExecutionContext.global

  lazy val driver = SlickContext.driver
  lazy val profile = driver.profile

  private[this] var _db: SlickContext.driver.backend.DatabaseDef = _

  implicit lazy val db = {
    if (_db == null) {
      _db = SlickContext.forDataSource()
    }
    _db
  }

  override protected def beforeAll(): Unit = {
    initialize()
  }

  override protected def afterAll(): Unit = {
    shutdown()
  }

  private def initialize(): Unit = synchronized {
    SlickContext.init("slick-h2", "slick")
    // SlickContext.init("slick-hsqldb", "slick")
    // SlickContext.init("slick-mysql", "slick")
    // SlickContext.init("slick-mariadb", "slick")
    // SlickContext.init("slick-postgres", "slick")
    // SlickContext.init("slick-mariadb-master-slaves", "slick")

    LOG.info(s"Slick Driver = ${ SlickContext.driver.getClass.getSimpleName }")
  }

  private def shutdown() = synchronized {
    if (_db ne null) {
      _db.close()
      _db = null
    }
  }

  def capabilities = profile.capabilities
  def rcap = RelationalProfile.capabilities
  def scap = SqlProfile.capabilities
  def jcap = JdbcProfile.capabilities
  def tcap = TestDB.capabilities

  def ifCap[E <: Effect, R](caps: Capability*)(f: => DBIOAction[R, NoStream, E]): DBIOAction[Unit, NoStream, E] =
    if(caps.forall(c => capabilities.contains(c))) f.andThen(DBIO.successful(())) else DBIO.successful(())

  def ifNotCap[E <: Effect, R](caps: Capability*)(f: => DBIOAction[R, NoStream, E]): DBIOAction[Unit, NoStream, E] =
    if(!caps.forall(c => capabilities.contains(c))) f.andThen(DBIO.successful(())) else DBIO.successful(())

  def ifCapF[R](caps: Capability*)(f: => Future[R]): Future[Unit] =
    if (caps.forall(c => capabilities.contains(c))) f.map(_ => ()) else Future.successful(())

  def ifNotCapF[R](caps: Capability*)(f: => Future[R]): Future[Unit] =
    if (!caps.forall(c => capabilities.contains(c))) f.map(_ => ()) else Future.successful(())

}
