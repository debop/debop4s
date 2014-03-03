package com.github.debop4s.data.tests.spring

import com.github.debop4s.data.jpa.spring.AbstractJpaHSqlHikariConfiguration
import com.github.debop4s.data.tests.mapping.Employee
import java.util.Properties
import org.hibernate.cfg.AvailableSettings
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
 * com.github.debop4s.data.tests.spring.HSqlJpaConfiguration
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 26. 오전 10:30
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = Array("com.github.debop4s.data.tests"))
class JpaHSqlConfiguration extends AbstractJpaHSqlHikariConfiguration {

  override def getMappedPackageNames: Array[String] =
    Array(classOf[Employee].getPackage.getName)

  override def jpaProperties: Properties = {
    val props = super.jpaProperties
    props.put(AvailableSettings.HBM2DDL_AUTO, "auto")
    props
  }
}
