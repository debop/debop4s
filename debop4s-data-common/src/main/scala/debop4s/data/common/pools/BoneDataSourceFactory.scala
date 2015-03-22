package debop4s.data.common.pools

import java.util
import javax.sql.DataSource

import com.jolbox.bonecp.{ BoneCPConfig, BoneCPDataSource }
import debop4s.data.common.DataSources

/**
 * BoneCP 라이브러리를 이용하여 DataSource 를 생성합니다.
 *
 * @author Sunghyouk Bae
 */
class BoneDataSourceFactory extends AbstractDataSourceFactory {

  /**
   * BoneCP DataSource를 생성합니다.
   * @param dataSourceClassName dataSourceClassName
   *                            ( 기존 driverClass 가 아닙니다 : mysql용은 com.mysql.jdbc.jdbc2.optional.MysqlDataSource 입니다 )
   * @param url         Database 주소
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
    log.info("BoneCP DataSource를 빌드합니다... " +
             s"dataSourceClassName=[$dataSourceClassName], driverClass=[$driverClass] url=[$url], username=[$username]")

    Class.forName(driverClass)

    val config = new BoneCPConfig()
    config.setJdbcUrl(url)
    config.setUsername(username)
    config.setPassword(passwd)

    val partitionCount = 4 min DataSources.processCount
    config.setPartitionCount(partitionCount)
    config.setMaxConnectionsPerPartition(( maxPoolSize / partitionCount ) max DataSources.MIN_POOL_SIZE)
    config.setMinConnectionsPerPartition(DataSources.MIN_IDLE_SIZE)

    // NOTE: AutoCommit 을 false로 하면 모든 write 작업은 Transaction 하에서 수행해야 합니다.
    // NOTE: 특히 slick은 withSession 대신 withTransaction 을 사용해야 합니다.
    // 안정성을 위해 명시적으로 설정하지는 않습니다. 설정하지 않으면 driver 기본값을 따릅니다.
    // config.setDefaultAutoCommit(false)
    config.setStatementsCacheSize(1024)
    config.setMaxConnectionAgeInSeconds(300)
    config.setDisableJMX(true)

    new BoneCPDataSource(config)
  }
}