package com.github.debop4s.data.jpa.spring

import com.github.debop4s.data._
import java.util.Properties
import javax.sql.DataSource
import org.hibernate.cfg.AvailableSettings
import org.springframework.context.annotation.Bean

/**
 * org.hibernate.examples.jpa.spring.AbstractMySqlJpaConfiguration
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 9. 오후 4:08
 */
abstract class AbstractJpaMySqlConfiguration extends AbstractJpaConfiguration {

    @Bean
    override def dataSource: DataSource = {
        buildDataSource(DRIVER_CLASS_MYSQL, "jdbc:mysql://localhost/" + getDatabaseName, "root", "root")
    }

    override def jpaProperties: Properties = {
        val props: Properties = super.jpaProperties
        props.put(AvailableSettings.DIALECT, DIALECT_MYSQL)
        props
    }
}