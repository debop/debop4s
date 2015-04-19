package debop4s.data.orm.hibernate.spring

import java.util.Properties
import javax.sql.DataSource

import debop4s.data.orm.DataConst._
import org.hibernate.cfg.AvailableSettings

abstract class AbstractHibernatePostgreSqlConfiguration extends AbstractHibernateConfiguration {

  override def dataSource: DataSource =
    buildDataSource(DRIVER_CLASS_POSTGRESQL,
                     "jdbc:postgresql://localhost/" + getDatabaseName,
                     "root",
                     "root")

  override def hibernateProperties: Properties = {
    val props = super.hibernateProperties
    props.put(AvailableSettings.DIALECT, DIALECT_POSTGRESQL)
    props
  }
}
