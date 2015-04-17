package org.hibernate.cache.rediscala.hibernate

import java.util.Properties
import javax.sql.DataSource

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import org.hibernate.SessionFactory
import org.hibernate.cache.rediscala.SingletonRedisRegionFactory
import org.hibernate.cache.rediscala.domain.Account
import org.hibernate.cfg.AvailableSettings._
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor
import org.springframework.orm.hibernate4.{HibernateExceptionTranslator, HibernateTransactionManager, LocalSessionFactoryBean}
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
 * org.hibernate.cache.rediscala.tests.HibernateRedisConfiguration
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 21. 오후 4:50
 */
@Configuration
@EnableTransactionManagement
class HibernateRedisConfiguration {

  def databaseName: String = "hibernate"

  def getMappedPackageNames: Array[String] =
    Array(classOf[Account].getPackage.getName)

  def hibernateProperties: Properties = {

    val props = new Properties()

    props.setProperty(FORMAT_SQL, "true")
    // create | create-drop | spawn | spawn-drop | update | validate | none
    props.setProperty(HBM2DDL_AUTO, "create")
    props.setProperty(POOL_SIZE, "30")
    props.setProperty(SHOW_SQL, "false")
    props.setProperty(FORMAT_SQL, "true")

    // NOTE: 명시적인 Transaction 하에서만 DB에 적용되도록 false 정의한다.
    props.setProperty(AUTOCOMMIT, "false")


    props.setProperty(USE_REFLECTION_OPTIMIZER, "true")

    // Secondary Cache
    props.setProperty(USE_SECOND_LEVEL_CACHE, "true")
    props.setProperty(USE_QUERY_CACHE, "true")
    props.setProperty(CACHE_REGION_FACTORY, classOf[SingletonRedisRegionFactory].getName)
    props.setProperty(CACHE_REGION_PREFIX, "hibernate")

    props.setProperty(CACHE_PROVIDER_CONFIG, "hibernate-redis.conf")

    props
  }

  @Bean
  def dataSource: DataSource = {
    //
    // Hibernate 4 만 사용하는 경우 HikariCP 에서 예외가 발생한다. 그래서 BoneCP 를 사용하여 테스트하였다.
    // JPA 를 사용할 경우는 HikariCP 를 사용해도 된다.
    //
    //        val ds = new BoneCPDataSource()
    //        ds.setDriverClass("org.h2.Driver")
    //        ds.setJdbcUrl("jdbc:h2:mem:test;MVCC=true")
    //        ds.setUsername("sa")
    //        ds.setPassword("")
    //
    //        ds.setMaxConnectionsPerPartition(50)
    //        ds.setMinConnectionsPerPartition(2)
    //
    //        ds

    val config = new HikariConfig()

    config.setInitializationFailFast(true)

    config.setMaximumPoolSize(32)
    config.setMinimumIdle(2)

    //config.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource")
    //config.addDataSourceProperty("url", "jdbc:h2:mem:test;MVCC=true")
    config.setDriverClassName("org.h2.Driver")
    config.setJdbcUrl("jdbc:h2:mem:test;MVCC=true")
    config.setUsername("sa")
    config.setPassword("")

    new HikariDataSource(config)
  }

  @Bean
  def sessionFactory: SessionFactory = {
    val factoryBean = new LocalSessionFactoryBean()
    factoryBean.setPackagesToScan(getMappedPackageNames: _*)
    factoryBean.setHibernateProperties(hibernateProperties)
    factoryBean.setDataSource(dataSource)

    factoryBean.afterPropertiesSet()
    factoryBean.getObject
  }

  @Bean
  def transactionManager: HibernateTransactionManager = {
    new HibernateTransactionManager(sessionFactory)
  }

  @Bean
  def hibernateExceptionTranslator: HibernateExceptionTranslator =
    new HibernateExceptionTranslator()

  @Bean
  def exceptionTranslation: PersistenceExceptionTranslationPostProcessor =
    new PersistenceExceptionTranslationPostProcessor()

}
