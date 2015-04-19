package debop4s.data.orm.jpa.spring

import javax.sql.DataSource

import debop4s.data.orm.DataConst._

/**
 * JPA 에서 MariaDB 을 사용하기 위한 환경설정
 *
 * Created by debop on 2014. 1. 29..
 */
abstract class AbstractJpaMariaDbConfiguration extends AbstractJpaMySqlConfiguration {

  override def dataSource: DataSource = {
    buildDataSource(DRIVER_CLASS_MARIADB,
                     s"jdbc:mysql://localhost/$getDatabaseName",
                     "root",
                     "root")
  }
}
