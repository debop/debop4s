package debop4s.data.orm.jpa.spring

import java.util.Properties
import javax.sql.DataSource

import debop4s.data.orm.DataConst._
import org.hibernate.cfg.AvailableSettings

/**
 * JPA 에서 H2 을 사용하기 위한 환경설정
 *
 * Created by debop on 2014. 1. 29..
 */
abstract class AbstractJpaH2Configuration extends AbstractJpaConfiguration {

  override def dataSource: DataSource = {
    buildDataSource(DRIVER_CLASS_H2,
                     s"jdbc:h2:mem:$getDatabaseName;DB_CLOSE_ON_EXIT=FALSE;MVCC=TRUE;",
                     "sa",
                     "")
  }

  override def jpaProperties: Properties = {
    val props: Properties = super.jpaProperties
    props.put(AvailableSettings.DIALECT, DIALECT_H2)
    props
  }
}
