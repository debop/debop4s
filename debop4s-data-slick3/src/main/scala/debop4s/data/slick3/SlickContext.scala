package debop4s.data.slick3

import javax.sql.DataSource

import com.typesafe.config.ConfigFactory
import debop4s.data.common.DataSources
import debop4s.data.slick3.config.SlickConfig
import org.slf4j.LoggerFactory
import slick.driver._

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
}
