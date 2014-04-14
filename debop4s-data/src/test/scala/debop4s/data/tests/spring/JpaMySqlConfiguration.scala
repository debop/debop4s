package debop4s.data.tests.spring

import debop4s.data.jpa.spring.AbstractJpaMySqlHikariConfiguration
import debop4s.data.tests.mapping.Employee
import java.util.Properties
import org.hibernate.cache.rediscala.SingletonRedisRegionFactory
import org.hibernate.cfg.AvailableSettings
import org.springframework.context.annotation.{ComponentScan, Configuration}
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
 * JpaMySqlConfiguration
 * Created by debop on 2014. 2. 27.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = Array("debop4s.data.tests"))
@ComponentScan(basePackages = Array("debop4s.data.jpa"))
class JpaMySqlConfiguration extends AbstractJpaMySqlHikariConfiguration {

    override def getMappedPackageNames: Array[String] =
        Array(classOf[Employee].getPackage.getName)

    override def jpaProperties: Properties = {
        val props = super.jpaProperties

        props.setProperty(AvailableSettings.HBM2DDL_AUTO, "create-drop")
        props.setProperty(AvailableSettings.SHOW_SQL, "true")

        // add second cache provider using redis
        props.setProperty(AvailableSettings.USE_SECOND_LEVEL_CACHE, "true")
        props.setProperty(AvailableSettings.USE_QUERY_CACHE, "true")
        props.setProperty(AvailableSettings.CACHE_REGION_PREFIX, "debop4s")
        props.setProperty(AvailableSettings.CACHE_REGION_FACTORY, classOf[SingletonRedisRegionFactory].getName)
        props.setProperty(AvailableSettings.CACHE_PROVIDER_CONFIG, "hibernate-redis.properties")

        props
    }
}
