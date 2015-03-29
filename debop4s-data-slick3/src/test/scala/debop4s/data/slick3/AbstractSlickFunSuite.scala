package debop4s.data.slick3

import com.typesafe.slick.testkit.util.TestDB
import debop4s.data.slick3.TestDatabase.driver.api._
import org.reactivestreams.{ Publisher, Subscriber, Subscription }
import org.scalatest._
import org.slf4j.LoggerFactory
import slick.driver.JdbcProfile
import slick.profile.{ Capability, RelationalProfile, SqlProfile }

import scala.concurrent.{ Future, Promise }
import scala.util.control.NonFatal

/**
 * AbstractSlickFunSuite
 * @author sunghyouk.bae@gmail.com 15. 3. 28.
 */
abstract class AbstractSlickFunSuite
  extends FunSuite with Matchers with OptionValues with BeforeAndAfter with BeforeAndAfterAll {

  protected lazy val LOG = LoggerFactory.getLogger(getClass)

  lazy val driver = SlickContext.driver
  lazy val profile = driver.profile

  private[this] var _db: SlickContext.driver.backend.DatabaseDef = _

  lazy val db = {
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

  def ifCap[T](caps: Capability*)(f: => T): Unit =
    if (caps.forall(capabilities.contains)) f

  def ifNotCap[T](caps: Capability*)(f: => T): Unit =
    if (!caps.forall(capabilities.contains)) f


  def seq[E <: Effect](actions: DBIOAction[_, NoStream, E]*): DBIOAction[Unit, NoStream, E] =
    DBIO.seq[E](actions: _*)

  /**
   * 동기 방식으로 reactive stream 을 읽어드여 Vector 로 빌드합니다.
   */
  def materialize[T](p: Publisher[T]): Future[Vector[T]] = {
    val builder = Vector.newBuilder[T]
    val pr = Promise[Vector[T]]()
    try p.subscribe(new Subscriber[T] {
      override def onSubscribe(s: Subscription): Unit = s.request(Long.MaxValue)
      override def onComplete(): Unit = pr.success(builder.result())
      override def onError(throwable: Throwable): Unit = pr.failure(throwable)
      override def onNext(t: T): Unit = builder += t
    }) catch { case NonFatal(e) => pr.failure(e) }

    pr.future
  }

  def foreach[T](p: Publisher[T])(f: T => Any): Future[Unit] = {
    val pr = Promise[Unit]()

    try p.subscribe(new Subscriber[T] {
      override def onSubscribe(s: Subscription): Unit = s.request(Long.MaxValue)
      override def onComplete(): Unit = pr.success(())
      override def onError(throwable: Throwable): Unit = pr.failure(throwable)
      override def onNext(t: T): Unit = f(t)
    }) catch { case NonFatal(e) => pr.failure(e) }

    pr.future
  }

}
