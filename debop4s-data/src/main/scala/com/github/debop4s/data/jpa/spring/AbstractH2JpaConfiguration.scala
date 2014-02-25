package com.github.debop4s.data.jpa.spring

import com.github.debop4s.data._
import java.util.Properties
import javax.sql.DataSource
import org.hibernate.cfg.AvailableSettings

/**
 * AbstractH2JpaConfiguration
 * Created by debop on 2014. 1. 29..
 */
abstract
class AbstractH2JpaConfiguration extends AbstractJpaConfiguration {

    override def dataSource: DataSource = {
        buildDataSource(DRIVER_CLASS_H2,
                           s"jdbc:h2:mem:$getDatabaseName;DB_CLOSE_DELAY=-1;MVCC=TRUE;",
                           "sa",
                           "")
    }

    override def jpaProperties: Properties = {
        val props: Properties = super.jpaProperties
        props.put(AvailableSettings.DIALECT, DIALECT_H2)
        props
    }

}
