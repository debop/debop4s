package debop4s.data.orm.hibernate.spring

import java.util.Properties
import javax.sql.DataSource

import debop4s.data.common.DataSources
import debop4s.data.orm.hibernate.interceptor.PersistentObjectInterceptor
import debop4s.data.orm.hibernate.repository.{HibernateDao, HibernateQueryDslDao}
import org.hibernate.cfg.AvailableSettings._
import org.hibernate.cfg.NamingStrategy
import org.hibernate.{Interceptor, SessionFactory}
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.orm.hibernate4.{HibernateExceptionTranslator, HibernateTransactionManager, LocalSessionFactoryBean}
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
 * Spring 용 Hibernate 환경 설정 Class 입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 24. 오후 2:05
 */
@Configuration
@EnableTransactionManagement
abstract class AbstractHibernateConfiguration {

  lazy val log = LoggerFactory.getLogger(getClass)

  def getDatabaseName = "hibernate"

  def getMappedPackageNames: Array[String]

  def getNamingStrategy: NamingStrategy = null

  def hibernateProperties: Properties = {
    val props = new Properties()

    // create | create-drop | spawn | spawn-drop | update | validate | none
    props.setProperty(HBM2DDL_AUTO, "none")

    props.setProperty(POOL_SIZE, "30")
    props.setProperty(SHOW_SQL, "true")
    props.setProperty(FORMAT_SQL, "true")

    props.setProperty(AUTOCOMMIT, "true")

    // 참고 : http://stackoverflow.com/questions/15573370/my-spring-application-leaks-database-connections-whereas-i-use-the-default-roo-c
    // 기본값을 사용하면 connection이 release 되지 않을 수 있다.
    props.setProperty(RELEASE_CONNECTIONS, "after_transaction")

    props
  }

  def buildDataSource(driverClass: String, url: String, username: String, password: String): DataSource =
    DataSources.getDataSource(driverClass, url, username, password)

  @Bean
  def dataSource: DataSource

  @Bean
  def jdbcTemplate = new JdbcTemplate(dataSource)

  protected def setupSessionFactory(factoryBean: LocalSessionFactoryBean) {
    // 추가 작업 시 override 해서 사용하세요.
  }

  @Bean
  def sessionFactory: SessionFactory = {
    log.info("SessionFactory를 생성합니다.")

    val factoryBean = new LocalSessionFactoryBean()
    val packagenames = getMappedPackageNames
    if (packagenames != null && packagenames.length > 0) {
      log.debug(s"hibernate용 entity를 scan 합니다. packages=[$packagenames]")
      factoryBean.setPackagesToScan(packagenames: _*)
    }

    factoryBean.setNamingStrategy(getNamingStrategy)

    factoryBean.setHibernateProperties(hibernateProperties)
    factoryBean.setDataSource(dataSource)
    val interceptor = hibernateInterceptor
    if (interceptor != null)
      factoryBean.setEntityInterceptor(hibernateInterceptor)

    setupSessionFactory(factoryBean)

    factoryBean.afterPropertiesSet()
    log.info("SessionFactory Bean에 대해 설정합니다.")

    factoryBean.getObject
  }

  @Bean
  def transactionManager: HibernateTransactionManager =
    new HibernateTransactionManager(sessionFactory)

  @Bean
  def hibernateInterceptor: Interceptor = new PersistentObjectInterceptor()

  @Bean
  def hibernateExceptionTranslator: HibernateExceptionTranslator =
    new HibernateExceptionTranslator()

  @Bean
  def exceptionTranslation: PersistenceExceptionTranslationPostProcessor =
    new PersistenceExceptionTranslationPostProcessor()

  @Bean
  def hibernateDao: HibernateDao = new HibernateDao(sessionFactory)

  @Bean
  def queryDslDao: HibernateQueryDslDao = new HibernateQueryDslDao(sessionFactory)
}
