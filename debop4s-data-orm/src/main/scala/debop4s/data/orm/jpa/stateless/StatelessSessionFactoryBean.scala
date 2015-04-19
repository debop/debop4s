package debop4s.data.orm.jpa.stateless

import java.sql.Connection
import javax.persistence.EntityManagerFactory

import debop4s.core.Logging
import org.aopalliance.intercept.MethodInvocation
import org.hibernate.engine.transaction.spi.TransactionContext
import org.hibernate.internal.SessionImpl
import org.hibernate.jpa.HibernateEntityManagerFactory
import org.hibernate.{Session, SessionFactory, StatelessSession}
import org.springframework.aop.framework.ProxyFactory
import org.springframework.beans.factory.FactoryBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.orm.jpa.EntityManagerFactoryUtils
import org.springframework.transaction.support.{TransactionSynchronizationAdapter, TransactionSynchronizationManager}
import org.springframework.util.ReflectionUtils

/**
 * Hibernate 의 `StatelessSession`을 JPA에서 사용할 수 있도록, `StatelessSession` 을 생성해주는 Factory Bean입니다.
 *
 * 참고 : https://gist.github.com/jelies/5181262
 *
 * @author sunghyouk.bae@gmail.com 2014. 9. 7.
 */
class StatelessSessionFactoryBean @Autowired()(val emf: HibernateEntityManagerFactory)
  extends FactoryBean[StatelessSession] with Logging {

  var _sf: SessionFactory = emf.getSessionFactory

  def sessionFactory = _sf

  /**
   * `EntityManagerFactory`로부터 얻은 `SessionFactory`를 override 합니다.
   * NOTE: 단 connection 은 `EntityManager` 로 부터 얻는다.
   *
   * @param sf SessionFactory instance
   */
  def sessionFactory_=(sf: SessionFactory) { _sf = sf }

  override def getObject: StatelessSession = {
    val statelessInterceptor = new StatelessSessionInterceptor(emf, _sf)
    ProxyFactory.getProxy(classOf[StatelessSession], statelessInterceptor)
  }

  override def getObjectType: Class[_] = classOf[StatelessSession]

  override def isSingleton: Boolean = true

  /**
   * Stateless Session 의 Proxy 에 대한 Interceptor 입니다.
   */
  private class StatelessSessionInterceptor(val emf: EntityManagerFactory,
                                            val sf: SessionFactory)
    extends org.aopalliance.intercept.MethodInterceptor {

    override def invoke(invocation: MethodInvocation) = {
      val stateless = getCurrentStateless
      ReflectionUtils.invokeMethod(invocation.getMethod, stateless, invocation.getArguments: _*)
    }

    @inline
    private def getCurrentStateless: StatelessSession = {
      if (!TransactionSynchronizationManager.isActualTransactionActive) {
        throw new IllegalStateException("현 스레드에 활성화된 트랜잭션이 없습니다.")
      }
      var stateless = TransactionSynchronizationManager.getResource(sf).asInstanceOf[StatelessSession]
      if (stateless == null) {
        trace("현 스레드에 새로운 Stateless Session을 생성합니다.")
        stateless = newStatelessSession
        bindWithTransaction(stateless)
      }

      stateless
    }

    private def newStatelessSession: StatelessSession = {
      val conn = obtainPhysicalConnection
      sf.openStatelessSession(conn)
    }

    /**
     * 실제 Connection 을 얻는 게 중요하다. 이렇게 안하면 Proxy 를 이중으로 수행하여, 실제 Connection이 닫히지 않을 수 있다.
     */
    def obtainPhysicalConnection: Connection = {
      debug("(Proxy가 아닌) Real Connection을 얻습니다...")

      val em = EntityManagerFactoryUtils.getTransactionalEntityManager(emf)

      val session = em.unwrap(classOf[Session]).asInstanceOf[SessionImpl]

      session
      .getTransactionCoordinator
      .getJdbcCoordinator
      .getLogicalConnection
      .getConnection
    }

    def bindWithTransaction(stateless: StatelessSession): Unit = {
      trace("bind with transaction.")
      TransactionSynchronizationManager
      .registerSynchronization(new StatelessSessionSynchronization(sf, stateless))

      TransactionSynchronizationManager.bindResource(sf, stateless)
    }
  }

  /**
   * Stateless Session을 Transaction 에 동기화합니다.
   */
  class StatelessSessionSynchronization(val sf: SessionFactory,
                                        val stateless: StatelessSession)
    extends TransactionSynchronizationAdapter {

    override def getOrder: Int =
      EntityManagerFactoryUtils.ENTITY_MANAGER_SYNCHRONIZATION_ORDER - 100

    override def beforeCommit(readOnly: Boolean): Unit = {
      if (!readOnly) {
        stateless.asInstanceOf[TransactionContext].managedFlush()
      }
    }
    override def beforeCompletion(): Unit = {
      TransactionSynchronizationManager.unbindResource(sf)
      stateless.close()
    }
  }
}
