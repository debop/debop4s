package debop4s.data.slick

import java.util.concurrent.atomic.AtomicInteger
import javax.sql.DataSource

import debop4s.config.ConfigUtils
import debop4s.config.server.DatabaseSetting
import debop4s.data.common._
import debop4s.data.slick.config.SlickConfig
import debop4s.data.slick.databases.StandardDB
import org.slf4j.LoggerFactory

import scala.slick.driver.{ H2Driver, HsqldbDriver, MySQLDriver, PostgresDriver }

/**
 * Slick 사용 시 환경설정 정보를 이용하여 Database와 Driver를 사용할 수 있도록 합니다.
 * {{{
 *
 * }}}
 *
 * @author sunghyouk.bae@gmail.com 15. 3. 21.
 */
object SlickContext {

  lazy val LOG = LoggerFactory.getLogger(getClass)

  private val masterIndex = new AtomicInteger(0)
  private val slaveIndex = new AtomicInteger(0)

  private var slickConfig: SlickConfig = null

  lazy val defaultSetting = slickConfig.database.dbSetting
  lazy val defaultDataSource = DataSources.getDataSource(defaultSetting)
  lazy val defaultSlickDB = StandardDB.getDB(defaultSetting)
  lazy val defaultDB = defaultSlickDB.driver.simple.Database.forDataSource(defaultDataSource)

  lazy val driver = defaultSlickDB.driver
  lazy val jdbcDriver = defaultSlickDB.jdbcDriver

  lazy val masterSettings: IndexedSeq[DatabaseSetting] = slickConfig.masterSettings
  lazy val masterDataSources: IndexedSeq[DataSource] = masterSettings.map(DataSources.getDataSource)

  lazy val masterDBs: IndexedSeq[defaultSlickDB.driver.backend.DatabaseDef] =
    masterDataSources.map { ds =>
      defaultSlickDB.driver.simple.Database.forDataSource(ds)
    }

  lazy val slaveSettings: IndexedSeq[DatabaseSetting] = slickConfig.slaveSettings
  lazy val slaveDataSources: IndexedSeq[DataSource] = slaveSettings.map(DataSources.getDataSource)

  lazy val slaveDBs: IndexedSeq[defaultSlickDB.driver.backend.DatabaseDef] =
    slaveDataSources.map { ds =>
      defaultSlickDB.driver.simple.Database.forDataSource(ds)
    }

  /**
   * Master DB가 여려 개인 경우 round-robin 방식으로 Master DB를 선택합니다.
   */
  def masterDB: defaultSlickDB.driver.backend.DatabaseDef = {
    if (masterSettings.isEmpty) defaultDB
    else {
      val index = masterIndex.getAndIncrement % masterSettings.length
      masterIndex.compareAndSet(index + 1, ( index + 1 ) % masterSettings.length)
      masterDBs(index)
    }
  }

  /**
   * Slave DB가 여려 개인 경우 round-robin 방식으로 Master DB를 선택합니다.
   */
  def slaveDB: defaultSlickDB.driver.backend.DatabaseDef = {
    if (slaveSettings.isEmpty) defaultDB
    else {
      val index = slaveIndex.getAndIncrement % masterSettings.length
      slaveIndex.compareAndSet(index + 1, ( index + 1 ) % masterSettings.length)
      slaveDBs(index)
    }
  }

  def init(slickConfig: SlickConfig): Unit = {
    LOG.info(s"Slick 환경설정을 통해 Driver 등을 정의합니다.")
    this.slickConfig = slickConfig
  }

  def init(resourceBasename: String, rootPath: String = "slick"): Unit = {
    LOG.info(s"Slick 환경설정을 통해 Driver 등을 정의합니다. resourceBasename=$resourceBasename, rootPath=$rootPath")
    this.slickConfig = SlickConfig(ConfigUtils.load(resourceBasename, rootPath))
  }

  def isInitialized: Boolean = this.slickConfig != null

  def isMySQL: Boolean = driver == MySQLDriver
  def isMariaDB: Boolean = jdbcDriver == JdbcDrivers.DRIVER_CLASS_MARIADB
  def isH2: Boolean = driver == H2Driver
  def isHsqlDB: Boolean = driver == HsqldbDriver
  def isPostgres: Boolean = driver == PostgresDriver

}
