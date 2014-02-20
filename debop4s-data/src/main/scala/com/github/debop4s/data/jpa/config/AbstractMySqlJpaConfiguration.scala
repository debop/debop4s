package com.github.debop4s.data.jpa.config

import java.util.Properties
import javax.sql.DataSource
import org.hibernate.cfg.AvailableSettings
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.transaction.annotation.EnableTransactionManagement
import scala.Predef.String

/**
 * org.hibernate.examples.jpa.config.AbstractMySqlJpaConfiguration 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 9. 오후 4:08
 */
@Configuration
@EnableTransactionManagement
abstract class AbstractMySqlJpaConfiguration extends AbstractJpaConfiguration {

    val DRIVER_CLASS_MYSQL: String = "com.mysql.jdbc.Driver"
    val DIALECT_MYSQL: String = "org.hibernate.dialect.MySQL5InnoDBDialect"

    @Bean
    override def dataSource(): DataSource = {
        buildDataSource(DRIVER_CLASS_MYSQL, "jdbc:mysql://localhost/" + getDatabaseName, "root", "root")
    }

    override def jpaProperties(): Properties = {
        val props: Properties = super.jpaProperties()
        props.put(AvailableSettings.DIALECT, DIALECT_MYSQL)
        props
    }
}
