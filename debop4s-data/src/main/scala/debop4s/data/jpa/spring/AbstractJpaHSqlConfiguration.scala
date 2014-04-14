package debop4s.data.jpa.spring

import debop4s.data._
import debop4s.data.jdbc.DataSources
import java.util.Properties
import javax.sql.DataSource
import org.hibernate.cfg.AvailableSettings

/**
 * AbstractHSqlJpaConfiguration
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 9. 오후 4:07
 */
abstract class AbstractJpaHSqlConfiguration extends AbstractJpaConfiguration {

    override def dataSource: DataSource = {
        buildDataSource(DRIVER_CLASS_HSQL,
            s"jdbc:hsqldb:mem:$getDatabaseName;MVCC=TRUE;",
            "sa",
            "")
    }

    override def jpaProperties: Properties = {
        val props: Properties = super.jpaProperties
        props.put(AvailableSettings.DIALECT, DIALECT_HSQL)
        props
    }
}

abstract class AbstractJpaHSqlHikariConfiguration extends AbstractJpaHSqlConfiguration {

    override def dataSource: DataSource = {
        DataSources.getHikariDataSource(DATASOURCE_CLASS_HSQL,
            s"jdbc:hsqldb:mem:$getDatabaseName;MVCC=TRUE;",
            "sa",
            "")
    }
}
