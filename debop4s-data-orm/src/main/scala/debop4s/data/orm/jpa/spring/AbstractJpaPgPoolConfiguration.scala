package debop4s.data.orm.jpa.spring

import javax.sql.DataSource

import debop4s.data.orm.DataConst._

/**
 * JPA 에서 pgpool-II 을 사용하기 위한 환경설정
 *
 * Created by debop on 2014. 1. 29..
 */
abstract class AbstractJpaPgPoolConfiguration extends AbstractJpaPostgreSqlConfiguration {

  override def dataSource: DataSource = {
    buildDataSource(DRIVER_CLASS_POSTGRESQL,
                     s"jdbc:postgresql://localhost:9999/$getDatabaseName?Set=UTF8",
                     "root",
                     "root")
  }
}
