package debop4s.data.orm.hibernate.tools;

import debop4s.core.JAction1;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;

/**
 * {@link StatelessSession} 을 이용한 작업을 수행합니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 8. 오후 7:26
 * @deprecated use {@link debop4s.data.orm.hibernate.utils.StatelessUtils}
 */
@Deprecated
@Slf4j
public final class StatelessTool {

    private StatelessTool() { }

    public static StatelessSession openStatelessSession(Session session) {
        return session.getSessionFactory().openStatelessSession();
    }

    public static void executeTransactional(SessionFactory sessionFactory, JAction1<StatelessSession> action) {
        log.debug("StatelessSession을 이용하여 Transaction 작업을 수행합니다...");

        StatelessSession stateless = sessionFactory.openStatelessSession();
        Transaction tx = stateless.beginTransaction();
        try {
            action.perform(stateless);
            tx.commit();
        } catch (Exception e) {
            log.error("StatelessSession을 이용한 작업에 실패했습니다. rollback 합니다.", e);
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        } finally {
            if (stateless != null)
                stateless.close();
        }
    }

    public static void executeTransactional(Session session, JAction1<StatelessSession> action) {
        log.debug("StatelessSession을 이용하여 Transaction 작업을 수행합니다...");

        StatelessSession stateless = openStatelessSession(session);
        Transaction tx = stateless.beginTransaction();
        try {
            action.perform(stateless);
            tx.commit();
        } catch (Exception e) {
            log.error("StatelessSession을 이용한 작업에 실패했습니다. rollback 합니다.", e);
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        } finally {
            if (stateless != null)
                stateless.close();
        }
    }

    @SafeVarargs
    public static void executeTransactional(Session session, JAction1<StatelessSession>... actions) {
        log.debug("StatelessSession을 이용하여 Transaction 작업을 수행합니다...");

        StatelessSession stateless = openStatelessSession(session);
        Transaction tx = stateless.beginTransaction();

        try {
            for (JAction1<StatelessSession> action : actions) {
                action.perform(stateless);
            }
            tx.commit();
        } catch (Exception e) {
            log.error("StatelessSession을 이용한 작업에 실패했습니다. rollback 합니다.", e);
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        } finally {
            if (stateless != null)
                stateless.close();
        }
    }

    public static void executeTransactional(Session session, Iterable<JAction1<StatelessSession>> actions) {
        log.debug("StatelessSession을 이용하여 Transaction 작업을 수행합니다...");

        StatelessSession stateless = openStatelessSession(session);
        Transaction tx = stateless.beginTransaction();

        try {
            for (JAction1<StatelessSession> action : actions) {
                action.perform(stateless);
            }
            tx.commit();
        } catch (Exception e) {
            log.error("StatelessSession을 이용한 작업에 실패했습니다. rollback 합니다.", e);
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        } finally {
            if (stateless != null)
                stateless.close();
        }
    }


    public static void execute(SessionFactory sessionFactory, JAction1<StatelessSession> action) {
        log.debug("StatelessSession을 이용하여 Transaction 작업을 수행합니다...");

        StatelessSession stateless = sessionFactory.openStatelessSession();
        try {
            action.perform(stateless);
        } catch (Exception e) {
            log.error("StatelessSession을 이용한 작업에 실패했습니다. rollback 합니다.", e);
            throw new RuntimeException(e);
        } finally {
            if (stateless != null)
                stateless.close();
        }
    }

    public static void execute(Session session, JAction1<StatelessSession> action) {
        log.debug("StatelessSession을 이용하여 Transaction 작업을 수행합니다...");

        StatelessSession stateless = openStatelessSession(session);
        try {
            action.perform(stateless);
        } catch (Exception e) {
            log.error("StatelessSession을 이용한 작업에 실패했습니다. rollback 합니다.", e);
            throw new RuntimeException(e);
        } finally {
            if (stateless != null)
                stateless.close();
        }
    }

    @SafeVarargs
    public static void execute(Session session, JAction1<StatelessSession>... actions) {
        log.debug("StatelessSession을 이용하여 Transaction 작업을 수행합니다...");

        StatelessSession stateless = openStatelessSession(session);

        try {
            for (JAction1<StatelessSession> action : actions) {
                action.perform(stateless);
            }
        } catch (Exception e) {
            log.error("StatelessSession을 이용한 작업에 실패했습니다. rollback 합니다.", e);
            throw new RuntimeException(e);
        } finally {
            if (stateless != null)
                stateless.close();
        }
    }

    public static void execute(Session session, Iterable<JAction1<StatelessSession>> actions) {
        log.debug("StatelessSession을 이용하여 Transaction 작업을 수행합니다...");

        StatelessSession stateless = openStatelessSession(session);

        try {
            for (JAction1<StatelessSession> action : actions) {
                action.perform(stateless);
            }
        } catch (Exception e) {
            log.error("StatelessSession을 이용한 작업에 실패했습니다. rollback 합니다.", e);
            throw new RuntimeException(e);
        } finally {
            if (stateless != null)
                stateless.close();
        }
    }


}
