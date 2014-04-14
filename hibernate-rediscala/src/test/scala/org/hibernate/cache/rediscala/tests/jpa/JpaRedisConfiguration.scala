package org.hibernate.cache.rediscala.tests.jpa

import com.zaxxer.hikari.{HikariDataSource, HikariConfig}
import java.util.Properties
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource
import org.hibernate.cache.rediscala.SingletonRedisRegionFactory
import org.hibernate.cache.rediscala.tests.domain.Event
import org.hibernate.cache.rediscala.tests.jpa.repository.EventRepository
import org.hibernate.cfg.{AvailableSettings, NamingStrategy}
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
    lazy val log = LoggerFactory.getLogger(getClass)

    def getDatabaseName = "hibernate"

    def getMappedPackageNames: Array[String] =
        Array(classOf[JpaAccount].getPackage.getName,
            classOf[Event].getPackage.getName)

    def getNamingStrategy: NamingStrategy = null

    def jpaProperties: Properties = {
        val props = new Properties()

        props.setProperty(AvailableSettings.FORMAT_SQL, "true")
        // create | create-drop | spawn | spawn-drop | update | validate | none
        props.setProperty(AvailableSettings.HBM2DDL_AUTO, "create")
        props.setProperty(AvailableSettings.POOL_SIZE, "30")
        props.setProperty(AvailableSettings.SHOW_SQL, "true")
        props.setProperty(AvailableSettings.FORMAT_SQL, "true")
        props.setProperty(AvailableSettings.AUTOCOMMIT, "true")

        // Secondary Cache
        props.setProperty(AvailableSettings.USE_SECOND_LEVEL_CACHE, "true")
        props.setProperty(AvailableSettings.USE_QUERY_CACHE, "true")
        props.setProperty(AvailableSettings.CACHE_REGION_FACTORY, classOf[SingletonRedisRegionFactory].getName)
        props.setProperty(AvailableSettings.CACHE_REGION_PREFIX, "")
        props.setProperty(AvailableSettings.CACHE_PROVIDER_CONFIG, "hibernate-redis.properties")

        // props.setProperty(AvailableSettings.CONNECTION_PROVIDER, "com.zaxxer.hikari.hibernate.HikariConnectionProvider")

        props
    }

    @Bean
    def dataSource: DataSource = {
        val config = new HikariConfig()
        config.setMaximumPoolSize(200)

        config.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource")
        config.addDataSourceProperty("url", "jdbc:h2:mem:test;MVCC=true")
        config.addDataSourceProperty("user", "sa")
        config.addDataSourceProperty("password", "")

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
