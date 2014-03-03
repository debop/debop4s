package com.github.debop4s.data.tests.jpa.config

import com.github.debop4s.data.jpa.spring.AbstractJpaH2Configuration
import java.util.Properties
import org.hibernate.cfg.AvailableSettings
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
 * ScalaJpaConfiguration 
 *
 * @author sunghyouk.bae@gmail.com
 * @since 2014. 2. 28.
 */
@Configuration
@EnableTransactionManagement
class ScalaJpaConfiguration extends AbstractJpaH2Configuration {

  override def getMappedPackageNames: Array[String] =
    Array(classOf[ScalaJpaEntity].getPackage.getName)

  override def jpaProperties: Properties = {
    val props = super.jpaProperties
    props.put(AvailableSettings.HBM2DDL_AUTO, "create")
    props
  }
}
