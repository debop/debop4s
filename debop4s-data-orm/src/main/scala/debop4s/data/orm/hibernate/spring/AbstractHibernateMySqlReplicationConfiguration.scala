package debop4s.data.orm.hibernate.spring

import debop4s.data.orm.hibernate.mysql.HibernateReadOnlyConnectionInterceptor
import org.springframework.context.annotation.{Bean, EnableAspectJAutoProxy}

/**
 * MySQL Master-Slaves Replication 환경에서 사용하는 Configuration입니다.
 * `ReadOnlyConnection` annotation이 정의된 클래스나 메소드에 대해 읽기전용 DB에 접속하도록 합니다.
 *
 * @author sunghyouk.bae@gmail.com 2014. 9. 7.
 */
@EnableAspectJAutoProxy
abstract class AbstractHibernateMySqlReplicationConfiguration extends AbstractHibernateMySqlConfiguration {

  /**
   * MySQL Master-Slave 환경에서 ReadOnly connection 을 사용하도록 합니다.
   */
  @Bean
  def readOnlyConnectionInterceptor: HibernateReadOnlyConnectionInterceptor = {
    new HibernateReadOnlyConnectionInterceptor
  }
}
