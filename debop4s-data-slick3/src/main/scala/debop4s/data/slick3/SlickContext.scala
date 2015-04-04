package debop4s.data.slick3

import java.util.concurrent.{LinkedBlockingQueue, ThreadPoolExecutor, TimeUnit}
import javax.sql.DataSource

import com.typesafe.config.ConfigFactory
import debop4s.data.common.DataSources
import debop4s.data.slick3.config.SlickConfig
import org.reactivestreams.{Publisher, Subscription, Subscriber}
import org.slf4j.LoggerFactory
import slick.driver._

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Promise, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import scala.util.control.NonFatal

/**
 * Slick 사용 시 환경설정에서 지정한 Database 와 Driver 를 사용할 수 있도록 합니다.
 *
 * @author sunghyouk.bae@gmail.com
 */
object SlickContext {

  lazy val LOG = LoggerFactory.getLogger(getClass)

  private var slickConfig: SlickConfig = _

  lazy val defaultSetting = slickConfig.database.dbSetting
  lazy val defaultDataSource = DataSources.getDataSource(defaultSetting)

  lazy val defaultDriver = SlickDrivers.getOrElse(defaultSetting.driverClass,
    sys.error("No suitable driver was found."))

  lazy val defaultDB = defaultDriver.api.Database.forDataSource(defaultDataSource)

  def forDataSource(ds: DataSource = defaultDataSource): driver.backend.DatabaseDef =
    driver.api.Database.forDataSource(ds)

  lazy val driver: JdbcDriver = defaultDriver
  lazy val jdbcDriver: String = defaultSetting.driverClass

  def getDB(ds: DataSource = defaultDataSource) =
    driver.api.Database.forDataSource(ds)

  def init(config: SlickConfig): Unit = {
    LOG.info(s"Slick 환경설정을 통해 Driver 등을 정의합니다.")
    require(config != null)
    this.slickConfig = config
  }

  def init(configPath: String, rootConfig: String = "slick"): Unit = {
    LOG.info(s"Slick 환경설정을 읽습니다. configPath=$configPath, rootConfig=$rootConfig")
    val config = SlickConfig(ConfigFactory.load(configPath).getConfig(rootConfig))
    this.slickConfig = config
  }

  def isInitialized: Boolean = this.slickConfig != null

  def isH2: Boolean = driver == H2Driver
  def isHqlDB: Boolean = driver == HsqldbDriver
  def isMariaDB: Boolean = jdbcDriver == "org.mariadb.jdbc.Driver"
  def isMySQL: Boolean = driver == MySQLDriver
  def isPostgres: Boolean = driver == PostgresDriver
  def isSQLite: Boolean = driver == SQLiteDriver


  /**
   * reactive stream 을 읽어 각 row 를 처리합니다.
   * @param f
   * @return
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
  def materialize[T](p:Publisher[T]): Future[Vector[T]] = {
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

  /** Asynchronously consume a Reactive Stream and materialize it as a Vector, requesting new
    * elements one by one and transforming them after the specified delay. This ensures that the
    * transformation does not run in the synchronous database context but still preserves
    * proper sequencing. */
  def materializeAsync[T, R](p: Publisher[T])
                            (tr: T => Future[R],
                             delay: Duration = Duration(100L, TimeUnit.MILLISECONDS)): Future[Vector[R]] = {
    val exe = new ThreadPoolExecutor(1, 1, 1L, TimeUnit.SECONDS, new LinkedBlockingQueue[Runnable]())
    val ec = ExecutionContext.fromExecutor(exe)
    val builder = Vector.newBuilder[R]
    val pr = Promise[Vector[R]]()
    var sub: Subscription = null

    def async[T](thunk: => T): Future[T] = {
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
    try
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
            case Failure(t) =>
              pr.tryFailure(t)
              sub.cancel()
          }(ec)
        }
      }) catch {
      case NonFatal(ex) => pr.tryFailure(ex)
    }
    val f = pr.future
    f.onComplete(_ => exe.shutdown())
    f
  }


}
