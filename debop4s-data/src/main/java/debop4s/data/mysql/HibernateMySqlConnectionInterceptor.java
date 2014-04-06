package debop4s.data.mysql;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.SQLException;

// NOTE: scala 코드로 만들면 컴파일러에서 에러가 납니다.

/**
 * MySQL Replication 환경 (Master-Slave)에서
 * {@link org.springframework.transaction.annotation.Transactional#readOnly()} 이 true로 정의된 Method에 대해서는
 * Slave 서버로 접속하기 위해, {@link java.sql.Connection#isReadOnly()}의 속성을 true로 변경하여 작업을 수행하도록 합니다.
 *
 * @author sunghyouk.bae@gmail.com
 * @since 2014. 2. 28.
 */
@Aspect
@Component
public class HibernateMySqlConnectionInterceptor {

    private static final Logger log = LoggerFactory.getLogger(HibernateMySqlConnectionInterceptor.class);

    @Autowired
    private SessionFactory sessionFactory;

    /**
     * {@link @Transactional} 이 있는 메소드를 intercept 해서 readOnly 값에 따라 MySQL의 Master / Slave 서버를 구분합니다.
     *
     * @param pjp           Intercepting 한 메소드 정보
     * @param transactional @Transactional 정보
     */
    @Around(value = "@annotation(transactional) if transactional.readOnly()", argNames = "pjp, transactional")
    public Object proceed(ProceedingJoinPoint pjp, Transactional transactional) throws Throwable {
        log.trace("읽기전용 작업을 수행하기 위해 현 connection를 readonly로 설정합니다...");

        Session session = sessionFactory.getCurrentSession();
        ConnectionReadOnlyWork readOnlyWork = new ConnectionReadOnlyWork();

        try {
            session.doWork(readOnlyWork);
            return pjp.proceed();
        } finally {
            session.doWork(new RestoreConnectionWork(readOnlyWork.readOnly));
        }
    }

    /**
     * Connection을 readonly 속성을 true로  설정합니다.
     */
    static class ConnectionReadOnlyWork implements Work {

        public boolean readOnly;

        @Override
        public void execute(Connection connection) throws SQLException {
            this.readOnly = connection.isReadOnly();
            connection.setReadOnly(true);
        }
    }

    /**
     * Connection의 readonly 속성을 기존 속성으로 변환합니다.
     */
    static class RestoreConnectionWork implements Work {

        boolean readOnly;

        public RestoreConnectionWork(boolean readOnly) {
            this.readOnly = readOnly;
        }

        @Override
        public void execute(Connection connection) throws SQLException {
            connection.setReadOnly(readOnly);
            log.trace("읽기전용 작업을 수행하고, connection의 원래 설정으로 재설정했습니다.");
        }
    }
}
