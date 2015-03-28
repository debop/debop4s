package debop4s.data.tests.spring

import debop4s.data.jpa.spring.AbstractJpaHSqlHikariConfiguration
import debop4s.data.tests.mapping.Employee
import java.util.Properties
import org.hibernate.cache.rediscala.SingletonRedisRegionFactory
import org.hibernate.cfg.AvailableSettings
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
 * debop4s.data.tests.spring.HSqlJpaConfiguration
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 26. 오전 10:30
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = Array("debop4s.data.tests"))
class JpaHSqlConfiguration extends AbstractJpaHSqlHikariConfiguration {

  override def getMappedPackageNames: Array[String] =
    Array(classOf[Employee].getPackage.getName)

  override def jpaProperties: Properties = {
    val props = super.jpaProperties

    props.put(AvailableSettings.HBM2DDL_AUTO, "create")

    // add second cache provider using redis
    props.setProperty(AvailableSettings.USE_SECOND_LEVEL_CACHE, "true")
    props.setProperty(AvailableSettings.USE_QUERY_CACHE, "true")
    props.setProperty(AvailableSettings.CACHE_REGION_PREFIX, "debop4s")
    props.setProperty(AvailableSettings.CACHE_REGION_FACTORY, classOf[SingletonRedisRegionFactory].getName)
    props.setProperty(AvailableSettings.CACHE_PROVIDER_CONFIG, "hibernate-redis.properties")

    props
  }
}
