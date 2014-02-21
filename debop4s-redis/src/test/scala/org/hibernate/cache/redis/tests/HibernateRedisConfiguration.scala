package org.hibernate.cache.redis.tests

import java.util.Properties
import org.hibernate.SessionFactory
import org.hibernate.cache.redis.SingletonRedisRegionFactory
import org.hibernate.cache.redis.tests.domain.Account
import org.hibernate.cfg.AvailableSettings
import org.hibernate.engine.transaction.internal.jdbc.JdbcTransactionFactory
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor
import org.springframework.jdbc.datasource.embedded.{EmbeddedDatabaseType, EmbeddedDatabaseBuilder}
import org.springframework.orm.hibernate4.{HibernateExceptionTranslator, HibernateTransactionManager, LocalSessionFactoryBean}

/**
 * org.hibernate.cache.redis.tests.HibernateRedisConfiguration 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 21. 오후 4:50
 */
@Configuration
class HibernateRedisConfiguration {

    def databaseName: String = "hibernate"

    def getMappedPackageNames: Array[String] = Array(classOf[Account].getPackage.getName)

    def hibernateProperties: Properties = {

        val props = new Properties()

        props.setProperty(AvailableSettings.HBM2DDL_AUTO, "create")
        props.setProperty(AvailableSettings.FORMAT_SQL, "true")
        props.setProperty(AvailableSettings.SHOW_SQL, "true")
        props.setProperty(AvailableSettings.POOL_SIZE, "100")

        // Secondary Cache
        props.setProperty(AvailableSettings.USE_SECOND_LEVEL_CACHE, "true")
        props.setProperty(AvailableSettings.USE_QUERY_CACHE, "true")
        props.setProperty(AvailableSettings.CACHE_REGION_FACTORY, classOf[SingletonRedisRegionFactory].getName)
        props.setProperty(AvailableSettings.CACHE_REGION_PREFIX, "hibernate")
        props.setProperty(AvailableSettings.TRANSACTION_STRATEGY, classOf[JdbcTransactionFactory].getName)

        props.setProperty(AvailableSettings.CACHE_PROVIDER_CONFIG, "hibernate-redis.properties")

        props
    }

    @Bean
    def dataSource() =
        new EmbeddedDatabaseBuilder()
        .setType(EmbeddedDatabaseType.HSQL)
        .build()

    @Bean
    def sessionFactory: SessionFactory = {
        val factoryBean = new LocalSessionFactoryBean()
        factoryBean.setPackagesToScan(getMappedPackageNames: _*)
        factoryBean.setDataSource(dataSource())
        factoryBean.setHibernateProperties(hibernateProperties)

        factoryBean.afterPropertiesSet()

        factoryBean.getObject
    }

    @Bean
    def transactionManager =
        new HibernateTransactionManager(sessionFactory)

    @Bean
    def hibernateExceptionTranslator =
        new HibernateExceptionTranslator()

    @Bean
    def exceptionTranslation =
        new PersistenceExceptionTranslationPostProcessor()

}
