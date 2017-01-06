package debop4s.data.slick3

import java.util.concurrent.atomic.AtomicInteger
import javax.sql.DataSource

import com.typesafe.config.ConfigFactory
import debop4s.config.server.DatabaseSetting
import debop4s.data.common.{DataSources, JdbcDrivers}
import debop4s.data.slick3.config.SlickConfig
import org.slf4j.LoggerFactory
import slick.driver._

/**
  * Slick 사용 시 환경설정에서 지정한 Database 와 Driver 를 사용할 수 있도록 합니다.
  *
  * @author sunghyouk.bae@gmail.com
  */
object SlickContext {

  private[this] lazy val log = LoggerFactory.getLogger(getClass)

  private[this] val masterIndex = new AtomicInteger(0)
  private[this] val slaveIndex = new AtomicInteger(0)

  def getMasterIndex: Int = masterIndex.get()

  def getSlaveIndex: Int = slaveIndex.get()

  private var slickConfig: SlickConfig = null

  lazy val defaultSetting: DatabaseSetting = slickConfig.database.dbSetting
  lazy val defaultDataSource: DataSource = DataSources.getDataSource(defaultSetting)

  lazy val defaultDriver: JdbcDriver = SlickDrivers.getOrElse(defaultSetting.driverClass,
    sys.error("No suitable driver was found."))

  def defaultDB: driver.backend.DatabaseDef = forDataSource(defaultDataSource)

  def forDataSource(ds: DataSource = defaultDataSource): driver.backend.DatabaseDef =
    driver.api.Database.forDataSource(ds)

  def createDefaultDB(): driver.backend.DatabaseDef = forDataSource(defaultDataSource)

  def createMasterDB(): driver.backend.DatabaseDef = {
    if (masterSettings.isEmpty) createDefaultDB()
    else {
      synchronized {
        val index = masterIndex.getAndIncrement
        masterIndex.compareAndSet(masterDataSources.length, 0)
        forDataSource(masterDataSources(index % masterDataSources.length))
      }
    }
  }

  def createSlaveDB(): driver.backend.DatabaseDef = {
    if (masterSettings.isEmpty) createDefaultDB()
    else {
      synchronized {
        val index = slaveIndex.getAndIncrement
        slaveIndex.compareAndSet(slaveDataSources.length, 0)
        forDataSource(slaveDataSources(index % slaveDataSources.length))
      }
    }
  }

  lazy val driver: JdbcDriver = defaultDriver
  lazy val jdbcDriver: String = defaultSetting.driverClass

  def getDB(ds: DataSource = defaultDataSource) =
    driver.api.Database.forDataSource(ds)

  lazy val masterSettings: IndexedSeq[DatabaseSetting] = slickConfig.masterSettings
  lazy val masterDataSources: IndexedSeq[DataSource] = masterSettings.map(DataSources.getDataSource)
  lazy val masterDBs = masterDataSources.map { ds => driver.api.Database.forDataSource(ds) }

  lazy val slaveSettings = slickConfig.slaveSettings
  lazy val slaveDataSources = slaveSettings.map(DataSources.getDataSource)
  lazy val slaveDBs = slaveDataSources.map { ds => driver.api.Database.forDataSource(ds) }

  /**
    * Master DB가 여려 개인 경우 round-robin 방식으로 Master DB를 선택합니다.
    */
  def masterDB: driver.backend.DatabaseDef = {
    if (masterSettings.isEmpty) defaultDB
    else {
      synchronized {
        val index = masterIndex.getAndIncrement
        masterIndex.compareAndSet(masterSettings.length, 0)
        masterDBs(index % masterSettings.length)
      }
    }
  }

  /**
    * Slave DB가 여려 개인 경우 round-robin 방식으로 Slave DB를 선택합니다.
    */
  def slaveDB: driver.backend.DatabaseDef = {
    if (slaveSettings.isEmpty) defaultDB
    else {
      synchronized {
        val index = slaveIndex.getAndIncrement
        slaveIndex.compareAndSet(slaveSettings.length, 0)
        slaveDBs(index % slaveSettings.length)
      }
    }
  }


  /**
    * Database 환경설정 정보를 이용하여 SlickContext를 초기화합니다.
    */
  def init(config: SlickConfig): Unit = {
    log.info(s"Slick 환경설정을 통해 Driver 등을 정의합니다.")
    require(config != null)
    this.slickConfig = config
  }

  /**
    * Database 환경설정 정보를 이용하여 SlickContext를 초기화합니다.
    */
  def init(configPath: String, rootConfig: String = "slick"): Unit = {
    log.info(s"Slick 환경설정을 읽습니다. configPath=$configPath, rootConfig=$rootConfig")
    val config = SlickConfig(ConfigFactory.load(configPath).getConfig(rootConfig))
    this.slickConfig = config
  }

  def isInitialized: Boolean = this.slickConfig != null

  def isMySQL: Boolean = (driver == MySQLDriver) || isMariaDB

  def isMariaDB: Boolean = jdbcDriver == JdbcDrivers.DRIVER_CLASS_MARIADB

  def isH2: Boolean = driver == H2Driver

  def isHsqlDB: Boolean = driver == HsqldbDriver

  def isPostgres: Boolean = driver == PostgresDriver

  def isSQLite: Boolean = driver == SQLiteDriver

  def isOracle: Boolean = driver.profile.toString.contains("OracleDriver")

}
