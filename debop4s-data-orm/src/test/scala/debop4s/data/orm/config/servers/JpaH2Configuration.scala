package debop4s.data.orm.config.servers

import java.util.Properties

import debop4s.data.orm.jpa.spring.AbstractJpaH2Configuration
import debop4s.data.orm.mapping.ScalaEmployee
import org.hibernate.cache.rediscala.SingletonRedisRegionFactory
import org.hibernate.cfg.AvailableSettings._
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
 * JpaH2Configuration 
 *
 * @author sunghyouk.bae@gmail.com
 * @since 2014. 2. 28.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackageClasses = Array(classOf[ScalaEmployee]))
class JpaH2Configuration extends AbstractJpaH2Configuration {

  override def getMappedPackageNames: Array[String] =
    Array(classOf[ScalaEmployee].getPackage.getName)

  override def jpaProperties: Properties = {
    val props = super.jpaProperties

    props.put(HBM2DDL_AUTO, "create")

    // add second cache provider using redis
    props.setProperty(USE_SECOND_LEVEL_CACHE, "true")
    props.setProperty(USE_QUERY_CACHE, "false")
    props.setProperty(CACHE_REGION_PREFIX, "")
    props.setProperty(CACHE_REGION_FACTORY, classOf[SingletonRedisRegionFactory].getName)
    props.setProperty(CACHE_PROVIDER_CONFIG, "hibernate-redis.conf")

    props
  }
}
