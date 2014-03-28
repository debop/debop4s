package com.github.debop4s.data.jpa.spring

import com.github.debop4s.data.jdbc.DataSources
import java.util.Properties
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource
import org.hibernate.cfg.{AvailableSettings, NamingStrategy}
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor
import org.springframework.orm.hibernate4.HibernateExceptionTranslator
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.orm.jpa.{JpaTransactionManager, LocalContainerEntityManagerFactoryBean}
import org.springframework.scala.jdbc.core.JdbcTemplate
import org.springframework.transaction.PlatformTransactionManager

/**
 * org.hibernate.examples.jpa.spring.AbstractJpaConfiguration
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 9. 오후 4:02
 */
abstract class AbstractJpaConfiguration {

    lazy val log = LoggerFactory.getLogger(getClass)

    def getMappedPackageNames: Array[String]

    def getDatabaseName = "hibernate4s"

    def getNamingStrategy: NamingStrategy = null

    def jpaProperties: Properties = {
        val props = new Properties()

        // create | create-drop | spawn | spawn-drop | update | validate | none
        props.setProperty(AvailableSettings.HBM2DDL_AUTO, "none")
        props.setProperty(AvailableSettings.POOL_SIZE, "100")
        props.setProperty(AvailableSettings.SHOW_SQL, "false")
        props.setProperty(AvailableSettings.FORMAT_SQL, "true")
        props.setProperty(AvailableSettings.AUTOCOMMIT, "true")

        props
    }

    def buildDataSource(driverClass: String, url: String, username: String, password: String): DataSource =
        DataSources.getDataSource(driverClass, url, username, password)

    def buildEmbeddedDataSource = DataSources.getEmbeddedH2DataSource

    @Bean
    def dataSource: DataSource = buildEmbeddedDataSource

    @Bean
    def jdbcTemplate: JdbcTemplate = new JdbcTemplate(dataSource)

    protected def setupEntityManagerFactory(factoryBean: LocalContainerEntityManagerFactoryBean) {
        // 추가 작업 시 override 해서 사용하세요.
    }

    @Bean
    def entityManagerFactory: EntityManagerFactory = {
        log.info("SessionFactory를 생성합니다.")

        val factoryBean = new LocalContainerEntityManagerFactoryBean()

        val packagenames = getMappedPackageNames
        if (packagenames != null && packagenames.length > 0) {
            log.debug(s"hibernate용 entity를 scan 합니다. packages=[$packagenames]")
            factoryBean.setPackagesToScan(packagenames: _*)
        }
        factoryBean.setJpaProperties(jpaProperties)
        factoryBean.setDataSource(dataSource)

        val adapter = new HibernateJpaVendorAdapter()
        adapter.setGenerateDdl(true)
        factoryBean.setJpaVendorAdapter(adapter)

        setupEntityManagerFactory(factoryBean)

        factoryBean.afterPropertiesSet()
        log.info("EntityManagerFactory Bean에 대해 설정합니다.")

        factoryBean.getObject
    }

    @Bean
    def transactionManager: PlatformTransactionManager =
        new JpaTransactionManager(entityManagerFactory)

    @Bean
    def hibernateExceptionTranslator: HibernateExceptionTranslator =
        new HibernateExceptionTranslator()

    @Bean
    def exceptionTranslation: PersistenceExceptionTranslationPostProcessor =
        new PersistenceExceptionTranslationPostProcessor()
}
