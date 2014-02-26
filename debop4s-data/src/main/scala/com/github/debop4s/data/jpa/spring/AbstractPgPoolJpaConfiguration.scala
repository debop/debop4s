package com.github.debop4s.data.jpa.spring

import com.github.debop4s.data._
import javax.sql.DataSource
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
 * AbstractPgPoolJpaConfiguration
 * Created by debop on 2014. 1. 29..
 */
@Configuration
@EnableTransactionManagement
abstract
class AbstractPgPoolJpaConfiguration extends AbstractPostgreSqlJpaConfiguration {

    @Bean
    override def dataSource: DataSource = {
        buildDataSource(DRIVER_CLASS_POSTGRESQL,
                           s"jdbc:postgresql://localhost:9999/$getDatabaseName?Set=UTF8",
                           "root",
                           "root")
    }
}
