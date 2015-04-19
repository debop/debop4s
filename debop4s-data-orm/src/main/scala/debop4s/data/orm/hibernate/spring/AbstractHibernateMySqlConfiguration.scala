package debop4s.data.orm.hibernate.spring

import java.util.Properties
import javax.sql.DataSource

import debop4s.data.orm.DataConst._
import org.hibernate.cfg.AvailableSettings

/**
 * MySQL DB를 사용하는 Spring 용 Hibernate 환경설정 정보
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 24. 오후 5:37
 */
abstract class AbstractHibernateMySqlConfiguration extends AbstractHibernateConfiguration {

  override def dataSource: DataSource =
    buildDataSource(DRIVER_CLASS_MYSQL,
                     "jdbc:mysql://localhost/" + getDatabaseName,
                     "root",
                     "root")

  override def hibernateProperties: Properties = {
    val props = super.hibernateProperties
    props.put(AvailableSettings.DIALECT, DIALECT_MYSQL)
    props
  }

}
