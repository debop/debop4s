package com.github.debop4s.data.hibernate.spring

import com.github.debop4s.data._
import javax.sql.DataSource
import org.springframework.context.annotation.Bean

/**
 * AbstractHibernatePgPoolConfiguration
 * Created by debop on 2014. 2. 27.
 */
abstract class AbstractHibernatePgPoolConfiguration extends AbstractHibernatePostgreSqlConfiguration {

    @Bean
    override def dataSource: DataSource = {
        buildDataSource(DRIVER_CLASS_POSTGRESQL,
            s"jdbc:postgresql://localhost:9999/$getDatabaseName?Set=UTF8",
            "root",
            "root")
    }
}
