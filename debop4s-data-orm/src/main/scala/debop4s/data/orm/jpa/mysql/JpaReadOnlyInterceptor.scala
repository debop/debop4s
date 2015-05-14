package debop4s.data.orm.jpa.mysql

import javax.persistence.{EntityManager, PersistenceContext}

import debop4s.data.orm.jpa.utils.JpaUtils
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.{Around, Aspect}
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional

/**
 * MySQL Replication 환경 (Master-Slave)에서
 * `org.springframework.transaction.annotation.Transactional#readOnly()` 이 true로 정의된 Method에 대해서는
 * Slave 서버로 접속하기 위해, `java.sql.Connection#isReadOnly()` 의 속성을 true로 변경하여 작업을 수행하도록 합니다.
 *
 * ==> 위의 방식은 @Transactional 이 상위 메소드에 선언되면, 내부에서 아무리 readOnly 라도 읽어오지 않습니다.
 *
 * 결구 @ReadOnlyConnection 이라는 Annotation을 만들고, 이를 이용하여 처리하도록 합니다.
 *
 * @author sunghyouk.bae@gmail.com
 */
@deprecated(message = "Spring 4.1.x 부터는 Spring이 자동으로 처리해줍니다.", since = "2.0.0")
@Aspect
class JpaReadOnlyInterceptor {

  private lazy val log = LoggerFactory.getLogger(getClass)

  @PersistenceContext val em: EntityManager = null

  /**
   * `Transactional` annotation 이 있는 메소드를 intercept 해서 readonly 인 경우,
   * Slave 로 접속하도록 connection 의 readonly 속성을 true로 설정하여 작업 한 후, 기존 readonly 값으로 복원합니다.
   * 만약 Transactional 이 readonly 가 아닌 경우에는 connection 속성 변경 없이 작업합니다.
   *
   * @param pjp           Intercepting 한 메소드 정보
   */
  @Around(value = "@annotation(transactional) if transactional.isReadOnly()", argNames = "transactional")
  def proceed(pjp: ProceedingJoinPoint, transactional: Transactional): AnyRef = {
    if (transactional.readOnly()) {
      proceedByReadOnly(pjp)
    } else
      pjp.proceed()
  }

  private def proceedByReadOnly(pjp: ProceedingJoinPoint): AnyRef = {
    val conn = JpaUtils.currentConnection(em)
    val readOnly = conn.isReadOnly
    val autoCommit = conn.getAutoCommit

    try {
      log.trace(s"읽기전용 환경으로 변경합니다. readOnly=true, autoCommit=false")
      conn.setReadOnly(true)
      conn.setAutoCommit(false)

      val result = pjp.proceed()
      log.trace(s"읽기전용 작업을 완료했습니다.")
      result
    } finally {
      conn.setReadOnly(readOnly)
      conn.setAutoCommit(autoCommit)
      log.trace(s"읽기전용 작업 완료 후, Connection의 기존 설정으로 복원합니다. readOnly=$readOnly, autoCommit=$autoCommit")
    }
  }
}
