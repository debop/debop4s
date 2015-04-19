package debop4s.data.orm.jpa.spring

import javax.sql.DataSource

import debop4s.data.common.DataSources
import debop4s.data.orm.DataConst._
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
 * MySQL Master-Slaves Replication 환경에서 사용하는 Configuration입니다.
 * @@Transaction(readOnly=true) 일 경우에는 Slave DB에 접속하도록 합니다.
 */
@Configuration
@EnableTransactionManagement
abstract class AbstractJpaMySqlReplicationConfiguration extends AbstractJpaMySqlConfiguration {

  override def dataSource: DataSource = {
    DataSources.getDataSource(DRIVER_CLASS_MYSQL,
      "jdbc:mysql:replication://localhost,localhost/" + getDatabaseName,
      "root",
      "root",
      defaultProperties)
  }

  //  /**
  //   * MySQL Master-Slave 환경에서 `@Transactional(readOnly=true)` 이 정의된 메소드에 대해 ReadOnly connection 을 사용하도록 합니다.
  //   */
  //  @Bean
  //  def jpaReadOnlyInterceptor: JpaReadOnlyInterceptor = {
  //    new JpaReadOnlyInterceptor()
  //  }
}
