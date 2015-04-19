package debop4s.data.orm.hibernate.spring

import javax.sql.DataSource

import debop4s.data.orm.DataConst._

/**
 * AbstractHibernatePgPoolConfiguration
 * Created by debop on 2014. 2. 27.
 */
abstract class AbstractHibernatePgPoolConfiguration extends AbstractHibernatePostgreSqlConfiguration {

  override def dataSource: DataSource = {
    buildDataSource(DRIVER_CLASS_POSTGRESQL,
                     s"jdbc:postgresql://localhost:9999/$getDatabaseName?Set=UTF8",
                     "root",
                     "root")
  }
}
