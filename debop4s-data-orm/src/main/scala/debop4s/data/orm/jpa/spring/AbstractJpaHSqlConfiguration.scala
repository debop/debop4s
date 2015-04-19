package debop4s.data.orm.jpa.spring

import java.util.Properties
import javax.sql.DataSource

import debop4s.data.orm.DataConst._
import org.hibernate.cfg.AvailableSettings

/**
 * JPA 에서 HSql 을 사용하기 위한 환경설정
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

