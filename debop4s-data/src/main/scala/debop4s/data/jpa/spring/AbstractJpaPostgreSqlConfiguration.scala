package debop4s.data.jpa.spring

import debop4s.data._
import debop4s.data.jdbc.DataSources
import java.util.Properties
import javax.sql.DataSource
import org.hibernate.cfg.AvailableSettings
import org.springframework.context.annotation.Bean

/**
 * AbstractPostgreSqlJpaConfiguration
 * Created by debop on 2014. 1. 29..
 */
abstract class AbstractJpaPostgreSqlConfiguration extends AbstractJpaConfiguration {
    @Bean
    override def dataSource: DataSource = {
        buildDataSource(DRIVER_CLASS_POSTGRESQL,
                           s"jdbc:postgresql://localhost:5432/$getDatabaseName?charSet=UTF8",
                           "root",
                           "root")
    }

    override def jpaProperties: Properties = {
        val props = super.jpaProperties
        props.put(AvailableSettings.DIALECT, DIALECT_POSTGRESQL)
        props
    }
}

/**
 * AbstractJpaPostgreSqlHikariConfiguration
 * Created by debop on 2014. 1. 29..
 */
abstract class AbstractJpaPostgreSqlHikariConfiguration extends AbstractJpaPostgreSqlConfiguration {
    @Bean
    override def dataSource: DataSource = {
        DataSources.getHikariDataSource(DATASOURCE_CLASS_POSTGRESQL,
                                           s"jdbc:postgresql://localhost:5432/$getDatabaseName?charSet=UTF8",
                                           "root",
                                           "root")
    }
}
