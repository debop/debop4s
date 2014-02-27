package com.github.debop4s.data.jpa.spring

import com.github.debop4s.data._
import java.util.Properties
import javax.sql.DataSource
import org.hibernate.cfg.AvailableSettings

/**
 * org.hibernate.examples.jpa.spring.AbstractHSqlJpaConfiguration
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 9. 오후 4:07
 */
abstract class AbstractJpaHSqlConfiguration extends AbstractJpaConfiguration {

    override def dataSource: DataSource = {
        buildDataSource(DRIVER_CLASS_HSQL, "jdbc:hsqldb:mem:" + getDatabaseName + ";MVCC=TRUE;", "sa", "")
    }

    override def jpaProperties: Properties = {
        val props: Properties = super.jpaProperties
        props.put(AvailableSettings.DIALECT, DIALECT_HSQL)
        props
    }
}
