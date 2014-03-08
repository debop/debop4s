package com.github.debop4s.data.hibernate.spring

import com.github.debop4s.data._
import java.util.Properties
import javax.sql.DataSource
import org.hibernate.cfg.AvailableSettings
import org.springframework.context.annotation.Bean

/**
 * H2 Database를 사용하는 Spring 용 Hibernate 환경 설정 Class 입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 24. 오후 2:13
 */
abstract class AbstractHibernateH2Configuration extends AbstractHibernateConfiguration {

  @Bean
  override def dataSource: DataSource =
    buildDataSource(DRIVER_CLASS_H2,
      "jdbc:h2:mem:" + getDatabaseName + ";MVCC=TRUE;DB_CLOSE_ON_EXIT=FALSE",
      "sa",
      "")

  override def hibernateProperties: Properties = {
    val props = super.hibernateProperties
    props.put(AvailableSettings.DIALECT, DIALECT_H2)
    props
  }
}
