package org.hibernate.cache.rediscala.jpa

import java.util.Properties
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import org.hibernate.cache.rediscala.SingletonRedisRegionFactory
import org.hibernate.cache.rediscala.domain.Event
import org.hibernate.cache.rediscala.jpa.repository.EventRepository
import org.hibernate.cfg.AvailableSettings._
import org.hibernate.cfg.NamingStrategy
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.hibernate4.HibernateExceptionTranslator
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.orm.jpa.{JpaTransactionManager, LocalContainerEntityManagerFactoryBean}
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
 * JapRedisConfiguration 
 *
 * @author sunghyouk.bae@gmail.com
 * @since 2014. 2. 26.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackageClasses = Array(classOf[JpaAccountRepository], classOf[EventRepository]))
class JpaRedisConfiguration {

  private lazy val log = LoggerFactory.getLogger(getClass)

  def getDatabaseName = "hibernate"

  def getMappedPackageNames: Array[String] =
    Array(classOf[JpaAccount].getPackage.getName,
           classOf[Event].getPackage.getName)

  def getNamingStrategy: NamingStrategy = null

  def jpaProperties: Properties = {
    val props = new Properties()

    props.setProperty(FORMAT_SQL, "true")
    // create | create-drop | spawn | spawn-drop | update | validate | none
    props.setProperty(HBM2DDL_AUTO, "create")
    props.setProperty(POOL_SIZE, "30")
    props.setProperty(SHOW_SQL, "false")
    props.setProperty(FORMAT_SQL, "true")

    // NOTE: 명시적인 Transaction 하에서만 DB에 적용되도록 false 정의한다.
    props.setProperty(AUTOCOMMIT, "false")

    // Secondary Cache
    props.setProperty(USE_SECOND_LEVEL_CACHE, "true")
    props.setProperty(USE_QUERY_CACHE, "true")
    props.setProperty(CACHE_REGION_FACTORY, classOf[SingletonRedisRegionFactory].getName)
    props.setProperty(CACHE_REGION_PREFIX, "")
    props.setProperty(CACHE_PROVIDER_CONFIG, "hibernate-redis.conf")

    props
  }

  @Bean
  def dataSource: DataSource = {
    val config = new HikariConfig()

    config.setInitializationFailFast(true)

    config.setMaximumPoolSize(32)
    config.setMinimumIdle(2)

    config.setDriverClassName("org.h2.Driver")
    config.setJdbcUrl("jdbc:h2:mem:test;MVCC=true")
    config.setUsername("sa")
    config.setPassword("")

    new HikariDataSource(config)
  }

  protected def setupEntityManagerFactory(factoryBean: LocalContainerEntityManagerFactoryBean) {
    // 추가 작업 시 override 해서 사용하세요.
  }

  @Bean
  def entityManagerFactory(): EntityManagerFactory = {
    log.info("SessionFactory를 생성합니다.")

    val factoryBean = new LocalContainerEntityManagerFactoryBean()

    val packagenames = getMappedPackageNames
    if (packagenames != null && packagenames.length > 0) {
      log.debug(s"hibernate용 entity를 scan 합니다. packages=[$packagenames]")
      factoryBean.setPackagesToScan(packagenames: _*)
    }
    factoryBean.setDataSource(dataSource)
    factoryBean.setJpaProperties(jpaProperties)


    val adapter = new HibernateJpaVendorAdapter()
    adapter.setGenerateDdl(true)
    factoryBean.setJpaVendorAdapter(adapter)

    setupEntityManagerFactory(factoryBean)

    factoryBean.afterPropertiesSet()
    log.info("EntityManagerFactory Bean에 대해 설정합니다.")

    factoryBean.getObject
  }

  @Bean
  def transactionManager(): PlatformTransactionManager =
    new JpaTransactionManager(entityManagerFactory())

  @Bean
  def hibernateExceptionTranslator() = new HibernateExceptionTranslator()

  @Bean
  def exceptionTranslation(): PersistenceExceptionTranslationPostProcessor =
    new PersistenceExceptionTranslationPostProcessor()
}
