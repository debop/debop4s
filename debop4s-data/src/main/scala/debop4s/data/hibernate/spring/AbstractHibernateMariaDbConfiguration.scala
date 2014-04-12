package debop4s.data.hibernate.spring

import debop4s.data._
import java.util.Properties
import javax.sql.DataSource
import org.hibernate.cfg.AvailableSettings
import org.springframework.context.annotation.Bean

/**
 * Maria DB를 사용하는 Spring용 Hibernate 환경설정
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 24. 오후 2:15
 */
abstract class AbstractHibernateMariaDbConfiguration extends AbstractHibernateConfiguration {


  @Bean
  override def dataSource: DataSource = {
    buildDataSource(DRIVER_CLASS_MARIADB,
      "jdbc:mysql://localhost/" + getDatabaseName,
      "root",
      "root")
  }

  override def hibernateProperties: Properties = {
    val props = super.hibernateProperties
    props.put(AvailableSettings.DIALECT, DIALECT_MYSQL)
    props
  }
}
