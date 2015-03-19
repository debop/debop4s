package debop4s.data.common

import javax.sql.DataSource

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import org.slf4j.LoggerFactory

/**
 * DataSourceFactory
 * @author sunghyouk.bae@gmail.com
 */
object DataSources {

  private val log = LoggerFactory.getLogger(getClass)

  def createDataSource(driverClass: String,
                       jdbcUrl: String,
                       username: String,
                       password: String): DataSource = {
    createDataSource(JdbcSetting(driverClass, jdbcUrl, username, password))
  }

  def createDataSource(setting: JdbcSetting): DataSource = {
    assert(setting != null)

    log.debug(s"Create DataSource... setting=$setting")

    val cfg = new HikariConfig()

    cfg.setDriverClassName(setting.driverClass)
    cfg.setJdbcUrl(setting.jdbcUrl)
    cfg.setUsername(setting.username)
    cfg.setPassword(setting.password)

    cfg.setMinimumIdle(setting.minIdleSize)
    cfg.setMaximumPoolSize(setting.maxPoolSize)

    new HikariDataSource(cfg)
  }


}
