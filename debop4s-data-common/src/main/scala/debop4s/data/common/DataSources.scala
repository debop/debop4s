package debop4s.data.common

import java.util
import java.util.{ Map => JMap, Properties }
import javax.sql.DataSource

import com.zaxxer.hikari.{ HikariConfig, HikariDataSource }
import debop4s.config.server.DatabaseSetting
import debop4s.data.common.pools.HikariDataSourceFactory
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._


/**
 * `javax.sql.DataSource` 를 생성하는 Factory 입니다.
 * @author sunghyouk.bae@gmail.com
 */
object DataSources {

  private[this] lazy val log = LoggerFactory.getLogger(getClass)

  lazy val processCount = sys.runtime.availableProcessors()
  lazy val MAX_POOL_SIZE = processCount * 16: Int
  lazy val MIN_POOL_SIZE = processCount: Int
  lazy val MIN_IDLE_SIZE = processCount

  def getDataSource(database: DatabaseSetting): DataSource = {
    getDataSource(database.driverClass,
                   database.url,
                   database.username,
                   database.password,
                   database.props.asScala.toMap[String, String].asJava,
                   database.maxPoolSize)
  }

  def getDataSource(hikariConfig: HikariConfig): HikariDataSource = {
    new HikariDataSource(hikariConfig)
  }

  /**
   * HikariCP DataSource를 생성합니다.
   * @param driverClass DriverClass 명
   * @param url         Database 주소
   * @param username    사용자 명
   * @param passwd      사용자 패스워드
   * @return [[javax.sql.DataSource]] 인스턴스
   */
  def getDataSource(driverClass: String,
                    url: String,
                    username: String,
                    passwd: String): DataSource = {
    getDataSourceInternal("", driverClass, url, username, passwd)
  }

  def getDataSource(driverClass: String,
                    url: String,
                    username: String,
                    passwd: String,
                    props: JMap[String, String]): DataSource = {
    getDataSourceInternal("", driverClass, url, username, passwd, props)
  }

  def getDataSource(driverClass: String,
                    url: String,
                    username: String,
                    passwd: String,
                    props: JMap[String, String],
                    maxPoolSize: Int): DataSource = {
    getDataSourceInternal("", driverClass, url, username, passwd, props, maxPoolSize)
  }

  /**
   * HikariCP DataSource를 생성합니다.
   * @param dataSourceClassName dataSourceClassName
   *                            ( 기존 driverClass 가 아닙니다 : mysql용은 com.mysql.jdbc.jdbc2.optional.MysqlDataSource 입니다 )
   * @param url         Database 주소
   * @param username    사용자 명
   * @param passwd      사용자 패스워드
   * @return [[javax.sql.DataSource]] 인스턴스
   */
  def getDataSourceByDataSourceClass(dataSourceClassName: String,
                                     url: String,
                                     username: String = "",
                                     passwd: String = "",
                                     props: JMap[String, String] = new util.HashMap(),
                                     maxPoolSize: Int = MAX_POOL_SIZE): DataSource = {
    getDataSourceInternal(dataSourceClassName, "", url, username, passwd, props, maxPoolSize)
  }

  /**
   * DataSource 속성을 가진 Properties 로 설정합니다.
   *
   * {{{
   *   connectionTestQuery=SELECT 1
   *   dataSourceClassName=org.postgresql.ds.PGSimpleDataSource
   *   dataSource.username=test
   *   dataSource.password=test
   *   dataSource.databaseName=mydb
   *   dataSource.serverName=localhost
   *   dataSource.encoding=UTF-8
   *   dataSource.useUnicode=true
   * }}}
   */
  def getDataSource(props: Properties): DataSource = {
    log.info(s"Hikari DataSource를 빌드합니다... props=$props")

    val config = new HikariConfig(props)
    new HikariDataSource(config)
  }

  /** 테스트에 사용하기 위해 메모리를 사용하는 HSql DB 에 대한 DataSource 를 반환합니다. */
  def getEmbeddedHSqlDataSource: DataSource =
    getDataSource(JdbcDrivers.DRIVER_CLASS_HSQL,
                   "jdbc:hsqldb:mem:test;MVCC=TRUE;",
                   "sa",
                   "",
                   null)

  /** 테스트에 사용하기 위해 메모리를 사용하는 H2 DB 에 대한 DataSource 를 반환합니다. */
  def getEmbeddedH2DataSource: DataSource =
    getDataSource(JdbcDrivers.DRIVER_CLASS_H2,
                   "jdbc:h2:mem:test;DB_CLOSE_ON_EXIT=FALSE;MVCC=TRUE;",
                   "sa",
                   "",
                   null)


  private lazy val hikariFactory = new HikariDataSourceFactory()
  //  private lazy val bonecpFactory = new BoneDataSourceFactory()
  //  private lazy val tomcatFactory = new TomcatDataSourceFactory()

  private val dataSourceCache = new util.HashMap[String, DataSource]()

  private def getDataSourceInternal(dataSourceClassName: String = "",
                                    driverClass: String = "",
                                    url: String = "jdbc:h2:mem:test",
                                    username: String = "",
                                    passwd: String = "",
                                    props: JMap[String, String] = new util.HashMap(),
                                    maxPoolSize: Int = MAX_POOL_SIZE): DataSource =
    synchronized {

      // NOTE: 재사용을 위해 DataSource 도 cache로 두었다!!!
      // NOTE: 이렇게 해야 여러 라이브러리에서 DataSource를 생성할 때 중복해서 생성하지 않고, Connection 이 고갈되지 않는다

      var key = s"$dataSourceClassName:$driverClass:$url:$username:$passwd:$maxPoolSize"
      if (props != null)
        key += ":" + props.asScala.mkString

      var dataSource = dataSourceCache.get(key)

      if (dataSource == null) {
        // NOTE: HikariCP 가 slick 등 일반적인 jdbc에서는 잘 수행되나, Hibernate 에서 수행할 때에는 문제가 발생한다.
        // NOTE: 이유는 setMinimumIdle 을 설정해주지 않아서, maxPoolSize 와 같은 값으로 설정되어 버린다.
        // NOTE: 이 값을 4 정도의 최소값으로 설정하니, connection이 안정적으로 생성됨!!!
        // dataSource = bonecpFactory.createDataSource(dataSourceClassName,
        dataSource = hikariFactory.createDataSource(dataSourceClassName,
                                                     driverClass,
                                                     url,
                                                     username,
                                                     passwd,
                                                     props,
                                                     maxPoolSize)
        dataSourceCache.put(key, dataSource)
      }

      dataSource
    }
}
