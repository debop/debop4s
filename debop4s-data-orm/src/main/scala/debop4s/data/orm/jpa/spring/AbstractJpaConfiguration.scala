package debop4s.data.orm.jpa.spring

import java.util.Properties
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

import debop4s.data.common.DataSources
import debop4s.data.orm.jpa.repository.JpaDao
import debop4s.data.orm.jpa.stateless.StatelessSessionFactoryBean
import org.hibernate.SessionFactory
import org.hibernate.cfg.AvailableSettings._
import org.hibernate.cfg.NamingStrategy
import org.hibernate.jpa.HibernateEntityManagerFactory
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.{Bean, ComponentScan, EnableAspectJAutoProxy}
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.orm.hibernate4.HibernateExceptionTranslator
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.orm.jpa.{JpaTransactionManager, LocalContainerEntityManagerFactoryBean}

/**
 * Spring framework을 이용한 JPA 환경설정에 대한 기본 클래스입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 9. 오후 4:02
 */
// NOTE: EnableAspectJAutoProxy 을 제대로 사용하려면, 직접 Bean으로 정의하면 안되고, JpaDao를 ComponentScan으로 등록해야 한다 (이유는 몰라...)
@EnableAspectJAutoProxy
@ComponentScan(basePackageClasses = Array(classOf[JpaDao]))
abstract class AbstractJpaConfiguration {

  protected lazy val log = LoggerFactory.getLogger(getClass)

  def getMappedPackageNames: Array[String]

  def getDatabaseName = "hibernate"

  def getNamingStrategy: NamingStrategy = null

  def jpaProperties: Properties = {
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
      log.debug(s"hibernate용 entity를 scan 합니다. packages=[${ packagenames.mkString }]")
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

  /**
   * Hibernate Session Factory
   */
  @Bean def sessionFactory: SessionFactory = {
    entityManagerFactory.asInstanceOf[HibernateEntityManagerFactory].getSessionFactory
  }

  @Bean
  def statlessSessionFactory: StatelessSessionFactoryBean = {
    new StatelessSessionFactoryBean(entityManagerFactory.asInstanceOf[HibernateEntityManagerFactory])
  }

  @Bean
  def transactionManager: JpaTransactionManager =
    new JpaTransactionManager(entityManagerFactory)

  @Bean
  def hibernateExceptionTranslator: HibernateExceptionTranslator =
    new HibernateExceptionTranslator()

  @Bean
  def exceptionTranslation: PersistenceExceptionTranslationPostProcessor =
    new PersistenceExceptionTranslationPostProcessor()

}
