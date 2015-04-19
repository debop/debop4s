package debop4s.data.orm.jpa.spring

import java.util
import java.util.Properties
import javax.sql.DataSource

import debop4s.data.common.DataSources
import debop4s.data.orm.DataConst._
import org.hibernate.cfg.AvailableSettings

import scala.collection.JavaConverters._
import scala.collection.immutable.HashMap


/**
 * JPA 에서 MySQL 을 사용하기 위한 환경설정
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 9. 오후 4:08
 */
abstract class AbstractJpaMySqlConfiguration extends AbstractJpaConfiguration {

  override def dataSource: DataSource = {
    DataSources.getDataSource(DRIVER_CLASS_MYSQL,
      "jdbc:mysql://localhost/" + getDatabaseName,
      "root",
      "root",
      defaultProperties)
  }

  def defaultProperties: util.Map[String, String] = {
    HashMap(
      "cachePrepStmts" -> "true",
      "prepStmtCacheSize" -> "500",
      "prepStmtCacheSqlLimit" -> "2048",
      "useServerPrepStmts" -> "true",
      "characterEncoding" -> "UTF-8",
      "useUnicode" -> "true"
    )
    .asJava
  }

  override def jpaProperties: Properties = {
    val props: Properties = super.jpaProperties
    props.put(AvailableSettings.DIALECT, DIALECT_MYSQL)
    props
  }

}
