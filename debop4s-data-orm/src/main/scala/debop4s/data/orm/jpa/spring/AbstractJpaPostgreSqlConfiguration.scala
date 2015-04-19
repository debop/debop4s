package debop4s.data.orm.jpa.spring

import java.util.Properties
import javax.sql.DataSource

import debop4s.data.orm.DataConst._
import org.hibernate.cfg.AvailableSettings

/**
 * JPA 에서 PostgreSql 을 사용하기 위한 환경설정
 * Created by debop on 2014. 1. 29..
 */
abstract class AbstractJpaPostgreSqlConfiguration extends AbstractJpaConfiguration {

  override def dataSource: DataSource = {
    buildDataSource(DRIVER_CLASS_POSTGRESQL,
      s"jdbc:postgresql://localhost:5432/$getDatabaseName?charSet=UTF8",
      "root",
      "root")
  }

  override def jpaProperties: Properties = {
    val props = super.jpaProperties
    props.put(AvailableSettings.DIALECT, DIALECT_POSTGRESQL)
    props
  }
}
