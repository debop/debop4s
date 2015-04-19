package debop4s.data.orm.hibernate.utils

import javax.persistence.EntityManager

import debop4s.core.JFunction1
import debop4s.core.utils.Closer._
import org.hibernate._
import org.hibernate.internal.StatelessSessionImpl
import org.slf4j.LoggerFactory


/**
 * `StatelessSession`을 이용한 DB 작업을 지원하는 Helper class 입니다.
 */
object StatelessUtils {

  private val log = LoggerFactory.getLogger(getClass)

  def openStatelessSession(session: Session): StatelessSession = {
    session.getSessionFactory.openStatelessSession
  }

  def openStatelessSession(sessionFactory: SessionFactory): StatelessSession = {
    sessionFactory.openStatelessSession()
  }

  def openStatelessSession(em: EntityManager): StatelessSession = {
    em.unwrap[Session](classOf[Session]).getSessionFactory.openStatelessSession()
  }

  def withTransaction[T](em: EntityManager)(block: StatelessSession => T): T = {
    assert(em != null)
    val sf = em.unwrap(classOf[Session]).getSessionFactory
    withTransaction(sf)(block)
  }

  def withTransaction[T](em: EntityManager, func: JFunction1[StatelessSession, T]): T = {
    withTransaction(em) { stateless =>
      func.execute(stateless)
    }
  }

  def withTransaction[T](sf: SessionFactory)(block: StatelessSession => T): T = {
    assert(sf != null)
    log.debug("StatelessSession을 생성하여 DB 작업을 수행합니다...")

    using(sf.openStatelessSession().asInstanceOf[StatelessSessionImpl]) { stateless =>
      val tx = stateless.beginTransaction()
      try {
        val result = block(stateless)
        tx.commit()
        result
      } catch {
        case e: Throwable =>
          log.error("Hibernate StatelessSession 작업 중에 예외가 발생했습니다.", e)
          tx.rollback()
          null.asInstanceOf[T]
      }
    }
  }

  def withTransaction[T](sf: SessionFactory, func: JFunction1[StatelessSession, T]): T = {
    withTransaction(sf) { stateless =>
      func.execute(stateless)
    }
  }


  def withReadOnly[T](em: EntityManager)(block: StatelessSession => T): T = {
    assert(em != null)

    val sf = em.unwrap(classOf[Session]).getSessionFactory
    withReadOnly(sf) { stateless => block(stateless) }
  }

  def withReadOnly[T](em: EntityManager, func: JFunction1[StatelessSession, T]): T = {
    withReadOnly(em) { stateless =>
      func.execute(stateless)
    }
  }

  def withReadOnly[T](sf: SessionFactory)(block: StatelessSession => T): T = {
    assert(sf != null)
    log.trace("StatelessSession을 생성하여 DB 읽기전용 작업을 수행합니다...")

    using(sf.openStatelessSession().asInstanceOf[StatelessSessionImpl]) { stateless =>
      val conn = stateless.connection()
      conn.setReadOnly(true)
      conn.setAutoCommit(false)
      val tx = stateless.beginTransaction()
      try {
        val result = block(stateless)
        tx.commit()
        result
      } catch {
        case e: Throwable =>
          log.error("Hibernate StatelessSession 작업 중에 예외가 발생했습니다.", e)
          tx.rollback()
          null.asInstanceOf[T]
      }
    }
  }

  def withReadOnly[T](sf: SessionFactory, func: JFunction1[StatelessSession, T]): T = {
    withReadOnly(sf) { stateless =>
      func.execute(stateless)
    }
  }
}
