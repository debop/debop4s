package debop4s.data.hibernate.tools

import org.hibernate._
import org.slf4j.LoggerFactory
import scala.Some
import scala.annotation.varargs

/**
 * Hibernate [[StatelessSession]] 을 이용한 작업을 수행합니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 25. 오후 5:35
 */
object StatelessTool {

  private lazy val log = LoggerFactory.getLogger(getClass)

  def openStatelessSession(session: Session): StatelessSession =
    session.getSessionFactory.openStatelessSession()

  def executeTransactional(sessionFactory: SessionFactory)(action: StatelessSession => Unit) {
    var stateless = None: Option[StatelessSession]
    var tx = None: Option[Transaction]

    try {
      stateless = Some(sessionFactory.openStatelessSession())
      tx = Some(stateless.get.beginTransaction())
      action(stateless.get)
      tx.get.commit()
    } catch {
      case e: Throwable =>
        log.error(s"StatelessSession을 이용한 작업에 실패했습니다. rollback합니다.", e)
        if (tx.isDefined) tx.get.rollback()
        throw new HibernateException(e)
    } finally {
      if (stateless.isDefined)
        stateless.get.close()
    }
  }

  @varargs
  def executeAllTransactional(sessionFactory: SessionFactory, actions: (StatelessSession => Unit)*) {
    executeAllTransactional(sessionFactory, actions.toIterable)
  }

  def executeAllTransactional(sessionFactory: SessionFactory, actions: Iterable[StatelessSession => Unit]) {
    var stateless = None: Option[StatelessSession]
    var tx = None: Option[Transaction]

    try {
      stateless = Some(sessionFactory.openStatelessSession())
      tx = Some(stateless.get.beginTransaction())

      actions.foreach(action => action(stateless.get))
      tx.get.commit()
    } catch {
      case e: Throwable =>
        log.error(s"StatelessSession을 이용한 작업에 실패했습니다. rollback합니다.", e)
        if (tx.isDefined) tx.get.rollback()
        throw new HibernateException(e)
    } finally {
      if (stateless.isDefined)
        stateless.get.close()
    }
  }

  def execute(sessionFactory: SessionFactory, action: StatelessSession => Unit) {
    var stateless = None: Option[StatelessSession]

    try {
      stateless = Some(sessionFactory.openStatelessSession())
      action(stateless.get)
    } catch {
      case e: Throwable =>
        log.error(s"StatelessSession을 이용한 작업에 실패했습니다.", e)
        throw new HibernateException(e)
    } finally {
      if (stateless.isDefined)
        stateless.get.close()
    }
  }

  @varargs
  def executeAll(sessionFactory: SessionFactory, actions: (StatelessSession => Unit)*) {
    executeAll(sessionFactory, actions.toIterable)
  }

  def executeAll(sessionFactory: SessionFactory, actions: Iterable[StatelessSession => Unit]) {
    var stateless = None: Option[StatelessSession]

    try {
      stateless = Some(sessionFactory.openStatelessSession())
      actions.foreach(action => action(stateless.get))
    } catch {
      case e: Throwable =>
        log.error(s"StatelessSession을 이용한 작업에 실패했습니다.", e)
        throw new HibernateException(e)
    } finally {
      if (stateless.isDefined)
        stateless.get.close()
    }
  }
}
