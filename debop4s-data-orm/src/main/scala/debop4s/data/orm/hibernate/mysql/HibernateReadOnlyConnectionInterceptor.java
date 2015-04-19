package debop4s.data.orm.hibernate.mysql;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionImpl;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;

/**
 * MySQL Replication 환경 (Master-Slave)에서
 * `org.springframework.transaction.annotation.Transactional#readOnly()` 이 true로 정의된 Method에 대해서는
 * Slave 서버로 접속하기 위해, `java.sql.Connection#isReadOnly()` 의 속성을 true로 변경하여 작업을 수행하도록 합니다.
 * <p/>
 * ==> 위의 방식은 @Transactional 이 상위 메소드에 선언되면, 내부에서 아무리 readOnly 라도 읽어오지 않습니다.
 * <p/>
 * 결구 @ReadOnlyConnection 이라는 Annotation을 만들고, 이를 이용하여 처리하도록 합니다.
 *
 * @author sunghyouk.bae@gmail.com
 * @deprecated Spring 4.1.x 부터는 Spring이 자동으로 처리해줍니다.
 */
@Deprecated
@Aspect
@Slf4j
public class HibernateReadOnlyConnectionInterceptor {

    @Autowired SessionFactory sessionFactory;

    /**
     * `debop4s.data.orm.ReadOnlyConnection` annotation 이 있는 메소드를 intercept 해서 readonly 인 경우,
     * Slave 로 접속하도록 connection 의 readonly 속성을 true로 설정하여 작업 한 후, 기존 readonly 값으로 복원합니다.
     * 만약 Transactional 이 readonly 가 아닌 경우에는 connection 속성 변경 없이 작업합니다.
     *
     * @param pjp Intercepting 한 메소드 정보
     */
    @Around(value = "@annotation(debop4s.data.orm.ReadOnlyConnection)")
    public Object proceed(ProceedingJoinPoint pjp) throws Throwable {
        SessionImpl session = (SessionImpl) sessionFactory.getCurrentSession();
        Connection conn = session.getTransactionCoordinator().getJdbcCoordinator().getLogicalConnection().getConnection();
        boolean readOnly = conn.isReadOnly();
        boolean autoCommit = conn.getAutoCommit();

        try {
            log.trace("읽기전용 작업을 시작합니다... readOnly={}, autoCommit={}", readOnly, autoCommit);
            conn.setReadOnly(true);
            conn.setAutoCommit(false);

            return pjp.proceed();

        } finally {
            log.trace("읽기전용 작업 완료 후, 기존 설정을 복원합니다. readOnly={}, autoCommit={}", readOnly, autoCommit);
            conn.setReadOnly(readOnly);
            conn.setAutoCommit(autoCommit);
        }
    }
}
