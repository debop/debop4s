package debop4s.data.common.pools

import java.util
import javax.sql.DataSource

import com.zaxxer.hikari.{ HikariConfig, HikariDataSource }
import debop4s.data.common.{ DataSources, JdbcDrivers }

import scala.collection.JavaConverters._

/**
 * Hikari CP 의 DataSource를 생성해주는 Factory 입니다.
 *
 * NOTE: HikariCP 는 Hibernate 환경 하에서는 제대로 실행되지 않는다. Slick의 경우는 상관없다.
 *
 * @author Sunghyouk Bae
 */
class HikariDataSourceFactory extends AbstractDataSourceFactory {

  /**
   * HikariCP DataSource를 생성합니다.
   * @param dataSourceClassName dataSourceClassName
   *                            ( 기존 driverClass 가 아닙니다 : mysql용은 com.mysql.jdbc.jdbc2.optional.MysqlDataSource 입니다 )
   * @param driverClass jdbc driver class
   * @param url         jdbc url
   * @param username    사용자 명
   * @param passwd      사용자 패스워드
   * @return [[javax.sql.DataSource]] 인스턴스
   */
  override def createDataSource(dataSourceClassName: String = "",
                                driverClass: String = "",
                                url: String = "jdbc:h2:mem:test",
                                username: String = "",
                                passwd: String = "",
                                props: util.Map[String, String] = new util.HashMap(),
                                maxPoolSize: Int = DataSources.MAX_POOL_SIZE): DataSource = {
    log.info("Hikari DataSource를 빌드합니다... " +
             s"dataSourceClassName=[$dataSourceClassName], driverClass=[$driverClass] url=[$url], username=[$username]")

    val config = new HikariConfig()

    config.setInitializationFailFast(true)

    // AutoCommit은 Driver 기본 값을 사용하도록 합니다. (mysql 은 auto commit = true)
    //config.setAutoCommit(false)

    if (dataSourceClassName != null && dataSourceClassName.length > 0) {
      config.setDataSourceClassName(dataSourceClassName)
      config.addDataSourceProperty("url", url)
      config.addDataSourceProperty("user", username)
      config.addDataSourceProperty("password", passwd)
    } else {
      config.setDriverClassName(driverClass)
      config.setJdbcUrl(url)
      config.setUsername(username)
      config.setPassword(passwd)
    }

    // MySQL 인 경우 성능을 위해 아래 설정을 사용합니다.
    val isMySQL = JdbcDrivers.DATASOURCE_CLASS_MYSQL.equals(dataSourceClassName) ||
                  JdbcDrivers.DRIVER_CLASS_MYSQL.equals(driverClass)
    if (isMySQL) {
      config.addDataSourceProperty("cachePrepStmts", "true")
      config.addDataSourceProperty("prepStmtCacheSize", "500")
      config.addDataSourceProperty("prepStmtCacheSqlLimit", "4096")
      config.addDataSourceProperty("useServerPrepStmts", "true")
    }

    if (props != null) {
      props.asScala foreach {
        case (name, value) => config.addDataSourceProperty(name, value)
      }
    }

    config.setConnectionTestQuery("SELECT 1")
    val poolSize = maxPoolSize max DataSources.MIN_POOL_SIZE
    config.setMaximumPoolSize(poolSize)

    // NOTE: 이게 상당히 중요하다!!!
    // NOTE: 설정하지 않으면 max pool size 와 같게 둬서 connection pool 을 고갈 시킨다. 최소 갯수만 남겨둬야 한다.
    config.setMinimumIdle(DataSources.processCount min DataSources.MIN_IDLE_SIZE)

    // Timeout 설정
    config.setMaxLifetime(600000) // 10 minutes
    config.setIdleTimeout(300000) // 5 minutes
    // config.setJdbc4ConnectionTest(false)

    new HikariDataSource(config)
  }
}
