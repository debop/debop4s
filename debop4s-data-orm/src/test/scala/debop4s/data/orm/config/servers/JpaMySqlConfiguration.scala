package debop4s.data.orm.config.servers

import java.util.Properties
import javax.sql.DataSource

import debop4s.data.common.DataSources
import debop4s.data.orm.config.DataConfig
import debop4s.data.orm.jpa.ScalaJpaEntity
import debop4s.data.orm.jpa.converter.ConverterEntity
import debop4s.data.orm.jpa.spring.AbstractJpaMySqlReplicationConfiguration
import debop4s.data.orm.mapping.ScalaEmployee
import org.hibernate.cache.rediscala.SingletonRedisRegionFactory
import org.hibernate.cfg.AvailableSettings._
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.{Configuration, EnableAspectJAutoProxy}
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
 * JpaMySqlConfiguration
 * Created by debop on 2014. 2. 27.
 */
@Configuration
@EnableAspectJAutoProxy
@EnableTransactionManagement
@EnableJpaRepositories(basePackageClasses = Array(classOf[ScalaEmployee]))
class JpaMySqlConfiguration extends AbstractJpaMySqlReplicationConfiguration {

  override def getMappedPackageNames: Array[String] =
    Array(classOf[ScalaEmployee].getPackage.getName,
      classOf[ScalaJpaEntity].getPackage.getName,
      classOf[ConverterEntity].getPackage.getName)

  @Autowired val dataConfig: DataConfig = null


  override def dataSource: DataSource = {

    val driverClass = dataConfig.database.driverClass
    val jdbcUrl = dataConfig.database.url
    val username = dataConfig.database.username
    val password = dataConfig.database.password

    log.info(s"Database 설정. driverClass=$driverClass, jdbcUrl=$jdbcUrl, username=$username")

    DataSources.getDataSource(driverClass, jdbcUrl, username, password)
  }

  override def jpaProperties: Properties = {
    val props = super.jpaProperties

    // create | create-drop | spawn | spawn-drop | update | validate | none
    props.setProperty(HBM2DDL_AUTO, dataConfig.hibernate.hbm2ddl)

    props.setProperty(SHOW_SQL, dataConfig.hibernate.showSql.toString)

    // hibernate-redis 2nd cache 를 위한 환경설정 정보를 나타냅니다.
    val cacheProviderCfg = dataConfig.hibernate.cacheProviderConfig


    props.setProperty(USE_SECOND_LEVEL_CACHE, dataConfig.hibernate.useSecondCache.toString)
    props.setProperty(USE_QUERY_CACHE, dataConfig.hibernate.useSecondCache.toString)
    props.setProperty(CACHE_REGION_PREFIX, "")
    props.setProperty(CACHE_REGION_FACTORY, classOf[SingletonRedisRegionFactory].getName)
    props.setProperty(CACHE_PROVIDER_CONFIG, cacheProviderCfg)

    props
  }
}
