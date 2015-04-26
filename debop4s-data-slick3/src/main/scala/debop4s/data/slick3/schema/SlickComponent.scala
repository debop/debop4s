package debop4s.data.slick3.schema

import java.util.concurrent.{LinkedBlockingQueue, ThreadPoolExecutor, TimeUnit}

import debop4s.core._
import debop4s.core.concurrent._
import debop4s.data.common.JdbcDrivers
import debop4s.data.slick3.SlickContext
import debop4s.data.slick3.SlickContext._
import debop4s.data.slick3.SlickContext.driver.api._
import org.reactivestreams.{Subscription, Subscriber, Publisher}
import org.slf4j.LoggerFactory
import slick.backend.DatabasePublisher
import slick.driver._

import scala.concurrent.{ExecutionContext, Promise, Future}
import scala.concurrent.duration.{Duration, FiniteDuration, _}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import scala.util.control.NonFatal


/**
 * Slick 사용 시 기본적으로 사용할 Database 용 trait 입니다.
 * {{{
 *    // 사용할 데이터베이스 정의
 *    object ApplicationDatabase extends SlickComponent {}
 *
 *    // 실제 DB 작업용 소스에서는 두개의 import 를 수행하고, withSession 구문을 사용해서 DB 작업을 정의합니다.
 *    import ApplicationDatabase._
 *    import ApplicationDatabase.driver.simple._
 *
 *    object Repository {
 *
 *      def get() = {
 *        withSession { implicit session =>
 *          // read data
 *        }
 *      }
 *    }
 * }}}
 * @author sunghyouk.bae@gmail.com 15. 3. 22.
 */
trait SlickComponent
  extends EntityTableQueries
  with ActiveRecordExtensions
  with SlickQueryExtensions
  with SlickSchema
  with SlickProfile
  with SlickColumnMapper {

  protected val LOG = LoggerFactory.getLogger(getClass)

  //  def isMySQL: Boolean = (driver == MySQLDriver) || isMariaDB
  //  def isMariaDB: Boolean = jdbcDriver == JdbcDrivers.DRIVER_CLASS_MARIADB
  //  def isH2: Boolean = driver == H2Driver
  //  def isHsqlDB: Boolean = driver == HsqldbDriver
  //  def isPostgres: Boolean = driver == PostgresDriver
  //  def isSQLite: Boolean = driver == SQLiteDriver
  //  def isOracle: Boolean = driver.profile.toString.contains("OracleDriver")

  private[this] var _db: SlickContext.driver.backend.DatabaseDef = _

  lazy val db: SlickContext.driver.backend.DatabaseDef = {
    if (_db == null) {
      _db = SlickContext.forDataSource()
    }
    _db
  }

  protected def shutdown() = synchronized {
    if (_db ne null) {
      _db.close()
      _db = null
    }
  }

  val defaultTimeout: Duration = FiniteDuration(5, TimeUnit.MINUTES)

  implicit def autoCommit[T](dbAction: DBIO[T])(implicit timeout: Duration = defaultTimeout): T = {
    runAction(dbAction)(timeout)
  }

  implicit def commit[T](dbAction: DBIO[T])(implicit timeout: Duration = defaultTimeout): T = {
    runAction(dbAction.transactionally)(timeout)
  }

  implicit def readonly[T](dbAction: DBIO[T])(implicit timeout: Duration = defaultTimeout): T = {
    runReadOnly(dbAction)(timeout)
  }

  private def runAction[T](dbAction: DBIO[T])(implicit timeout: Duration = defaultTimeout): T = {
    using(SlickContext.createMasterDB()) { db =>
      // keey the database in memory with an extra connection
      db.createSession().force()
      db.run(dbAction).await(timeout)
    }
  }

  private def runReadOnly[T](dbAction: DBIO[T])(implicit timeout: Duration = defaultTimeout): T = {
    using(SlickContext.createSlaveDB()) { db =>
      // keey the database in memory with an extra connection
      // db.createSession().force()
      db.run(dbAction).await(timeout)
    }
  }


  /**
   * reactive stream 을 읽어 각 row 를 처리합니다.
   */
  def foreach[T](p: Publisher[T])(f: T => Any): Future[Unit] = {
    val pr = Promise[Unit]()

    try {
      p.subscribe(new Subscriber[T] {
        override def onSubscribe(s: Subscription): Unit = s.request(Long.MaxValue)
        override def onComplete(): Unit = pr.success(())
        override def onError(throwable: Throwable): Unit = pr.failure(throwable)
        override def onNext(t: T): Unit = f(t)
      })
    } catch {
      case NonFatal(e) => pr.failure(e)
    }

    pr.future
  }

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

  /**
   * Asynchronously consume a Reactive Stream and materialize it as a Vector, requesting new
   * elements one by one and transforming them after the specified delay. This ensures that the
   * transformation does not run in the synchronous database context but still preserves
   * proper sequencing.
   **/
  def materializeAsync[T, R](p: Publisher[T])
                            (tr: T => Future[R], delay: Duration = Duration(100L, TimeUnit.MILLISECONDS)): Future[Vector[R]] = {
    val exe = new ThreadPoolExecutor(1, 1, 1L, TimeUnit.SECONDS, new LinkedBlockingQueue[Runnable]())
    val ec = ExecutionContext.fromExecutor(exe)
    val builder = Vector.newBuilder[R]
    val pr = Promise[Vector[R]]()
    var sub: Subscription = null

    def async[A](thunk: => A): Future[A] = {
      val f = Future {
        Thread.sleep(delay.toMillis)
        thunk
      }(ec)
      f.onFailure { case t =>
        pr.tryFailure(t)
        sub.cancel()
      }
      f
    }

    try {
      p.subscribe(new Subscriber[T] {
        def onSubscribe(s: Subscription): Unit = async {
          sub = s
          sub.request(1L)
        }
        def onComplete(): Unit = async(pr.trySuccess(builder.result()))
        def onError(t: Throwable): Unit = async(pr.tryFailure(t))
        def onNext(t: T): Unit = async {
          tr(t).onComplete {
            case Success(r) =>
              builder += r
              sub.request(1L)
            case Failure(ex) =>
              pr.tryFailure(ex)
              sub.cancel()
          }(ec)
        }
      })
    } catch {
      case NonFatal(ex) => pr.tryFailure(ex)
    }

    val f = pr.future
    f.onComplete(_ => exe.shutdown())
    f
  }
}