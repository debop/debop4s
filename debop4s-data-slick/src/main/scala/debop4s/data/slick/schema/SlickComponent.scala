package debop4s.data.slick.schema

import debop4s.data.slick.SlickContext
import org.slf4j.LoggerFactory

import scala.util.{ Failure, Success, Try }

/**
 * Slick 사용 시
 * @author sunghyouk.bae@gmail.com 15. 3. 22.
 */
trait SlickComponent
  extends SlickQueryExtensions with SlickSchema with SlickProfile with SlickColumnMapper {

  protected val LOG = LoggerFactory.getLogger(getClass)

  import driver.simple._
  import SlickContext._

  /**
   * 새로운 Session 하에서 작업합니다. 기본적으로 AutoCommit이 false 이므로,
   * Transaction을 명시적으로 만들던가, withTransaction 을 사용하세요.
   *
   * @param func 실행할 코드
   * @tparam M   반환 값의 수형
   * @return     실행 결과 값
   */
  implicit def withSession[M](func: Session => M): M =
    defaultDB.withSession { session =>
      func(session)
    }

  /**
   * 일기 전용으로 작업합니다.
   *
   * {{{
   *   import SlickContext._
   *   import SlickContext.driver.simple._
   *
   *   def findById(id:Long) = withTransaction {
   *      sql"select * from USER where id=$id".as[User].firstOption
   *   }
   * }}}
   * @param func 읽기 수행할 함수
   * @tparam M 엔티티의 수형
   * @return 읽기 결과
   */
  implicit def withReadOnly[M](func: Session => M): M =
    slaveDB.withSession { session =>
      func(session)
    }

  /**
   * Transaction 하에서 작업을 수행합니다.
   *
   * {{{
   *   import SlickContext._
   *   import SlickContext.driver.simple._
   *
   *   def saveUser(user:User) = withTransaction {
   *      TableQuery[Users].save(user)
   *   }
   * }}}
   * @param func Tx 하에서 수행할 코드
   * @tparam M 엔티티의 수형
   * @return 수행 결과
   */
  implicit def withTransaction[M](func: Session => M): M =
    masterDB.withTransaction { implicit session =>
      func(session)
    }

  /**
   * Transaction 하에서 작업을 수행하고, rollback 합니다.
   * 테스트 시에 수행하면 기존 데이터 변경 없이, 해당 코드가 제대로 작동하는 것을 테스트 할 수 있습니다.

   * @param func  Tx 하에서 수행할 작업
   */
  // FIXME: rollback 이 제대로 안된다.다른 방식으로 테스트해야 한다. 대신 withDynTransaction을 이용해야 합니다.
  implicit def withRollback[M](func: Session => M): M = masterDB.withDynTransaction {
    try {
      func(Database.dynamicSession)
    } finally {
      Try(Database.dynamicSession.rollback()) match {
        case Success(s) => LOG.debug(s"Success to rollback")
        case Failure(e) => LOG.error(s"Fail to rollback", e)
      }
    }
  }

  implicit def withDynSession[M](func: Session => M): M = masterDB.withDynSession {
    func(Database.dynamicSession)
  }

  implicit def withDynReadOnly[M](func: Session => M): M = slaveDB.withDynSession {
    func(Database.dynamicSession)
  }

  /**
   * Thread 별로 동적 Session을 사용하여 transaction 하에서 작업을 수행합니다.
   * 단 Thread 별로 Tx를 생성하므로, 병렬 작업 시에는 문제가 발생할 수 있습니다.
   *
   * @param func  Tx 하에서 수행할 작업
   */
  implicit def withDynTransaction[M](func: Session => M): M = masterDB.withDynTransaction {
    func(Database.dynamicSession)
  }

  /**
   * Thread 별로 동적 Session을 사용하여 transaction 하에서 작업을 수행하고, rollback 합니다.
   * 테스트 시에 수행하면 기존 데이터 변경 없이, 해당 코드가 제대로 작동하는 것을 테스트 할 수 있습니다.
   *
   * 단 Thread 별로 Tx를 생성하므로, 병렬 작업 시에는 문제가 발생할 수 있습니다.
   *
   * @param func  Tx 하에서 수행할 작업
   */
  implicit def withDynRollback[M](func: Session => M): M = masterDB.withDynTransaction {
    try {
      func(Database.dynamicSession)
    } finally {
      Try(Database.dynamicSession.rollback()) match {
        case Success(s) => LOG.debug(s"Success to rollback")
        case Failure(e) => LOG.error(s"Fail to rollback", e)
      }
    }
  }

}
