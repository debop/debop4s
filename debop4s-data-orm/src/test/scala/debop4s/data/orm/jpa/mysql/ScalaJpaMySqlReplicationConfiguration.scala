package debop4s.data.orm.jpa.mysql

import java.util.Properties

import debop4s.data.orm.jpa.ScalaJpaEntity
import debop4s.data.orm.jpa.mysql.model.MySqlOrder
import debop4s.data.orm.jpa.mysql.repository.ScalaJpaEntityRepository
import debop4s.data.orm.jpa.mysql.service.ScalaJpaEntityService
import debop4s.data.orm.jpa.spring.AbstractJpaMySqlReplicationConfiguration
import org.hibernate.cache.rediscala.SingletonRedisRegionFactory
import org.hibernate.cfg.AvailableSettings._
import org.springframework.context.annotation.{ComponentScan, Configuration, EnableAspectJAutoProxy}
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
 * JpaMySqlReplicationConfiguration
 * @author sunghyouk.bae@gmail.com 2014. 9. 7.
 */
@Configuration
@EnableAspectJAutoProxy // Transaction readonly 시에 intercept 작업을 위해 꼭 추가해주어야 합니다.
@EnableTransactionManagement
@EnableJpaRepositories(basePackageClasses = Array(classOf[ScalaJpaEntityRepository]))
@ComponentScan(basePackageClasses = Array(classOf[ScalaJpaEntityService]))
class ScalaJpaMySqlReplicationConfiguration extends AbstractJpaMySqlReplicationConfiguration {

  override def getMappedPackageNames: Array[String] =
    Array(classOf[ScalaJpaEntity].getPackage.getName,
      classOf[MySqlOrder].getPackage.getName)

  override def jpaProperties: Properties = {

    val props = super.jpaProperties

    // Replication 시에는 꼭 "none" 으로 해야 deadlock 이 안 걸린다.
    props.put(HBM2DDL_AUTO, "create")

    props.setProperty(SHOW_SQL, "true")

    // add second cache provider using redis
    props.setProperty(USE_SECOND_LEVEL_CACHE, "false")
    props.setProperty(USE_QUERY_CACHE, "false")
    props.setProperty(CACHE_REGION_PREFIX, "")
    props.setProperty(CACHE_REGION_FACTORY, classOf[SingletonRedisRegionFactory].getName)
    props.setProperty(CACHE_PROVIDER_CONFIG, "hibernate-redis.conf")

    props
  }
}
