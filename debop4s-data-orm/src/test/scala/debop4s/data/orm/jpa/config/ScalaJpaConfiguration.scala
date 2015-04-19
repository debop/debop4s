package debop4s.data.orm.jpa.config

import java.util.Properties

import debop4s.data.orm.jpa.ScalaJpaEntity
import debop4s.data.orm.jpa.spring.AbstractJpaH2Configuration
import org.hibernate.cache.rediscala.SingletonRedisRegionFactory
import org.hibernate.cfg.AvailableSettings
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
 * JpaConfiguration
 * Created by debop on 2014. 1. 29.
 */
@Configuration
@EnableTransactionManagement
class ScalaJpaConfiguration extends AbstractJpaH2Configuration {

  override def getMappedPackageNames: Array[String] =
    Array(classOf[ScalaJpaEntity].getPackage.getName)

  override def jpaProperties: Properties = {

    val props = super.jpaProperties

    props.put(AvailableSettings.HBM2DDL_AUTO, "create-drop")

    // add second cache provider using redis
    props.setProperty(AvailableSettings.USE_SECOND_LEVEL_CACHE, "true")
    props.setProperty(AvailableSettings.USE_QUERY_CACHE, "true")
    props.setProperty(AvailableSettings.CACHE_REGION_PREFIX, "hconnect")
    props.setProperty(AvailableSettings.CACHE_REGION_FACTORY, classOf[SingletonRedisRegionFactory].getName)
    props.setProperty(AvailableSettings.CACHE_PROVIDER_CONFIG, "hibernate-redis.conf")

    props
  }
}
