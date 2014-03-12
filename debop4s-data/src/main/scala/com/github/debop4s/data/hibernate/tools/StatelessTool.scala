package com.github.debop4s.data.hibernate.tools

import org.hibernate.{Session, HibernateException, SessionFactory, StatelessSession}
import org.slf4j.LoggerFactory
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
        log.debug(s"StatelessSession을 이용하여 transaction 작업을 수행합니다...")

        val stateless = sessionFactory.openStatelessSession()
        val tx = stateless.beginTransaction()

        try {
            action(stateless)
            tx.commit()
        } catch {
            case e: Throwable =>
                log.error(s"StatelessSession을 이용한 작업에 실패했습니다. rollback합니다.", e)
                if (tx != null) tx.rollback()
                throw new HibernateException(e)
        } finally {
            if (stateless != null)
                stateless.close()
        }
    }

    @varargs
    def executeAllTransactional(sessionFactory: SessionFactory, actions: (StatelessSession => Unit)*) {
        executeAllTransactional(sessionFactory, actions.toIterable)
    }

    def executeAllTransactional(sessionFactory: SessionFactory, actions: Iterable[StatelessSession => Unit]) {
        log.debug(s"StatelessSession을 이용하여 transaction 작업을 수행합니다...")

        val stateless = sessionFactory.openStatelessSession()
        val tx = stateless.beginTransaction()

        try {
            actions.foreach(action => action(stateless))
            tx.commit()
        } catch {
            case e: Throwable =>
                log.error(s"StatelessSession을 이용한 작업에 실패했습니다. rollback합니다.", e)
                if (tx != null) tx.rollback()
                throw new HibernateException(e)
        } finally {
            if (stateless != null)
                stateless.close()
        }
    }

    def execute(sessionFactory: SessionFactory, action: StatelessSession => Unit) {
        val stateless = sessionFactory.openStatelessSession()

        try {
            action(stateless)
        } catch {
            case e: Throwable =>
                log.error(s"StatelessSession을 이용한 작업에 실패했습니다.", e)
                throw new HibernateException(e)
        } finally {
            if (stateless != null)
                stateless.close()
        }
    }

    @varargs
    def executeAll(sessionFactory: SessionFactory, actions: (StatelessSession => Unit)*) {
        executeAll(sessionFactory, actions.toIterable)
    }

    def executeAll(sessionFactory: SessionFactory, actions: Iterable[StatelessSession => Unit]) {
        log.debug(s"StatelessSession을 이용하여 작업을 수행합니다...")

        val stateless = sessionFactory.openStatelessSession()

        try {
            actions.foreach(action => action(stateless))
        } catch {
            case e: Throwable =>
                log.error(s"StatelessSession을 이용한 작업에 실패했습니다.", e)
                throw new HibernateException(e)
        } finally {
            if (stateless != null)
                stateless.close()
        }
    }
}
