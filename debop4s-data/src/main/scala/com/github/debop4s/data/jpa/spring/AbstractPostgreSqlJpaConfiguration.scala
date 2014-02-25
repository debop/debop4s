package com.github.debop4s.data.jpa.spring

import com.github.debop4s.data._
import java.util.Properties
import javax.sql.DataSource
import org.hibernate.cfg.AvailableSettings
import org.springframework.context.annotation.{Configuration, Bean}
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
 * AbstractPostgreSqlJpaConfiguration
 * Created by debop on 2014. 1. 29..
 */
@Configuration
@EnableTransactionManagement
abstract
class AbstractPostgreSqlJpaConfiguration extends AbstractJpaConfiguration {
    @Bean
    override def dataSource: DataSource =
        buildDataSource(DRIVER_CLASS_POSTGRESQL,
                           s"jdbc:postgresql://localhost/$getDatabaseName?SET=UTF8",
                           "root",
                           "root")

    override def jpaProperties: Properties = {
        val props = super.jpaProperties
        props.put(AvailableSettings.DIALECT, DIALECT_POSTGRESQL)
        props
    }
}
