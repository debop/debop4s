package debop4s.data.hibernate.spring

import debop4s.data._
import java.util.Properties
import javax.sql.DataSource
import org.hibernate.cfg.AvailableSettings
import org.springframework.context.annotation.Bean

/**
 * debop4s.data.hibernate.spring.AbstractPostgreSqlHibernateConfiguration
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 24. 오후 2:11
 */
abstract class AbstractHibernatePostgreSqlConfiguration extends AbstractHibernateConfiguration {

    @Bean
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
